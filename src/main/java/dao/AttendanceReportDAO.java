package dao;

import model.AttendanceReportRowDTO;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceReportDAO {
    
    public List<AttendanceReportRowDTO> generateAttendanceReport(LocalDate startDate, LocalDate endDate, Integer departmentId) {
        List<AttendanceReportRowDTO> list = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder("""
            SELECT 
                u.id AS user_id,
                u.employee_code,
                u.full_name,
                p.name AS position_name,
                d.name AS department_name,
                COALESCE(SUM(CASE WHEN ar.status IN ('ON_TIME', 'LATE', 'EARLY_LEAVE', 'LATE_AND_EARLY_LEAVE', 'FORGOT_CHECK_IN', 'FORGOT_CHECK_OUT') THEN 1 ELSE 0 END), 0) AS present_days,
                COALESCE(SUM(CASE WHEN ar.status = 'ABSENT' THEN 1 ELSE 0 END), 0) AS absent_days,
                COALESCE(SUM(CASE WHEN ar.status IN ('LATE', 'LATE_AND_EARLY_LEAVE') OR ar.late_hours > 0 THEN 1 ELSE 0 END), 0) AS late_days,
                COALESCE(SUM(CASE WHEN ar.status IN ('EARLY_LEAVE', 'LATE_AND_EARLY_LEAVE') OR ar.early_leave_hours > 0 THEN 1 ELSE 0 END), 0) AS early_leave_days,
                COALESCE(SUM(CASE WHEN ar.status = 'FORGOT_CHECK_IN' THEN 1 ELSE 0 END), 0) AS forgot_check_in_days,
                COALESCE(SUM(CASE WHEN ar.status = 'FORGOT_CHECK_OUT' THEN 1 ELSE 0 END), 0) AS forgot_check_out_days,
                COALESCE(SUM(ar.total_work_hours), 0.0) AS total_work_hours,
                COALESCE(SUM(ar.overtime_hours), 0.0) AS total_overtime_hours,
                COALESCE(SUM(CASE WHEN ar.status IN ('ON_LEAVE', 'SICK_LEAVE') THEN 1 ELSE 0 END), 0) AS leave_days,
                COALESCE(ot_reg.registered_ot_hours, 0.0) AS registered_ot_hours
            FROM users u
            LEFT JOIN positions p ON u.position_id = p.id
            LEFT JOIN departments d ON u.department_id = d.id
            LEFT JOIN attendance_records ar ON u.id = ar.user_id AND ar.work_date BETWEEN ? AND ?
            LEFT JOIN (
                SELECT op.user_id, COALESCE(SUM(otr.total_hours), 0.0) AS registered_ot_hours
                FROM overtime_participants op
                JOIN overtime_requests otr ON op.overtime_request_id = otr.id
                JOIN requests r ON otr.request_id = r.id
                WHERE otr.overtime_date BETWEEN ? AND ?
                  AND r.status IN ('APPROVED', 'CONFIRMED')
                GROUP BY op.user_id
            ) ot_reg ON u.id = ot_reg.user_id
            WHERE u.active = TRUE
        """);
        
        if (departmentId != null && departmentId > 0) {
            sql.append(" AND u.department_id = ?");
        }
        
        sql.append("""
            GROUP BY u.id, u.employee_code, u.full_name, p.name, d.name, ot_reg.registered_ot_hours
            ORDER BY d.name, u.employee_code
        """);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int idx = 1;
            ps.setDate(idx++, Date.valueOf(startDate));
            ps.setDate(idx++, Date.valueOf(endDate));
            ps.setDate(idx++, Date.valueOf(startDate));
            ps.setDate(idx++, Date.valueOf(endDate));
            
            if (departmentId != null && departmentId > 0) {
                ps.setInt(idx++, departmentId);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceReportRowDTO row = new AttendanceReportRowDTO();
                    row.setEmployeeId(rs.getInt("user_id"));
                    row.setEmployeeCode(rs.getString("employee_code"));
                    row.setEmployeeName(rs.getString("full_name"));
                    row.setPositionName(rs.getString("position_name") != null ? rs.getString("position_name") : "N/A");
                    row.setDepartmentName(rs.getString("department_name") != null ? rs.getString("department_name") : "N/A");
                    row.setPresentDays(rs.getInt("present_days"));
                    row.setAbsentDays(rs.getInt("absent_days"));
                    row.setLateDays(rs.getInt("late_days"));
                    row.setEarlyLeaveDays(rs.getInt("early_leave_days"));
                    row.setForgotCheckInDays(rs.getInt("forgot_check_in_days"));
                    row.setForgotCheckOutDays(rs.getInt("forgot_check_out_days"));
                    row.setTotalWorkHours(rs.getDouble("total_work_hours"));
                    row.setTotalOvertimeHours(rs.getDouble("total_overtime_hours"));
                    row.setLeaveDays(rs.getDouble("leave_days"));
                    row.setRegisteredOvertimeHours(rs.getDouble("registered_ot_hours"));
                    
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
}
