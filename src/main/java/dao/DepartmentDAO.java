package dao;

import model.Department;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public Department getDepartmentById(int id) {
        String sql = "SELECT * FROM departments WHERE id = ?";
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
    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                departments.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }
    public boolean updateManager(int deptId, int managerUserId) {
        String sql = "UPDATE departments SET manager_user_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerUserId);
            ps.setInt(2, deptId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean assignManager(int departmentId, int newManagerId, Integer currentManagerId,
                                 Integer oldManagerPositionId, int newManagerPositionId) {
        String updateOldManagerSql =
                "UPDATE users SET position_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        String updateNewManagerSql = """
                UPDATE users
                SET department_id = ?, position_id = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                  AND active = TRUE
                  AND (department_id = ? OR department_id IS NULL)
                """;
        String updateDepartmentSql = """
                UPDATE departments
                SET manager_user_id = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (currentManagerId != null) {
                    try (PreparedStatement ps = conn.prepareStatement(updateOldManagerSql)) {
                        if (oldManagerPositionId == null) {
                            ps.setNull(1, Types.INTEGER);
                        } else {
                            ps.setInt(1, oldManagerPositionId);
                        }
                        ps.setInt(2, currentManagerId);
                        if (ps.executeUpdate() != 1) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(updateNewManagerSql)) {
                    ps.setInt(1, departmentId);
                    ps.setInt(2, newManagerPositionId);
                    ps.setInt(3, newManagerId);
                    ps.setInt(4, departmentId);
                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(updateDepartmentSql)) {
                    ps.setInt(1, newManagerId);
                    ps.setInt(2, departmentId);
                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Lấy toàn bộ dept ngoại trừ dept hiện tại
    public List<Department> getDepartmentsExcept(int excludeDeptId) {
        List<Department> list = new ArrayList<>();

        String sql = "SELECT * FROM departments d WHERE d.active = true AND d.id != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, excludeDeptId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Department d = new Department();
                    d.setId(rs.getInt("id"));
                    d.setName(rs.getString("name"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean removeManager(int deptId) {
        String sql = "UPDATE departments SET manager_user_id = NULL, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Department getDepartmentByIdWithManager(int id) {
        String sql = "SELECT d.*, u.full_name AS manager_name FROM departments d " +
                "LEFT JOIN users u ON d.manager_user_id = u.id WHERE d.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowWithManager(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean toggleStatus(int id, boolean active) {
        String sql = "UPDATE departments SET active = ? WHERE id = ?";
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

    public boolean updateDepartment(int id, String name, String description, boolean active) {
        String sql = "UPDATE departments SET name = ?, description = ?, active = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setBoolean(3, active);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int addDepartment(Department department) {
        String sqlDept = "INSERT INTO departments (name, description, manager_user_id, active) VALUES (?, ?, ?, ?)";
        // SQL để thêm quan hệ phòng ban - vị trí
        String sqlDeptPos = "INSERT INTO department_positions (department_id, position_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int newDeptId = -1;
            // 1. Thêm phòng ban
            try (PreparedStatement psDept = conn.prepareStatement(sqlDept, Statement.RETURN_GENERATED_KEYS)) {
                psDept.setString(1, department.getName());
                psDept.setString(2, department.getDescription());
                if (department.getManagerUserId() != null) {
                    psDept.setInt(3, department.getManagerUserId());
                } else {
                    psDept.setNull(3, Types.INTEGER);
                }
                psDept.setBoolean(4, department.isActive());
                psDept.executeUpdate();

                try (ResultSet rs = psDept.getGeneratedKeys()) {
                    if (rs.next()) newDeptId = rs.getInt(1);
                }
            }

            if (newDeptId != -1) {
                // 2. Lấy ID của 'Employee' và 'Department Manager'
                int empPosId = getPositionIdByName(conn, "Employee");
                int mgrPosId = getPositionIdByName(conn, "Department Manager");

                // 3. Gán vị trí cho phòng ban mới
                try (PreparedStatement psPos = conn.prepareStatement(sqlDeptPos)) {
                    // Gán Employee
                    psPos.setInt(1, newDeptId);
                    psPos.setInt(2, empPosId);
                    psPos.executeUpdate();

                    // Gán Department Manager
                    psPos.setInt(1, newDeptId);
                    psPos.setInt(2, mgrPosId);
                    psPos.executeUpdate();
                }
            }

            conn.commit();
            return newDeptId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Lấy ID của position bằng Position Name
    private int getPositionIdByName(Connection conn, String positionName) throws SQLException {
        String sql = "SELECT id FROM positions WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, positionName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
    }


    public boolean isManager(int userId) {
        String sql = "SELECT COUNT(*) FROM departments WHERE manager_user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Department> getDepartmentsSortedByName(boolean ascending) {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT d.*, u.full_name AS manager_name FROM departments d " +
                "LEFT JOIN users u ON d.manager_user_id = u.id " +
                "ORDER BY d.name " + (ascending ? "ASC" : "DESC");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRowWithManager(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Department> getDepartmentsSortedByMemberCount(boolean mostFirst) {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT d.*, u.full_name AS manager_name, COUNT(usr.id) AS member_count " +
                "FROM departments d " +
                "LEFT JOIN users u ON d.manager_user_id = u.id " +
                "LEFT JOIN users usr ON usr.department_id = d.id " +
                "GROUP BY d.id " +
                "ORDER BY member_count " + (mostFirst ? "DESC" : "ASC");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRowWithManager(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalDepartments(String keyword, Boolean active) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM departments d WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty()) sql.append(" AND d.name LIKE ?");
        if (active != null) sql.append(" AND d.active = ?");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) ps.setString(idx++, "%" + keyword.trim() + "%");
            if (active != null) ps.setBoolean(idx++, active);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Department> getDepartmentsWithPaging(String keyword, Boolean active, String sortBy, String sortOrder, int offset, int limit) {
        List<Department> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT d.*, u.full_name AS manager_name FROM departments d " +
                        "LEFT JOIN users u ON d.manager_user_id = u.id WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty()) sql.append(" AND d.name LIKE ?");
        if (active != null) sql.append(" AND d.active = ?");
        if ("name".equals(sortBy)) {
            sql.append(" ORDER BY d.name ").append("asc".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC");
        } else {
            sql.append(" ORDER BY d.id");
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
                while (rs.next()) list.add(mapRowWithManager(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean isDepartmentActive(int deptId) {
        String sql = "SELECT active FROM departments WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("active");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setId(rs.getInt("id"));
        dept.setName(rs.getString("name"));
        dept.setDescription(rs.getString("description"));
        int managerUserId = rs.getInt("manager_user_id");
        if (!rs.wasNull()) dept.setManagerUserId(managerUserId);
        dept.setActive(rs.getBoolean("active"));
        dept.setCreatedAt(rs.getTimestamp("created_at"));
        dept.setUpdatedAt(rs.getTimestamp("updated_at"));
        return dept;
    }

    public boolean assignDefaultEmployeePosition(int departmentId) {
        String getPosIdSql = "SELECT id FROM positions WHERE name = 'Employee'";
        String insertSql = "INSERT INTO department_positions (department_id, position_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement getPs = conn.prepareStatement(getPosIdSql)) {
            ResultSet rs = getPs.executeQuery();
            if (rs.next()) {
                int employeePosId = rs.getInt("id");
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setInt(1, departmentId);
                    insertPs.setInt(2, employeePosId);
                    return insertPs.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private Department mapRowWithManager(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setId(rs.getInt("id"));
        dept.setName(rs.getString("name"));
        dept.setDescription(rs.getString("description"));
        int mgrId = rs.getInt("manager_user_id");
        if (!rs.wasNull()) dept.setManagerUserId(mgrId);
        dept.setManagerName(rs.getString("manager_name"));
        dept.setActive(rs.getBoolean("active"));
        dept.setCreatedAt(rs.getTimestamp("created_at"));
        dept.setUpdatedAt(rs.getTimestamp("updated_at"));
        return dept;
    }
}
