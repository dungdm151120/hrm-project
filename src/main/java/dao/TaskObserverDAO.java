package dao;

import model.TaskObserver;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskObserverDAO {

    public List<TaskObserver> getObserversByTaskId(long taskId) {
        List<TaskObserver> observers = new ArrayList<>();
        String sql = """
                SELECT tob.*, u.full_name AS user_name
                FROM task_observers tob
                JOIN users u ON u.id = tob.user_id
                WHERE tob.task_id = ?
                ORDER BY u.full_name
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    observers.add(mapObserver(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return observers;
    }

    public void insertObservers(long taskId, List<Long> userIds) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            insertObservers(conn, taskId, userIds);
        }
    }

    public void insertObservers(Connection conn, long taskId, List<Long> userIds) throws SQLException {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO task_observers (task_id, user_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Long userId : userIds) {
                if (userId == null) {
                    continue;
                }
                ps.setLong(1, taskId);
                ps.setLong(2, userId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void deleteObserversByTaskId(long taskId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            deleteObserversByTaskId(conn, taskId);
        }
    }

    public void deleteObserversByTaskId(Connection conn, long taskId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM task_observers WHERE task_id = ?")) {
            ps.setLong(1, taskId);
            ps.executeUpdate();
        }
    }

    public boolean existsByTaskIdAndUserId(long taskId, long userId) {
        String sql = "SELECT 1 FROM task_observers WHERE task_id = ? AND user_id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private TaskObserver mapObserver(ResultSet rs) throws SQLException {
        TaskObserver observer = new TaskObserver();
        observer.setId(rs.getLong("id"));
        observer.setTaskId(rs.getLong("task_id"));
        observer.setUserId(rs.getLong("user_id"));
        observer.setUserName(rs.getString("user_name"));
        return observer;
    }
}
