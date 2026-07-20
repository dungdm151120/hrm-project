package dao;

import model.AttendanceChangeRequest;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceChangeRequestDAO {

    public void create(AttendanceChangeRequest acr) throws SQLException {
        String sql = "INSERT INTO attendance_change_requests (request_id, work_date, desired_check_in, desired_check_out, reason) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, acr.getRequestId());
            ps.setDate(2, Date.valueOf(acr.getWorkDate()));
            if (acr.getDesiredCheckIn() != null) {
                ps.setTime(3, Time.valueOf(acr.getDesiredCheckIn()));
            } else {
                ps.setNull(3, Types.TIME);
            }
            if (acr.getDesiredCheckOut() != null) {
                ps.setTime(4, Time.valueOf(acr.getDesiredCheckOut()));
            } else {
                ps.setNull(4, Types.TIME);
            }
            ps.setString(5, acr.getReason());
            ps.executeUpdate();
        }
    }

    public AttendanceChangeRequest getByRequestId(int requestId) {
        String sql = "SELECT * FROM attendance_change_requests WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AttendanceChangeRequest acr = new AttendanceChangeRequest();
                    acr.setId(rs.getInt("id"));
                    acr.setRequestId(rs.getInt("request_id"));
                    acr.setWorkDate(rs.getDate("work_date").toLocalDate());
                    Time checkIn = rs.getTime("desired_check_in");
                    if (checkIn != null) acr.setDesiredCheckIn(checkIn.toLocalTime());
                    Time checkOut = rs.getTime("desired_check_out");
                    if (checkOut != null) acr.setDesiredCheckOut(checkOut.toLocalTime());
                    acr.setReason(rs.getString("reason"));
                    acr.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    acr.setApplied(rs.getBoolean("is_applied"));
                    return acr;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean markApplied(int requestId) {
        String sql = "UPDATE attendance_change_requests SET is_applied = TRUE WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countCurrentMonthByUser(int userId, int month, int year) {
        String sql = "SELECT COUNT(*) FROM attendance_change_requests acr " +
                "JOIN requests r ON acr.request_id = r.id " +
                "WHERE r.user_id = ? AND MONTH(acr.work_date) = ? AND YEAR(acr.work_date) = ? " +
                "AND r.status IN ('PENDING', 'APPROVED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean existsRequestForDate(int userId, LocalDate workDate) {
        String sql = "SELECT COUNT(*) FROM attendance_change_requests acr " +
                "JOIN requests r ON acr.request_id = r.id " +
                "WHERE r.user_id = ? AND acr.work_date = ? AND r.status IN ('PENDING', 'APPROVED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(workDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}