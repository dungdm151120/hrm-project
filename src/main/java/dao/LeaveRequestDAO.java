package dao;

import model.LeaveRequest;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {

    public void createLeaveRequest(int requestId, LocalDate leaveDate, String leaveType) throws SQLException {
        createLeaveRequest(requestId, leaveDate, leaveDate, leaveType, java.util.Collections.singletonList(leaveDate));
    }

    public int createLeaveRequest(int requestId, LocalDate startDate, LocalDate endDate, String leaveType, List<LocalDate> workdays) throws SQLException {
        String sqlLeave = "INSERT INTO leave_requests (request_id, start_date, end_date, leave_date, leave_type) VALUES (?, ?, ?, ?, ?)";
        String sqlDate = "INSERT INTO leave_dates (leave_request_id, leave_date) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int leaveRequestId;
            try (PreparedStatement ps = conn.prepareStatement(sqlLeave, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, requestId);
                ps.setDate(2, Date.valueOf(startDate));
                ps.setDate(3, Date.valueOf(endDate));
                ps.setDate(4, Date.valueOf(startDate));
                ps.setString(5, leaveType);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        leaveRequestId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to create leave request entry");
                    }
                }
            }

            if (workdays != null && !workdays.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlDate)) {
                    for (LocalDate d : workdays) {
                        ps.setInt(1, leaveRequestId);
                        ps.setDate(2, Date.valueOf(d));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();
            return leaveRequestId;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    public LeaveRequest getByRequestId(int requestId) {
        String sql = "SELECT * FROM leave_requests WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LeaveRequest lr = new LeaveRequest();
                    lr.setId(rs.getInt("id"));
                    lr.setRequestId(rs.getInt("request_id"));

                    Date sDate = rs.getDate("start_date");
                    Date eDate = rs.getDate("end_date");
                    Date lDate = rs.getDate("leave_date");

                    if (sDate != null) lr.setStartDate(sDate.toLocalDate());
                    else if (lDate != null) lr.setStartDate(lDate.toLocalDate());

                    if (eDate != null) lr.setEndDate(eDate.toLocalDate());
                    else if (lDate != null) lr.setEndDate(lDate.toLocalDate());

                    if (lDate != null) lr.setLeaveDate(lDate.toLocalDate());

                    lr.setLeaveType(rs.getString("leave_type"));
                    if (rs.getTimestamp("created_at") != null) {
                        lr.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                    return lr;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<LocalDate> getDatesByLeaveRequestId(int leaveRequestId) {
        List<LocalDate> dates = new ArrayList<>();
        String sql = "SELECT leave_date FROM leave_dates WHERE leave_request_id = ? ORDER BY leave_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, leaveRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dates.add(rs.getDate("leave_date").toLocalDate());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }

    public boolean existsLeaveRequestForDate(int userId, LocalDate leaveDate) {
        String sql = "SELECT COUNT(*) FROM leave_requests lr " +
                "JOIN requests r ON lr.request_id = r.id " +
                "LEFT JOIN leave_dates ld ON ld.leave_request_id = lr.id " +
                "WHERE r.user_id = ? AND (lr.leave_date = ? OR ld.leave_date = ?) AND r.status IN ('PENDING', 'APPROVED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(leaveDate));
            ps.setDate(3, Date.valueOf(leaveDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> checkDateConflicts(int userId, List<LocalDate> dates) {
        List<String> conflicts = new ArrayList<>();
        if (dates == null || dates.isEmpty()) return conflicts;

        HolidayDAO holidayDAO = new HolidayDAO();

        String sqlAttendance = "SELECT work_date, status FROM attendance_records " +
                "WHERE user_id = ? AND work_date = ? AND status IN ('ON_LEAVE', 'ABSENT', 'SICK_LEAVE')";

        String sqlLeave = "SELECT lr.leave_type " +
                "FROM leave_requests lr " +
                "JOIN requests r ON lr.request_id = r.id " +
                "LEFT JOIN leave_dates ld ON ld.leave_request_id = lr.id " +
                "WHERE r.user_id = ? AND r.status IN ('PENDING', 'APPROVED') " +
                "AND (ld.leave_date = ? OR lr.leave_date = ? OR (? BETWEEN lr.start_date AND lr.end_date))";

        String sqlSick = "SELECT sd.leave_date " +
                "FROM sick_leave_dates sd " +
                "JOIN sick_leave_requests sr ON sd.sick_leave_request_id = sr.id " +
                "JOIN requests r ON sr.request_id = r.id " +
                "WHERE r.user_id = ? AND r.status IN ('PENDING', 'APPROVED') " +
                "AND sd.leave_date = ?";

        try (Connection conn = DBConnection.getConnection()) {
            for (LocalDate date : dates) {
                boolean foundConflict = false;

                String holidayName = holidayDAO.getHolidayName(date);
                if (holidayName != null) {
                    conflicts.add("Ngày " + date + " là ngày nghỉ lễ (" + holidayName + ")");
                    continue;
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlAttendance)) {
                    ps.setInt(1, userId);
                    ps.setDate(2, Date.valueOf(date));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String st = rs.getString("status");
                            String label = "ON_LEAVE".equals(st) ? "On Leave (Paid)" : ("SICK_LEAVE".equals(st) ? "Sick Leave" : "Absent");
                            conflicts.add("Ngày " + date + " đã có điểm danh: " + label);
                            foundConflict = true;
                        }
                    }
                }

                if (foundConflict) continue;

                try (PreparedStatement ps = conn.prepareStatement(sqlLeave)) {
                    ps.setInt(1, userId);
                    ps.setDate(2, Date.valueOf(date));
                    ps.setDate(3, Date.valueOf(date));
                    ps.setDate(4, Date.valueOf(date));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String lt = rs.getString("leave_type");
                            String label = "ON_LEAVE".equals(lt) ? "On Leave (Paid)" : "Leave (Absent)";
                            conflicts.add("Ngày " + date + " đã có đơn " + label);
                            foundConflict = true;
                        }
                    }
                }

                if (foundConflict) continue;

                try (PreparedStatement ps = conn.prepareStatement(sqlSick)) {
                    ps.setInt(1, userId);
                    ps.setDate(2, Date.valueOf(date));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            conflicts.add("Ngày " + date + " đã có đơn Sick Leave");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conflicts;
    }
}