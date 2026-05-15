package dao;

import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
    public User login(String email, String password) {
        String sql = "SELECT u.*, r.name AS role_name\n" +
                "                FROM users u\n" +
                "                JOIN roles r ON u.role_id = r.id\n" +
                "                WHERE u.email = ?\n" +
                "                AND u.password = ?\n" +
                "                AND u.active = TRUE\n" +
                "                AND r.active = TRUE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setRoleName(rs.getString("role_name"));
                    user.setActive(rs.getBoolean("active"));
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
