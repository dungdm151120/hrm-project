package dao;

import model.Request;
import model.RequestNotification;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

            if (requestId != -1) {
                createNotifications(conn, requestId, notificationRecipients(req, observerIds), req.getUserId(), "NEW_REQUEST");
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

            if (requestId != -1) {
                createNotifications(conn, requestId, notificationRecipients(req, observerIds), req.getUserId(), "NEW_REQUEST");
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
                        "LEFT JOIN users h ON r.handler_id = h.id " +
                        "WHERE r.user_id = ?"
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
                    r.setDepartmentId(rs.getInt("department_id"));
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

        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT r.*, u.full_name as proposer_name, h.full_name as handler_name " +
                        "FROM requests r " +
                        "LEFT JOIN request_observers ro ON r.id = ro.request_id " +
                        "JOIN users u ON r.user_id = u.id " +
                        "LEFT JOIN users h ON r.handler_id = h.id " +
                        "WHERE ro.observer_id = ? AND r.status != 'CANCELLED' "
        );

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

    public boolean updateRequestStatusOnly(int requestId, String status) {
        String sql = "UPDATE requests SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRequestStatusAndHandler(int requestId, String status, String comment, int handlerId) {
        String sql = "UPDATE requests SET status = ?, processed_at = NOW(), approver_comment = ?, handler_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, comment);
            ps.setInt(3, handlerId);
            ps.setInt(4, requestId);
            return ps.executeUpdate() > 0;
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
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT r.id) FROM requests r " +
                        "LEFT JOIN request_observers ro ON r.id = ro.request_id " +
                        "WHERE ro.observer_id = ? AND r.status != 'CANCELLED'"
        );
        if (status != null && !status.isEmpty()) sql.append(" AND r.status = ?");
        if (type != null && !type.isEmpty()) sql.append(" AND r.type = ?");
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

    // Lấy danh sách đơn đang chờ duyệt (Pending Approvals)
    public List<Request> getPendingApprovals(int approverId, String type, String sort, int offset, int limit) {
        List<Request> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.full_name as proposer_name, h.full_name as handler_name FROM requests r " +
                        "LEFT JOIN users u ON r.user_id = u.id " +
                        "LEFT JOIN users h ON r.handler_id = h.id " +
                        "WHERE r.approver_id = ? AND r.status = 'PENDING' "
        );

        if (type != null && !type.isEmpty()) sql.append(" AND r.type = ?");
        sql.append(" ORDER BY r.created_at ").append("oldest".equals(sort) ? "ASC" : "DESC").append(" LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, approverId);
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

    public int countPendingApprovals(int approverId, String type) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM requests WHERE approver_id = ? AND status = 'PENDING'");

        if (type != null && !type.isEmpty()) sql.append(" AND type = ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            ps.setInt(index++, approverId);
            if (type != null && !type.isEmpty()) ps.setString(index++, type);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countUnreadRequestNotifications(int userId) {
        String sql = "SELECT COUNT(*) FROM request_notifications WHERE user_id = ? AND read_at IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            if (!isMissingNotificationTable(e)) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public List<RequestNotification> getLatestUnreadRequestNotifications(int userId, int limit) {
        List<RequestNotification> notifications = new ArrayList<>();
        String sql = """
                SELECT rn.*, u.full_name AS actor_name
                FROM request_notifications rn
                LEFT JOIN users u ON rn.actor_user_id = u.id
                WHERE rn.user_id = ?
                  AND rn.read_at IS NULL
                ORDER BY rn.created_at DESC, rn.id DESC
                LIMIT ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapNotification(rs));
                }
            }
        } catch (SQLException e) {
            if (!isMissingNotificationTable(e)) {
                e.printStackTrace();
            }
        }

        return notifications;
    }

    public void markRequestNotificationsRead(int requestId, int userId) {
        String sql = """
                UPDATE request_notifications
                SET read_at = NOW()
                WHERE request_id = ?
                  AND user_id = ?
                  AND read_at IS NULL
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (!isMissingNotificationTable(e)) {
                e.printStackTrace();
            }
        }
    }

    public void notifyRequestChanged(int requestId, int actorUserId, String eventType) {
        Request request = getRequestById(requestId);
        if (request == null) {
            return;
        }

        createNotifications(requestId, notificationRecipients(request, observerIds(request.getObserver())), actorUserId, eventType);
    }

    private Set<Integer> notificationRecipients(Request req, Collection<Integer> observerIds) {
        Set<Integer> recipients = new LinkedHashSet<>();
        if (req.getUserId() > 0) {
            recipients.add(req.getUserId());
        }
        if (req.getApproverId() > 0) {
            recipients.add(req.getApproverId());
        }
        if (req.getHandlerId() > 0) {
            recipients.add(req.getHandlerId());
        }
        if (observerIds != null) {
            for (Integer observerId : observerIds) {
                if (observerId != null && observerId > 0) {
                    recipients.add(observerId);
                }
            }
        }
        return recipients;
    }

    private List<Integer> observerIds(List<User> observers) {
        List<Integer> ids = new ArrayList<>();
        if (observers != null) {
            for (User observer : observers) {
                if (observer != null && observer.getId() > 0) {
                    ids.add(observer.getId());
                }
            }
        }
        return ids;
    }

    private void createNotifications(int requestId, Collection<Integer> recipientIds, int actorUserId, String eventType) {
        try (Connection conn = DBConnection.getConnection()) {
            createNotifications(conn, requestId, recipientIds, actorUserId, eventType);
        } catch (SQLException e) {
            if (!isMissingNotificationTable(e)) {
                e.printStackTrace();
            }
        }
    }

    private void createNotifications(Connection conn, int requestId, Collection<Integer> recipientIds,
                                     int actorUserId, String eventType) {
        if (recipientIds == null || recipientIds.isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO request_notifications (request_id, user_id, actor_user_id, event_type, message, created_at)
                VALUES (?, ?, ?, ?, ?, NOW())
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String message = notificationMessage(eventType, requestId);
            for (Integer recipientId : recipientIds) {
                if (recipientId == null || recipientId <= 0 || recipientId == actorUserId) {
                    continue;
                }
                ps.setInt(1, requestId);
                ps.setInt(2, recipientId);
                if (actorUserId > 0) {
                    ps.setInt(3, actorUserId);
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setString(4, eventType);
                ps.setString(5, message);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            if (!isMissingNotificationTable(e)) {
                e.printStackTrace();
            }
        }
    }

    private String notificationMessage(String eventType, int requestId) {
        if ("NEW_REQUEST".equals(eventType)) {
            return "New request #" + requestId + " needs your attention.";
        }
        if ("APPROVED".equals(eventType)) {
            return "Request #" + requestId + " was approved.";
        }
        if ("REJECTED".equals(eventType)) {
            return "Request #" + requestId + " was rejected.";
        }
        if ("CANCELLED".equals(eventType)) {
            return "Request #" + requestId + " was cancelled.";
        }
        if ("CONFIRMED".equals(eventType)) {
            return "Request #" + requestId + " was confirmed.";
        }
        return "Request #" + requestId + " was updated.";
    }

    private RequestNotification mapNotification(ResultSet rs) throws SQLException {
        RequestNotification notification = new RequestNotification();
        notification.setId(rs.getInt("id"));
        notification.setRequestId(rs.getInt("request_id"));
        notification.setUserId(rs.getInt("user_id"));
        int actorUserId = rs.getInt("actor_user_id");
        notification.setActorUserId(rs.wasNull() ? null : actorUserId);
        notification.setActorName(rs.getString("actor_name"));
        notification.setEventType(rs.getString("event_type"));
        notification.setMessage(rs.getString("message"));
        notification.setReadAt(rs.getTimestamp("read_at"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        return notification;
    }

    private boolean isMissingNotificationTable(SQLException e) {
        return e.getErrorCode() == 1146 || "42S02".equals(e.getSQLState());
    }
}
