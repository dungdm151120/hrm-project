package dao;

import model.Conversation;
import model.Message;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatDAO {

    public List<Conversation> getConversationsByUserId(int userId) throws SQLException {
        List<Conversation> conversations = new ArrayList<>();
        String sql = "SELECT c.id, c.is_group, c.name, c.created_at, " +
                "last_msg.id AS msg_id, last_msg.content AS msg_content, last_msg.sent_at AS msg_sent_at, " +
                "last_msg.sender_id AS msg_sender_id, sender.full_name AS msg_sender_name " +
                "FROM conversations c " +
                "JOIN conversation_participants cp ON c.id = cp.conversation_id " +
                "LEFT JOIN (SELECT conversation_id, MAX(sent_at) AS max_sent FROM messages GROUP BY conversation_id) latest ON c.id = latest.conversation_id " +
                "LEFT JOIN messages last_msg ON last_msg.conversation_id = latest.conversation_id AND last_msg.sent_at = latest.max_sent " +
                "LEFT JOIN users sender ON last_msg.sender_id = sender.id " +
                "WHERE cp.user_id = ? " +
                "ORDER BY COALESCE(last_msg.sent_at, c.created_at) DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Conversation conv = new Conversation();
                    conv.setId(rs.getInt("id"));
                    conv.setGroup(rs.getBoolean("is_group"));
                    conv.setName(rs.getString("name"));
                    conv.setCreatedAt(getDateTime(rs, "created_at"));

                    if (rs.getObject("msg_id") != null) {
                        Message lastMsg = new Message();
                        lastMsg.setId(rs.getInt("msg_id"));
                        lastMsg.setContent(rs.getString("msg_content"));
                        lastMsg.setSentAt(getDateTime(rs, "msg_sent_at"));
                        lastMsg.setSenderId(rs.getInt("msg_sender_id"));
                        lastMsg.setSenderName(rs.getString("msg_sender_name"));
                        conv.setLastMessage(lastMsg);
                    }

                    conv.setParticipants(getParticipantsByConversationId(conv.getId(), conn));
                    conversations.add(conv);
                }
            }
        }
        return conversations;
    }

    private List<User> getParticipantsByConversationId(int conversationId, Connection conn) throws SQLException {
        List<User> participants = new ArrayList<>();
        String sql = "SELECT u.id, u.full_name, u.avatar_url FROM users u " +
                "JOIN conversation_participants cp ON u.id = cp.user_id " +
                "WHERE cp.conversation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setAvatarUrl(rs.getString("avatar_url"));
                    participants.add(user);
                }
            }
        }
        return participants;
    }

    public List<Message> getMessagesByConversation(int conversationId, int offset, int limit) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.id, m.sender_id, m.content, m.sent_at, m.is_read, " +
                "u.full_name AS sender_name, u.avatar_url AS sender_avatar " +
                "FROM messages m " +
                "JOIN users u ON m.sender_id = u.id " +
                "WHERE m.conversation_id = ? " +
                "ORDER BY m.sent_at DESC " +
                "LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message();
                    msg.setId(rs.getInt("id"));
                    msg.setSenderId(rs.getInt("sender_id"));
                    msg.setContent(rs.getString("content"));
                    msg.setSentAt(getDateTime(rs, "sent_at"));
                    msg.setRead(rs.getBoolean("is_read"));
                    msg.setSenderName(rs.getString("sender_name"));
                    msg.setSenderAvatar(rs.getString("sender_avatar"));
                    messages.add(msg);
                }
            }
        }
        List<Message> reversed = new ArrayList<>(messages);
        Collections.reverse(reversed);
        return reversed;
    }

    public Message sendMessage(int conversationId, int senderId, String content) throws SQLException {
        String sql = "INSERT INTO messages (conversation_id, sender_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, conversationId);
            ps.setInt(2, senderId);
            ps.setString(3, content);
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int msgId = generatedKeys.getInt(1);
                    return getMessageById(msgId);
                }
            }
        }
        return null;
    }

    private Message getMessageById(int messageId) throws SQLException {
        String sql = "SELECT m.id, m.conversation_id, m.sender_id, m.content, m.sent_at, m.is_read, " +
                "u.full_name AS sender_name, u.avatar_url AS sender_avatar " +
                "FROM messages m JOIN users u ON m.sender_id = u.id WHERE m.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Message msg = new Message();
                    msg.setId(rs.getInt("id"));
                    msg.setConversationId(rs.getInt("conversation_id"));
                    msg.setSenderId(rs.getInt("sender_id"));
                    msg.setContent(rs.getString("content"));
                    msg.setSentAt(getDateTime(rs, "sent_at"));
                    msg.setRead(rs.getBoolean("is_read"));
                    msg.setSenderName(rs.getString("sender_name"));
                    msg.setSenderAvatar(rs.getString("sender_avatar"));
                    return msg;
                }
            }
        }
        return null;
    }

    public void markMessagesAsRead(int conversationId, int userId) throws SQLException {
        String sql = "UPDATE messages SET is_read = TRUE WHERE conversation_id = ? AND sender_id != ? AND is_read = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public int createDirectConversation(int user1Id, int user2Id) throws SQLException {
        String checkSql = "SELECT cp1.conversation_id FROM conversation_participants cp1 " +
                "JOIN conversation_participants cp2 ON cp1.conversation_id = cp2.conversation_id " +
                "JOIN conversations c ON cp1.conversation_id = c.id " +
                "WHERE c.is_group = FALSE AND cp1.user_id = ? AND cp2.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, user1Id);
            ps.setInt(2, user2Id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("conversation_id");
                }
            }
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String insertConv = "INSERT INTO conversations (is_group) VALUES (FALSE)";
                int convId;
                try (PreparedStatement ps = conn.prepareStatement(insertConv, Statement.RETURN_GENERATED_KEYS)) {
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            convId = rs.getInt(1);
                        } else {
                            throw new SQLException("Failed to create conversation");
                        }
                    }
                }

                String insertPart = "INSERT INTO conversation_participants (conversation_id, user_id) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertPart)) {
                    ps.setInt(1, convId);
                    ps.setInt(2, user1Id);
                    ps.executeUpdate();
                    ps.setInt(2, user2Id);
                    ps.executeUpdate();
                }
                conn.commit();
                return convId;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<User> searchUsers(String keyword, int currentUserId) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, full_name, email, avatar_url FROM users " +
                "WHERE active = TRUE AND id != ? " +
                "AND (full_name LIKE ? OR email LIKE ?) " +
                "ORDER BY full_name LIMIT 20";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            String like = "%" + keyword + "%";
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setAvatarUrl(rs.getString("avatar_url"));
                    users.add(user);
                }
            }
        }
        return users;
    }

    private LocalDateTime getDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}