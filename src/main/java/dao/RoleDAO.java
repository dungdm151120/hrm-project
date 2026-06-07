package dao;

import model.Role;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public Role getRoleById(int id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean toggleStatus(int id, boolean active) {
        String sql = "UPDATE roles SET active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRoleInfo(int id, String name, String description) {
        String sql = "UPDATE roles SET name = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int addRole(Role role) {
        String sql = "INSERT INTO roles (name, description, active) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, role.getName());
            ps.setString(2, role.getDescription());
            ps.setBoolean(3, role.isActive());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Thêm role thất bại, không có dòng nào bị ảnh hưởng.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Thêm role thất bại, không lấy được ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private boolean isBusinessAdminRole(int roleId) {
        Role role = getRoleById(roleId);
        return role != null && "BUSINESS ADMIN".equals(role.getName());
    }

    public void deleteRolePermissions(int roleId) {
        if (isBusinessAdminRole(roleId)) {
            return;
        }
        String sql = "DELETE FROM role_permissions WHERE role_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insertRolePermissions(int roleId, List<Integer> permissionIds) {
        if (isBusinessAdminRole(roleId)) {
            return false;
        }
        if (permissionIds == null || permissionIds.isEmpty()) return true;
        String sql = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int permId : permissionIds) {
                ps.setInt(1, roleId);
                ps.setInt(2, permId);
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Integer> getPermissionIdsByRoleId(int roleId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT permission_id FROM role_permissions WHERE role_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("permission_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public List<Role> getRolesSortedByPermissionCount(boolean mostFirst) {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.*, COUNT(rp.permission_id) AS perm_count " +
                "FROM roles r LEFT JOIN role_permissions rp ON r.id = rp.role_id " +
                "GROUP BY r.id " +
                "ORDER BY perm_count " + (mostFirst ? "DESC" : "ASC");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public int getTotalRoles(String keyword, Boolean active) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM roles WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
        }
        if (active != null) {
            sql.append(" AND active = ?");
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword.trim() + "%");
            }
            if (active != null) {
                ps.setBoolean(idx++, active);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Role> getRolesWithPaging(String keyword, Boolean active, String sortBy, String sortOrder, int offset, int limit) {
        List<Role> roles = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM roles WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
        }
        if (active != null) {
            sql.append(" AND active = ?");
        }
        if ("name".equals(sortBy)) {
            sql.append(" ORDER BY name ").append("asc".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC");
        } else {
            sql.append(" ORDER BY id");
        }
        sql.append(" LIMIT ? OFFSET ?");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword.trim() + "%");
            }
            if (active != null) {
                ps.setBoolean(idx++, active);
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx++, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    private Role mapRow(ResultSet rs) throws SQLException {
        return new Role(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBoolean("active")
        );
    }
}