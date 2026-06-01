package dao;

import model.Department;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

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
    public Department getDepartmentByIdWithManager(int id) {
        String sql = "SELECT d.*, u.full_name AS manager_name FROM departments d " +
                "LEFT JOIN users u ON d.manager_user_id = u.id WHERE d.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowWithManager(rs);
                }
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

    public boolean updateDepartmentInfo(int id, String name, String description) {
        String sql = "UPDATE departments SET name = ?, description = ? WHERE id = ?";
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

    public int addDepartment(Department department) {
        String sql = "INSERT INTO departments (name, description, manager_user_id, active) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, department.getName());
            ps.setString(2, department.getDescription());
            if (department.getManagerUserId() != null) {
                ps.setInt(3, department.getManagerUserId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setBoolean(4, department.isActive());
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Department> searchDepartments(String keyword, Boolean active) {
        List<Department> departments = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM departments WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
        }
        if (active != null) {
            sql.append(" AND active = ?");
        }
        sql.append(" ORDER BY id");
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
                while (rs.next()) {
                    departments.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setId(rs.getInt("id"));
        dept.setName(rs.getString("name"));
        dept.setDescription(rs.getString("description"));
        int managerUserId = rs.getInt("manager_user_id");
        if (!rs.wasNull()) {
            dept.setManagerUserId(managerUserId);
        }
        dept.setActive(rs.getBoolean("active"));
        dept.setCreatedAt(rs.getTimestamp("created_at"));
        dept.setUpdatedAt(rs.getTimestamp("updated_at"));
        return dept;
    }
    public List<Department> getAllDepartmentsWithManager() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT d.*, u.full_name AS manager_name FROM departments d " +
                "LEFT JOIN users u ON d.manager_user_id = u.id ORDER BY d.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowWithManager(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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

    public List<Department> searchDepartmentsWithManager(String keyword, Boolean active) {
        List<Department> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT d.*, u.full_name AS manager_name FROM departments d " +
                        "LEFT JOIN users u ON d.manager_user_id = u.id WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND d.name LIKE ?");
        }
        if (active != null) {
            sql.append(" AND d.active = ?");
        }
        sql.append(" ORDER BY d.id");
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
                while (rs.next()) {
                    list.add(mapRowWithManager(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isManager(int userId) {
        String sql = "SELECT COUNT(*) FROM departments WHERE manager_user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Nếu COUNT(*) > 0 nghĩa là user này đang là Manager
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
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
        if (!rs.wasNull()) {
            dept.setManagerUserId(mgrId);
        }
        dept.setManagerName(rs.getString("manager_name")); // có thể null
        dept.setActive(rs.getBoolean("active"));
        dept.setCreatedAt(rs.getTimestamp("created_at"));
        dept.setUpdatedAt(rs.getTimestamp("updated_at"));
        return dept;
    }

}
