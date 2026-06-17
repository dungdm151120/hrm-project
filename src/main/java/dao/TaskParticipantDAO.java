package dao;

import model.TaskParticipant;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskParticipantDAO {

    public List<TaskParticipant> getParticipantsByTaskId(long taskId) {
        List<TaskParticipant> participants = new ArrayList<>();
        String sql = """
                SELECT tp.*, u.full_name AS user_name
                FROM task_participants tp
                JOIN users u ON u.id = tp.user_id
                WHERE tp.task_id = ?
                ORDER BY u.full_name
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    participants.add(mapParticipant(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    public void insertParticipants(long taskId, List<Long> userIds) throws SQLException {
        String sql = "INSERT INTO task_participants (task_id, user_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            insertParticipants(conn, taskId, userIds);
        }
    }

    public void insertParticipants(Connection conn, long taskId, List<Long> userIds) throws SQLException {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO task_participants (task_id, user_id) VALUES (?, ?)";
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

    public void deleteParticipantsByTaskId(long taskId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            deleteParticipantsByTaskId(conn, taskId);
        }
    }

    public void deleteParticipantsByTaskId(Connection conn, long taskId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM task_participants WHERE task_id = ?")) {
            ps.setLong(1, taskId);
            ps.executeUpdate();
        }
    }

    public boolean existsByTaskIdAndUserId(long taskId, long userId) {
        String sql = "SELECT 1 FROM task_participants WHERE task_id = ? AND user_id = ? LIMIT 1";
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

    private TaskParticipant mapParticipant(ResultSet rs) throws SQLException {
        TaskParticipant participant = new TaskParticipant();
        participant.setId(rs.getLong("id"));
        participant.setTaskId(rs.getLong("task_id"));
        participant.setUserId(rs.getLong("user_id"));
        participant.setUserName(rs.getString("user_name"));
        return participant;
    }
}
