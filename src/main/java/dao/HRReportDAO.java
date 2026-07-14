package dao;

import model.HRReportDTO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HRReportDAO {

    public HRReportDTO getHRReportData(LocalDate targetDate, Integer departmentId) {
        HRReportDTO dto = new HRReportDTO();
        Map<String, Integer> contractTypeData = new HashMap<>();
        Map<String, Integer> departmentData = new HashMap<>();
        dto.setContractTypeData(contractTypeData);
        dto.setDepartmentData(departmentData);

        java.sql.Date sqlTargetDate = java.sql.Date.valueOf(targetDate);

        try (Connection conn = DBConnection.getConnection()) {

            /* =================================================================
             * SUB-QUERY ĐƯỢC CHUẨN HÓA (Gộp cả Hợp đồng hiện tại & Lịch sử cũ)
             * ================================================================= */
            String allContractsView =
                    " (SELECT user_id, contract_type, start_date, end_date FROM labor_contracts "
                            + "  UNION "
                            + "  SELECT user_id, contract_type, start_date, end_date FROM contract_history) lc ";

            /* =================================================================
             * QUERY 1: Lấy số liệu Tổng số, Giới tính, Loại hợp đồng, Chức vụ
             * ================================================================= */
            String summarySql =
                    "SELECT COUNT(DISTINCT u.id) as total, "
                            + "SUM(CASE WHEN u.gender = 'Male' THEN 1 ELSE 0 END) as males, "
                            + "SUM(CASE WHEN lc.contract_type IN ('INDEFINITE_TERM', 'FIXED_TERM', 'PART_TIME') THEN 1 ELSE 0 END) as regulars, "
                            + "SUM(CASE WHEN lc.contract_type = 'PROBATION' THEN 1 ELSE 0 END) as probations, "
                            + "SUM(CASE WHEN p.name LIKE '%MANAGER%' THEN 1 ELSE 0 END) as managers, "
                            + "SUM(CASE WHEN p.name NOT LIKE '%MANAGER%' THEN 1 ELSE 0 END) as employees "
                            + "FROM users u "
                            + "INNER JOIN " + allContractsView + " ON u.id = lc.user_id "
                            + "LEFT JOIN positions p ON u.position_id = p.id "
                            + "LEFT JOIN department_history dh ON u.id = dh.user_id "
                            + "  AND dh.start_date <= ? AND (dh.end_date IS NULL OR dh.end_date >= ?) "
                            + "WHERE lc.start_date <= ? "
                            + "  AND (lc.end_date IS NULL OR lc.end_date >= ?)";

            if (departmentId != null) {
                summarySql += " AND dh.department_id = ? ";
            }

            try (PreparedStatement ps = conn.prepareStatement(summarySql)) {
                ps.setDate(1, sqlTargetDate);
                ps.setDate(2, sqlTargetDate);
                ps.setDate(3, sqlTargetDate);
                ps.setDate(4, sqlTargetDate);
                if (departmentId != null) {
                    ps.setInt(5, departmentId);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int total = rs.getInt("total");
                        dto.setTotalEmployees(total);
                        dto.setMaleCount(rs.getInt("males"));
                        dto.setFemaleCount(total - rs.getInt("males"));
                        dto.setRegularCount(rs.getInt("regulars"));
                        dto.setProbationCount(rs.getInt("probations"));
                        dto.setManagerCount(rs.getInt("managers"));
                        dto.setEmployeeCount(rs.getInt("employees"));
                    }
                }
            }

            /* =================================================================
             * QUERY 2: Cơ cấu Loại Hợp Đồng
             * ================================================================= */
            String contractSql =
                    "SELECT lc.contract_type as type, COUNT(DISTINCT u.id) as count "
                            + "FROM users u "
                            + "INNER JOIN " + allContractsView + " ON u.id = lc.user_id "
                            + "WHERE lc.start_date <= ? "
                            + "  AND (lc.end_date IS NULL OR lc.end_date >= ?) "
                            + "GROUP BY lc.contract_type";

            try (PreparedStatement ps = conn.prepareStatement(contractSql)) {
                ps.setDate(1, sqlTargetDate);
                ps.setDate(2, sqlTargetDate);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        contractTypeData.put(rs.getString("type"), rs.getInt("count"));
                    }
                }
            }

            /* =================================================================
             * QUERY 3: Cơ cấu Phòng Ban
             * ================================================================= */
            String deptSql =
                    "SELECT d.name as dept_name, COUNT(DISTINCT u.id) as count "
                            + "FROM departments d "
                            + "LEFT JOIN users u ON d.id = u.department_id "
                            + "INNER JOIN " + allContractsView + " ON u.id = lc.user_id "
                            + "  AND lc.start_date <= ? AND (lc.end_date IS NULL OR lc.end_date >= ?) "
                            + "GROUP BY d.id, d.name";

            try (PreparedStatement ps = conn.prepareStatement(deptSql)) {
                ps.setDate(1, sqlTargetDate);
                ps.setDate(2, sqlTargetDate);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        departmentData.put(rs.getString("dept_name"), rs.getInt("count"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
}