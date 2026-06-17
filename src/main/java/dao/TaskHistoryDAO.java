package dao;

import model.TaskHistory;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskHistoryDAO {

    public List<TaskHistory> getHistoriesByTaskId(long taskId) {
        List<TaskHistory> histories = new ArrayList<>();
        String sql = """
                SELECT th.*, u.full_name AS user_name
                FROM task_histories th
                JOIN users u ON u.id = th.user_id
                WHERE th.task_id = ?
                ORDER BY th.created_at DESC, th.id DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapHistory(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return histories;
    }

    public void insertHistory(long taskId, long userId, String actionType, String content) throws SQLException {
        String sql = "INSERT INTO task_histories (task_id, user_id, action_type, content) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ps.setLong(2, userId);
            ps.setString(3, actionType);
            ps.setString(4, content);
            ps.executeUpdate();
        }
    }

    public void deleteHistoriesByTaskId(long taskId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            deleteHistoriesByTaskId(conn, taskId);
        }
    }

    public void deleteHistoriesByTaskId(Connection conn, long taskId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM task_histories WHERE task_id = ?")) {
            ps.setLong(1, taskId);
            ps.executeUpdate();
        }
    }

    private TaskHistory mapHistory(ResultSet rs) throws SQLException {
        TaskHistory history = new TaskHistory();
        history.setId(rs.getLong("id"));
        history.setTaskId(rs.getLong("task_id"));
        history.setUserId(rs.getLong("user_id"));
        history.setUserName(rs.getString("user_name"));
        history.setActionType(rs.getString("action_type"));
        history.setContent(rs.getString("content"));
        history.setCreatedAt(rs.getTimestamp("created_at"));
        return history;
    }
}
