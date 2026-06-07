package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
                SELECT u.*, r.name AS role_name, d.name AS department_name, p.name AS position_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN positions p ON u.position_id = p.id
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
        return getEmployeesByDepartment(id, keyword, "all", "name_asc", 1, Integer.MAX_VALUE);
    }

    public List<User> getEmployeesByDepartment(int departmentId, String keyword, String status, String sort, int page, int pageSize) {
        List<User> employees = getAllEmployeesByDepartment(departmentId);
        employees = searchEmployeesByKeyword(employees, keyword);
        employees = filterEmployeesByStatus(employees, status);
        employees = sortEmployeesByName(employees, sort);
        return pagingEmployees(employees, page, pageSize);
    }

    public List<User> getAllEmployeesByDepartment(int departmentId) {
        List<User> users = new ArrayList<>();
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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, departmentId);

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

    public List<User> searchEmployeesByKeyword(List<User> employees, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return employees;
        }

        String lowerKeyword = keyword.trim().toLowerCase();
        List<User> result = new ArrayList<>();
        for (User user : employees) {
            String fullName = user.getFullName() == null ? "" : user.getFullName().toLowerCase();
            String email = user.getEmail() == null ? "" : user.getEmail().toLowerCase();
            if (fullName.contains(lowerKeyword) || email.contains(lowerKeyword)) {
                result.add(user);
            }
        }
        return result;
    }

    public List<User> filterEmployeesByStatus(List<User> employees, String status) {
        if (!"active".equals(status) && !"inactive".equals(status)) {
            return employees;
        }

        boolean active = "active".equals(status);
        List<User> result = new ArrayList<>();
        for (User user : employees) {
            if (user.isActive() == active) {
                result.add(user);
            }
        }
        return result;
    }

    public List<User> sortEmployeesByName(List<User> employees, String sort) {
        List<User> result = new ArrayList<>(employees);
        Comparator<User> comparator = Comparator.comparing(
                user -> user.getFullName() == null ? "" : user.getFullName(),
                String.CASE_INSENSITIVE_ORDER
        );
        if ("name_desc".equals(sort)) {
            comparator = comparator.reversed();
        }
        result.sort(comparator);
        return result;
    }

    public List<User> pagingEmployees(List<User> employees, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) return employees;

        int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= employees.size()) {
            return new ArrayList<>();
        }

        int toIndex = Math.min(fromIndex + pageSize, employees.size());
        return new ArrayList<>(employees.subList(fromIndex, toIndex));
    }

    public int countEmployeesByDepartment(int departmentId, String keyword, String status) {
        List<User> employees = getAllEmployeesByDepartment(departmentId);
        employees = searchEmployeesByKeyword(employees, keyword);
        employees = filterEmployeesByStatus(employees, status);
        return employees.size();
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
                SELECT u.*, r.name AS role_name, d.name AS department_name, p.name AS position_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                LEFT JOIN departments d ON u.department_id = d.id
                OIN positions p ON u.position_id = p.id
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
                SET phone = ?,
                    gender = ?,
                    date_of_birth = ?,
                    address = ?,
                    avatar_url = ?
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getPhone());
            ps.setString(2, user.getGender());
            setNullableTimestamp(ps, 3, user.getDateOfBirth());
            ps.setString(4, user.getAddress());
            ps.setString(5, user.getAvatarUrl());
            ps.setInt(6, user.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean updateUserStatus(int userId, boolean active) {
        String sql = """
                UPDATE users u
                LEFT JOIN department_positions dp ON u.department_id = dp.department_id AND u.position_id = dp.position_id
                LEFT JOIN departments d ON d.id = u.department_id
                LEFT JOIN positions p ON p.id = u.position_id
                SET u.active = ?,
                u.department_id = IF(? = true, IF(d.active = true AND p.active = true, u.department_id, NULL), u.department_id),
                u.position_id = IF(? = true, IF(d.active = true AND p.active = true, u.position_id, NULL), u.position_id)
                WHERE u.id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, active);
            ps.setBoolean(2, active);
            ps.setBoolean(3, active);
            ps.setInt(4, userId);

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

    public void updateDepartmentMember(int userId, Integer newDeptId, Integer newPositionId, boolean activeStatus) {
        String sql = "UPDATE users SET department_id = ?, position_id = ?, active = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1. Set department_id
            if (newDeptId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, newDeptId);
            }

            // 2. Set position_id (Tự động đưa về vị trí Employee khi chuyển phòng)
            if (newPositionId == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, newPositionId);
            }

            // 3. Set active status
            ps.setBoolean(3, activeStatus);

            // 4. Set user ID
            ps.setInt(4, userId);

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<User> findUnassignedUsers() {
        List<User> users = new ArrayList<>();
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
                WHERE u.department_id IS NULL
                  AND u.active = TRUE
                ORDER BY u.full_name
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapEmployeeResultSetToUser(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
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

        //mapping cho position name
        try {
            user.setPositionName(rs.getString("position_name"));
        } catch (Exception e) {

        }

        return user;
    }

    // Move
    public String moveDepartmentMember(int userId, int newDeptId) {
        String checkPositionSql = "SELECT p.name FROM users u " +
                "JOIN positions p ON u.position_id = p.id " +
                "WHERE u.id = ?";

        String updateSql = "UPDATE users SET department_id = ?, " +
                "position_id = (SELECT id FROM positions WHERE name = 'Employee' LIMIT 1) " +
                "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // 1. Kiểm tra chức danh (Position) hiện tại của User
            try (PreparedStatement psCheck = conn.prepareStatement(checkPositionSql)) {
                psCheck.setInt(1, userId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String positionName = rs.getString("name");

                        // Nếu chức danh có chứa từ "Manager", từ chối di chuyển
                        if (positionName != null && positionName.toLowerCase().contains("manager")) {
                            return "ERROR_IS_MANAGER";
                        }
                    } else {
                        return "ERROR_USER_NOT_FOUND";
                    }
                }
            }

            // 2. Thực hiện cập nhật phòng ban mới và chuyển Position về Employee
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, newDeptId);
                psUpdate.setInt(2, userId);

                int rowsAffected = psUpdate.executeUpdate();
                if (rowsAffected > 0) {
                    return "SUCCESS";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR_SYSTEM";
        }
        return "ERROR_FAILED";
    }
    public boolean updateUserPosition(int userId, Integer positionId) {
        String sql = "UPDATE users SET position_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (positionId == null || positionId <= 0) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, positionId);
            }
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean clearPositionForUsers(int positionId) {
        String sql = "UPDATE users SET position_id = NULL, updated_at = CURRENT_TIMESTAMP WHERE position_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, positionId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<User> findActiveByDepartmentId(int departmentId) {
        List<User> users = new ArrayList<>();
        String sql = """
            SELECT u.id, u.full_name, u.email, u.phone, u.active,
                   u.department_id, u.position_id,
                   p.name AS position_name,
                   d.name AS department_name
            FROM users u
            LEFT JOIN departments d ON d.id = u.department_id
            LEFT JOIN positions p ON p.id = u.position_id
            WHERE u.department_id = ? AND u.active = TRUE
            ORDER BY u.full_name
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
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
    //Remove
    public String removeMemberFromDepartment(int userId) {
        String checkPositionSql = "SELECT p.name FROM users u " +
                "JOIN positions p ON u.position_id = p.id WHERE u.id = ?";

        String removeSql = "UPDATE users SET department_id = NULL, " +
                "position_id = (SELECT id FROM positions WHERE name = 'Employee' LIMIT 1)" +
                "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Kiểm tra xem có phải Manager không
            try (PreparedStatement psCheck = conn.prepareStatement(checkPositionSql)) {
                psCheck.setInt(1, userId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String pos = rs.getString("name");
                        if (pos != null && pos.toLowerCase().contains("manager")) {
                            return "ERROR_IS_MANAGER";
                        }
                    }
                }
            }

            // 2. Thực hiện cập nhật
            try (PreparedStatement psUpdate = conn.prepareStatement(removeSql)) {
                psUpdate.setInt(1, userId);
                return psUpdate.executeUpdate() > 0 ? "SUCCESS" : "FAILED";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR_SYSTEM";
        }
    }

    public boolean isPositionAssigned(int positionId) {
        String sql = "SELECT COUNT(*) FROM users WHERE position_id = ? AND active = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, positionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy danh sách nhân viên chưa thuộc phòng ban nào và đang Active
    public List<User> getUnassignedUsers() {
        List<User> list = new ArrayList<>();
        // department_id IS NULL hoặc có thể là 0 tùy vào cách bạn thiết kế DB
        String sql = "SELECT id, full_name FROM users " +
                "WHERE (department_id IS NULL OR department_id = 0)" +
                "AND active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name"));
                list.add(u);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Add member vào Dept
    public boolean addMembersToDept(int[] userIds, int deptId) {
        String sql = "UPDATE users SET department_id = ?, " +
                "position_id = (SELECT id FROM positions WHERE name = 'Employee' LIMIT 1), " +
                "active = 1 " +
                "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (int userId : userIds) {
                ps.setInt(1, deptId);
                ps.setInt(2, userId);
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean removeDepartmentFromUsers(int departmentId) {
        String sql = "UPDATE users SET department_id = NULL WHERE department_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    //
    public int countUsers(String keyword, Boolean active) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users u WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (u.full_name LIKE ?)");
        }
        if (active != null) {
            sql.append(" AND u.active = ?");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(idx++, search);
                ps.setString(idx++, search);
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

    // Lấy danh sách user theo trang
    public List<User> getUsersWithPaging(String keyword, Boolean active, String sortBy, String sortOrder, int offset, int limit) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT u.*, r.name AS role_name, d.name AS department_name, p.name AS position_name " +
                        "FROM users u " +
                        "JOIN roles r ON u.role_id = r.id " +
                        "LEFT JOIN departments d ON u.department_id = d.id " +
                        "LEFT JOIN positions p ON u.position_id = p.id " +
                        "WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND u.full_name LIKE ?");
        }
        if (active != null) {
            sql.append(" AND u.active = ?");
        }

        // Logic Sort động đồng bộ giống Dept
        if ("name".equals(sortBy)) {
            sql.append(" ORDER BY u.full_name ").append("asc".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC");
        } else {
            sql.append(" ORDER BY u.id ASC");
        }

        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) ps.setString(idx++, "%" + keyword.trim() + "%");
            if (active != null) ps.setBoolean(idx++, active);

            ps.setInt(idx++, limit);
            ps.setInt(idx++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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

    public boolean deactivateUsersByDepartment(int departmentId) {
        String sql = "UPDATE users SET active = 0 WHERE department_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
