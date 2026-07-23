package dao;

import model.*;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    public boolean savePayroll(Payroll payroll) {
        String sql = """
        INSERT INTO payrolls (
            user_id, month, year, expected_hours, actual_hours, 
            basic_salary, rate_multiplier, total_income, bonus, description, 
            social_insurance, health_insurance, unemployment_insurance, 
            union_fee, income_before_tax, taxable_income, income_tax, overtime_pay, sick_leave_pay, net_pay, 
            company_social_insurance, company_health_insurance, company_unemployment_insurance, 
            company_union_fee, status, created_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
            union_fee = VALUES(union_fee),
            income_before_tax = VALUES(income_before_tax),
            taxable_income = VALUES(taxable_income),
            income_tax = VALUES(income_tax),
            overtime_pay = VALUES(overtime_pay),
            sick_leave_pay = VALUES(sick_leave_pay),
            net_pay = VALUES(net_pay),
            company_social_insurance = VALUES(company_social_insurance),
            company_health_insurance = VALUES(company_health_insurance),
            company_unemployment_insurance = VALUES(company_unemployment_insurance),
            company_union_fee = VALUES(company_union_fee),                      
            status = VALUES(status);
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1-5: Thông tin cơ bản
            ps.setInt(1, payroll.getUserId());
            ps.setInt(2, payroll.getMonth());
            ps.setInt(3, payroll.getYear());
            ps.setDouble(4, payroll.getExpectedHours());
            ps.setDouble(5, payroll.getActualHours());

            // 6-10: Thu nhập
            ps.setLong(6, payroll.getBasicSalary());
            ps.setDouble(7, payroll.getRateMultiplier());
            ps.setLong(8, payroll.getTotalIncome());
            ps.setLong(9, payroll.getBonus());
            ps.setString(10, payroll.getDescription());

            // 11-14: Các khoản trừ của nhân viên
            ps.setLong(11, payroll.getSocialInsurance());
            ps.setLong(12, payroll.getHealthInsurance());
            ps.setLong(13, payroll.getUnemploymentInsurance());
            ps.setLong(14, payroll.getUnionFee());

            // 15-17: Thuế
            ps.setLong(15, payroll.getIncomeBeforeTax());
            ps.setLong(16, payroll.getTaxableIncome());
            ps.setLong(17, payroll.getIncomeTax());

            // 18-20: Các khoản phúc lợi và Net
            ps.setLong(18, payroll.getOvertimePay());
            ps.setLong(19, payroll.getSickLeavePay());
            ps.setLong(20, payroll.getNetPay());

            // 20-24: Các khoản công ty đóng
            ps.setLong(21, payroll.getCompanySocialInsurance());
            ps.setLong(22, payroll.getCompanyHealthInsurance());
            ps.setLong(23, payroll.getCompanyUnemploymentInsurance());
            ps.setLong(24, payroll.getCompanyUnionFee());

            // 25-26: Trạng thái và Ngày tạo
            ps.setString(25, payroll.getStatus());
            ps.setTimestamp(26, Timestamp.valueOf(payroll.getCreatedAt() != null ? payroll.getCreatedAt() : java.time.LocalDateTime.now()));

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
                    payroll.setBasicSalary(rs.getLong("basic_salary"));
                    payroll.setRateMultiplier(rs.getDouble("rate_multiplier"));
                    payroll.setTotalIncome(rs.getLong("total_income"));
                    payroll.setBonus(rs.getLong("bonus"));
                    payroll.setDescription(rs.getString("description"));
                    payroll.setSocialInsurance(rs.getLong("social_insurance"));
                    payroll.setHealthInsurance(rs.getLong("health_insurance"));
                    payroll.setUnemploymentInsurance(rs.getLong("unemployment_insurance"));
                    payroll.setUnionFee(rs.getLong("union_fee"));
                    payroll.setIncomeBeforeTax(rs.getLong("income_before_tax"));
                    payroll.setTaxableIncome(rs.getLong("taxable_income"));
                    payroll.setIncomeTax(rs.getLong("income_tax"));
                    payroll.setOvertimePay(rs.getLong("overtime_pay"));
                    payroll.setSickLeavePay(rs.getLong("sick_leave_pay"));
                    payroll.setNetPay(rs.getLong("net_pay"));
                    payroll.setCompanySocialInsurance(rs.getLong("company_social_insurance"));
                    payroll.setCompanyHealthInsurance(rs.getLong("company_health_insurance"));
                    payroll.setCompanyUnemploymentInsurance(rs.getLong("company_unemployment_insurance"));
                    payroll.setCompanyUnionFee(rs.getLong("company_union_fee"));
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
                    payroll.setStatus(rs.getString("status"));

                    payroll.setEmployeeName(rs.getString("employee_name"));
                    payroll.setDepartmentName(rs.getString("department_name"));
                    payroll.setPositionName(rs.getString("position_name"));

                    payroll.setExpectedHours(rs.getInt("expected_hours"));
                    payroll.setActualHours(rs.getInt("actual_hours"));

                    payroll.setBasicSalary(rs.getLong("basic_salary"));
                    payroll.setTotalIncome(rs.getLong("total_income"));
                    payroll.setNetPay(rs.getLong("net_pay"));

                    payroll.setSocialInsurance(rs.getLong("social_insurance"));
                    payroll.setHealthInsurance(rs.getLong("health_insurance"));
                    payroll.setUnemploymentInsurance(rs.getLong("unemployment_insurance"));
                    payroll.setUnionFee(rs.getLong("union_fee"));

                    payroll.setIncomeBeforeTax(rs.getLong("income_before_tax"));
                    payroll.setTaxableIncome(rs.getLong("taxable_income"));
                    payroll.setIncomeTax(rs.getLong("income_tax"));

                    payroll.setOvertimePay(rs.getLong("overtime_pay"));
                    payroll.setSickLeavePay(rs.getLong("sick_leave_pay"));
                    payroll.setBonus(rs.getLong("bonus"));
                    list.add(payroll);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Payroll calculatePayrollSummary(String keyword, String status, Integer userId, Integer month, Integer year, Integer departmentId) {
        Payroll summary = new Payroll();

        StringBuilder sql = new StringBuilder("""
        SELECT 
            SUM(p.expected_hours) AS sum_expected_hours,
            SUM(p.actual_hours) AS sum_actual_hours,
            SUM(p.basic_salary) AS sum_basic_salary,
            SUM(p.total_income) AS sum_total_income,
            SUM(p.social_insurance) AS sum_social_insurance,
            SUM(p.health_insurance) AS sum_health_insurance,
            SUM(p.unemployment_insurance) AS sum_unemployment_insurance,
            SUM(p.union_fee) AS sum_union_fee,
            SUM(p.income_before_tax) AS sum_income_before_tax,
            SUM(p.taxable_income) AS sum_taxable_income,
            SUM(p.income_tax) AS sum_income_tax,
            SUM(p.overtime_pay) AS sum_overtime_pay,
            SUM(p.sick_leave_pay) AS sum_sick_leave_pay,
            SUM(p.bonus) AS sum_bonus,
            SUM(p.net_pay) AS sum_net_pay
        FROM payrolls p
        JOIN users u ON p.user_id = u.id
        WHERE 1=1
    """);

        // Thêm các điều kiện WHERE tương tự như hàm findPayrollsAdvanced...
        if (keyword != null && !keyword.trim().isEmpty()) {
            boolean hasAccent = keyword.trim().matches(".*[áàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ].*");
            if (hasAccent) {
                sql.append(" AND LOWER(u.full_name) COLLATE utf8mb4_bin LIKE LOWER(?) ");
            } else {
                sql.append(" AND u.full_name COLLATE utf8mb4_general_ci LIKE ? ");
            }
        }
        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            sql.append(" AND LOWER(p.status) = LOWER(?) ");
        }
        if (userId != null) sql.append(" AND p.user_id = ? ");
        if (month != null) sql.append(" AND p.month = ? ");
        if (year != null) sql.append(" AND p.year = ? ");
        if (departmentId != null) sql.append(" AND u.department_id = ? ");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) ps.setString(paramIndex++, "%" + keyword.trim() + "%");
            if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) ps.setString(paramIndex++, status.trim());
            if (userId != null) ps.setInt(paramIndex++, userId);
            if (month != null) ps.setInt(paramIndex++, month);
            if (year != null) ps.setInt(paramIndex++, year);
            if (departmentId != null) ps.setInt(paramIndex++, departmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.setExpectedHours(rs.getInt("sum_expected_hours"));
                    summary.setActualHours(rs.getInt("sum_actual_hours"));
                    summary.setBasicSalary(rs.getLong("sum_basic_salary"));
                    summary.setTotalIncome(rs.getLong("sum_total_income"));
                    summary.setSocialInsurance(rs.getLong("sum_social_insurance"));
                    summary.setHealthInsurance(rs.getLong("sum_health_insurance"));
                    summary.setUnemploymentInsurance(rs.getLong("sum_unemployment_insurance"));
                    summary.setUnionFee(rs.getLong("sum_union_fee"));
                    summary.setIncomeBeforeTax(rs.getLong("sum_income_before_tax"));
                    summary.setTaxableIncome(rs.getLong("sum_taxable_income"));
                    summary.setIncomeTax(rs.getLong("sum_income_tax"));
                    summary.setOvertimePay(rs.getLong("sum_overtime_pay"));
                    summary.setSickLeavePay(rs.getLong("sum_sick_leave_pay"));
                    summary.setBonus(rs.getLong("sum_bonus"));
                    summary.setNetPay(rs.getLong("sum_net_pay"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return summary;
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

    public List<Payroll> findPayrollsByDepartment(Integer departmentId, Integer month, Integer year) {
        List<Payroll> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT p.*, 
               u.full_name AS employee_name, 
               d.name AS department_name, 
               pos.name AS position_name
        FROM payrolls p
        JOIN users u ON p.user_id = u.id
        LEFT JOIN departments d ON u.department_id = d.id
        LEFT JOIN positions pos ON u.position_id = pos.id
        WHERE p.month = ? AND p.year = ?
    """);

        if (departmentId != null && departmentId > 0) {
            sql.append(" AND u.department_id = ? ");
        }

        sql.append(" ORDER BY u.full_name ASC ");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, month);
            ps.setInt(2, year);

            if (departmentId != null && departmentId > 0) {
                ps.setInt(3, departmentId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payroll p = new Payroll();
                    p.setId(rs.getInt("id"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setMonth(rs.getInt("month"));
                    p.setYear(rs.getInt("year"));
                    p.setExpectedHours(rs.getDouble("expected_hours"));
                    p.setActualHours(rs.getDouble("actual_hours"));
                    p.setBasicSalary(rs.getLong("basic_salary"));
                    p.setRateMultiplier(rs.getDouble("rate_multiplier"));
                    p.setTotalIncome(rs.getLong("total_income"));
                    p.setBonus(rs.getLong("bonus"));
                    p.setDescription(rs.getString("description"));
                    p.setSocialInsurance(rs.getLong("social_insurance"));
                    p.setHealthInsurance(rs.getLong("health_insurance"));
                    p.setUnemploymentInsurance(rs.getLong("unemployment_insurance"));
                    p.setIncomeBeforeTax(rs.getLong("income_before_tax"));
                    p.setTaxableIncome(rs.getLong("taxable_income"));
                    p.setIncomeTax(rs.getLong("income_tax"));
                    p.setOvertimePay(rs.getLong("overtime_pay"));
                    p.setNetPay(rs.getLong("net_pay"));
                    p.setStatus(rs.getString("status"));

                    // Set thêm các thuộc tính phụ phục vụ hiển thị báo cáo Excel
                    p.setEmployeeName(rs.getString("employee_name"));
                    p.setDepartmentName(rs.getString("department_name"));
                    p.setPositionName(rs.getString("position_name"));

                    list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countEmployeesWithPayroll(Integer departmentId, int month, int year) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT p.user_id)
            FROM payrolls p
            JOIN users u ON p.user_id = u.id
            WHERE p.month = ? AND p.year = ?
        """);

        if (departmentId != null && departmentId > 0) {
            sql.append(" AND u.department_id = ? ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, month);
            ps.setInt(2, year);
            if (departmentId != null && departmentId > 0) {
                ps.setInt(3, departmentId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countDependentByUserId(Integer userId, int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate endDate = yearMonth.atEndOfMonth();

        String sql = """
                        SELECT COUNT(*) AS active_count FROM dependents
                        WHERE user_id = ? AND status = 'ACTIVE' AND effective_date <= ?
                    """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("active_count");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public PayrollSetting getPayrollSettingById(int id) {
        String sql = """
            SELECT * FROM payroll_settings
            WHERE id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSetting(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getLatestPayrollSetting();
    }

    public boolean isUnionMember(int userId, int month, int year) {
        YearMonth payrollMonth = YearMonth.of(year, month);
        String sql = """
                SELECT union_member
                FROM labor_contracts
                WHERE user_id = ?
                  AND start_date <= ?
                  AND (end_date IS NULL OR end_date >= ?)
                ORDER BY start_date DESC, id DESC
                LIMIT 1
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(payrollMonth.atEndOfMonth()));
            ps.setDate(3, Date.valueOf(payrollMonth.atDay(1)));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("union_member");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public PayrollSetting getLatestPayrollSetting() {
        String sql = "SELECT * FROM payroll_settings ORDER BY effective_date DESC LIMIT 1";
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToSetting(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PayrollSetting> getPayrollSettings(Integer month, Integer year, int offset, int limit) {
        List<PayrollSetting> settings = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT * FROM payroll_settings 
            WHERE 1=1
            """);

        if (month != null) {
            sql.append(" AND MONTH(effective_date) = ? ");
        }
        if (year != null) {
            sql.append(" AND YEAR(effective_date) = ? ");
        }

        sql.append(" ORDER BY effective_date DESC LIMIT ? OFFSET ? ");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int idx = 1;
            if (month != null) {
                ps.setInt(idx++, month);
            }
            if (year != null) {
                ps.setInt(idx++, year);
            }

            ps.setInt(idx++, limit);
            ps.setInt(idx++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PayrollSetting payrollSetting = mapResultSetToSetting(rs);
                    settings.add(payrollSetting);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return settings;
    }

    public boolean insertPayrollSetting(PayrollSetting setting) {
        String checkSql = "SELECT id FROM payroll_settings WHERE effective_date = ?";

        String insertSql = """
        INSERT INTO payroll_settings (
            employee_social_insurance, employee_health_insurance, employee_unemployment_insurance, employee_union, 
            company_social_insurance, company_health_insurance, company_unemployment_insurance, company_union, sick_leave_rate,
            ot_weekday_rate, ot_weekend_rate, ot_holiday_rate, self_deduction, dependent_deduction, effective_date
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        String updateSql = """
        UPDATE payroll_settings SET 
            employee_social_insurance = ?, employee_health_insurance = ?, employee_unemployment_insurance = ?, employee_union = ?, 
            company_social_insurance = ?, company_health_insurance = ?, company_unemployment_insurance = ?, company_union = ?, 
            sick_leave_rate = ?, ot_weekday_rate = ?, ot_weekend_rate = ?, ot_holiday_rate = ?, self_deduction = ?, dependent_deduction = ? 
        WHERE effective_date = ?
        """;

        try (Connection conn = DBConnection.getConnection()) {
            boolean isExisted = false;

            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setDate(1, java.sql.Date.valueOf(setting.getEffectiveDate()));
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        isExisted = true;
                    }
                }
            }

            String finalSql = isExisted ? updateSql : insertSql;

            try (PreparedStatement ps = conn.prepareStatement(finalSql)) {
                ps.setDouble(1, Math.round(setting.getEmployeeSocialInsurance() * 100.0) / 100.0);
                ps.setDouble(2, Math.round(setting.getEmployeeHealthInsurance() * 100.0) / 100.0);
                ps.setDouble(3, Math.round(setting.getEmployeeUnemploymentInsurance() * 100.0) / 100.0);
                ps.setDouble(4, Math.round(setting.getEmployeeUnion() * 100.0) / 100.0);
                ps.setDouble(5, Math.round(setting.getCompanySocialInsurance() * 100.0) / 100.0);
                ps.setDouble(6, Math.round(setting.getCompanyHealthInsurance() * 100.0) / 100.0);
                ps.setDouble(7, Math.round(setting.getCompanyUnemploymentInsurance() * 100.0) / 100.0);
                ps.setDouble(8, Math.round(setting.getCompanyUnion() * 100.0) / 100.0);
                ps.setDouble(9, Math.round(setting.getSickLeaveRate() * 100.0) / 100.0);
                ps.setDouble(10, Math.round(setting.getOtWeekdayRate() * 100.0) / 100.0);
                ps.setDouble(11, Math.round(setting.getOtWeekendRate() * 100.0) / 100.0);
                ps.setDouble(12, Math.round(setting.getOtHolidayRate() * 100.0) / 100.0);
                ps.setLong(13, setting.getSelfDeduction());
                ps.setLong(14, setting.getDependentDeduction());
                ps.setDate(15, java.sql.Date.valueOf(setting.getEffectiveDate()));

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<PitBracketVersion> getPitBracketVersions(Integer month, Integer year, int offset, int limit) {
        List<PitBracketVersion> pitBracketVersions = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
        SELECT * FROM pit_bracket_versions 
        WHERE 1=1
        """);

        if (month != null) {
            sql.append(" AND MONTH(effective_date) = ? ");
        }
        if (year != null) {
            sql.append(" AND YEAR(effective_date) = ? ");
        }

        sql.append(" ORDER BY effective_date DESC LIMIT ? OFFSET ? ");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int idx = 1;
            if (month != null) {
                ps.setInt(idx++, month);
            }
            if (year != null) {
                ps.setInt(idx++, year);
            }

            ps.setInt(idx++, limit);
            ps.setInt(idx++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PitBracketVersion version = new PitBracketVersion();
                    version.setId(rs.getInt("id"));
                    version.setVersionName(rs.getString("version_name"));
                    version.setEffectiveDate(rs.getDate("effective_date").toLocalDate());
                    if (rs.getTimestamp("created_at") != null) {
                        version.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().toLocalDate());
                    }
                    pitBracketVersions.add(version);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pitBracketVersions;
    }

    public List<PitBracket> getPitBrackets(int versionId) {
        String sql = """ 
                    SELECT * FROM pit_brackets
                    WHERE version_id = ?
                    ORDER BY bracket_level ASC
                    """;

        List<PitBracket> brackets = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, versionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PitBracket bracket = new PitBracket();
                    bracket.setId(rs.getInt("id"));
                    bracket.setBracketLevel(rs.getInt("bracket_level"));
                    bracket.setMinValue(rs.getLong("min_value"));
                    bracket.setMaxValue(rs.getObject("max_value", Long.class));
                    bracket.setTaxRate(rs.getDouble("tax_rate"));
                    brackets.add(bracket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brackets;
    }

    public PitBracketVersion getLatestPitBracketVersion() {
        String fallbackSql = "SELECT * FROM pit_bracket_versions ORDER BY effective_date DESC LIMIT 1";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(fallbackSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToBracketVersion(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<PitBracket> getPitBracketsByDate(LocalDate calculationDate) {
        PitBracketVersion version = getActivePitVersionIdByDate(calculationDate);

        if (version != null) {
            return getPitBrackets(version.getId());
        }
        return new ArrayList<>();
    }

    public int getPayrollSettingCount(Integer month, Integer year) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) 
            FROM payroll_settings
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (month != null && month > 0) {
            sql.append(" AND MONTH(effective_date) = ?");
            params.add(month);
        }

        if (year != null && year > 0) {
            sql.append(" AND YEAR(effective_date) = ?");
            params.add(year);
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int countPitBracketVersions(Integer month, Integer year) {
        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*) FROM pit_bracket_versions 
        WHERE 1=1
        """);

        if (month != null) {
            sql.append(" AND MONTH(effective_date) = ? ");
        }
        if (year != null) {
            sql.append(" AND YEAR(effective_date) = ? ");
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int idx = 1;
            if (month != null) {
                ps.setInt(idx++, month);
            }
            if (year != null) {
                ps.setInt(idx++, year);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean saveOrUpdatePitVersionAndBrackets(int month, int year, List<PitBracket> brackets) {
        String checkSql = "SELECT id FROM pit_bracket_versions WHERE effective_date = ?";
        String insertVersionSql = "INSERT INTO pit_bracket_versions (version_name, effective_date) VALUES (?, ?)";
        String deleteBracketsSql = "DELETE FROM pit_brackets WHERE version_id = ?";
        String insertBracketSql = "INSERT INTO pit_brackets (version_id, bracket_level, min_value, max_value, tax_rate) VALUES (?, ?, ?, ?, ?)";

        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            java.time.LocalDate effectiveDate = java.time.LocalDate.of(year, month, 1);
            int versionId = -1;

            ps = conn.prepareStatement(checkSql);
            ps.setDate(1, java.sql.Date.valueOf(effectiveDate));
            rs = ps.executeQuery();

            if (rs.next()) {
                versionId = rs.getInt("id");
                try (java.sql.PreparedStatement psDel = conn.prepareStatement(deleteBracketsSql)) {
                    psDel.setInt(1, versionId);
                    psDel.executeUpdate();
                }
            } else {
                // Trường hợp 2: CHƯA CÓ -> Tạo mới một phiên bản (Version)
                try (java.sql.PreparedStatement psInsVer = conn.prepareStatement(insertVersionSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    psInsVer.setString(1, "PIT Version " + month + "/" + year);
                    psInsVer.setDate(2, java.sql.Date.valueOf(effectiveDate));
                    psInsVer.executeUpdate();

                    try (java.sql.ResultSet rsKey = psInsVer.getGeneratedKeys()) {
                        if (rsKey.next()) {
                            versionId = rsKey.getInt(1);
                        }
                    }
                }
            }

            if (versionId != -1 && brackets != null && !brackets.isEmpty()) {
                try (java.sql.PreparedStatement psInsBracket = conn.prepareStatement(insertBracketSql)) {
                    for (PitBracket b : brackets) {
                        psInsBracket.setInt(1, versionId);
                        psInsBracket.setInt(2, b.getBracketLevel());
                        psInsBracket.setLong(3, b.getMinValue());
                        if (b.getMaxValue() != null) {
                            psInsBracket.setLong(4, b.getMaxValue());
                        } else {
                            psInsBracket.setNull(4, java.sql.Types.DECIMAL);
                        }
                        psInsBracket.setDouble(5, b.getTaxRate());
                        psInsBracket.addBatch();
                    }
                    psInsBracket.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    private PayrollSetting mapResultSetToSetting(ResultSet rs) throws Exception {
        PayrollSetting setting = new PayrollSetting();
        setting.setId(rs.getInt("id"));
        setting.setEmployeeSocialInsurance(rs.getDouble("employee_social_insurance"));
        setting.setEmployeeHealthInsurance(rs.getDouble("employee_health_insurance"));
        setting.setEmployeeUnemploymentInsurance(rs.getDouble("employee_unemployment_insurance"));
        setting.setEmployeeUnion(rs.getDouble("employee_union"));
        setting.setCompanySocialInsurance(rs.getDouble("company_social_insurance"));
        setting.setCompanyHealthInsurance(rs.getDouble("company_health_insurance"));
        setting.setCompanyUnemploymentInsurance(rs.getDouble("company_unemployment_insurance"));
        setting.setCompanyUnion(rs.getDouble("company_union"));

        setting.setSickLeaveRate(rs.getDouble("sick_leave_rate"));
        setting.setOtWeekdayRate(rs.getDouble("ot_weekday_rate"));
        setting.setOtWeekendRate(rs.getDouble("ot_weekend_rate"));
        setting.setOtHolidayRate(rs.getDouble("ot_holiday_rate"));

        setting.setSelfDeduction(rs.getLong("self_deduction"));
        setting.setDependentDeduction(rs.getLong("dependent_deduction"));
        setting.setEffectiveDate(rs.getDate("effective_date").toLocalDate());
        return setting;
    }

    private PitBracketVersion mapResultSetToBracketVersion(ResultSet rs) throws Exception {
        PitBracketVersion version = new PitBracketVersion();
        version.setId(rs.getInt("id"));
        version.setVersionName(rs.getString("version_name"));
        version.setEffectiveDate(rs.getDate("effective_date").toLocalDate());
        if (rs.getTimestamp("created_at") != null) {
            version.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().toLocalDate());
        }
        return version;
    }

    public PayrollSetting getCurrentlyActivePayrollSetting() {
        String sql = "SELECT * FROM payroll_settings WHERE effective_date <= CURRENT_DATE ORDER BY effective_date DESC LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapResultSetToSetting(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PayrollSetting getPayrollSettingByDate(LocalDate calculationDate) {
        if (calculationDate == null) calculationDate = LocalDate.now();

        String sql = "SELECT * FROM payroll_settings WHERE effective_date <= ? ORDER BY effective_date DESC LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(calculationDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSetting(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PitBracketVersion getCurrentlyActivePitVersion() {
        String sql = "SELECT * FROM pit_bracket_versions WHERE effective_date <= CURRENT_DATE ORDER BY effective_date DESC LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapResultSetToBracketVersion(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PitBracketVersion getActivePitVersionIdByDate(LocalDate calculationDate) {
        if (calculationDate == null) calculationDate = LocalDate.now();

        String sql = "SELECT * FROM pit_bracket_versions WHERE effective_date <= ? ORDER BY effective_date DESC LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(calculationDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBracketVersion(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getCurrentlyActivePitVersion();
    }

    public boolean deletePayrollSetting(int id) {
        String sql = "DELETE FROM payroll_settings WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
