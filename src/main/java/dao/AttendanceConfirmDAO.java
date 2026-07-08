package dao;

import util.DBConnection;
import model.DepartmentConfirmStatusDTO;
import model.AttendanceConfirmedSummaryDTO;
import model.AttendanceConfirmedDetailDTO;
import model.AttendanceConfirmedMonthOverviewDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceConfirmDAO {

    public List<DepartmentConfirmStatusDTO> getDepartmentLockStatuses(int month, int year) {
        List<DepartmentConfirmStatusDTO> list = new ArrayList<>();
        String sql = "SELECT d.id AS department_id, d.name AS department_name, " +
                "d.manager_user_id, u.full_name AS manager_name, " +
                "log.action AS last_action, log.created_at AS confirmed_at " +
                "FROM departments d " +
                "LEFT JOIN users u ON d.manager_user_id = u.id " +
                "LEFT JOIN (" +
                "  SELECT department_id, action, created_at FROM attendance_lock_log " +
                "  WHERE month = ? AND year = ? AND action = 'DEPT_CONFIRM' " +
                "  AND id IN (SELECT MAX(id) FROM attendance_lock_log WHERE month = ? AND year = ? AND action = 'DEPT_CONFIRM' GROUP BY department_id)" +
                ") log ON d.id = log.department_id " +
                "WHERE d.active = TRUE " +
                "ORDER BY d.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ps.setInt(3, month);
            ps.setInt(4, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartmentConfirmStatusDTO dto = new DepartmentConfirmStatusDTO();
                    dto.setDepartmentId(rs.getInt("department_id"));
                    dto.setDepartmentName(rs.getString("department_name"));
                    dto.setManagerUserId(rs.getInt("manager_user_id"));
                    dto.setManagerName(rs.getString("manager_name"));
                    
                    String action = rs.getString("last_action");
                    if ("DEPT_CONFIRM".equals(action)) {
                        dto.setStatus("CONFIRMED");
                        dto.setConfirmedAt(rs.getTimestamp("confirmed_at"));
                    } else {
                        dto.setStatus("PENDING");
                    }
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getOverallStatus(int month, int year) {
        String sql = "SELECT action FROM attendance_lock_log WHERE month = ? AND year = ? AND action IN ('HR_SEND', 'BUSINESS_APPROVE') ORDER BY id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String action = rs.getString("action");
                    if ("HR_SEND".equals(action)) return "HR_SENT";
                    if ("BUSINESS_APPROVE".equals(action)) return "APPROVED";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "PENDING";
    }

    public void logAction(int month, int year, String action, int userId, Integer departmentId, String note) {
        String sql = "INSERT INTO attendance_lock_log (month, year, action, user_id, department_id, note) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ps.setString(3, action);
            ps.setInt(4, userId);
            if (departmentId != null) {
                ps.setInt(5, departmentId);
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, note);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createSnapshot(int month, int year, int businessAdminId) {
        String sql = "INSERT INTO attendance_snapshot " +
                "(user_id, work_date, check_in, check_out, total_work_hours, overtime_hours, late_hours, early_leave_hours, status, note, snapshot_month, snapshot_year, confirmed_by_dept, confirmed_at_dept, confirmed_by_hr, confirmed_at_hr, confirmed_by_business, confirmed_at_business) " +
                "SELECT ar.user_id, ar.work_date, ar.check_in, ar.check_out, ar.total_work_hours, ar.overtime_hours, ar.late_hours, ar.early_leave_hours, ar.status, ar.note, ?, ?, " +
                "dept_log.user_id, dept_log.created_at, " +
                "hr_log.user_id, hr_log.created_at, " +
                "?, NOW() " +
                "FROM attendance_records ar " +
                "JOIN users u ON ar.user_id = u.id " +
                "LEFT JOIN (" +
                "  SELECT department_id, user_id, created_at FROM attendance_lock_log " +
                "  WHERE month = ? AND year = ? AND action = 'DEPT_CONFIRM' " +
                "  AND id IN (SELECT MAX(id) FROM attendance_lock_log WHERE month = ? AND year = ? AND action = 'DEPT_CONFIRM' GROUP BY department_id)" +
                ") dept_log ON u.department_id = dept_log.department_id " +
                "LEFT JOIN (" +
                "  SELECT user_id, created_at FROM attendance_lock_log WHERE month = ? AND year = ? AND action = 'HR_SEND' ORDER BY id DESC LIMIT 1" +
                ") hr_log ON 1=1 " +
                "WHERE MONTH(ar.work_date) = ? AND YEAR(ar.work_date) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            ps.setInt(index++, month);
            ps.setInt(index++, year);
            ps.setInt(index++, businessAdminId);
            ps.setInt(index++, month);
            ps.setInt(index++, year);
            ps.setInt(index++, month);
            ps.setInt(index++, year);
            ps.setInt(index++, month);
            ps.setInt(index++, year);
            ps.setInt(index++, month);
            ps.setInt(index++, year);
            
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<AttendanceConfirmedSummaryDTO> getConfirmedMonths(int year, Integer departmentId) {
        List<AttendanceConfirmedSummaryDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT s.snapshot_month AS month, s.snapshot_year AS year, " +
            "MAX(s.confirmed_at_business) AS confirmed_at, " +
            "(SELECT u2.full_name FROM users u2 WHERE u2.id = MAX(s.confirmed_by_business)) AS confirmed_by, " +
            "COUNT(DISTINCT s.user_id) AS employee_count, " +
            "SUM(s.total_work_hours) AS total_hours " +
            "FROM attendance_snapshot s " +
            "JOIN users emp ON s.user_id = emp.id " +
            "WHERE s.snapshot_year = ? "
        );

        if (departmentId != null) {
            sql.append("AND emp.department_id = ? ");
        }

        sql.append("GROUP BY s.snapshot_month, s.snapshot_year ORDER BY s.snapshot_month DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            ps.setInt(1, year);
            if (departmentId != null) {
                ps.setInt(2, departmentId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceConfirmedSummaryDTO dto = new AttendanceConfirmedSummaryDTO();
                    dto.setMonth(rs.getInt("month"));
                    dto.setYear(rs.getInt("year"));
                    dto.setConfirmedAt(rs.getTimestamp("confirmed_at"));
                    dto.setConfirmedBy(rs.getString("confirmed_by"));
                    dto.setEmployeeCount(rs.getInt("employee_count"));
                    dto.setTotalHours(rs.getDouble("total_hours"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public AttendanceConfirmedMonthOverviewDTO getConfirmedMonthOverview(int month, int year, Integer departmentId) {
        AttendanceConfirmedMonthOverviewDTO overview = new AttendanceConfirmedMonthOverviewDTO();
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(DISTINCT s.user_id) AS total_employees, " +
            "COUNT(s.work_date) AS total_work_days, " +
            "SUM(s.total_work_hours) AS total_work_hours, " +
            "SUM(s.overtime_hours) AS total_overtime_hours " +
            "FROM attendance_snapshot s " +
            "JOIN users emp ON s.user_id = emp.id " +
            "WHERE s.snapshot_month = ? AND s.snapshot_year = ? "
        );

        if (departmentId != null) {
            sql.append("AND emp.department_id = ? ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            ps.setInt(1, month);
            ps.setInt(2, year);
            if (departmentId != null) {
                ps.setInt(3, departmentId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    overview.setTotalEmployees(rs.getInt("total_employees"));
                    overview.setTotalWorkDays(rs.getInt("total_work_days"));
                    overview.setTotalWorkHours(rs.getDouble("total_work_hours"));
                    overview.setTotalOvertimeHours(rs.getDouble("total_overtime_hours"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return overview;
    }

    public int getConfirmedDetailsCount(int month, int year, String searchQuery, Integer departmentId) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(DISTINCT s.user_id) " +
            "FROM attendance_snapshot s " +
            "JOIN users emp ON s.user_id = emp.id " +
            "WHERE s.snapshot_month = ? AND s.snapshot_year = ? "
        );

        if (departmentId != null) {
            sql.append("AND emp.department_id = ? ");
        }
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (emp.employee_code LIKE ? OR emp.full_name LIKE ?) ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            ps.setInt(paramIndex++, month);
            ps.setInt(paramIndex++, year);
            
            if (departmentId != null) {
                ps.setInt(paramIndex++, departmentId);
            }
            
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String likeQuery = "%" + searchQuery.trim() + "%";
                ps.setString(paramIndex++, likeQuery);
                ps.setString(paramIndex++, likeQuery);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<AttendanceConfirmedDetailDTO> getConfirmedDetails(int month, int year, String searchQuery, Integer departmentId, int offset, int limit) {
        List<AttendanceConfirmedDetailDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT emp.id AS employee_id, emp.employee_code, emp.full_name AS employee_name, " +
            "d.name AS department_name, " +
            "COUNT(s.work_date) AS work_days, " +
            "SUM(s.total_work_hours) AS total_hours, " +
            "SUM(s.overtime_hours) AS overtime_hours " +
            "FROM attendance_snapshot s " +
            "JOIN users emp ON s.user_id = emp.id " +
            "LEFT JOIN departments d ON emp.department_id = d.id " +
            "WHERE s.snapshot_month = ? AND s.snapshot_year = ? "
        );

        if (departmentId != null) {
            sql.append("AND emp.department_id = ? ");
        }
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (emp.employee_code LIKE ? OR emp.full_name LIKE ?) ");
        }

        sql.append("GROUP BY emp.id, emp.employee_code, emp.full_name, d.name ");
        sql.append("ORDER BY d.name, emp.employee_code ");
        sql.append("LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            ps.setInt(paramIndex++, month);
            ps.setInt(paramIndex++, year);
            
            if (departmentId != null) {
                ps.setInt(paramIndex++, departmentId);
            }
            
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String likeQuery = "%" + searchQuery.trim() + "%";
                ps.setString(paramIndex++, likeQuery);
                ps.setString(paramIndex++, likeQuery);
            }
            
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceConfirmedDetailDTO dto = new AttendanceConfirmedDetailDTO();
                    dto.setEmployeeId(rs.getInt("employee_id"));
                    dto.setEmployeeCode(rs.getString("employee_code"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setDepartmentName(rs.getString("department_name"));
                    dto.setWorkDays(rs.getInt("work_days"));
                    dto.setTotalHours(rs.getDouble("total_hours"));
                    dto.setOvertimeHours(rs.getDouble("overtime_hours"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
