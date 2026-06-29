package dao;

import model.SickLeaveRequest;
import model.SickLeaveDate;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SickLeaveRequestDAO {

    public int createSickLeaveRequest(int requestId, String filePath, List<LocalDate> dates) throws SQLException {
        String sqlSick = "INSERT INTO sick_leave_requests (request_id, file_path) VALUES (?, ?)";
        String sqlDate = "INSERT INTO sick_leave_dates (sick_leave_request_id, leave_date) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int sickRequestId;
            try (PreparedStatement ps = conn.prepareStatement(sqlSick, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, requestId);
                ps.setString(2, filePath);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    sickRequestId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to create sick leave request");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlDate)) {
                for (LocalDate date : dates) {
                    ps.setInt(1, sickRequestId);
                    ps.setDate(2, Date.valueOf(date));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return sickRequestId;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    public SickLeaveRequest getByRequestId(int requestId) {
        String sql = "SELECT * FROM sick_leave_requests WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SickLeaveRequest sr = new SickLeaveRequest();
                sr.setId(rs.getInt("id"));
                sr.setRequestId(rs.getInt("request_id"));
                sr.setFilePath(rs.getString("file_path"));
                sr.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return sr;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<LocalDate> getDatesBySickRequestId(int sickRequestId) {
        List<LocalDate> dates = new ArrayList<>();
        String sql = "SELECT leave_date FROM sick_leave_dates WHERE sick_leave_request_id = ? ORDER BY leave_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sickRequestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dates.add(rs.getDate("leave_date").toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }

    public int countSickLeaveDaysUsed(int userId, int year) {
        String sql = "SELECT COALESCE(SUM(CASE WHEN ar.status = 'SICK_LEAVE' THEN 1 ELSE 0 END), 0) " +
                "FROM attendance_records ar WHERE ar.user_id = ? AND YEAR(ar.work_date) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countPendingOrApprovedFuture(int userId, int year) {
        String sql = "SELECT COUNT(*) FROM sick_leave_dates sd " +
                "JOIN sick_leave_requests sr ON sd.sick_leave_request_id = sr.id " +
                "JOIN requests r ON sr.request_id = r.id " +
                "WHERE r.user_id = ? AND r.status IN ('PENDING', 'APPROVED') AND YEAR(sd.leave_date) = ? " +
                "AND sd.leave_date > CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean hasDuplicateRequest(int userId, List<LocalDate> dates) {
        String sql = "SELECT COUNT(*) FROM sick_leave_dates sd " +
                "JOIN sick_leave_requests sr ON sd.sick_leave_request_id = sr.id " +
                "JOIN requests r ON sr.request_id = r.id " +
                "WHERE r.user_id = ? AND sd.leave_date IN (" +
                String.join(",", dates.stream().map(d -> "?").toArray(String[]::new)) + ") " +
                "AND r.status IN ('PENDING', 'APPROVED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            for (int i = 0; i < dates.size(); i++) {
                ps.setDate(i + 2, Date.valueOf(dates.get(i)));
            }
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}