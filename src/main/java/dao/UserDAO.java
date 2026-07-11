package dao;

import model.User;
import util.DBConnection;
import util.PasswordUtil;

import java.sql.*;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class UserDAO {


    public User findByEmailAndPassword(String email, String password) {
        String sql = """
                SELECT u.*, r.name AS role_name, p.name AS position_name, d.name AS department_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                LEFT JOIN positions p ON u.position_id = p.id
                LEFT JOIN departments d ON u.department_id = d.id
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

    public User findActiveUserByEmail(String email) {
        String sql = """
                SELECT u.*, r.name AS role_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                WHERE u.email = ?
                  AND u.active = TRUE
                  AND r.active = TRUE
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

    public User findByIdWithEmployeeCode(int id) {
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
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setEmployeeCode(rs.getString("employee_code"));
                    user.setRoleName(rs.getString("role_name"));
                    user.setDepartmentName(rs.getString("department_name"));
                    user.setPositionName(rs.getString("position_name"));

                    return user;
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
        return getEmployeesByDepartment(departmentId, keyword, status, sort, page, pageSize, null);
    }

    public List<User> getEmployeesByDepartment(int departmentId, String keyword, String status, String sort,
                                               int page, int pageSize, Integer managerUserId) {
        List<User> employees = getAllEmployeesByDepartment(departmentId);
        employees = searchEmployeesByKeyword(employees, keyword);
        employees = filterEmployeesByStatus(employees, status);
        employees = sortEmployeesByName(employees, sort, managerUserId);
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
    public int countActiveUsersByDepartment(int departmentId) {
        String sql = "SELECT COUNT(*) FROM users WHERE department_id = ? AND active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public boolean updateUserRole(int userId, int roleId) {
        String sql = "UPDATE users SET role_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<User> searchEmployeesByKeyword(List<User> employees, String keyword) {
        boolean accentSensitive = containsVietnameseDiacritics(keyword);
        String normalizedKeyword = normalizeSearchText(keyword, accentSensitive);
        if (normalizedKeyword.isEmpty()) {
            return employees;
        }

        String[] searchTerms = normalizedKeyword.split(" ");
        List<User> result = new ArrayList<>();
        for (User user : employees) {
            String searchableText = normalizeSearchText(String.join(" ",
                    valueOrEmpty(user.getFullName()),
                    valueOrEmpty(user.getEmail()),
                    valueOrEmpty(user.getPhone()),
                    valueOrEmpty(user.getPositionName())
            ), accentSensitive);

            boolean matchesAllTerms = true;
            for (String term : searchTerms) {
                if (!searchableText.contains(term)) {
                    matchesAllTerms = false;
                    break;
                }
            }
            if (matchesAllTerms) {
                result.add(user);
            }
        }
        return result;
    }

    private String normalizeSearchText(String value, boolean preserveDiacritics) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        String normalized = value;
        if (!preserveDiacritics) {
            normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}+", "")
                    .replace('đ', 'd')
                    .replace('Đ', 'D');
        }
        normalized = normalized.toLowerCase(Locale.ROOT);
        return normalized.trim().replaceAll("\\s+", " ");
    }

    private boolean containsVietnameseDiacritics(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        String withoutDiacritics = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
        return !value.equals(withoutDiacritics);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
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
        return sortEmployeesByName(employees, sort, null);
    }

    public List<User> sortEmployeesByName(List<User> employees, String sort, Integer managerUserId) {
        List<User> result = new ArrayList<>(employees);
        Comparator<User> nameComparator = (first, second) ->
                compareNamesFromLastWord(first.getFullName(), second.getFullName());
        if ("name_desc".equals(sort)) {
            nameComparator = nameComparator.reversed();
        }

        Comparator<User> comparator = Comparator
                .comparing((User user) -> managerUserId == null || user.getId() != managerUserId)
                .thenComparing(nameComparator);
        result.sort(comparator);
        return result;
    }

    private int compareNamesFromLastWord(String firstName, String secondName) {
        String[] firstWords = splitName(firstName);
        String[] secondWords = splitName(secondName);

        int firstIndex = firstWords.length - 1;
        int secondIndex = secondWords.length - 1;
        while (firstIndex >= 0 && secondIndex >= 0) {
            int comparison = String.CASE_INSENSITIVE_ORDER.compare(
                    firstWords[firstIndex],
                    secondWords[secondIndex]
            );
            if (comparison != 0) {
                return comparison;
            }
            firstIndex--;
            secondIndex--;
        }

        return Integer.compare(firstWords.length, secondWords.length);
    }

    private String[] splitName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new String[0];
        }
        return fullName.trim().split("\\s+");
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
            LEFT JOIN positions p ON u.position_id = p.id
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
        WHERE (u.full_name LIKE ?
               OR u.email LIKE ?
               OR u.phone LIKE ?
               OR r.name LIKE ?)
          AND r.name NOT IN ('SYSTEM ADMIN', 'BUSINESS ADMIN')
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
        User user = findById(userId);
        return user != null && PasswordUtil.verifyPassword(oldPassword, user.getPassword());
    }

    public List<User> getAllActiveUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, full_name, email, active FROM users WHERE active = 1 " +
                "AND role_id NOT IN (SELECT id FROM roles WHERE name IN ('SYSTEM ADMIN','BUSINESS ADMIN')) " +
                "ORDER BY full_name";
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
          AND u.role_id NOT IN (SELECT id FROM roles WHERE name IN ('BUSINESS ADMIN','SYSTEM ADMIN'))
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
                "JOIN positions p ON u.position_id = p.id WHERE u.id = ?";
        String getDeptNameSql = "SELECT name FROM departments WHERE id = ?";
        String getPositionIdSql = "SELECT id FROM positions WHERE name = ?";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkPositionSql)) {
                psCheck.setInt(1, userId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String posName = rs.getString("name");
                        if (posName != null && posName.toLowerCase().contains("manager")) {
                            return "ERROR_IS_MANAGER";
                        }
                    } else {
                        return "ERROR_USER_NOT_FOUND";
                    }
                }
            }

            String deptName = null;
            try (PreparedStatement psDept = conn.prepareStatement(getDeptNameSql)) {
                psDept.setInt(1, newDeptId);
                try (ResultSet rs = psDept.executeQuery()) {
                    if (rs.next()) deptName = rs.getString("name");
                }
            }

            String defaultPositionName;
            String roleName;
            if ("Human Resources".equalsIgnoreCase(deptName)) {
                defaultPositionName = "HR Staff";
                roleName = "HR_STAFF";
            } else if ("Finance".equalsIgnoreCase(deptName)) {
                defaultPositionName = "Payroll Staff";
                roleName = "PAYROLL_STAFF";
            } else {
                defaultPositionName = "Employee";
                roleName = "EMPLOYEE";
            }

            int positionId = -1;
            try (PreparedStatement psPos = conn.prepareStatement(getPositionIdSql)) {
                psPos.setString(1, defaultPositionName);
                try (ResultSet rs = psPos.executeQuery()) {
                    if (rs.next()) positionId = rs.getInt("id");
                }
            }
            if (positionId == -1) return "ERROR_FAILED";

            int roleId = getRoleIdByName(conn, roleName);
            if (roleId == -1) return "ERROR_FAILED";

            String updateSql = "UPDATE users SET department_id = ?, position_id = ?, role_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, newDeptId);
                psUpdate.setInt(2, positionId);
                psUpdate.setInt(3, roleId);
                psUpdate.setInt(4, userId);
                int rows = psUpdate.executeUpdate();
                if (rows > 0) return "SUCCESS";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR_SYSTEM";
        }
        return "ERROR_FAILED";
    }

    private int getRoleIdByName(Connection conn, String roleName) throws SQLException {
        String sql = "SELECT id FROM roles WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
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
        return findActiveUsersByDepartmentScope(departmentId, false);
    }

    public List<User> findActiveManagerCandidates(int departmentId) {
        return findActiveUsersByDepartmentScope(departmentId, true);
    }

    private List<User> findActiveUsersByDepartmentScope(int departmentId, boolean includeUnassigned) {
        List<User> users = new ArrayList<>();
        String departmentCondition = includeUnassigned
                ? "(u.department_id = ? OR u.department_id IS NULL)"
                : "u.department_id = ?";
        String sql = """
    SELECT u.id, u.full_name, u.email, u.phone, u.active,
           u.department_id, u.position_id,
           p.name AS position_name,
           d.name AS department_name
    FROM users u
    LEFT JOIN departments d ON d.id = u.department_id
    LEFT JOIN positions p ON p.id = u.position_id
    WHERE %s
      AND u.active = TRUE
      AND u.role_id NOT IN (SELECT id FROM roles WHERE name IN ('BUSINESS ADMIN','SYSTEM ADMIN'))
    ORDER BY u.department_id IS NULL, u.full_name
    """.formatted(departmentCondition);
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

    // Remove
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

    public List<User> getUnassignedUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, full_name FROM users " +
                "WHERE (department_id IS NULL OR department_id = 0) " +
                "AND active = 1 " +
                "AND role_id NOT IN (SELECT id FROM roles WHERE name IN ('BUSINESS ADMIN','SYSTEM ADMIN'))";
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
        String getDeptNameSql = "SELECT name FROM departments WHERE id = ?";
        String getPositionIdSql = "SELECT id FROM positions WHERE name = ?";
        String sql = "UPDATE users SET department_id = ?, position_id = ?, role_id = ?, active = 1 WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String deptName = null;
            try (PreparedStatement psDept = conn.prepareStatement(getDeptNameSql)) {
                psDept.setInt(1, deptId);
                try (ResultSet rs = psDept.executeQuery()) {
                    if (rs.next()) deptName = rs.getString("name");
                }
            }

            String defaultPositionName;
            String roleName;
            if ("Human Resources".equalsIgnoreCase(deptName)) {
                defaultPositionName = "HR Staff";
                roleName = "HR_STAFF";
            } else if ("Finance".equalsIgnoreCase(deptName)) {
                defaultPositionName = "Payroll Staff";
                roleName = "PAYROLL_STAFF";
            } else {
                defaultPositionName = "Employee";
                roleName = "EMPLOYEE";
            }

            int positionId = -1;
            try (PreparedStatement psPos = conn.prepareStatement(getPositionIdSql)) {
                psPos.setString(1, defaultPositionName);
                try (ResultSet rs = psPos.executeQuery()) {
                    if (rs.next()) positionId = rs.getInt("id");
                }
            }
            if (positionId == -1) {
                conn.rollback();
                return false;
            }

            int roleId = getRoleIdByName(conn, roleName);
            if (roleId == -1) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int userId : userIds) {
                    ps.setInt(1, deptId);
                    ps.setInt(2, positionId);
                    ps.setInt(3, roleId);
                    ps.setInt(4, userId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void clearDepartmentAndPosition(int userId) {
        String sql = "UPDATE users SET department_id = NULL, position_id = NULL, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int countUsersByRole(int roleId) {
        String sql = "SELECT COUNT(*) FROM users WHERE role_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int countUsersByDepartment(int departmentId) {
        String sql = "SELECT COUNT(*) FROM users WHERE department_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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

    public int countUsers(String keyword, Boolean active) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users u WHERE u.role_id <> (SELECT id FROM roles WHERE name = 'BUSINESS ADMIN')");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND u.full_name LIKE ?");
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

    public List<User> getUsersWithPaging(String keyword, Boolean active, String sortBy, String sortOrder, int offset, int limit) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT u.*, r.name AS role_name, d.name AS department_name, p.name AS position_name " +
                        "FROM users u " +
                        "JOIN roles r ON u.role_id = r.id " +
                        "LEFT JOIN departments d ON u.department_id = d.id " +
                        "LEFT JOIN positions p ON u.position_id = p.id " +
                        "WHERE u.role_id NOT IN (SELECT id FROM roles WHERE name IN ('BUSINESS ADMIN','SYSTEM ADMIN'))");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND u.full_name LIKE ?");
        }
        if (active != null) sql.append(" AND u.active = ?");

        if ("name".equals(sortBy)) {
            sql.append(" ORDER BY SUBSTRING_INDEX(TRIM(u.full_name), ' ', -1) ").append("asc".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC");
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
                while (rs.next()) list.add(mapResultSetToUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.trim().toLowerCase();
            String[][] map = {{"á", "a"}, {"à", "a"}, {"ả", "a"}, {"ã", "a"}, {"ạ", "a"}, {"ă", "a"}, {"ắ", "a"}, {"ằ", "a"}, {"ẳ", "a"}, {"ẵ", "a"}, {"ặ", "a"}, {"â", "a"}, {"ấ", "a"}, {"ầ", "a"}, {"ẩ", "a"}, {"ẫ", "a"}, {"ậ", "a"},
                    {"é", "e"}, {"è", "e"}, {"ẻ", "e"}, {"ẽ", "e"}, {"ẹ", "e"}, {"ê", "e"}, {"ế", "e"}, {"ề", "e"}, {"ể", "e"}, {"ễ", "e"}, {"ệ", "e"},
                    {"í", "i"}, {"ì", "i"}, {"ỉ", "i"}, {"ĩ", "i"}, {"ị", "i"},
                    {"ó", "o"}, {"ò", "o"}, {"ỏ", "o"}, {"õ", "o"}, {"ọ", "o"}, {"ô", "o"}, {"ố", "o"}, {"ồ", "o"}, {"ổ", "o"}, {"ỗ", "o"}, {"ộ", "o"}, {"ơ", "o"}, {"ớ", "o"}, {"ờ", "o"}, {"ở", "o"}, {"ỡ", "o"}, {"ợ", "o"},
                    {"ú", "u"}, {"ù", "u"}, {"ủ", "u"}, {"ũ", "u"}, {"ụ", "u"}, {"ư", "u"}, {"ứ", "u"}, {"ừ", "u"}, {"ử", "u"}, {"ữ", "u"}, {"ự", "u"},
                    {"ý", "y"}, {"ỳ", "y"}, {"ỷ", "y"}, {"ỹ", "y"}, {"ỵ", "y"}, {"đ", "d"}};

            list = list.stream().filter(u -> {
                String fullName = u.getFullName().toLowerCase();
                if (k.matches(".*[áàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđ].*")) {
                    return fullName.contains(k);
                }
                String nameNormalized = fullName;
                for (String[] pair : map) nameNormalized = nameNormalized.replace(pair[0], pair[1]);
                return nameNormalized.contains(k);
            }).collect(java.util.stream.Collectors.toList());
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

    public List<User> getUserByRole(String roleName) {
        List<User> list = new ArrayList<>();

        String sql = """
                        SELECT u.*, r.name AS role_name, d.name AS department_name, p.name AS position_name
                        FROM users u
                        LEFT JOIN roles r ON u.role_id = r.id
                        LEFT JOIN departments d ON u.department_id = d.id
                        LEFT JOIN positions p ON u.position_id = p.id
                        WHERE r.name = ? AND u.active = TRUE
                    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roleName);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setFullName(rs.getString("full_name"));
                    list.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getAllDeptManager() {
        List<User> list = new ArrayList<>();
        String sql = """
                SELECT u.*, r.name AS role_name, d.name AS department_name, p.name AS position_name
                FROM users u
                LEFT JOIN roles r ON u.role_id = r.id
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN positions p ON u.position_id = p.id
                WHERE r.name = 'DEPARTMENT_MANAGER' AND u.active = TRUE
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getUserByPosition(String positionName) {
        List<User> list = new ArrayList<>();

        String sql = "SELECT u.*, r.name AS role_name, d.name AS department_name, p.name AS position_name " +
                "from users u " +
                "JOIN roles r ON u.role_id = r.id\n" +
                "LEFT JOIN departments d ON u.department_id = d.id\n" +
                "LEFT JOIN positions p ON u.position_id = p.id " +
                "WHERE p.name = ? AND u.active = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, positionName);
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

    public String getPositionNameByUserId(int userId) {
        String sql = "SELECT p.name FROM positions p " +
                "JOIN users u ON u.position_id = p.id " +
                "WHERE u.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<User> getActiveUsersForTaskSelection() {
        List<User> list = new ArrayList<>();
        String sql = """
                SELECT u.*, r.name AS role_name, d.name AS department_name, p.name AS position_name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN positions p ON u.position_id = p.id
                WHERE u.active = TRUE
                ORDER BY u.full_name
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
