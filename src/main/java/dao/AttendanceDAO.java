package dao;

import model.AttendanceLog;
import model.AttendanceRecord;
import model.AttendanceRecordDTO;
import model.AttendanceSummary;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    private static final DateTimeFormatter MATRIX_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private static final LocalTime STANDARD_CHECK_IN = LocalTime.of(8, 0);
    private static final LocalTime STANDARD_CHECK_OUT = LocalTime.of(17, 0);
    private static final double STANDARD_WORK_HOURS = 8.0;
    private static final double HALF_DAY_WORK_HOURS = STANDARD_WORK_HOURS / 2;
    private static final int LATE_GRACE_MINUTES = 5;
    private static final int PENALTY_BLOCK_MINUTES = 30;

    public boolean saveAttendanceLog(AttendanceLog log) {
        String sql = "INSERT INTO attendance_logs (work_date, employee_id, check_in, check_out) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE check_in = VALUES(check_in), check_out = VALUES(check_out)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(log.getWorkDate()));
            ps.setInt(2, log.getEmployeeId());
            setNullableTimestamp(ps, 3, log.getCheckIn());
            setNullableTimestamp(ps, 4, log.getCheckOut());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int saveAllAttendanceLogs(List<AttendanceLog> logs) {
        String sql = "INSERT INTO attendance_logs (work_date, employee_id, check_in, check_out) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE check_in = VALUES(check_in), check_out = VALUES(check_out)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (AttendanceLog log : logs) {
                ps.setDate(1, Date.valueOf(log.getWorkDate()));
                ps.setInt(2, log.getEmployeeId());
                setNullableTimestamp(ps, 3, log.getCheckIn());
                setNullableTimestamp(ps, 4, log.getCheckOut());
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            conn.commit();
            return results.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<AttendanceLog> getLogsByEmployee(int employeeId, LocalDate start, LocalDate end) {
        List<AttendanceLog> list = new ArrayList<>();
        String sql = "SELECT id, work_date, employee_id, check_in, check_out FROM attendance_logs " +
                "WHERE employee_id = ? AND work_date BETWEEN ? AND ? ORDER BY work_date";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapLogResultSet(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void deleteAttendanceLog(int employeeId, LocalDate date) {
        String sql = "DELETE FROM attendance_logs WHERE employee_id = ? AND work_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void calculateAndSaveRecord(int employeeId, LocalDate workDate) {
        AttendanceRecord existing = getRecordByUserAndDate(employeeId, workDate);
        if (existing != null && ("ON_LEAVE".equals(existing.getStatus()) || "ABSENT".equals(existing.getStatus()))) {
            return;
        }

        AttendanceLog log = getLogByEmployeeAndDate(employeeId, workDate);
        if (log == null) return;

        UserDAO userDAO = new UserDAO();
        User user = userDAO.findById(employeeId);

        AttendanceRecord record = new AttendanceRecord();
        record.setUserId(employeeId);
        record.setWorkDate(workDate);
        record.setCheckIn(log.getCheckIn());
        record.setCheckOut(log.getCheckOut());

        calculateWorkingHours(record);

        String status = determineStatus(record);
        record.setStatus(status);

        record.setNote(null);
        saveAttendanceRecord(record);
    }

    public void processAllPendingLogs() {
        String sql = "SELECT DISTINCT employee_id, work_date FROM attendance_logs " +
                "WHERE (employee_id, work_date) NOT IN (SELECT user_id, work_date FROM attendance_records)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int empId = rs.getInt("employee_id");
                LocalDate date = rs.getDate("work_date").toLocalDate();
                calculateAndSaveRecord(empId, date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean saveAttendanceRecord(AttendanceRecord record) {
        String sql = "INSERT INTO attendance_records (user_id, work_date, check_in, check_out, " +
                "total_work_hours, overtime_hours, late_hours, early_leave_hours, status, note, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE " +
                "check_in = VALUES(check_in), check_out = VALUES(check_out), " +
                "total_work_hours = VALUES(total_work_hours), " +
                "overtime_hours = VALUES(overtime_hours), " +
                "late_hours = VALUES(late_hours), " +
                "early_leave_hours = VALUES(early_leave_hours), " +
                "status = VALUES(status), note = VALUES(note), updated_at = NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, record.getUserId());
            ps.setDate(2, Date.valueOf(record.getWorkDate()));
            setNullableTimestamp(ps, 3, record.getCheckIn());
            setNullableTimestamp(ps, 4, record.getCheckOut());
            ps.setObject(5, record.getTotalWorkHours(), Types.DECIMAL);
            ps.setObject(6, record.getOvertimeHours() != null ? record.getOvertimeHours() : 0.0, Types.DECIMAL);
            ps.setObject(7, record.getLateHours() != null ? record.getLateHours() : 0.0, Types.DECIMAL);
            ps.setObject(8, record.getEarlyLeaveHours() != null ? record.getEarlyLeaveHours() : 0.0, Types.DECIMAL);
            ps.setString(9, record.getStatus());
            ps.setString(10, record.getNote());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public AttendanceRecord getRecordByUserAndDate(int userId, LocalDate date) {
        String sql = "SELECT * FROM attendance_records WHERE user_id = ? AND work_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRecordResultSet(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AttendanceRecord getAttendanceRecordById(int id) {
        String sql = "SELECT * FROM attendance_records WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRecordResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AttendanceRecordDTO getAttendanceRecordDetailById(int id) {
        String sql =
                "SELECT ar.id AS attendance_record_id, ar.user_id, u.employee_code, " +
                        "u.full_name AS employee_name, d.name AS department_name, ar.work_date, " +
                        "ar.check_in, ar.check_out, ar.total_work_hours, ar.overtime_hours, " +
                        "ar.late_hours, ar.early_leave_hours, ar.status, ar.note " +
                        "FROM attendance_records ar " +
                        "JOIN users u ON u.id = ar.user_id " +
                        "LEFT JOIN departments d ON d.id = u.department_id " +
                        "WHERE ar.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AttendanceRecordDTO dto = new AttendanceRecordDTO();
                    LocalDateTime checkIn = getNullableLocalDateTime(rs, "check_in");
                    LocalDateTime checkOut = getNullableLocalDateTime(rs, "check_out");

                    dto.setAttendanceRecordId(rs.getInt("attendance_record_id"));
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setEmployeeCode(rs.getString("employee_code"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setDepartmentName(rs.getString("department_name"));
                    dto.setWorkDate(rs.getDate("work_date").toLocalDate());
                    dto.setCheckIn(checkIn);
                    dto.setCheckOut(checkOut);
                    dto.setCheckInText(
                            checkIn == null ? "" : checkIn.format(MATRIX_TIME_FORMAT)
                    );
                    dto.setCheckOutText(
                            checkOut == null ? "" : checkOut.format(MATRIX_TIME_FORMAT)
                    );
                    dto.setTotalWorkHours(getNullableDouble(rs, "total_work_hours"));
                    dto.setOvertimeHours(getNullableDouble(rs, "overtime_hours"));
                    dto.setLateHours(getNullableDouble(rs, "late_hours"));
                    dto.setEarlyLeaveHours(getNullableDouble(rs, "early_leave_hours"));
                    dto.setStatus(rs.getString("status"));
                    dto.setNote(rs.getString("note"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAttendanceRecord(AttendanceRecord record) {
        String sql =
                "UPDATE attendance_records SET " +
                        "check_in = ?, check_out = ?, total_work_hours = ?, " +
                        "late_hours = ?, early_leave_hours = ?, status = ?, note = ?, " +
                        "updated_at = NOW() WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setNullableTimestamp(ps, 1, record.getCheckIn());
            setNullableTimestamp(ps, 2, record.getCheckOut());
            ps.setDouble(3, record.getTotalWorkHours());
            ps.setDouble(4, record.getLateHours());
            ps.setDouble(5, record.getEarlyLeaveHours());
            ps.setString(6, record.getStatus());
            ps.setString(7, record.getNote());
            ps.setInt(8, record.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateOvertimeHours(int recordId, double overtimeHours) {
        String sql = "UPDATE attendance_records SET overtime_hours = ?, updated_at = updated_at WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, overtimeHours);
            ps.setInt(2, recordId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AttendanceRecord> getRecordsByUser(int userId, LocalDate start, LocalDate end) {
        List<AttendanceRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance_records WHERE user_id = ? AND work_date BETWEEN ? AND ? ORDER BY work_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRecordResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AttendanceRecord> getRecordsByDepartment(int departmentId, LocalDate date) {
        List<AttendanceRecord> list = new ArrayList<>();
        String sql = "SELECT ar.* FROM attendance_records ar " +
                "JOIN users u ON ar.user_id = u.id " +
                "WHERE u.department_id = ? AND ar.work_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRecordResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AttendanceRecordDTO> getAttendanceRecordsForMatrix(
            int month,
            int year,
            Integer departmentId,
            String keyword,
            int page,
            int pageSize
    ) {
        List<AttendanceRecordDTO> records = new ArrayList<>();
        YearMonth period = YearMonth.of(year, month);
        LocalDate startDate = period.atDay(1);
        LocalDate endDate = period.atEndOfMonth();
        int offset = Math.max(0, page - 1) * pageSize;
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        StringBuilder employeeFilter = new StringBuilder(
                " FROM users u " +
                        "JOIN attendance_records filter_ar ON filter_ar.user_id = u.id " +
                        "WHERE filter_ar.work_date BETWEEN ? AND ? AND u.active = TRUE"
        );
        if (departmentId != null) {
            employeeFilter.append(" AND u.department_id = ?");
        }
        if (!normalizedKeyword.isEmpty()) {
            employeeFilter.append(" AND (u.full_name LIKE ? OR u.employee_code LIKE ?)");
        }

        String sql =
                "SELECT ar.id AS attendance_record_id, u.id AS user_id, u.employee_code, " +
                        "u.full_name AS employee_name, d.name AS department_name, ar.work_date, " +
                        "ar.check_in, ar.check_out, ar.total_work_hours, ar.overtime_hours, " +
                        "ar.late_hours, ar.early_leave_hours, ar.status, ar.note, ar.updated_at, " +
                        "ot.status AS ot_status " +
                        "FROM (" +
                        "SELECT DISTINCT u.id, u.full_name " + employeeFilter +
                        " ORDER BY u.full_name, u.id LIMIT ? OFFSET ?" +
                        ") page_users " +
                        "JOIN users u ON u.id = page_users.id " +
                        "LEFT JOIN departments d ON d.id = u.department_id " +
                        "JOIN attendance_records ar ON ar.user_id = u.id " +
                        "AND ar.work_date BETWEEN ? AND ? " +
                        "LEFT JOIN (SELECT op1.user_id, op1.status, oreq1.overtime_date FROM overtime_participants op1 JOIN overtime_requests oreq1 ON op1.overtime_request_id = oreq1.id) ot " +
                        "ON ot.user_id = ar.user_id AND ot.overtime_date = ar.work_date " +
                        "ORDER BY u.full_name, u.id, ar.work_date";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            ps.setDate(index++, Date.valueOf(startDate));
            ps.setDate(index++, Date.valueOf(endDate));
            if (departmentId != null) {
                ps.setInt(index++, departmentId);
            }
            if (!normalizedKeyword.isEmpty()) {
                String likeKeyword = "%" + normalizedKeyword + "%";
                ps.setString(index++, likeKeyword);
                ps.setString(index++, likeKeyword);
            }
            ps.setInt(index++, pageSize);
            ps.setInt(index++, offset);
            ps.setDate(index++, Date.valueOf(startDate));
            ps.setDate(index, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(mapAttendanceMatrixResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public int countEmployeesForAttendanceMatrix(
            int month,
            int year,
            Integer departmentId,
            String keyword
    ) {
        YearMonth period = YearMonth.of(year, month);
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT u.id) " +
                        "FROM users u " +
                        "JOIN attendance_records ar ON ar.user_id = u.id " +
                        "WHERE ar.work_date BETWEEN ? AND ? AND u.active = TRUE"
        );
        if (departmentId != null) {
            sql.append(" AND u.department_id = ?");
        }
        if (!normalizedKeyword.isEmpty()) {
            sql.append(" AND (u.full_name LIKE ? OR u.employee_code LIKE ?)");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            ps.setDate(index++, Date.valueOf(period.atDay(1)));
            ps.setDate(index++, Date.valueOf(period.atEndOfMonth()));
            if (departmentId != null) {
                ps.setInt(index++, departmentId);
            }
            if (!normalizedKeyword.isEmpty()) {
                String likeKeyword = "%" + normalizedKeyword + "%";
                ps.setString(index++, likeKeyword);
                ps.setString(index, likeKeyword);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public AttendanceSummary getSummaryByUser(int userId, LocalDate start, LocalDate end) {
        AttendanceSummary summary = new AttendanceSummary();

        String attendanceSql =
                "SELECT " +
                        "COALESCE(SUM(total_work_hours), 0) AS total_work_hours, " +
                        "COALESCE(SUM(overtime_hours), 0) AS overtime_hours, " +
                        "COALESCE(SUM(COALESCE(late_hours, 0) + COALESCE(early_leave_hours, 0)), 0) AS penalty_hours, " +
                        "COALESCE(SUM(CASE WHEN late_hours > 0 THEN 1 ELSE 0 END), 0) AS late_count, " +
                        "COALESCE(SUM(CASE WHEN early_leave_hours > 0 THEN 1 ELSE 0 END), 0) AS early_count, " +
                        "COALESCE(SUM(CASE WHEN (check_in IS NULL) <> (check_out IS NULL) THEN 1 ELSE 0 END), 0) AS forgot_count, " +
                        "COALESCE(SUM(CASE WHEN status = 'ON_LEAVE' THEN 1 ELSE 0 END), 0) AS leave_days, " +
                        "COALESCE(SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END), 0) AS absent_days " +
                        "FROM attendance_records WHERE user_id = ? AND work_date BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(attendanceSql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.setTotalWorkHours(rs.getDouble("total_work_hours"));
                    summary.setOvertimeHours(rs.getDouble("overtime_hours"));
                    summary.setTotalLateAndEarlyHours(rs.getDouble("penalty_hours"));
                    summary.setLateCount(rs.getInt("late_count"));
                    summary.setEarlyLeaveCount(rs.getInt("early_count"));
                    summary.setForgotCheckCount(rs.getInt("forgot_count"));
                    summary.setLeaveDaysInMonth(rs.getDouble("leave_days"));
                    summary.setAbsentDaysInMonth(rs.getDouble("absent_days"));
                }
            }

            calculateLeaveBalance(conn, userId, end.getYear(), end.getMonthValue(), summary);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return summary;
    }

    private void calculateLeaveBalance(Connection conn, int userId, int year, int month,
                                       AttendanceSummary summary) throws SQLException {
        double entitled = month;

        String sqlLeave = "SELECT COALESCE(SUM(CASE WHEN status = 'ON_LEAVE' THEN 1 ELSE 0 END), 0) AS used_days " +
                "FROM attendance_records WHERE user_id = ? " +
                "AND work_date BETWEEN ? AND ?";
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfMonth = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());
        double usedLeave = 0;
        try (PreparedStatement ps = conn.prepareStatement(sqlLeave)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(startOfYear));
            ps.setDate(3, Date.valueOf(endOfMonth));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usedLeave = rs.getDouble("used_days");
                }
            }
        }
        summary.setEntitledLeaveDays(entitled);
        summary.setRemainingLeaveDays(entitled - usedLeave);

        double entitledAbsent = month;
        String sqlAbsent = "SELECT COALESCE(SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END), 0) AS used_days " +
                "FROM attendance_records WHERE user_id = ? " +
                "AND work_date BETWEEN ? AND ?";
        double usedAbsent = 0;
        try (PreparedStatement ps = conn.prepareStatement(sqlAbsent)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(startOfYear));
            ps.setDate(3, Date.valueOf(endOfMonth));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usedAbsent = rs.getDouble("used_days");
                }
            }
        }
        summary.setEntitledAbsentDays(entitledAbsent);
        summary.setRemainingAbsentDays(entitledAbsent - usedAbsent);
    }

    public List<AttendanceRecordDTO> getAttendanceDetailByUserAndMonth(int userId, int month, int year) {
        List<AttendanceRecordDTO> list = new ArrayList<>();
        YearMonth ym = YearMonth.of(year, month);
        String sql = "SELECT ar.id AS attendance_record_id, u.id AS user_id, u.employee_code, " +
                "u.full_name AS employee_name, d.name AS department_name, p.name AS position_name, " +
                "ar.work_date, ar.check_in, ar.check_out, ar.total_work_hours, ar.overtime_hours, " +
                "ar.late_hours, ar.early_leave_hours, ar.status, ar.note, ar.updated_at, ot.status AS ot_status " +
                "FROM attendance_records ar " +
                "JOIN users u ON u.id = ar.user_id " +
                "LEFT JOIN departments d ON d.id = u.department_id " +
                "LEFT JOIN positions p ON p.id = u.position_id " +
                "LEFT JOIN (SELECT op1.user_id, op1.status, oreq1.overtime_date FROM overtime_participants op1 JOIN overtime_requests oreq1 ON op1.overtime_request_id = oreq1.id) ot " +
                "ON ot.user_id = ar.user_id AND ot.overtime_date = ar.work_date " +
                "WHERE ar.user_id = ? AND MONTH(ar.work_date) = ? AND YEAR(ar.work_date) = ? " +
                "ORDER BY ar.work_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecordDTO dto = new AttendanceRecordDTO();
                    LocalDateTime checkIn = getNullableLocalDateTime(rs, "check_in");
                    LocalDateTime checkOut = getNullableLocalDateTime(rs, "check_out");
                    dto.setAttendanceRecordId(rs.getInt("attendance_record_id"));
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setEmployeeCode(rs.getString("employee_code"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setDepartmentName(rs.getString("department_name"));
                    dto.setPositionName(rs.getString("position_name"));
                    dto.setWorkDate(rs.getDate("work_date").toLocalDate());
                    dto.setCheckIn(checkIn);
                    dto.setCheckOut(checkOut);
                    dto.setTotalWorkHours(getNullableDouble(rs, "total_work_hours"));
                    dto.setOvertimeHours(getNullableDouble(rs, "overtime_hours"));
                    dto.setLateHours(getNullableDouble(rs, "late_hours"));
                    dto.setEarlyLeaveHours(getNullableDouble(rs, "early_leave_hours"));
                    dto.setStatus(rs.getString("status"));
                    dto.setNote(rs.getString("note"));
                    dto.setCheckInText(checkIn == null ? "" : checkIn.format(MATRIX_TIME_FORMAT));
                    dto.setCheckOutText(checkOut == null ? "" : checkOut.format(MATRIX_TIME_FORMAT));
                    dto.setEdited(rs.getTimestamp("updated_at") != null && !"ON_LEAVE".equals(dto.getStatus()));
                    dto.setOtStatus(rs.getString("ot_status"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AttendanceRecordDTO> getAllAttendanceRecordsForExport(int month, int year, Integer departmentId, String keyword) {
        List<AttendanceRecordDTO> records = new ArrayList<>();
        YearMonth period = YearMonth.of(year, month);
        LocalDate startDate = period.atDay(1);
        LocalDate endDate = period.atEndOfMonth();
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        StringBuilder sql = new StringBuilder(
                "SELECT ar.id AS attendance_record_id, u.id AS user_id, u.employee_code, " +
                        "u.full_name AS employee_name, d.name AS department_name, p.name AS position_name, " +
                        "ar.work_date, ar.check_in, ar.check_out, ar.total_work_hours, ar.overtime_hours, " +
                        "ar.late_hours, ar.early_leave_hours, ar.status, ar.note, ot.status AS ot_status " +
                        "FROM attendance_records ar " +
                        "JOIN users u ON u.id = ar.user_id " +
                        "LEFT JOIN departments d ON d.id = u.department_id " +
                        "LEFT JOIN positions p ON p.id = u.position_id " +
                        "LEFT JOIN (SELECT op1.user_id, op1.status, oreq1.overtime_date FROM overtime_participants op1 JOIN overtime_requests oreq1 ON op1.overtime_request_id = oreq1.id) ot " +
                        "ON ot.user_id = ar.user_id AND ot.overtime_date = ar.work_date " +
                        "WHERE ar.work_date BETWEEN ? AND ? AND u.active = TRUE"
        );
        if (departmentId != null) {
            sql.append(" AND u.department_id = ?");
        }
        if (!normalizedKeyword.isEmpty()) {
            sql.append(" AND (u.full_name LIKE ? OR u.employee_code LIKE ?)");
        }
        sql.append(" ORDER BY u.full_name, u.id, ar.work_date");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setDate(idx++, Date.valueOf(startDate));
            ps.setDate(idx++, Date.valueOf(endDate));
            if (departmentId != null) {
                ps.setInt(idx++, departmentId);
            }
            if (!normalizedKeyword.isEmpty()) {
                String likeKeyword = "%" + normalizedKeyword + "%";
                ps.setString(idx++, likeKeyword);
                ps.setString(idx++, likeKeyword);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecordDTO dto = new AttendanceRecordDTO();
                    LocalDateTime checkIn = getNullableLocalDateTime(rs, "check_in");
                    LocalDateTime checkOut = getNullableLocalDateTime(rs, "check_out");
                    dto.setAttendanceRecordId(rs.getInt("attendance_record_id"));
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setEmployeeCode(rs.getString("employee_code"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setDepartmentName(rs.getString("department_name"));
                    dto.setPositionName(rs.getString("position_name"));
                    dto.setWorkDate(rs.getDate("work_date").toLocalDate());
                    dto.setCheckIn(checkIn);
                    dto.setCheckOut(checkOut);
                    dto.setTotalWorkHours(getNullableDouble(rs, "total_work_hours"));
                    dto.setOvertimeHours(getNullableDouble(rs, "overtime_hours"));
                    dto.setLateHours(getNullableDouble(rs, "late_hours"));
                    dto.setEarlyLeaveHours(getNullableDouble(rs, "early_leave_hours"));
                    dto.setStatus(rs.getString("status"));
                    dto.setNote(rs.getString("note"));
                    dto.setCheckInText(checkIn == null ? "" : checkIn.format(MATRIX_TIME_FORMAT));
                    dto.setCheckOutText(checkOut == null ? "" : checkOut.format(MATRIX_TIME_FORMAT));
                    dto.setOtStatus(rs.getString("ot_status"));
                    records.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public void calculateWorkingHours(AttendanceRecord record) {
        LocalDateTime checkIn = record.getCheckIn();
        LocalDateTime checkOut = record.getCheckOut();

        record.setTotalWorkHours(0.0);
        record.setLateHours(0.0);
        record.setEarlyLeaveHours(0.0);
        record.setOvertimeHours(0.0);

        if (checkIn != null && checkIn.toLocalTime().isAfter(STANDARD_CHECK_OUT)) {
            return;
        }

        if (checkIn == null && checkOut == null) return;

        if (checkIn == null || checkOut == null) {
            record.setTotalWorkHours(HALF_DAY_WORK_HOURS);
            return;
        }

        LocalTime checkInTime = checkIn.toLocalTime();
        long lateMinutes = Duration.between(STANDARD_CHECK_IN, checkInTime).toMinutes();
        if (lateMinutes < 0) lateMinutes = 0;

        LocalTime checkOutTime = checkOut.toLocalTime();
        long earlyMinutes = Duration.between(checkOutTime, STANDARD_CHECK_OUT).toMinutes();
        if (earlyMinutes < 0) earlyMinutes = 0;

        long countedLateMinutes = lateMinutes > LATE_GRACE_MINUTES ? lateMinutes : 0;
        record.setLateHours(roundToTwoDecimals(countedLateMinutes / 60.0));
        record.setEarlyLeaveHours(roundToTwoDecimals(earlyMinutes / 60.0));

        double latePenalty = calculateLatePenalty(lateMinutes);
        double earlyPenalty = calculateEarlyPenalty(earlyMinutes);

        double totalWork = STANDARD_WORK_HOURS - latePenalty - earlyPenalty;
        if (totalWork < 0) totalWork = 0;
        if (totalWork > STANDARD_WORK_HOURS) totalWork = STANDARD_WORK_HOURS;

        record.setTotalWorkHours(roundToTwoDecimals(totalWork));
    }

    private double calculateLatePenalty(long lateMinutes) {
        if (lateMinutes <= LATE_GRACE_MINUTES) {
            return 0.0;
        }

        long blocks = (lateMinutes + PENALTY_BLOCK_MINUTES - 1) / PENALTY_BLOCK_MINUTES;
        return blocks * 0.5;
    }

    private double calculateEarlyPenalty(long earlyMinutes) {
        if (earlyMinutes <= 0) {
            return 0.0;
        }
        long blocks = (earlyMinutes + PENALTY_BLOCK_MINUTES - 1) / PENALTY_BLOCK_MINUTES;
        return blocks * 0.5;
    }

    public String determineStatus(AttendanceRecord record) {
        if (record.getCheckIn() != null && record.getCheckIn().toLocalTime().isAfter(STANDARD_CHECK_OUT)) {
            return "ABSENT";
        }

        boolean hasCheckIn = record.getCheckIn() != null;
        boolean hasCheckOut = record.getCheckOut() != null;

        if (!hasCheckIn && !hasCheckOut) {
            return "ABSENT";
        } else if (!hasCheckIn) {
            return "FORGOT_CHECK_IN";
        } else if (!hasCheckOut) {
            return "FORGOT_CHECK_OUT";
        } else {
            double late = record.getLateHours() != null ? record.getLateHours() : 0.0;
            double early = record.getEarlyLeaveHours() != null ? record.getEarlyLeaveHours() : 0.0;
            if (late > 0 && early > 0) {
                return "LATE_AND_EARLY_LEAVE";
            } else if (late > 0) {
                return "LATE";
            } else if (early > 0) {
                return "EARLY_LEAVE";
            } else {
                return "ON_TIME";
            }
        }
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private AttendanceLog mapLogResultSet(ResultSet rs) throws SQLException {
        AttendanceLog log = new AttendanceLog();
        log.setId(rs.getInt("id"));
        log.setWorkDate(rs.getDate("work_date").toLocalDate());
        log.setEmployeeId(rs.getInt("employee_id"));
        log.setCheckIn(getNullableLocalDateTime(rs, "check_in"));
        log.setCheckOut(getNullableLocalDateTime(rs, "check_out"));
        return log;
    }

    private AttendanceRecord mapRecordResultSet(ResultSet rs) throws SQLException {
        AttendanceRecord record = new AttendanceRecord();
        record.setId(rs.getInt("id"));
        record.setUserId(rs.getInt("user_id"));
        record.setWorkDate(rs.getDate("work_date").toLocalDate());
        record.setCheckIn(getNullableLocalDateTime(rs, "check_in"));
        record.setCheckOut(getNullableLocalDateTime(rs, "check_out"));
        record.setTotalWorkHours(rs.getObject("total_work_hours") != null ? rs.getDouble("total_work_hours") : null);
        record.setOvertimeHours(rs.getDouble("overtime_hours"));
        record.setLateHours(rs.getDouble("late_hours"));
        record.setEarlyLeaveHours(rs.getDouble("early_leave_hours"));
        record.setStatus(rs.getString("status"));
        record.setNote(rs.getString("note"));
        record.setCreatedAt(getNullableLocalDateTime(rs, "created_at"));
        record.setUpdatedAt(getNullableLocalDateTime(rs, "updated_at"));
        return record;
    }

    private AttendanceRecordDTO mapAttendanceMatrixResultSet(ResultSet rs) throws SQLException {
        AttendanceRecordDTO dto = new AttendanceRecordDTO();
        LocalDateTime checkIn = getNullableLocalDateTime(rs, "check_in");
        LocalDateTime checkOut = getNullableLocalDateTime(rs, "check_out");
        double overtimeHours = rs.getDouble("overtime_hours");
        String status = rs.getString("status");

        dto.setAttendanceRecordId(rs.getInt("attendance_record_id"));
        dto.setUserId(rs.getInt("user_id"));
        dto.setEmployeeCode(rs.getString("employee_code"));
        dto.setEmployeeName(rs.getString("employee_name"));
        dto.setDepartmentName(rs.getString("department_name"));
        dto.setWorkDate(rs.getDate("work_date").toLocalDate());
        dto.setCheckIn(checkIn);
        dto.setCheckOut(checkOut);
        dto.setTotalWorkHours(getNullableDouble(rs, "total_work_hours"));
        dto.setOvertimeHours(overtimeHours);
        dto.setLateHours(getNullableDouble(rs, "late_hours"));
        dto.setEarlyLeaveHours(getNullableDouble(rs, "early_leave_hours"));
        dto.setStatus(status);
        dto.setNote(rs.getString("note"));
        dto.setCheckInText(checkIn == null ? "--" : checkIn.format(MATRIX_TIME_FORMAT));
        dto.setCheckOutText(checkOut == null ? "--" : checkOut.format(MATRIX_TIME_FORMAT));
        dto.setEdited(rs.getTimestamp("updated_at") != null);
        dto.setCssClass(resolveMatrixCssClass(status));
        dto.setOtStatus(rs.getString("ot_status"));
        return dto;
    }

    private Double getNullableDouble(ResultSet rs, String column) throws SQLException {
        Object value = rs.getObject(column);
        return value == null ? null : rs.getDouble(column);
    }

    private String resolveMatrixCssClass(String status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case "ON_TIME" -> "status-on-time";
            case "LATE", "EARLY_LEAVE", "LATE_AND_EARLY", "LATE_AND_EARLY_LEAVE" -> "status-late";
            case "ON_LEAVE" -> "status-leave";
            case "ABSENT" -> "status-absent";
            case "FORGOT_CHECKIN", "FORGOT_CHECKOUT",
                 "FORGOT_CHECK_IN", "FORGOT_CHECK_OUT" -> "status-forgot";
            default -> "";
        };
    }

    private LocalDateTime getNullableLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return (ts != null) ? ts.toLocalDateTime() : null;
    }

    private void setNullableTimestamp(PreparedStatement ps, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            ps.setTimestamp(index, null);
        } else {
            ps.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    public void markOnLeave(int userId, LocalDate date) {
        deleteAttendanceLog(userId, date);
        String sql = "INSERT INTO attendance_records (user_id, work_date, check_in, check_out, total_work_hours, overtime_hours, late_hours, early_leave_hours, status, note, created_at) " +
                "VALUES (?, ?, NULL, NULL, 8.0, 0.0, 0.0, 0.0, 'ON_LEAVE', 'Leave request approved', NOW()) " +
                "ON DUPLICATE KEY UPDATE check_in = NULL, check_out = NULL, total_work_hours = 8.0, overtime_hours = 0.0, late_hours = 0.0, early_leave_hours = 0.0, status = 'ON_LEAVE', note = VALUES(note), updated_at = NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markAbsent(int userId, LocalDate date) {
        deleteAttendanceLog(userId, date);
        String sql = "INSERT INTO attendance_records (user_id, work_date, check_in, check_out, total_work_hours, overtime_hours, late_hours, early_leave_hours, status, note, created_at) " +
                "VALUES (?, ?, NULL, NULL, 0.0, 0.0, 0.0, 0.0, 'ABSENT', 'Unpaid leave request approved', NOW()) " +
                "ON DUPLICATE KEY UPDATE check_in = NULL, check_out = NULL, total_work_hours = 0.0, overtime_hours = 0.0, late_hours = 0.0, early_leave_hours = 0.0, status = 'ABSENT', note = VALUES(note), updated_at = NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private AttendanceLog getLogByEmployeeAndDate(int employeeId, LocalDate date) {
        String sql = "SELECT id, work_date, employee_id, check_in, check_out FROM attendance_logs " +
                "WHERE employee_id = ? AND work_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapLogResultSet(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}