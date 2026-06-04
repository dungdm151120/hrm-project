package dao;

import model.Position;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PositionDAO {

    public List<Position> getAllPositions() {
        List<Position> list = new ArrayList<>();
        String sql = """
                SELECT p.*, d.name AS department_name
                FROM positions p
                LEFT JOIN department_positions dp ON p.id = dp.position_id
                LEFT JOIN departments d ON dp.department_id = d.id
                ORDER BY p.id 
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Position pos = new Position();
                pos.setId(rs.getInt("id"));
                pos.setName(rs.getString("name"));
                pos.setDescription(rs.getString("description"));
                pos.setActive(rs.getBoolean("active"));
                pos.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                pos.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);

                pos.setDepartmentName(rs.getString("department_name"));

                list.add(pos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Position findByName(String name) {
        String sql = "SELECT * FROM positions WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
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

    public List<Position> findPositionsAdvanced(String keyword, Boolean active, String sort, int offset, int limit) {
        List<Position> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT p.*, GROUP_CONCAT(d.name SEPARATOR ', ') AS department_name
            FROM positions p
            LEFT JOIN department_positions dp ON p.id = dp.position_id
            LEFT JOIN departments d ON dp.department_id = d.id
            WHERE 1=1
            """);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND p.name LIKE ? ");
        }
        if (active != null) {
            sql.append(" AND p.active = ? ");
        }

        sql.append(" GROUP BY p.id ");

        if (sort != null) {
            switch (sort) {
                case "name_asc" -> sql.append(" ORDER BY p.name ASC ");
                case "name_desc" -> sql.append(" ORDER BY p.name DESC ");
                case "id_asc" -> sql.append(" ORDER BY p.id ASC ");
                default -> sql.append(" ORDER BY p.id DESC ");
            }
        } else {
            sql.append(" ORDER BY p.id DESC ");
        }

        sql.append(" LIMIT ? OFFSET ? ");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword.trim() + "%");
            }
            if (active != null) {
                ps.setBoolean(paramIndex++, active);
            }

            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Position pos = new Position();
                    pos.setId(rs.getInt("id"));
                    pos.setName(rs.getString("name"));
                    pos.setDescription(rs.getString("description"));
                    pos.setActive(rs.getBoolean("active"));
                    pos.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                    pos.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                    pos.setDepartmentName(rs.getString("department_name"));

                    list.add(pos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<Position> getAssignablePositionsByDepartment(int departmentId) {
        List<Position> list = new ArrayList<>();
        String sql = "SELECT p.id, p.name FROM positions p " +
                "JOIN department_positions dp ON p.id = dp.position_id " +
                "WHERE dp.department_id = ? " +
                "AND p.active = true " +
                "AND p.name NOT IN ('HR Manager', 'System Administrator', 'Department Manager') " +
                "ORDER BY p.name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Position pos = new Position();
                    pos.setId(rs.getInt("id"));
                    pos.setName(rs.getString("name"));
                    list.add(pos);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public int countPositions(String keyword, Boolean active) {
        int totalRows = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM positions WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND name LIKE ? ");
        }
        if (active != null) { // Code ngắn gọn, không cần check trống chuỗi nữa
            sql.append("AND active = ? ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword.trim() + "%");
            }
            if (active != null) {
                ps.setBoolean(paramIndex++, active);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) totalRows = rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return totalRows;
    }

    public Position findById(int id) {
        String sql = """
            SELECT p.*, dp.department_id
            FROM positions p
            LEFT JOIN department_positions dp ON p.id = dp.position_id
            WHERE p.id = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Position pos = new Position();
                    pos.setId(rs.getInt("id"));
                    pos.setName(rs.getString("name"));
                    pos.setDescription(rs.getString("description"));
                    pos.setActive(rs.getBoolean("active"));

                    int deptId = rs.getInt("department_id");
                    if (rs.wasNull()) {
                        pos.setDepartmentId(-1);
                    } else {
                        pos.setDepartmentId(deptId);
                    }
                    return pos;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addPosition(Position position) {
        String sqlPosition = """
                INSERT INTO positions (
                    name,
                    description,
                    active,
                    created_at
                )
                VALUES (?, ?, ?, ?)
                """;

        String sqlMapping = """
                INSERT INTO department_positions (
                    department_id,
                    position_id
                )
                VALUES (?, ?)
                """;

        Connection conn = null;
        PreparedStatement psPos = null;
        PreparedStatement psMap = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            psPos = conn.prepareStatement(sqlPosition, java.sql.Statement.RETURN_GENERATED_KEYS);
            psPos.setString(1, position.getName());
            psPos.setString(2, position.getDescription());
            psPos.setBoolean(3, position.isActive());
            setNullableTimestamp(psPos, 4, position.getCreatedAt() != null ? position.getCreatedAt() : LocalDateTime.now());

            int affectedRows = psPos.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            rs = psPos.getGeneratedKeys();
            int generatedPositionId = 0;
            if (rs.next()) {
                generatedPositionId = rs.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            psMap = conn.prepareStatement(sqlMapping);
            psMap.setInt(1, position.getDepartmentId());
            psMap.setInt(2, generatedPositionId);
            psMap.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (psPos != null) psPos.close(); } catch (Exception e) {}
            try { if (psMap != null) psMap.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return false;
    }

    public boolean updatePosition(Position position) {
        // 1. Sửa thông tin cơ bản của Position
        String sqlPosition = """
                UPDATE positions
                SET name = ?,
                    description = ?,
                    updated_at = ?
                WHERE id = ?
                """;

        // 2. Xóa liên kết cũ trong bảng trung gian
        String sqlDeleteMapping = "DELETE FROM department_positions WHERE position_id = ?";

        // 3. Chèn liên kết mới vào bảng trung gian
        String sqlInsertMapping = "INSERT INTO department_positions (department_id, position_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement psPos = null;
        PreparedStatement psDel = null;
        PreparedStatement psIns = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            psPos = conn.prepareStatement(sqlPosition);
            psPos.setString(1, position.getName());
            psPos.setString(2, position.getDescription());
            setNullableTimestamp(psPos, 3, position.getUpdatedAt());
            psPos.setInt(4, position.getId());
            psPos.executeUpdate();

            psDel = conn.prepareStatement(sqlDeleteMapping);
            psDel.setInt(1, position.getId());
            psDel.executeUpdate();

            psIns = conn.prepareStatement(sqlInsertMapping);
            psIns.setInt(1, position.getDepartmentId());
            psIns.setInt(2, position.getId());
            psIns.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            try { if (psPos != null) psPos.close(); } catch (Exception e) {}
            try { if (psDel != null) psDel.close(); } catch (Exception e) {}
            try { if (psIns != null) psIns.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
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
