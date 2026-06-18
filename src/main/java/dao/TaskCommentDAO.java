package dao;

import model.TaskComment;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskCommentDAO {

    public List<TaskComment> getCommentsByTaskId(long taskId) {
        List<TaskComment> comments = new ArrayList<>();
        String sql = """
                SELECT tc.*, u.full_name AS user_name, p.name AS user_position_name
                FROM task_comments tc
                JOIN users u ON u.id = tc.user_id
                LEFT JOIN positions p ON p.id = u.position_id
                WHERE tc.task_id = ?
                ORDER BY tc.created_at ASC, tc.id ASC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapComment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public void insertComment(long taskId, long userId, String content) throws SQLException {
        String sql = "INSERT INTO task_comments (task_id, user_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ps.setLong(2, userId);
            ps.setString(3, content);
            ps.executeUpdate();
        }
    }

    public void deleteCommentsByTaskId(long taskId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            deleteCommentsByTaskId(conn, taskId);
        }
    }

    public void deleteCommentsByTaskId(Connection conn, long taskId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM task_comments WHERE task_id = ?")) {
            ps.setLong(1, taskId);
            ps.executeUpdate();
        }
    }

    private TaskComment mapComment(ResultSet rs) throws SQLException {
        TaskComment comment = new TaskComment();
        comment.setId(rs.getLong("id"));
        comment.setTaskId(rs.getLong("task_id"));
        comment.setUserId(rs.getLong("user_id"));
        comment.setUserName(rs.getString("user_name"));
        comment.setUserPositionName(rs.getString("user_position_name"));
        comment.setContent(rs.getString("content"));
        comment.setCreatedAt(rs.getTimestamp("created_at"));
        return comment;
    }
}
