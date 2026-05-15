package dao;

import model.Permission;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionDAO {

    // Lấy tất cả permissions
    public List<Permission> getAllPermissions() {
        List<Permission> list = new ArrayList<>();
        String sql = "SELECT * FROM permissions ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy permissions của một role cụ thể
    public List<Permission> getPermissionsByRoleId(int roleId) {
        List<Permission> list = new ArrayList<>();
        String sql = "SELECT p.* FROM permissions p " +
                     "JOIN role_permissions rp ON p.id = rp.permission_id " +
                     "WHERE rp.role_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Permission mapRow(ResultSet rs) throws SQLException {
        return new Permission(
            rs.getInt("id"),
            rs.getString("code"),
            rs.getString("name"),
            rs.getString("description")
        );
    }
}
