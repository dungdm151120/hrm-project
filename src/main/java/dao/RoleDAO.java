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
        String sql = "UPDATE roles SET name = ?, description = ? WHERE id = ?";
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


    public void deleteRolePermissions(int roleId) {
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

    private Role mapRow(ResultSet rs) throws SQLException {
        return new Role(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBoolean("active")
        );
    }
}
