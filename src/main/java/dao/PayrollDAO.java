package dao;

import model.DepartmentPayrollSummary;
import model.Payroll;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    public boolean savePayroll(Payroll payroll) {
        String sql = """
            INSERT INTO payrolls (
                user_id, month, year, expected_hours, actual_hours, 
                basic_salary, rate_multiplier, total_income, bonus, description, social_insurance, health_insurance, 
                unemployment_insurance, income_before_tax, taxable_income, income_tax, 
                net_pay, status, created_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                expected_hours = VALUES(expected_hours),
                actual_hours = VALUES(actual_hours),
                basic_salary = VALUES(basic_salary),
                rate_multiplier = VALUES(rate_multiplier),
                total_income = VALUES(total_income),
                bonus = VALUES(bonus), 
                description = VALUES(description),
                social_insurance = VALUES(social_insurance),
                health_insurance = VALUES(health_insurance),
                unemployment_insurance = VALUES(unemployment_insurance),
                income_before_tax = VALUES(income_before_tax),
                taxable_income = VALUES(taxable_income),
                income_tax = VALUES(income_tax),
                net_pay = VALUES(net_pay),
                status = VALUES(status);
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payroll.getUserId());
            ps.setInt(2, payroll.getMonth());
            ps.setInt(3, payroll.getYear());
            ps.setDouble(4, payroll.getExpectedHours());
            ps.setDouble(5, payroll.getActualHours());
            ps.setDouble(6, payroll.getBasicSalary());
            ps.setDouble(7, payroll.getRateMultiplier());
            ps.setDouble(8, payroll.getTotalIncome());
            ps.setDouble(9, payroll.getBonus());
            ps.setString(10, payroll.getDescription());
            ps.setDouble(11, payroll.getSocialInsurance());
            ps.setDouble(12, payroll.getHealthInsurance());
            ps.setDouble(13, payroll.getUnemploymentInsurance());
            ps.setDouble(14, payroll.getIncomeBeforeTax());
            ps.setDouble(15, payroll.getTaxableIncome());
            ps.setDouble(16, payroll.getIncomeTax());
            ps.setDouble(17, payroll.getNetPay());
            ps.setString(18, payroll.getStatus());

            ps.setTimestamp(19, Timestamp.valueOf(java.time.LocalDateTime.now()));

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Payroll findById(int payrollId) {
        Payroll payroll = null;
        String sql = "SELECT * FROM payrolls WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, payrollId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    payroll = new Payroll();
                    payroll.setId(rs.getInt("id"));
                    payroll.setUserId(rs.getInt("user_id"));
                    payroll.setMonth(rs.getInt("month"));
                    payroll.setYear(rs.getInt("year"));
                    payroll.setExpectedHours(rs.getDouble("expected_hours"));
                    payroll.setActualHours(rs.getDouble("actual_hours"));
                    payroll.setBasicSalary(rs.getDouble("basic_salary"));
                    payroll.setRateMultiplier(rs.getDouble("rate_multiplier"));
                    payroll.setTotalIncome(rs.getDouble("total_income"));
                    payroll.setBonus(rs.getDouble("bonus"));
                    payroll.setDescription(rs.getString("description"));
                    payroll.setSocialInsurance(rs.getDouble("social_insurance"));
                    payroll.setHealthInsurance(rs.getDouble("health_insurance"));
                    payroll.setUnemploymentInsurance(rs.getDouble("unemployment_insurance"));
                    payroll.setIncomeBeforeTax(rs.getDouble("income_before_tax"));
                    payroll.setTaxableIncome(rs.getDouble("taxable_income"));
                    payroll.setIncomeTax(rs.getDouble("income_tax"));
                    payroll.setNetPay(rs.getDouble("net_pay"));
                    payroll.setStatus(rs.getString("status"));

                    if (rs.getTimestamp("created_at") != null) {
                        payroll.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payroll;
    }

    public List<Payroll> findPayrollsAdvanced(String keyword, String status, String sort, int offset, int limit, Integer userId, Integer month, Integer year, Integer departmentId) {
        List<Payroll> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT p.*, u.full_name AS employee_name, 
                   d.name AS department_name, 
                   pos.name AS position_name
            FROM payrolls p
            JOIN users u ON p.user_id = u.id
            LEFT JOIN departments d ON u.department_id = d.id
            LEFT JOIN positions pos ON u.position_id = pos.id
            WHERE 1=1
            """);

        boolean hasAccent = false;
        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.trim();
            hasAccent = k.matches(".*[áàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ].*");

            if (hasAccent) {
                sql.append(" AND LOWER(u.full_name) COLLATE utf8mb4_bin LIKE LOWER(?) ");
            } else {
                sql.append(" AND u.full_name COLLATE utf8mb4_general_ci LIKE ? ");
            }
        }

        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            sql.append(" AND LOWER(p.status) = LOWER(?) ");
        }

        if (userId != null) {
            sql.append(" AND p.user_id = ? ");
        }

        if (month != null) {
            sql.append(" AND p.month = ? ");
        }

        if (year != null) {
            sql.append(" AND p.year = ? ");
        }

        if (departmentId != null) {
            sql.append(" AND u.department_id = ? ");
        }

        if (sort != null) {
            switch (sort) {
                case "name_asc" -> sql.append(" ORDER BY u.full_name ASC ");
                case "name_desc" -> sql.append(" ORDER BY u.full_name DESC ");
                default -> sql.append(" ORDER BY p.year DESC, p.month DESC, p.id DESC ");
            }
        } else {
            sql.append(" ORDER BY p.year DESC, p.month DESC, p.id DESC ");
        }

        sql.append(" LIMIT ? OFFSET ? ");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword.trim() + "%");
            }
            if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
                ps.setString(paramIndex++, status.trim());
            }
            if (userId != null) {
                ps.setInt(paramIndex++, userId);
            }
            if (month != null) {
                ps.setInt(paramIndex++, month);
            }
            if (year != null) {
                ps.setInt(paramIndex++, year);
            }
            if (departmentId != null) {
                ps.setInt(paramIndex++, departmentId);
            }

            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payroll payroll = new Payroll();
                    payroll.setId(rs.getInt("id"));
                    payroll.setUserId(rs.getInt("user_id"));
                    payroll.setMonth(rs.getInt("month"));
                    payroll.setYear(rs.getInt("year"));
                    payroll.setBasicSalary(rs.getDouble("basic_salary"));
                    payroll.setTotalIncome(rs.getDouble("total_income"));
                    payroll.setNetPay(rs.getDouble("net_pay"));
                    payroll.setStatus(rs.getString("status"));
                    payroll.setEmployeeName(rs.getString("employee_name"));
                    payroll.setDepartmentName(rs.getString("department_name"));
                    payroll.setPositionName(rs.getString("position_name"));
                    list.add(payroll);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int payrollId, String status) {
        String sql = "UPDATE payrolls SET status = ? WHERE id = ? AND LOWER(status) = 'draft'";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, payrollId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int confirmAllDepartmentPayrolls(Integer departmentId, Integer month, Integer year) {
        StringBuilder sql = new StringBuilder("""
            UPDATE payrolls p
            JOIN users u ON p.user_id = u.id
            SET p.status = 'CONFIRMED'
            WHERE LOWER(p.status) = 'draft'
        """);

        if (departmentId != null) sql.append(" AND u.department_id = ? ");
        if (month != null) sql.append(" AND p.month = ? ");
        if (year != null) sql.append(" AND p.year = ? ");

        int updatedRows = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (departmentId != null) ps.setInt(idx++, departmentId);
            if (month != null) ps.setInt(idx++, month);
            if (year != null) ps.setInt(idx++, year);

            updatedRows = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updatedRows;
    }

    public int countPayrolls(String keyword, String status, Integer userId, Integer month, Integer year, Integer departmentId) {
        int totalRows = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM payrolls p JOIN users u ON p.user_id = u.id WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND BINARY u.full_name LIKE ? ");
        }

        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            sql.append(" AND LOWER(p.status) = LOWER(?) ");
        }

        if (userId != null) {
            sql.append(" AND p.user_id = ? ");
        }

        if (month != null) {
            sql.append(" AND p.month = ? ");
        }

        if (year != null) {
            sql.append(" AND p.year = ? ");
        }

        if (departmentId != null) {
            sql.append(" AND u.department_id = ? ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword.trim() + "%");
            }
            if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
                ps.setString(paramIndex++, status.trim());
            }
            if (userId != null) {
                ps.setInt(paramIndex++, userId);
            }
            if (month != null) {
                ps.setInt(paramIndex++, month);
            }
            if (year != null) {
                ps.setInt(paramIndex++, year);
            }
            if (departmentId != null) {
                ps.setInt(paramIndex++, departmentId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalRows = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalRows;
    }

    public List<DepartmentPayrollSummary> findDepartmentPayrollSummaries(Integer departmentId, Integer month, Integer year, int offset, int limit) {
        List<DepartmentPayrollSummary> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT u.department_id, d.name AS department_name, p.month, p.year, SUM(p.net_pay) AS total_payroll
            FROM payrolls p
            JOIN users u ON p.user_id = u.id
            LEFT JOIN departments d ON u.department_id = d.id
            WHERE 1=1
            AND u.department_id IS NOT NULL
        """);

        if (departmentId != null) sql.append(" AND u.department_id = ? ");
        if (month != null) sql.append(" AND p.month = ? ");
        if (year != null) sql.append(" AND p.year = ? ");

        sql.append(" GROUP BY u.department_id, d.name, p.month, p.year ");
        sql.append(" ORDER BY p.year DESC, p.month DESC, d.name ASC ");
        sql.append(" LIMIT ? OFFSET ? ");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (departmentId != null) ps.setInt(idx++, departmentId);
            if (month != null) ps.setInt(idx++, month);
            if (year != null) ps.setInt(idx++, year);

            ps.setInt(idx++, limit);
            ps.setInt(idx++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartmentPayrollSummary summary = new DepartmentPayrollSummary();
                    summary.setDepartmentId(rs.getInt("department_id"));
                    summary.setDepartmentName(rs.getString("department_name"));
                    summary.setMonth(rs.getInt("month"));
                    summary.setYear(rs.getInt("year"));
                    summary.setTotalPayroll(rs.getDouble("total_payroll"));
                    list.add(summary);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int countDepartmentPayrollSummaries(Integer departmentId, Integer month, Integer year) {
        int totalRows = 0;

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*) FROM (
            SELECT 1
            FROM payrolls p
            JOIN users u ON p.user_id = u.id
            WHERE 1=1
            AND u.department_id IS NOT NULL
        """);

        if (departmentId != null) sql.append(" AND u.department_id = ? ");
        if (month != null) sql.append(" AND p.month = ? ");
        if (year != null) sql.append(" AND p.year = ? ");

        sql.append(" GROUP BY u.department_id, p.month, p.year ")
                .append(") AS subquery");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (departmentId != null) ps.setInt(idx++, departmentId);
            if (month != null) ps.setInt(idx++, month);
            if (year != null) ps.setInt(idx++, year);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalRows = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalRows;
    }
}
