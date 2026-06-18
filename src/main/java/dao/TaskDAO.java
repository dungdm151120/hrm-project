package dao;

import model.Task;
import util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private final TaskParticipantDAO participantDAO = new TaskParticipantDAO();
    private final TaskObserverDAO observerDAO = new TaskObserverDAO();
    private final TaskChecklistItemDAO checklistItemDAO = new TaskChecklistItemDAO();
    private final TaskCommentDAO commentDAO = new TaskCommentDAO();
    private final TaskHistoryDAO historyDAO = new TaskHistoryDAO();

    public List<Task> getTasks(String keyword, String status, int page, int pageSize) {
        List<Task> tasks = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT t.*, creator.full_name AS created_by_name, assignee.full_name AS assigned_to_name
                FROM tasks t
                JOIN users creator ON creator.id = t.created_by
                JOIN users assignee ON assignee.id = t.assigned_to
                WHERE 1 = 1
                """);

        appendFilters(sql, keyword, status);
        sql.append(" ORDER BY t.created_at DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = bindFilters(ps, 1, keyword, status);
            ps.setInt(index++, pageSize);
            ps.setInt(index, Math.max(0, (page - 1) * pageSize));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapTask(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public int countTasks(String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM tasks t WHERE 1 = 1");
        appendFilters(sql, keyword, status);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindFilters(ps, 1, keyword, status);
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

    public Task getTaskById(long id) {
        String sql = """
                SELECT t.*, creator.full_name AS created_by_name, assignee.full_name AS assigned_to_name
                FROM tasks t
                JOIN users creator ON creator.id = t.created_by
                JOIN users assignee ON assignee.id = t.assigned_to
                WHERE t.id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Task task = mapTask(rs);
                    task.setParticipants(participantDAO.getParticipantsByTaskId(id));
                    task.setObservers(observerDAO.getObserversByTaskId(id));
                    task.setChecklistItems(checklistItemDAO.getChecklistItemsByTaskId(id));
                    return task;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long insertTask(Task task) throws SQLException {
        String sql = """
                INSERT INTO tasks (title, description, status, deadline, progress, allow_participants_complete_checklist, created_by, assigned_to)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindTask(ps, task, false);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Could not insert task.");
    }

    public void updateTask(Task task) throws SQLException {
        String sql = """
                UPDATE tasks
                SET title = ?, description = ?, status = ?, deadline = ?, progress = ?,
                    allow_participants_complete_checklist = ?, assigned_to = ?
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindTask(ps, task, true);
            ps.executeUpdate();
        }
    }

    public void deleteTask(long id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                participantDAO.deleteParticipantsByTaskId(conn, id);
                observerDAO.deleteObserversByTaskId(conn, id);
                checklistItemDAO.deleteChecklistItemsByTaskId(conn, id);
                commentDAO.deleteCommentsByTaskId(conn, id);
                historyDAO.deleteHistoriesByTaskId(conn, id);
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void updateTaskStatus(long taskId, String status) throws SQLException {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, taskId);
            ps.executeUpdate();
        }
    }

    public void replaceTaskRelations(Task task, List<Long> participantIds, List<Long> observerIds) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                participantDAO.deleteParticipantsByTaskId(conn, task.getId());
                observerDAO.deleteObserversByTaskId(conn, task.getId());
                participantDAO.insertParticipants(conn, task.getId(), participantIds);
                observerDAO.insertObservers(conn, task.getId(), observerIds);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public int calculateProgress(long taskId) {
        int total = checklistItemDAO.countChecklistItems(taskId);
        if (total == 0) {
            return 0;
        }
        int completed = checklistItemDAO.countCompletedChecklistItems(taskId);
        return completed * 100 / total;
    }

    public String calculateStatusFromChecklist(long taskId) {
        int total = checklistItemDAO.countChecklistItems(taskId);
        if (total == 0) {
            return "TODO";
        }
        int completed = checklistItemDAO.countCompletedChecklistItems(taskId);
        if (completed == 0) {
            return "TODO";
        }
        return completed == total ? "COMPLETED" : "IN_PROGRESS";
    }

    public void refreshProgressAndAutoComplete(long taskId) throws SQLException {
        int progress = calculateProgress(taskId);
        String calculatedStatus = calculateStatusFromChecklist(taskId);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     UPDATE tasks
                     SET progress = ?,
                         status = CASE
                             WHEN status = 'PAUSED' THEN 'PAUSED'
                             ELSE ?
                         END
                     WHERE id = ?
                     """)) {
            ps.setInt(1, progress);
            ps.setString(2, calculatedStatus);
            ps.setLong(3, taskId);
            ps.executeUpdate();
        }
    }

    public void resumeTask(long taskId) throws SQLException {
        int progress = calculateProgress(taskId);
        String calculatedStatus = calculateStatusFromChecklist(taskId);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE tasks SET progress = ?, status = ? WHERE id = ?")) {
            ps.setInt(1, progress);
            ps.setString(2, calculatedStatus);
            ps.setLong(3, taskId);
            ps.executeUpdate();
        }
    }

    private void appendFilters(StringBuilder sql, String keyword, String status) {
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND t.title LIKE ?");
        }
        if (status != null && !status.isBlank()) {
            if ("OVERDUE".equals(status)) {
                sql.append(" AND t.deadline < CURDATE() AND t.status <> 'COMPLETED'");
            } else {
                sql.append(" AND t.status = ?");
            }
        }
    }

    private int bindFilters(PreparedStatement ps, int index, String keyword, String status) throws SQLException {
        if (keyword != null && !keyword.isBlank()) {
            ps.setString(index++, "%" + keyword.trim() + "%");
        }
        if (status != null && !status.isBlank() && !"OVERDUE".equals(status)) {
            ps.setString(index++, status);
        }
        return index;
    }

    private void bindTask(PreparedStatement ps, Task task, boolean update) throws SQLException {
        ps.setString(1, task.getTitle());
        ps.setString(2, task.getDescription());
        ps.setString(3, task.getStatus() == null ? "TODO" : task.getStatus());
        ps.setDate(4, task.getDeadline());
        ps.setInt(5, task.getProgress());
        ps.setBoolean(6, task.isAllowParticipantsCompleteChecklist());
        if (update) {
            ps.setLong(7, task.getAssignedTo());
            ps.setLong(8, task.getId());
        } else {
            ps.setLong(7, task.getCreatedBy());
            ps.setLong(8, task.getAssignedTo());
        }
    }

    private Task mapTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setStatus(rs.getString("status"));
        Date deadline = rs.getDate("deadline");
        if (!rs.wasNull()) {
            task.setDeadline(deadline);
        }
        task.setProgress(rs.getInt("progress"));
        task.setAllowParticipantsCompleteChecklist(rs.getBoolean("allow_participants_complete_checklist"));
        task.setCreatedBy(rs.getLong("created_by"));
        task.setAssignedTo(rs.getLong("assigned_to"));
        task.setCreatedAt(rs.getTimestamp("created_at"));
        task.setUpdatedAt(rs.getTimestamp("updated_at"));
        task.setCreatedByName(rs.getString("created_by_name"));
        task.setAssignedToName(rs.getString("assigned_to_name"));
        return task;
    }
}
