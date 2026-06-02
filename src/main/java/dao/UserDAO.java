package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {


    public User findByEmailAndPassword(String email, String password) {
        String sql = """
                SELECT u.*, r.name AS role_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                
                WHERE u.email = ?
                  AND u.password = ?
                  AND u.active = TRUE
                  AND r.active = TRUE
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public User findById(int id) {
        String sql = """
                SELECT u.*, r.name AS role_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                WHERE u.id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public User findProfileById(int id) {
        String sql = """
                SELECT u.*, r.name AS role_name, d.name AS department_name, p.name AS position_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                LEFT JOIN departments d ON d.id = u.department_id
                LEFT JOIN positions p ON p.id = u.position_id
                WHERE u.id = ?
                """;
        User user = new User();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getInt("id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setPhone(rs.getString("phone"));
                    user.setGender(rs.getString("gender"));
                    user.setDateOfBirth(getNullableLocalDateTime(rs, "date_of_birth"));
                    user.setAddress(rs.getString("address"));
                    user.setAvatarUrl(rs.getString("avatar_url"));
                    user.setDepartmentName(rs.getString("department_name"));
                    user.setPositionName(rs.getString("position_name"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public List<User> findByDepartmentId(int id) {
        return findByDepartmentId(id, null);
    }

    public List<User> findByDepartmentId(int id, String keyword) {
        List<User> users = new ArrayList<>();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String sql = """
                      SELECT u.id,
                             u.full_name,
                             u.email,
                             u.phone,
                             u.active,
                             u.department_id,
                             u.position_id,
                             p.name AS position_name,
                             d.name AS department_name
                      FROM users u
                      LEFT JOIN departments d ON d.id = u.department_id
                      LEFT JOIN positions p ON p.id = u.position_id
                      WHERE u.department_id = ?
                """;
        if (hasKeyword) {
            sql += " AND (u.full_name LIKE ? OR u.email LIKE ?) ";
        }
        sql += " ORDER BY u.full_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            if (hasKeyword) {
                String searchKeyword = "%" + keyword.trim() + "%";
                ps.setString(2, searchKeyword);
                ps.setString(3, searchKeyword);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapEmployeeResultSetToUser(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }


    public User findByEmail(String email) {
        String sql = """
                SELECT u.*, r.name AS role_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                WHERE u.email = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();

        String sql = """
                SELECT u.*, r.name AS role_name, d.name AS department_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                LEFT JOIN departments d ON u.department_id = d.id
                ORDER BY u.id ASC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }


    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();

        String sql = """
                SELECT u.*, r.name AS role_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                WHERE u.full_name LIKE ?
                   OR u.email LIKE ?
                   OR u.phone LIKE ?
                   OR r.name LIKE ?
                ORDER BY u.id ASC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword + "%";

            ps.setString(1, searchKeyword);
            ps.setString(2, searchKeyword);
            ps.setString(3, searchKeyword);
            ps.setString(4, searchKeyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }


    public boolean addUser(User user) {
        String sql = """
                INSERT INTO users (
                    full_name,
                    email,
                    password,
                    phone,
                    gender,
                    date_of_birth,
                    address,
                    avatar_url,
                    role_id,
                    active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getGender());
            setNullableTimestamp(ps, 6, user.getDateOfBirth());
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getAvatarUrl());
            ps.setInt(9, user.getRoleId());
            ps.setBoolean(10, user.isActive());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean updateUser(User user) {
        String sql = """
                UPDATE users
                SET full_name = ?,
                    email = ?,
                    phone = ?,
                    gender = ?,
                    date_of_birth = ?,
                    address = ?,
                    avatar_url = ?,
                    role_id = ?,
                    active = ?
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getGender());
            setNullableTimestamp(ps, 5, user.getDateOfBirth());
            ps.setString(6, user.getAddress());
            ps.setString(7, user.getAvatarUrl());
            ps.setInt(8, user.getRoleId());
            ps.setBoolean(9, user.isActive());
            ps.setInt(10, user.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean updateProfile(User user) {
        String sql = """
                UPDATE users
                SET full_name = ?,
                    phone = ?,
                    gender = ?,
                    date_of_birth = ?,
                    address = ?,
                    avatar_url = ?
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getGender());
            setNullableTimestamp(ps, 4, user.getDateOfBirth());
            ps.setString(5, user.getAddress());
            ps.setString(6, user.getAvatarUrl());
            ps.setInt(7, user.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean updateUserStatus(int userId, boolean active) {
        String sql = """
                UPDATE users
                SET active = ?
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, active);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean checkOldPassword(int userId, String oldPassword) {
        String sql = """
                SELECT id
                FROM users
                WHERE id = ?
                  AND password = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, oldPassword);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<User> getAllActiveUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, full_name, email, active FROM users WHERE active = 1 ORDER BY full_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setActive(rs.getBoolean("active"));
        return user;
    }


    public boolean updatePassword(int userId, String newPassword) {
        String sql = """
                UPDATE users
                SET password = ?
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    //Function cho forgot password

//    public boolean saveResetToken(String email, String token, LocalDateTime expiredAt) {
//        String sql = """
//                UPDATE users
//                SET reset_token = ?,
//                    reset_token_expired_at = ?
//                WHERE email = ?
//                """;
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, token);
//            setNullableTimestamp(ps, 2, expiredAt);
//            ps.setString(3, email);
//
//            return ps.executeUpdate() > 0;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//
//
//    public User findByResetToken(String token) {
//        String sql = """
//                SELECT u.*, r.name AS role_name
//                FROM users u
//                JOIN roles r ON u.role_id = r.id
//                WHERE u.reset_token = ?
//                  AND u.reset_token_expired_at > NOW()
//                  AND u.active = TRUE
//                """;
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, token);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return mapResultSetToUser(rs);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//
//    public boolean resetPasswordByToken(String token, String newPassword) {
//        String sql = """
//                UPDATE users
//                SET password = ?,
//                    reset_token = NULL,
//                    reset_token_expired_at = NULL
//                WHERE reset_token = ?
//                  AND reset_token_expired_at > NOW()
//                """;
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, newPassword);
//            ps.setString(2, token);
//
//            return ps.executeUpdate() > 0;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//
//
//    public boolean clearResetToken(int userId) {
//        String sql = """
//                UPDATE users
//                SET reset_token = NULL,
//                    reset_token_expired_at = NULL
//                WHERE id = ?
//                """;
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, userId);
//
//            return ps.executeUpdate() > 0;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }


    public boolean existsByEmail(String email) {
        String sql = """
                SELECT id
                FROM users
                WHERE email = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean existsByEmailExceptCurrentUser(String email, int userId) {
        String sql = """
                SELECT id
                FROM users
                WHERE email = ?
                  AND id <> ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public int countAllUsers() {
        String sql = """
                SELECT COUNT(*) AS total
                FROM users
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    public int countActiveUsers() {
        String sql = """
                SELECT COUNT(*) AS total
                FROM users
                WHERE active = TRUE
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    public int countInactiveUsers() {
        String sql = """
                SELECT COUNT(*) AS total
                FROM users
                WHERE active = FALSE
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<User> findUsersByDeptId(int id) {
        String sql = """
                    SELECT u.*, d.name AS department_name, r.name AS role_name
                    FROM users u
                    LEFT JOIN departments d ON u.department_id = d.id
                    LEFT JOIN roles r ON u.role_id = r.id
                    WHERE u.department_id = ?
                    """;

        List<User> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateDepartmentMember(int userId, Integer newDeptId, boolean activeStatus) {
        String sql = "UPDATE users SET department_id = ?, active = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set department_id
            if (newDeptId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, newDeptId);
            }

            // Set active status (true=1, false=0)
            ps.setBoolean(2, activeStatus);

            // Set user ID
            ps.setInt(3, userId);

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<User> findUnassignedUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE department_id IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new User(rs.getInt("id"), rs.getString("full_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private User mapResultSetToUser(ResultSet rs) throws Exception {
        User user = new User();

        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setGender(rs.getString("gender"));
        user.setDateOfBirth(getNullableLocalDateTime(rs, "date_of_birth"));
        user.setAddress(rs.getString("address"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setRoleId(rs.getInt("role_id"));
        user.setRoleName(rs.getString("role_name"));
        user.setActive(rs.getBoolean("active"));
        user.setResetToken(rs.getString("reset_token"));
        user.setResetTokenExpiredAt(getNullableLocalDateTime(rs, "reset_token_expired_at"));
        // Thêm mapping department_id và position_id
        int departmentId = rs.getInt("department_id");
        if (!rs.wasNull()) {
            user.setDepartmentId(departmentId);
        }

        int positionId = rs.getInt("position_id");
        if (!rs.wasNull()) {
            user.setPositionId(positionId);
        }

        //mapping cho dept name
        try {
            user.setDepartmentName(rs.getString("department_name"));
        } catch (Exception e) {

        }

        return user;
    }

    private User mapEmployeeResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setActive(rs.getBoolean("active"));
        user.setPositionName(rs.getString("position_name"));
        user.setDepartmentName(rs.getString("department_name"));

        int departmentId = rs.getInt("department_id");
        if (!rs.wasNull()) {
            user.setDepartmentId(departmentId);
        }

        int positionId = rs.getInt("position_id");
        if (!rs.wasNull()) {
            user.setPositionId(positionId);
        }

        return user;
    }


    private LocalDateTime getNullableLocalDateTime(ResultSet rs, String columnName) throws Exception {
        Timestamp timestamp = rs.getTimestamp(columnName);

        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime();
    }


    private void setNullableTimestamp(PreparedStatement ps, int parameterIndex, LocalDateTime value) throws Exception {
        if (value == null) {
            ps.setTimestamp(parameterIndex, null);
        } else {
            ps.setTimestamp(parameterIndex, Timestamp.valueOf(value));
        }
    }
}
