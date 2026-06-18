package dao;

import model.Request;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    public void createRequest(Request req, List<Integer> observerIds) throws SQLException {
        String sqlRequest = "INSERT INTO requests (user_id, department_id, type, reason, approver_id, created_at, status, handler_id) VALUES (?, ?, ?, ?, ?, NOW(), 'PENDING', ?)";
        String sqlObserver = "INSERT INTO request_observers (request_id, observer_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int requestId = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, req.getUserId());

                if (req.getDepartmentId() != null) {
                    ps.setInt(2, req.getDepartmentId());
                } else {
                    ps.setNull(2, java.sql.Types.INTEGER);
                }

                ps.setString(3, req.getType());
                ps.setString(4, req.getReason());
                ps.setInt(5, req.getApproverId());

                if (req.getHandlerId() > 0) {
                    ps.setInt(6, req.getHandlerId());
                } else {
                    ps.setNull(6, java.sql.Types.INTEGER);
                }

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        requestId = rs.getInt(1);
                    }
                }
            }

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
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    public int createRequestAndGetId(Request req, List<Integer> observerIds) throws SQLException {
        String sqlRequest = "INSERT INTO requests (user_id, department_id, type, reason, approver_id, created_at, status, handler_id) VALUES (?, ?, ?, ?, ?, NOW(), 'PENDING', ?)";
        String sqlObserver = "INSERT INTO request_observers (request_id, observer_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int requestId = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, req.getUserId());

                if (req.getDepartmentId() != null) {
                    ps.setInt(2, req.getDepartmentId());
                } else {
                    ps.setNull(2, java.sql.Types.INTEGER);
                }

                ps.setString(3, req.getType());
                ps.setString(4, req.getReason());
                ps.setInt(5, req.getApproverId());

                if (req.getHandlerId() > 0) {
                    ps.setInt(6, req.getHandlerId());
                } else {
                    ps.setNull(6, java.sql.Types.INTEGER);
                }

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        requestId = rs.getInt(1);
                    }
                }
            }

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
            return requestId;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    public List<Request> getRequestByUserId(int userId, String status, String type, String sort, int offset, int limit) {
        List<Request> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.full_name as proposer_name, h.full_name as handler_name FROM requests r " +
                        "LEFT JOIN users u ON r.user_id = u.id " +
                        "LEFT JOIN users h ON r.handler_id = h.id WHERE r.user_id = ?"
        );

        if (status != null && !status.isEmpty()) {
            sql.append(" AND r.status = ?");
        }
        if (type != null && !type.isEmpty()) {
            sql.append(" AND r.type = ?");
        }

        sql.append(" ORDER BY r.created_at ").append("oldest".equals(sort) ? "ASC" : "DESC");

        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            ps.setInt(index++, userId);

            // Gán tham số động theo đúng thứ tự xuất hiện trong SQL
            if (status != null && !status.isEmpty()) {
                ps.setString(index++, status);
            }
            if (type != null && !type.isEmpty()) {
                ps.setString(index++, type);
            }

            // Gán tham số phân trang
            ps.setInt(index++, limit);
            ps.setInt(index++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request r = new Request();
                    r.setId(rs.getInt("id"));
                    r.setType(rs.getString("type"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setProposerName(rs.getString("proposer_name"));
                    r.setHandlerName(rs.getString("handler_name"));
                    r.setProcessedAt(rs.getTimestamp("processed_at"));

                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Request getRequestById(int id) {
        String sql = "SELECT r.*, " +
                "u.full_name as proposer_name, " +
                "d.name as department_name, " +
                "a.full_name as approver_name, " +
                "h.full_name as handler_name " +
                "FROM requests r " +
                "LEFT JOIN users u ON r.user_id = u.id " +
                "LEFT JOIN departments d ON r.department_id = d.id " +
                "LEFT JOIN users a ON r.approver_id = a.id " +
                "LEFT JOIN users h ON r.handler_id = h.id " +
                "WHERE r.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Request r = new Request();
                    r.setId(rs.getInt("id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setType(rs.getString("type"));
                    r.setStatus(rs.getString("status"));
                    r.setReason(rs.getString("reason"));
                    r.setProposerName(rs.getString("proposer_name"));
                    r.setDepartmentName(rs.getString("department_name"));
                    r.setApproverId(rs.getInt("approver_id"));
                    r.setApproverName(rs.getString("approver_name"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setHandlerId(rs.getInt("handler_id"));
                    r.setHandlerName(rs.getString("handler_name"));
                    r.setObserver(getObserversByRequestId(id));
                    r.setProcessedAt(rs.getTimestamp("processed_at"));
                    r.setApproverComment(rs.getString("approver_comment"));

                    return r;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Request> getObservedRequests(int userId, String status, String type, String sort, int offset, int limit) {
        List<Request> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT DISTINCT r.*, 
               u.full_name as proposer_name, 
               h.full_name as handler_name 
        FROM requests r
        LEFT JOIN request_observers ro ON r.id = ro.request_id
        JOIN users u ON r.user_id = u.id
        LEFT JOIN users h ON r.handler_id = h.id
        WHERE ro.observer_id = ?
    """);

        if (status != null && !status.isEmpty()) sql.append(" AND r.status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND r.type = ?");

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
                    r.setUserId(rs.getInt("user_id"));
                    r.setType(rs.getString("type"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setProposerName(rs.getString("proposer_name"));
                    r.setHandlerId(rs.getInt("handler_id"));
                    r.setHandlerName(rs.getString("handler_name"));
                    r.setProcessedAt(rs.getTimestamp("processed_at"));

                    list.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getObserversByRequestId(int requestId) {
        List<User> list = new ArrayList<>();

        String sql = "SELECT u.id AS userId, u.full_name AS userFullName, p.name AS positionName " +
                "FROM request_observers ro " +
                "JOIN users u ON ro.observer_id = u.id " +
                "LEFT JOIN positions p ON u.position_id = p.id " +
                "WHERE ro.request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("userId"));
                    u.setFullName(rs.getString("userFullName"));
                    u.setPositionName(rs.getString("positionName"));

                    list.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateRequestStatus(int requestId, String status, String comment) {

        String sql = "UPDATE requests SET status = ?, processed_at = NOW(), approver_comment = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);

            ps.setString(2, comment);
            ps.setInt(3, requestId);

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countRequestByUserId(int userId, String status, String type) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM requests WHERE user_id = ?");
        if (status != null && !status.isEmpty()) sql.append(" AND status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND type = ?");
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
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

    public int countObservedRequests(int userId, String status, String type) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT r.id) FROM requests r LEFT JOIN request_observers ro ON r.id = ro.request_id WHERE ro.observer_id = ?");
        if (status != null && !status.isEmpty()) sql.append(" AND r.status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND r.type = ?");
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
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

    // Lấy danh sách các đơn thuộc một phòng ban (Department Requests)
    public List<Request> getRequestByDepartment(int departmentId, String status, String type, String sort, int offset, int limit) {
        List<Request> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.full_name as proposer_name, d.name as department_name, h.full_name as handler_name " +
                        "FROM requests r " +
                        "LEFT JOIN users u ON r.user_id = u.id " +
                        "LEFT JOIN departments d ON r.department_id = d.id " +
                        "LEFT JOIN users h ON r.handler_id = h.id " +
                        "WHERE r.department_id = ?"
        );

        if (status != null && !status.isEmpty()) sql.append(" AND r.status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND r.type = ?");

        sql.append(" ORDER BY r.created_at ").append("oldest".equals(sort) ? "ASC" : "DESC").append(" LIMIT ? OFFSET ?");

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
                    Request r = new Request();
                    r.setId(rs.getInt("id"));
                    r.setType(rs.getString("type"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setProposerName(rs.getString("proposer_name"));
                    r.setDepartmentName(rs.getString("department_name"));
                    r.setHandlerName(rs.getString("handler_name"));
                    r.setProcessedAt(rs.getTimestamp("processed_at"));

                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đếm số lượng đơn thuộc phòng ban
    public int countRequestByDepartment(int departmentId, String status, String type) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM requests WHERE department_id = ?");

        if (status != null && !status.isEmpty()) sql.append(" AND status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND type = ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            ps.setInt(index++, departmentId);
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

    public List<Request> getHandledRequests(int handlerId, String status, String type, String sort, int offset, int limit) {
        List<Request> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.full_name as proposer_name, h.full_name as handler_name FROM requests r " +
                        "JOIN users u ON r.user_id = u.id " +
                        "LEFT JOIN users h ON r.handler_id = h.id " +
                        "WHERE r.handler_id = ? AND r.status != 'CANCELLED' "
        );

        if (status != null && !status.isEmpty()) sql.append(" AND r.status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND r.type = ?");

        sql.append(" ORDER BY r.created_at ").append("oldest".equals(sort) ? "ASC" : "DESC").append(" LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, handlerId);
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
                    r.setHandlerName(rs.getString("handler_name"));
                    r.setProcessedAt(rs.getTimestamp("processed_at"));

                    list.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public int countHandledRequests(int handlerId, String status, String type) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM requests WHERE handler_id = ? AND status != 'CANCELLED'");

        if (status != null && !status.isEmpty()) sql.append(" AND status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND type = ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, handlerId);
            if (status != null && !status.isEmpty()) ps.setString(paramIndex++, status);
            if (type != null && !type.isEmpty()) ps.setString(paramIndex++, type);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy toàn bộ danh sách đơn (All Requests) cho Admin/HR Manager
    public List<Request> getAllRequest(String status, String type, String sort, int offset, int limit) {
        List<Request> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT r.*, " +
                        "u1.full_name AS proposerName, " +
                        "u2.full_name AS handlerName " +
                        "FROM requests r " +
                        "LEFT JOIN users u1 ON r.user_id = u1.id " +
                        "LEFT JOIN users u2 ON r.handler_id = u2.id " +
                        "WHERE 1=1 "
        );

        if (status != null && !status.isEmpty()) {
            sql.append(" AND r.status = ? ");
        }
        if (type != null && !type.isEmpty()) {
            sql.append(" AND r.type = ? ");
        }

        if ("oldest".equals(sort)) {
            sql.append(" ORDER BY r.created_at ASC ");
        } else {
            sql.append(" ORDER BY r.created_at DESC ");
        }

        sql.append(" LIMIT ? OFFSET ? ");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (type != null && !type.isEmpty()) {
                ps.setString(paramIndex++, type);
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request r = new Request();
                    r.setId(rs.getInt("id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setType(rs.getString("type"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setHandlerId(rs.getInt("handler_id"));
                    r.setProposerName(rs.getString("proposerName"));
                    r.setHandlerName(rs.getString("handlerName"));
                    r.setProcessedAt(rs.getTimestamp("processed_at"));

                    list.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đếm tổng số lượng đơn trong hệ thống
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
}