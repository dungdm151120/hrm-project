package dao;

import model.Request;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    // Tạo request
    public void createRequest(Request req, List<Integer> observerIds) throws SQLException {
        String sqlRequest = "INSERT INTO requests (user_id, department_id, type, reason, approver_id, created_at, status) VALUES (?, ?, ?, ?, ?, NOW(), 'PENDING')";
        String sqlObserver = "INSERT INTO request_observers (request_id, observer_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int requestId = -1;

            // 1. Insert Request và lấy Generated ID
            try (PreparedStatement ps = conn.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, req.getUserId());
                if (req.getDepartmentId() != null) ps.setInt(2, req.getDepartmentId());
                else ps.setNull(2, java.sql.Types.INTEGER);
                ps.setString(3, req.getType());
                ps.setString(4, req.getReason());
                ps.setInt(5, req.getApproverId());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        requestId = rs.getInt(1);
                    }
                }
            }

            // 2. Insert Observers (nếu có)
            if (requestId != -1 && observerIds != null && !observerIds.isEmpty()) {
                try (PreparedStatement psObs = conn.prepareStatement(sqlObserver)) {
                    for (Integer observerId : observerIds) {
                        psObs.setInt(1, requestId);
                        psObs.setInt(2, observerId);
                        psObs.addBatch();
                    }
                    psObs.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    // View my request
    public List<Request> getRequestByUserId(int userId, String status, String type, String sort, int offset, int limit) {
        List<Request> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.full_name as proposer_name, d.name as department_name, " +
                        "a.full_name as approver_name, o.full_name as observer_name " +
                        "FROM requests r " +
                        "LEFT JOIN users u ON r.user_id = u.id " +
                        "LEFT JOIN departments d ON r.department_id = d.id " +
                        "LEFT JOIN users a ON r.approver_id = a.id " +
                        "LEFT JOIN users o ON r.observer_id = o.id " +
                        "WHERE r.user_id = ?"
        );

        if (status != null && !status.isEmpty()) sql.append(" AND r.status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND r.type = ?");

        sql.append(" ORDER BY r.created_at ").append("oldest".equals(sort) ? "ASC" : "DESC");
        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            ps.setInt(index++, userId);
            if (status != null && !status.isEmpty()) ps.setString(index++, status);
            if (type != null && !type.isEmpty()) ps.setString(index++, type);
            ps.setInt(index++, limit);
            ps.setInt(index++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countRequestByUserId(int userId, String status, String type) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM requests WHERE user_id = ?");
        if (status != null && !status.isEmpty()) sql.append(" AND status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND type = ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            ps.setInt(index++, userId);
            if (status != null && !status.isEmpty()) ps.setString(index++, status);
            if (type != null && !type.isEmpty()) ps.setString(index++, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy Request theo ID
    public Request getRequestById(int id) {
        // Loại bỏ các phần liên quan đến observer cũ
        String sql = "SELECT r.*, u.full_name as proposer_name, d.name as department_name, " +
                "a.full_name as approver_name " +
                "FROM requests r " +
                "LEFT JOIN users u ON r.user_id = u.id " +
                "LEFT JOIN departments d ON r.department_id = d.id " +
                "LEFT JOIN users a ON r.approver_id = a.id " +
                "WHERE r.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Request req = mapRequest(rs);
                    List<User> obs = getObserversByRequestId(id);
                    req.setObserver(obs);

                    // DEBUG: Thêm dòng này để kiểm tra
                    System.out.println("DEBUG - ID Request: " + id + " | Số lượng observer nạp được: " + obs.size());

                    return req;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // View all request
    public List<Request> getAllRequest(String status, String type, String sort, int offset, int limit) {
        List<Request> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.full_name as proposer_name, d.name as department_name, " +
                        "a.full_name as approver_name, o.full_name as observer_name " +
                        "FROM requests r " +
                        "LEFT JOIN users u ON r.user_id = u.id " +
                        "LEFT JOIN departments d ON r.department_id = d.id " +
                        "LEFT JOIN users a ON r.approver_id = a.id " +
                        "LEFT JOIN users o ON r.observer_id = o.id " +
                        "WHERE r.status != 'CANCELLED'"
        );

        if (status != null && !status.isEmpty()) sql.append(" AND r.status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND r.type = ?");

        sql.append(" ORDER BY r.created_at ").append("oldest".equals(sort) ? "ASC" : "DESC");
        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (status != null && !status.isEmpty()) ps.setString(index++, status);
            if (type != null && !type.isEmpty()) ps.setString(index++, type);
            ps.setInt(index++, limit);
            ps.setInt(index++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAllRequest(String status, String type) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM requests WHERE status != 'CANCELLED'");
        if (status != null && !status.isEmpty()) sql.append(" AND status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND type = ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (status != null && !status.isEmpty()) ps.setString(index++, status);
            if (type != null && !type.isEmpty()) ps.setString(index++, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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

    public List<Request> getRequestByDepartment(int departmentId, String status, String type, String sort, int offset, int limit) {
        List<Request> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.full_name as proposer_name, d.name as department_name, " +
                        "a.full_name as approver_name, o.full_name as observer_name " +
                        "FROM requests r " +
                        "LEFT JOIN users u ON r.user_id = u.id " +
                        "LEFT JOIN departments d ON r.department_id = d.id " +
                        "LEFT JOIN users a ON r.approver_id = a.id " +
                        "LEFT JOIN users o ON r.observer_id = o.id " +
                        "WHERE r.department_id = ?"
        );

        // Filter động
        if (status != null && !status.isEmpty()) {
            sql.append(" AND r.status = ?");
        }
        if (type != null && !type.isEmpty()) {
            sql.append(" AND r.type = ?");
        }

        // Sort động (Mặc định là newest)
        if ("oldest".equals(sort)) {
            sql.append(" ORDER BY r.created_at ASC");
        } else {
            sql.append(" ORDER BY r.created_at DESC");
        }

        // Paging
        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            ps.setInt(index++, departmentId);

            if (status != null && !status.isEmpty()) ps.setString(index++, status);
            if (type != null && !type.isEmpty()) ps.setString(index++, type);

            ps.setInt(index++, limit);
            ps.setInt(index++, offset);

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

    public int countRequestByDepartment(int departmentId, String status, String type) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM requests WHERE department_id = ?");

        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
        }
        if (type != null && !type.isEmpty()) {
            sql.append(" AND type = ?");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            ps.setInt(index++, departmentId);

            if (status != null && !status.isEmpty()) ps.setString(index++, status);
            if (type != null && !type.isEmpty()) ps.setString(index++, type);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<User> getObserversByRequestId(int requestId) {
        List<User> observers = new ArrayList<>();
        // JOIN trực tiếp từ users tới request_observers
        String sql = "SELECT u.id, u.full_name " +
                "FROM users u " +
                "JOIN request_observers ro ON u.id = ro.observer_id " +
                "WHERE ro.request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFullName(rs.getString("full_name"));
                    observers.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return observers;
    }

    public List<Request> getObservedRequests(int userId, String status, String type, String sort, int offset, int limit) {
        List<Request> list = new ArrayList<>();
        // SỬA: ro.user_id thành ro.observer_id
        StringBuilder sql = new StringBuilder("""
                SELECT r.*, u.full_name as proposer_name 
                FROM requests r
                JOIN request_observers ro ON r.id = ro.request_id
                JOIN users u ON r.user_id = u.id
                WHERE ro.observer_id = ?
            """);

        if (status != null && !status.isEmpty()) sql.append(" AND r.status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND r.type = ?");

        // Sửa lỗi: Đảm bảo khoảng trắng trước ORDER BY
        sql.append(" ORDER BY r.created_at ").append("oldest".equals(sort) ? "ASC" : "DESC");
        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, userId);
            if (status != null && !status.isEmpty()) ps.setString(paramIndex++, status);
            if (type != null && !type.isEmpty()) ps.setString(paramIndex++, type);
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request r = new Request();
                    r.setId(rs.getInt("id"));
                    r.setType(rs.getString("type"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setProposerName(rs.getString("proposer_name"));
                    list.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countObservedRequests(int userId, String status, String type) {
        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(r.id) 
        FROM requests r
        JOIN request_observers ro ON r.id = ro.request_id
        WHERE ro.observer_id = ?
    """);

        if (status != null && !status.isEmpty()) {
            sql.append(" AND r.status = ?");
        }
        if (type != null && !type.isEmpty()) {
            sql.append(" AND r.type = ?");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            ps.setInt(index++, userId); // userId được truyền vào chính là observer_id

            if (status != null && !status.isEmpty()) {
                ps.setString(index++, status);
            }
            if (type != null && !type.isEmpty()) {
                ps.setString(index++, type);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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

        List<User> obsList = getObserversByRequestId(req.getId());
        req.setObserver(obsList != null ? obsList : new ArrayList<>());


        req.setReason(rs.getString("reason"));
        req.setStatus(rs.getString("status"));
        req.setApproverComment(rs.getString("approver_comment"));
        req.setCreatedAt(rs.getTimestamp("created_at"));

        return req;
    }
}