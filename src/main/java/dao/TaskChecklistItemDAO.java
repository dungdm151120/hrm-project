package dao;

import model.TaskChecklistItem;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TaskChecklistItemDAO {

    public List<TaskChecklistItem> getChecklistItemsByTaskId(long taskId) {
        List<TaskChecklistItem> items = new ArrayList<>();
        String sql = """
                SELECT ci.*, u.full_name AS assigned_to_name
                FROM task_checklist_items ci
                LEFT JOIN users u ON u.id = ci.assigned_to
                WHERE ci.task_id = ?
                ORDER BY ci.id
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapChecklistItem(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void insertChecklistItem(TaskChecklistItem item) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            insertChecklistItem(conn, item);
        }
    }

    public void insertChecklistItem(Connection conn, TaskChecklistItem item) throws SQLException {
        String sql = "INSERT INTO task_checklist_items (task_id, content, is_completed, assigned_to, completed_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, item.getTaskId());
            ps.setString(2, item.getContent());
            ps.setBoolean(3, item.isCompleted());
            setNullableLong(ps, 4, item.getAssignedTo());
            ps.setTimestamp(5, item.getCompletedAt());
            ps.executeUpdate();
        }
    }

    public void updateChecklistItem(TaskChecklistItem item) throws SQLException {
        String sql = "UPDATE task_checklist_items SET content = ?, assigned_to = ? WHERE id = ? AND task_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getContent());
            setNullableLong(ps, 2, item.getAssignedTo());
            ps.setLong(3, item.getId());
            ps.setLong(4, item.getTaskId());
            ps.executeUpdate();
        }
    }

    public void deleteChecklistItem(long itemId, long taskId) throws SQLException {
        String sql = "DELETE FROM task_checklist_items WHERE id = ? AND task_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            ps.setLong(2, taskId);
            ps.executeUpdate();
        }
    }

    public void deleteChecklistItemsByTaskId(long taskId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            deleteChecklistItemsByTaskId(conn, taskId);
        }
    }

    public void deleteChecklistItemsByTaskId(Connection conn, long taskId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM task_checklist_items WHERE task_id = ?")) {
            ps.setLong(1, taskId);
            ps.executeUpdate();
        }
    }

    public TaskChecklistItem getChecklistItemById(long itemId) {
        String sql = """
                SELECT ci.*, u.full_name AS assigned_to_name
                FROM task_checklist_items ci
                LEFT JOIN users u ON u.id = ci.assigned_to
                WHERE ci.id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapChecklistItem(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void toggleChecklistItem(long itemId, long taskId, boolean completed) throws SQLException {
        String sql = "UPDATE task_checklist_items SET is_completed = ?, completed_at = " +
                (completed ? "CURRENT_TIMESTAMP" : "NULL") + " WHERE id = ? AND task_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, completed);
            ps.setLong(2, itemId);
            ps.setLong(3, taskId);
            ps.executeUpdate();
        }
    }

    public int countChecklistItems(long taskId) {
        return countBySql("SELECT COUNT(*) FROM task_checklist_items WHERE task_id = ?", taskId);
    }

    public int countCompletedChecklistItems(long taskId) {
        return countBySql("SELECT COUNT(*) FROM task_checklist_items WHERE task_id = ? AND is_completed = TRUE", taskId);
    }

    private int countBySql(String sql, long taskId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
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

    private TaskChecklistItem mapChecklistItem(ResultSet rs) throws SQLException {
        TaskChecklistItem item = new TaskChecklistItem();
        item.setId(rs.getLong("id"));
        item.setTaskId(rs.getLong("task_id"));
        item.setContent(rs.getString("content"));
        item.setCompleted(rs.getBoolean("is_completed"));
        long assignedTo = rs.getLong("assigned_to");
        if (!rs.wasNull()) {
            item.setAssignedTo(assignedTo);
        }
        item.setAssignedToName(rs.getString("assigned_to_name"));
        item.setCompletedAt(rs.getTimestamp("completed_at"));
        return item;
    }

    private void setNullableLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.BIGINT);
        } else {
            ps.setLong(index, value);
        }
    }
}
