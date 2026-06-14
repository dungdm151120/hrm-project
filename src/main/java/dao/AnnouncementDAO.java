package dao;

import model.Announcement;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {
    public boolean add(Announcement announcement) {
        String insertAnnouncementSql = """
                INSERT INTO announcements (
                    title, content, target_scope, department_id, publish_date, created_by
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        String insertAllRecipientsSql = """
                INSERT INTO announcement_recipients (announcement_id, user_id)
                SELECT ?, u.id
                FROM users u
                WHERE u.active = TRUE
                """;
        String insertDepartmentRecipientsSql = """
                INSERT INTO announcement_recipients (announcement_id, user_id)
                SELECT ?, u.id
                FROM users u
                WHERE u.active = TRUE
                  AND u.department_id = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int announcementId;
                try (PreparedStatement ps = conn.prepareStatement(insertAnnouncementSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, announcement.getTitle());
                    ps.setString(2, announcement.getContent());
                    ps.setString(3, announcement.getTargetScope());
                    setNullableInteger(ps, 4, announcement.getDepartmentId());
                    ps.setTimestamp(5, Timestamp.valueOf(announcement.getPublishDate()));
                    ps.setInt(6, announcement.getCreatedBy());

                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                        announcementId = rs.getInt(1);
                    }
                }

                int recipientCount;
                if ("DEPARTMENT".equals(announcement.getTargetScope())) {
                    try (PreparedStatement ps = conn.prepareStatement(insertDepartmentRecipientsSql)) {
                        ps.setInt(1, announcementId);
                        ps.setInt(2, announcement.getDepartmentId());
                        recipientCount = ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = conn.prepareStatement(insertAllRecipientsSql)) {
                        ps.setInt(1, announcementId);
                        recipientCount = ps.executeUpdate();
                    }
                }

                if (recipientCount == 0) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                announcement.setId(announcementId);
                return true;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Announcement> searchVisible(int userId, Integer departmentId, String keyword, String scope,
                                            String readStatus, int offset, int limit) {
        List<Announcement> announcements = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(baseSelect()).append("""
                 JOIN announcement_recipients ar ON ar.announcement_id = a.id
                 WHERE ar.user_id = ?
                   AND a.publish_date <= CURRENT_TIMESTAMP
                """);
        params.add(userId);
        appendSearchFilters(sql, params, keyword, scope, readStatus, departmentId);
        sql.append(" ORDER BY a.created_at DESC, a.id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    announcements.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return announcements;
    }

    public int countVisible(int userId, Integer departmentId, String keyword, String scope, String readStatus) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) AS total
                FROM announcements a
                JOIN announcement_recipients ar ON ar.announcement_id = a.id
                WHERE ar.user_id = ?
                  AND a.publish_date <= CURRENT_TIMESTAMP
                """);
        params.add(userId);
        appendSearchFilters(sql, params, keyword, scope, readStatus, departmentId);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Announcement findVisibleById(int id, int userId) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(baseSelect())
                .append("""
                         JOIN announcement_recipients ar ON ar.announcement_id = a.id
                         WHERE a.id = ?
                           AND ar.user_id = ?
                           AND a.publish_date <= CURRENT_TIMESTAMP
                        """);
        params.add(id);
        params.add(userId);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean markAsRead(int announcementId, int userId) {
        String sql = """
                UPDATE announcement_recipients
                SET read_at = COALESCE(read_at, CURRENT_TIMESTAMP)
                WHERE announcement_id = ?
                  AND user_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, announcementId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int countUnread(int userId) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM announcements a
                JOIN announcement_recipients ar ON ar.announcement_id = a.id
                WHERE ar.user_id = ?
                  AND ar.read_at IS NULL
                  AND a.publish_date <= CURRENT_TIMESTAMP
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<Announcement> getLatestUnread(int userId, int limit) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = baseSelect() + """
                JOIN announcement_recipients ar ON ar.announcement_id = a.id
                WHERE ar.user_id = ?
                  AND ar.read_at IS NULL
                  AND a.publish_date <= CURRENT_TIMESTAMP
                ORDER BY a.created_at DESC, a.id DESC
                LIMIT ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    announcements.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return announcements;
    }

    private String baseSelect() {
        return """
                SELECT a.*,
                       d.name AS department_name,
                       u.full_name AS creator_name,
                       ar.read_at
                FROM announcements a
                LEFT JOIN departments d ON a.department_id = d.id
                JOIN users u ON a.created_by = u.id
                """;
    }

    private void appendSearchFilters(StringBuilder sql, List<Object> params, String keyword,
                                     String scope, String readStatus, Integer departmentId) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND LOWER(a.title) LIKE ?");
            params.add("%" + keyword.trim().toLowerCase() + "%");
        }
        if ("READ".equalsIgnoreCase(readStatus)) {
            sql.append(" AND ar.read_at IS NOT NULL");
        } else if ("UNREAD".equalsIgnoreCase(readStatus)) {
            sql.append(" AND ar.read_at IS NULL");
        }
        if ("MY_DEPARTMENT".equalsIgnoreCase(scope)) {
            if (departmentId == null) {
                sql.append(" AND 1 = 0");
            } else {
                sql.append(" AND a.target_scope = 'DEPARTMENT' AND a.department_id = ?");
                params.add(departmentId);
            }
        }
    }

    private void bindParams(PreparedStatement ps, List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    private Announcement mapRow(ResultSet rs) throws Exception {
        Announcement announcement = new Announcement();
        announcement.setId(rs.getInt("id"));
        announcement.setTitle(rs.getString("title"));
        announcement.setContent(rs.getString("content"));
        announcement.setTargetScope(rs.getString("target_scope"));
        int departmentId = rs.getInt("department_id");
        if (!rs.wasNull()) {
            announcement.setDepartmentId(departmentId);
        }
        announcement.setDepartmentName(rs.getString("department_name"));
        announcement.setPublishDate(getNullableLocalDateTime(rs, "publish_date"));
        announcement.setCreatedBy(rs.getInt("created_by"));
        announcement.setCreatorName(rs.getString("creator_name"));
        announcement.setReadAt(getNullableLocalDateTime(rs, "read_at"));
        announcement.setCreatedAt(getNullableLocalDateTime(rs, "created_at"));
        announcement.setUpdatedAt(getNullableLocalDateTime(rs, "updated_at"));
        return announcement;
    }

    private LocalDateTime getNullableLocalDateTime(ResultSet rs, String columnName) throws Exception {
        Timestamp timestamp = rs.getTimestamp(columnName);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private void setNullableInteger(PreparedStatement ps, int index, Integer value) throws Exception {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
}
