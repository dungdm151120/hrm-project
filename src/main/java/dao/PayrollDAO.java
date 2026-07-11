package dao;

import model.DepartmentPayrollSummary;
import model.Payroll;
import model.PayrollSetting;
import model.PitBracket;
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
            union_fee, income_before_tax, taxable_income, income_tax, net_pay, 
            company_social_insurance, company_health_insurance, company_unemployment_insurance, 
            company_union_fee, status, created_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

            // 15-18: Thuế và Net
            ps.setDouble(15, payroll.getIncomeBeforeTax());
            ps.setDouble(16, payroll.getTaxableIncome());
            ps.setDouble(17, payroll.getIncomeTax());
            ps.setDouble(18, payroll.getNetPay());

            // 19-22: Các khoản công ty đóng
            ps.setDouble(19, payroll.getCompanySocialInsurance());
            ps.setDouble(20, payroll.getCompanyHealthInsurance());
            ps.setLong(21, payroll.getCompanyUnemploymentInsurance());
            ps.setLong(22, payroll.getCompanyUnionFee());

            // 23-24: Trạng thái và Ngày tạo
            ps.setString(23, payroll.getStatus());
            ps.setTimestamp(24, Timestamp.valueOf(payroll.getCreatedAt() != null ? payroll.getCreatedAt() : java.time.LocalDateTime.now()));

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
                    payroll.setNetPay(rs.getLong("net_pay"));
                    payroll.setCompanySocialInsurance(rs.getLong("company_social_insurance"));
                    payroll.setCompanyHealthInsurance(rs.getLong("company_health_insurance"));
                    payroll.setCompanyUnemploymentInsurance(rs.getLong("company_unemployment_insurance"));
                    payroll.setCompanyUnionFee(rs.getLong("company_unemployment_insurance"));
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
                    payroll.setBasicSalary(rs.getLong("basic_salary"));
                    payroll.setTotalIncome(rs.getLong("total_income"));
                    payroll.setNetPay(rs.getLong("net_pay"));
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
                        SELECT dependent FROM dependent_number
                        WHERE user_id = ? AND effective_date <= ? 
                        ORDER BY effective_date LIMIT 1
                    """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("dependent");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public PayrollSetting getPayrollSetting() {
        String sql = """
                    SELECT * FROM payroll_settings
                    WHERE effective_date <= ?
                    ORDER BY effective_date DESC
                    LIMIT 1
                    """;
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
                    setting.setSelfDeduction(rs.getLong("self_deduction"));
                    setting.setDependentDeduction(rs.getLong("dependent_deduction"));

                    return setting;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<PitBracket> getPitBrackets() {
        String sql = """ 
                    SELECT * FROM pit_brackets\s
                    WHERE effective_date = (
                        SELECT MAX(effective_date)\s
                        FROM pit_brackets\s
                        WHERE effective_date <= ?
                    )
                    ORDER BY bracket_level ASC
                    """;

        List<PitBracket> brackets = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));

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

    public boolean isUnionMember(int userId) {
        String sql = "SELECT is_member FROM user_union_membership WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_member");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePayrollSetting(PayrollSetting setting) {
        String sql = "UPDATE payroll_settings SET " +
                "employee_social_insurance = ?, " +
                "employee_health_insurance = ?, " +
                "employee_unemployment_insurance = ?, " +
                "employee_union = ?, " +
                "company_social_insurance = ?, " +
                "company_health_insurance = ?, " +
                "company_unemployment_insurance = ?, " +
                "company_union = ?, " +
                "self_deduction = ?, " +
                "dependent_deduction = ?, " +
                "effective_date = ? " +
                "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, setting.getEmployeeSocialInsurance());
            ps.setDouble(2, setting.getEmployeeHealthInsurance());
            ps.setDouble(3, setting.getEmployeeUnemploymentInsurance());
            ps.setDouble(4, setting.getEmployeeUnion());
            ps.setDouble(5, setting.getCompanySocialInsurance());
            ps.setDouble(6, setting.getCompanyHealthInsurance());
            ps.setDouble(7, setting.getCompanyUnemploymentInsurance());
            ps.setDouble(8, setting.getCompanyUnion());
            ps.setLong(9, setting.getSelfDeduction());
            ps.setLong(10, setting.getDependentDeduction());
            ps.setDate(11, Date.valueOf(setting.getEffectiveDate()));
            ps.setInt(12, setting.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePitBrackets(java.util.List<model.PitBracket> bracketList, java.time.LocalDate effectiveDate) {
        String sql = "UPDATE pit_brackets SET min_value = ?, max_value = ?, tax_rate = ?, effective_date = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (model.PitBracket b : bracketList) {
                ps.setLong(1, b.getMinValue());

                if (b.getMaxValue() != null) {
                    ps.setLong(2, b.getMaxValue());
                } else {
                    ps.setNull(2, java.sql.Types.BIGINT);
                }

                ps.setDouble(3, b.getTaxRate());
                ps.setDate(4, java.sql.Date.valueOf(effectiveDate));
                ps.setInt(5, b.getId());

                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
