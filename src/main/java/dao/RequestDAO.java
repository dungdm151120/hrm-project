package dao;

import model.Request;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    // Tạo request
    public void createRequest(Request req) throws SQLException {
        String sql = "INSERT INTO requests (user_id, department_id, type, reason, approver_id, observer_id, created_at, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW(), 'PENDING')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, req.getUserId());

            Integer deptId = req.getDepartmentId();
            if (deptId != null) {
                ps.setInt(2, deptId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            ps.setString(3, req.getType());
            ps.setString(4, req.getReason());
            ps.setInt(5, req.getApproverId());

            // observerId co the null
            if (req.getObserverId() > 0) {
                ps.setInt(6, req.getObserverId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();
        }
    }

    // View my request
    public List<Request> getRequestsByUserId(int userId) {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name as proposer_name, d.name as department_name, " +
                "a.full_name as approver_name, o.full_name as observer_name " +
                "FROM requests r " +
                "LEFT JOIN users u ON r.user_id = u.id " +
                "LEFT JOIN departments d ON r.department_id = d.id " +
                "LEFT JOIN users a ON r.approver_id = a.id " +
                "LEFT JOIN users o ON r.observer_id = o.id " +
                "WHERE r.user_id = ? ORDER BY r.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRequest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy Request theo ID
    public Request getRequestById(int id) {
        String sql = "SELECT r.*, u.full_name as proposer_name, d.name as department_name, " +
                "a.full_name as approver_name, o.full_name as observer_name " +
                "FROM requests r " +
                "LEFT JOIN users u ON r.user_id = u.id " +
                "LEFT JOIN departments d ON r.department_id = d.id " +
                "LEFT JOIN users a ON r.approver_id = a.id " +
                "LEFT JOIN users o ON r.observer_id = o.id " +
                "WHERE r.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRequest(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // View all request
    public List<Request> getAllRequests() {
        List<Request> request = new ArrayList<>();

        String sql = """
                SELECT r.*, u.full_name as proposer_name, d.name as department_name,
                a.full_name as approver_name, o.full_name as observer_name
                FROM requests r
                LEFT JOIN users u ON r.user_id = u.id
                LEFT JOIN departments d ON r.department_id = d.id
                LEFT JOIN users a ON r.approver_id = a.id
                LEFT JOIN users o ON r.observer_id = o.id
                WHERE status != 'CANCELLED'
                ORDER BY r.created_at DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                request.add(mapRequest(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return request;
    }

    public boolean updateRequestStatus(int requestId, String status, String comment) throws SQLException {
        String sql = "UPDATE requests SET status = ?, approver_comment = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);

            if (comment != null) {
                ps.setString(2, comment);
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }

            ps.setInt(3, requestId);

            return ps.executeUpdate() > 0;
        }
    }

    // Map
    private Request mapRequest(ResultSet rs) throws SQLException {
        Request req = new Request();

        req.setId(rs.getInt("id"));
        req.setUserId(rs.getInt("user_id"));
        req.setProposerName(rs.getString("proposer_name"));

        int departmentId = rs.getInt("department_id");
        if (!rs.wasNull()) {
            req.setDepartmentId(departmentId);
        }

        String departmentName = rs.getString("department_name");
        if (!rs.wasNull()) {
            req.setDepartmentName(departmentName);
        }

        req.setType(rs.getString("type"));
        req.setApproverId(rs.getInt("approver_id"));
        req.setApproverName(rs.getString("approver_name"));

        int observerId = rs.getInt("observer_id");
        if (!rs.wasNull()) {
            req.setObserverId(observerId);
        }

        String observerName = rs.getString("observer_name");
        if (!rs.wasNull()) {
            req.setObserverName(observerName);
        }


        req.setReason(rs.getString("reason"));
        req.setStatus(rs.getString("status"));
        req.setApproverComment(rs.getString("approver_comment"));
        req.setCreatedAt(rs.getTimestamp("created_at"));

        return req;
    }
}