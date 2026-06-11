package dao;

import model.AttendanceLog;
import model.AttendanceRecord;
import model.AttendanceSummary;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    // Cấu hình giờ làm việc chuẩn
    private static final LocalTime STANDARD_CHECK_IN = LocalTime.of(8, 0);   // 8:00
    private static final LocalTime STANDARD_CHECK_OUT = LocalTime.of(17, 0); // 17:00
    private static final double STANDARD_WORK_HOURS = 8.0;                   // 8 giờ công/ngày
    private static final double HALF_DAY_WORK_HOURS = STANDARD_WORK_HOURS / 2;
    private static final int LATE_GRACE_MINUTES = 5;                         // grace cho đi muộn
    private static final int PENALTY_BLOCK_MINUTES = 30;                     // block phạt 30 phút

    // ==================== ATTENDANCE LOGS ====================

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

    // ==================== ATTENDANCE RECORDS ====================

    public void calculateAndSaveRecord(int employeeId, LocalDate workDate) {
        AttendanceLog log = getLogByEmployeeAndDate(employeeId, workDate);
        if (log == null) return;

        // Có thể kiểm tra user nếu cần (nghỉ phép...)
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
                "COALESCE(SUM(CASE WHEN status = 'ON_LEAVE' THEN 1 ELSE 0 END), 0) AS leave_days " +
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
                }
            }

            loadLeaveBalance(conn, userId, start.getYear(), summary);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return summary;
    }

    private void loadLeaveBalance(Connection conn, int userId, int year, AttendanceSummary summary)
            throws SQLException {
        String sql = "SELECT entitled_days, advanced_days, remaining_days " +
                "FROM leave_balances WHERE user_id = ? AND year = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.setEntitledLeaveDays(rs.getDouble("entitled_days"));
                    summary.setAdvancedLeaveDays(rs.getDouble("advanced_days"));
                    summary.setRemainingLeaveDays(rs.getDouble("remaining_days"));
                }
            }
        }
    }

    // ==================== TÍNH TOÁN THEO LUẬT MỚI ====================

    private void calculateWorkingHours(AttendanceRecord record) {
        LocalDateTime checkIn = record.getCheckIn();
        LocalDateTime checkOut = record.getCheckOut();

        // Mặc định
        record.setTotalWorkHours(0.0);
        record.setLateHours(0.0);
        record.setEarlyLeaveHours(0.0);
        record.setOvertimeHours(0.0); // OT do đơn xin, không tự tính

        if (checkIn == null && checkOut == null) return;

        // Quên một trong hai mốc chấm công: chỉ tính nửa ngày công.
        if (checkIn == null || checkOut == null) {
            record.setTotalWorkHours(HALF_DAY_WORK_HOURS);
            return;
        }

        // Tính số phút đi muộn (sau 8:00)
        LocalTime checkInTime = checkIn.toLocalTime();
        long lateMinutes = Duration.between(STANDARD_CHECK_IN, checkInTime).toMinutes();
        if (lateMinutes < 0) lateMinutes = 0; // đến sớm không tính muộn

        // Tính số phút về sớm (trước 17:00)
        LocalTime checkOutTime = checkOut.toLocalTime();
        long earlyMinutes = Duration.between(checkOutTime, STANDARD_CHECK_OUT).toMinutes();
        if (earlyMinutes < 0) earlyMinutes = 0; // về muộn không tính sớm

        // Lưu số giờ thực tế đi muộn / về sớm (để báo cáo)
        record.setLateHours(roundToTwoDecimals(lateMinutes / 60.0));
        record.setEarlyLeaveHours(roundToTwoDecimals(earlyMinutes / 60.0));

        // Tính phạt:
        double latePenalty = calculateLatePenalty(lateMinutes);
        double earlyPenalty = calculateEarlyPenalty(earlyMinutes);

        double totalWork = STANDARD_WORK_HOURS - latePenalty - earlyPenalty;
        if (totalWork < 0) totalWork = 0;
        if (totalWork > STANDARD_WORK_HOURS) totalWork = STANDARD_WORK_HOURS; // tối đa 9h

        record.setTotalWorkHours(roundToTwoDecimals(totalWork));
    }

    // Phạt đi muộn: grace 5 phút, sau đó block 30 phút
    private double calculateLatePenalty(long lateMinutes) {
        if (lateMinutes <= LATE_GRACE_MINUTES) {
            return 0.0;
        }
        long punishable = lateMinutes - LATE_GRACE_MINUTES;
        long blocks = (punishable + PENALTY_BLOCK_MINUTES - 1) / PENALTY_BLOCK_MINUTES; // ceil
        return blocks * 0.5;
    }

    // Phạt về sớm: không grace, block 30 phút làm tròn lên
    private double calculateEarlyPenalty(long earlyMinutes) {
        if (earlyMinutes <= 0) {
            return 0.0;
        }
        long blocks = (earlyMinutes + PENALTY_BLOCK_MINUTES - 1) / PENALTY_BLOCK_MINUTES; // ceil
        return blocks * 0.5;
    }

    private String determineStatus(AttendanceRecord record) {
        boolean hasCheckIn = record.getCheckIn() != null;
        boolean hasCheckOut = record.getCheckOut() != null;

        if (!hasCheckIn && !hasCheckOut) {
            return "ABSENT"; // có thể là FORGOT_BOTH nếu nghi ngờ
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

    // ==================== MAPPING ====================

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
