package dao;

import model.PasswordResetRequest;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PasswordResetRequestDAO {
    public boolean createRequest(int userId, String email, String reason) {
        String sql = """
                INSERT INTO password_reset_requests(user_id, email, reason, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, email);
            ps.setString(3, reason);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasPendingRequest(int userId) {
        String sql = """
                SELECT id
                FROM password_reset_requests
                WHERE user_id = ?
                  AND status = 'PENDING'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<PasswordResetRequest> findAll() {
        List<PasswordResetRequest> requests = new ArrayList<>();
        String sql = """
                SELECT prr.*, u.full_name
                FROM password_reset_requests prr
                JOIN users u ON prr.user_id = u.id
                ORDER BY prr.created_at DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    public PasswordResetRequest findById(int id) {
        String sql = """
                SELECT prr.*, u.full_name
                FROM password_reset_requests prr
                JOIN users u ON prr.user_id = u.id
                WHERE prr.id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRequest(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean markDone(int requestId, String generatedPassword, int handledBy, String adminNote) {
        String sql = """
                UPDATE password_reset_requests
                SET status = 'DONE',
                    generated_password = ?,
                    admin_note = ?,
                    handled_at = NOW(),
                    handled_by = ?
                WHERE id = ?
                  AND status = 'PENDING'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, generatedPassword);
            ps.setString(2, adminNote);
            ps.setInt(3, handledBy);
            ps.setInt(4, requestId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean reject(int requestId, int handledBy, String adminNote) {
        String sql = """
                UPDATE password_reset_requests
                SET status = 'REJECTED',
                    admin_note = ?,
                    handled_at = NOW(),
                    handled_by = ?
                WHERE id = ?
                  AND status = 'PENDING'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, adminNote);
            ps.setInt(2, handledBy);
            ps.setInt(3, requestId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private PasswordResetRequest mapResultSetToRequest(ResultSet rs) throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();

        request.setId(rs.getInt("id"));
        request.setUserId(rs.getInt("user_id"));
        request.setFullName(rs.getString("full_name"));
        request.setEmail(rs.getString("email"));
        request.setReason(rs.getString("reason"));
        request.setStatus(rs.getString("status"));
        request.setGeneratedPassword(rs.getString("generated_password"));
        request.setAdminNote(rs.getString("admin_note"));
        request.setCreatedAt(getNullableLocalDateTime(rs, "created_at"));
        request.setHandledAt(getNullableLocalDateTime(rs, "handled_at"));

        int handledBy = rs.getInt("handled_by");
        if (!rs.wasNull()) {
            request.setHandledBy(handledBy);
        }

        return request;
    }

    private LocalDateTime getNullableLocalDateTime(ResultSet rs, String columnName) throws Exception {
        Timestamp timestamp = rs.getTimestamp(columnName);

        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime();
    }
}
