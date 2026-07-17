package dao;

import model.SalaryReportRowDTO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class SalaryReportDAO {

    public List<SalaryReportRowDTO> generateSalaryReport(String groupBy, YearMonth startPeriod, YearMonth endPeriod) {
        List<SalaryReportRowDTO> rows = new ArrayList<>();

        String selectGroup;
        String selectDepartment;
        String groupClause;
        String orderClause;

        switch (groupBy) {
            case "employee" -> {
                selectGroup = "u.full_name";
                selectDepartment = "COALESCE(d.name, 'N/A')";
                groupClause = "u.id, u.full_name, d.name";
                orderClause = "u.full_name ASC";
            }
            case "department" -> {
                selectGroup = "COALESCE(d.name, 'N/A')";
                selectDepartment = "COALESCE(d.name, 'N/A')";
                groupClause = "d.id, d.name";
                orderClause = "d.name ASC";
            }
            case "position" -> {
                selectGroup = "CONCAT(COALESCE(pos.name, 'N/A'), ' (', COALESCE(d.name, 'N/A'), ')')";
                selectDepartment = "COALESCE(d.name, 'N/A')";
                groupClause = "pos.id, pos.name, d.id, d.name";
                orderClause = "pos.name ASC, d.name ASC";
            }
            default -> {
                selectGroup = "CONCAT(COALESCE(pos.name, 'N/A'), ' (', COALESCE(d.name, 'N/A'), ')')";
                selectDepartment = "COALESCE(d.name, 'N/A')";
                groupClause = "pos.id, pos.name, d.id, d.name";
                orderClause = "pos.name ASC, d.name ASC";
            }
        }

        String sql = """
            SELECT
                %s AS group_name,
                %s AS department_name,
                COUNT(DISTINCT p.user_id) AS employee_count,
                COALESCE(SUM(p.total_income), 0) AS workday_income,
                COALESCE(SUM(p.bonus), 0) AS product_income,
                COALESCE(SUM(p.overtime_pay), 0) AS overtime_income,
                COALESCE(SUM(p.net_pay), 0) AS total_income
            FROM payrolls p
            JOIN users u ON p.user_id = u.id
            LEFT JOIN departments d ON u.department_id = d.id
            LEFT JOIN positions pos ON u.position_id = pos.id
            WHERE ((p.year * 12) + p.month) BETWEEN ? AND ?
            GROUP BY %s
            ORDER BY %s
            """.formatted(selectGroup, selectDepartment, groupClause, orderClause);

        int startKey = startPeriod.getYear() * 12 + startPeriod.getMonthValue();
        int endKey = endPeriod.getYear() * 12 + endPeriod.getMonthValue();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, startKey);
            ps.setInt(2, endKey);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SalaryReportRowDTO row = new SalaryReportRowDTO();
                    row.setGroupName(rs.getString("group_name"));
                    row.setDepartmentName(rs.getString("department_name"));
                    row.setEmployeeCount(rs.getInt("employee_count"));
                    row.setWorkdayIncome(rs.getLong("workday_income"));
                    row.setProductIncome(rs.getLong("product_income"));
                    row.setOvertimeIncome(rs.getLong("overtime_income"));
                    row.setTotalIncome(rs.getLong("total_income"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }
}
