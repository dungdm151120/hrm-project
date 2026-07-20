package dao;

import model.DeptEmployeeChangeDTO;
import model.HRReportDTO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
             * SUB-QUERY KHÔI PHỤC TRẠNG THÁI HỢP ĐỒNG TẠI MỐC TARGET_DATE
             * ================================================================= */
            String activeContractsSubQuery =
                    "(SELECT lc.user_id, " +
                            "        COALESCE(" +
                            "            (SELECT log.old_value FROM labor_contract_change_logs log " +
                            "             WHERE log.contract_id = lc.id " +
                            "               AND log.field_name = 'contract_type' " +
                            "               AND CAST(log.changed_at AS DATE) > ? " +
                            "             ORDER BY log.changed_at ASC LIMIT 1), " +
                            "            lc.contract_type" +
                            "        ) as contract_type, " +
                            "        COALESCE(" +
                            "            (SELECT STR_TO_DATE(log.old_value, '%Y-%m-%d') FROM labor_contract_change_logs log " +
                            "             WHERE log.contract_id = lc.id " +
                            "               AND log.field_name = 'start_date' " +
                            "               AND CAST(log.changed_at AS DATE) > ? " +
                            "             ORDER BY log.changed_at ASC LIMIT 1), " +
                            "            lc.start_date" +
                            "        ) as start_date, " +
                            "        COALESCE(" +
                            "            (SELECT STR_TO_DATE(log.old_value, '%Y-%m-%d') FROM labor_contract_change_logs log " +
                            "             WHERE log.contract_id = lc.id " +
                            "               AND log.field_name = 'end_date' " +
                            "               AND CAST(log.changed_at AS DATE) > ? " +
                            "             ORDER BY log.changed_at ASC LIMIT 1), " +
                            "            lc.end_date" +
                            "        ) as end_date " +
                            " FROM labor_contracts lc) lc ";

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
                            + "INNER JOIN " + activeContractsSubQuery + " ON u.id = lc.user_id "
                            + "LEFT JOIN positions p ON u.position_id = p.id "
                            + "WHERE lc.start_date <= ? "
                            + "  AND (lc.end_date IS NULL OR lc.end_date >= ?) ";

            if (departmentId != null) {
                summarySql += " AND u.department_id = ? ";
            }

            try (PreparedStatement ps = conn.prepareStatement(summarySql)) {
                ps.setDate(1, sqlTargetDate);
                ps.setDate(2, sqlTargetDate);
                ps.setDate(3, sqlTargetDate);
                ps.setDate(4, sqlTargetDate);
                ps.setDate(5, sqlTargetDate);

                if (departmentId != null) {
                    ps.setInt(6, departmentId);
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
                    "SELECT lc.contract_type as type, COUNT(DISTINCT lc.user_id) as count "
                            + "FROM " + activeContractsSubQuery
                            + "WHERE lc.start_date <= ? "
                            + "  AND (lc.end_date IS NULL OR lc.end_date >= ?) "
                            + "GROUP BY lc.contract_type";

            try (PreparedStatement ps = conn.prepareStatement(contractSql)) {
                ps.setDate(1, sqlTargetDate);
                ps.setDate(2, sqlTargetDate);
                ps.setDate(3, sqlTargetDate);
                ps.setDate(4, sqlTargetDate);
                ps.setDate(5, sqlTargetDate);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        contractTypeData.put(rs.getString("type"), rs.getInt("count"));
                    }
                }
            }

            /* =================================================================
             * QUERY 3: Cơ cấu Phòng Ban (Đã sửa lỗi không cập nhật sau khi Move)
             * ================================================================= */
            String deptSql =
                    "SELECT d.name as dept_name, COUNT(DISTINCT lc.user_id) as count "
                            + "FROM departments d "
                            + "LEFT JOIN ("
                            + "    SELECT u.id as user_id, "
                            + "           COALESCE("
                            + "               (SELECT active.department_id "
                            + "                FROM ("
                            + "                    SELECT user_id, department_id, start_date, end_date FROM department_history "
                            + "                    UNION ALL "
                            + "                    SELECT user_id, department_id, start_date, COALESCE(end_date, '9999-12-31') as end_date FROM department_after_update"
                            + "                ) active "
                            + "                WHERE active.user_id = u.id "
                            + "                  AND active.start_date <= ? "
                            + "                  AND active.end_date >= ? "
                            + "                ORDER BY active.start_date DESC LIMIT 1)," // Lấy bản ghi mới nhất khớp với mốc thời gian
                            + "               u.department_id" // Nếu chưa từng dịch chuyển, lấy phòng mặc định trong users
                            + "           ) as active_dept_id "
                            + "    FROM users u"
                            + ") member ON d.id = member.active_dept_id "
                            + "INNER JOIN " + activeContractsSubQuery + " ON member.user_id = lc.user_id "
                            + "WHERE lc.start_date <= ? "
                            + "  AND (lc.end_date IS NULL OR lc.end_date >= ?) "
                            + "GROUP BY d.id, d.name";

            try (PreparedStatement ps = conn.prepareStatement(deptSql)) {
                // 1. Tham số lọc mốc thời gian để tìm phòng ban hoạt động của nhân viên tại targetDate
                ps.setDate(1, sqlTargetDate); // active.start_date <= targetDate
                ps.setDate(2, sqlTargetDate); // active.end_date >= targetDate

                // 2. Tham số khôi phục lịch sử hợp đồng (activeContractsSubQuery)
                ps.setDate(3, sqlTargetDate); // contract_type log
                ps.setDate(4, sqlTargetDate); // start_date log
                ps.setDate(5, sqlTargetDate); // end_date log

                // 3. Tham số lọc thời gian cho hợp đồng hoạt động (lc)
                ps.setDate(6, sqlTargetDate); // lc.start_date <= targetDate
                ps.setDate(7, sqlTargetDate); // lc.end_date >= targetDate

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

    public List<DeptEmployeeChangeDTO> getDeptEmployeeChanges(LocalDate startDate, LocalDate endDate) {
        List<DeptEmployeeChangeDTO> list = new ArrayList<>();

        String sql =
                "SELECT " +
                        "    d.id AS department_id, " +
                        "    d.name AS department_name, " +
                        "    COALESCE(e_in.in_count, 0) AS in_count, " +
                        "    COALESCE(e_out.out_count, 0) AS out_count " +
                        "FROM departments d " +
                        "LEFT JOIN ( " +
                        "    /* Đếm số người CHUYỂN ĐẾN (start_date) trong khoảng lọc */ " +
                        "    SELECT department_id, COUNT(*) AS in_count " +
                        "    FROM ( " +
                        "        SELECT department_id, start_date FROM department_history WHERE start_date BETWEEN ? AND ? " +
                        "        UNION ALL " +
                        "        SELECT department_id, start_date FROM department_after_update WHERE start_date BETWEEN ? AND ? " +
                        "    ) t_in GROUP BY department_id " +
                        ") e_in ON d.id = e_in.department_id " +
                        "LEFT JOIN ( " +
                        "    /* Đếm số người RỜI ĐI (end_date) trong khoảng lọc */ " +
                        "    SELECT department_id, COUNT(*) AS out_count " +
                        "    FROM ( " +
                        "        SELECT department_id, end_date FROM department_history WHERE end_date BETWEEN ? AND ? " +
                        "        UNION ALL " +
                        "        SELECT department_id, end_date FROM department_after_update WHERE end_date BETWEEN ? AND ? " +
                        "    ) t_out GROUP BY department_id " +
                        ") e_out ON d.id = e_out.department_id " +
                        "ORDER BY d.id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            java.sql.Date sqlStart = java.sql.Date.valueOf(startDate);
            java.sql.Date sqlEnd = java.sql.Date.valueOf(endDate);

            ps.setDate(1, sqlStart);
            ps.setDate(2, sqlEnd);
            ps.setDate(3, sqlStart);
            ps.setDate(4, sqlEnd);
            ps.setDate(5, sqlStart);
            ps.setDate(6, sqlEnd);
            ps.setDate(7, sqlStart);
            ps.setDate(8, sqlEnd);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String deptName = rs.getString("department_name");
                    int inCount = rs.getInt("in_count");
                    int outCount = rs.getInt("out_count");

                    list.add(new DeptEmployeeChangeDTO(deptName, inCount, outCount));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}