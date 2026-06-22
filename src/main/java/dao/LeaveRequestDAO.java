package dao;

import model.LeaveRequest;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class LeaveRequestDAO {

    public void createLeaveRequest(int requestId, LocalDate leaveDate, String leaveType) throws SQLException {
        String sql = "INSERT INTO leave_requests (request_id, leave_date, leave_type) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setDate(2, Date.valueOf(leaveDate));
            ps.setString(3, leaveType);
            ps.executeUpdate();
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
                    lr.setLeaveDate(rs.getDate("leave_date").toLocalDate());
                    lr.setLeaveType(rs.getString("leave_type"));
                    lr.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return lr;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean existsLeaveRequestForDate(int userId, LocalDate leaveDate) {
        String sql = "SELECT COUNT(*) FROM leave_requests lr " +
                "JOIN requests r ON lr.request_id = r.id " +
                "WHERE r.user_id = ? AND lr.leave_date = ? AND r.status IN ('PENDING', 'APPROVED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(leaveDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}