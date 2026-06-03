package dao;

import model.Position;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PositionDAO {

    public List<Position> findPositionsAdvanced(String keyword, String status, String sort) {
        List<Position> positions = new ArrayList<>();

        String cleanKeyword = (keyword != null) ? keyword.trim() : "";
        String cleanStatus = (status != null) ? status.trim() : "";

        StringBuilder sql = new StringBuilder("SELECT p.* FROM positions p WHERE 1=1 ");

        if (!cleanKeyword.isEmpty()) {
            sql.append("AND p.name LIKE ? ");
        }

        if (!cleanStatus.isEmpty()) {
            sql.append("AND p.active = ? ");
        }

        sql.append("ORDER BY ");
        switch (sort != null ? sort : "") {
            case "name_asc":    sql.append("p.name ASC"); break;
            case "name_desc":   sql.append("p.name DESC"); break;
            case "id_asc":      sql.append("p.id ASC"); break;
            case "id_desc":
            default:            sql.append("p.id DESC"); break;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (!cleanKeyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword.trim() + "%");
            }

            if (!cleanStatus.isEmpty()) {
                ps.setBoolean(paramIndex++, Boolean.parseBoolean(status));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    positions.add(mapResultSetToPosition(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return positions;
    }

    public Position findById(int id) {
        Position positions = new Position();

        String sql = """
                    SELECT p.* FROM positions p WHERE p.id = ?
                    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPosition(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addPosition(Position position) {
        String sql = """
                    INSERT INTO positions (
                        name,
                        description,
                        active,
                        created_at
                    )
                    VALUES (?, ?, ?, ?)
                    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, position.getName());
            ps.setString(2, position.getDescription());
            ps.setBoolean(3, position.isActive());
            setNullableTimestamp(ps, 4, position.getCreatedAt() != null ? position.getCreatedAt() : LocalDateTime.now());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePosition(Position position) {

        String sql = """
                    UPDATE positions
                    SET name = ?,
                        description = ?,
                        updated_at = ?
                    WHERE id = ?
                    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, position.getName());
            ps.setString(2, position.getDescription());
            setNullableTimestamp(ps, 3, position.getUpdatedAt());
            ps.setInt(4, position.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePositionStatus(int postId, boolean status) {
        String sql = """
                    UPDATE positions
                    SET active = ?
                    WHERE id = ?
                    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, status);
            ps.setInt(2, postId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Position mapResultSetToPosition(ResultSet rs) throws Exception {
        Position position = new Position();
        position.setId(rs.getInt("id"));
        position.setName(rs.getString("name"));
        position.setDescription(rs.getString("description"));
        position.setActive(rs.getBoolean("active"));
        position.setCreatedAt(getNullableLocalDateTime(rs, "created_at"));
        position.setUpdatedAt(getNullableLocalDateTime(rs, "updated_at"));

        return position;
    }

    private LocalDateTime getNullableLocalDateTime(ResultSet rs, String columnName) throws Exception {
        Timestamp timestamp = rs.getTimestamp(columnName);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private void setNullableTimestamp(PreparedStatement ps, int parameterIndex, LocalDateTime value) throws Exception {
        if (value == null) {
            ps.setTimestamp(parameterIndex, null);
        } else {
            ps.setTimestamp(parameterIndex, Timestamp.valueOf(value));
        }
    }
}
