package dao;

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
                basic_salary, total_income, social_insurance, health_insurance, 
                unemployment_insurance, income_before_tax, taxable_income, income_tax, 
                net_pay, status, created_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                expected_hours = VALUES(expected_hours),
                actual_hours = VALUES(actual_hours),
                basic_salary = VALUES(basic_salary),
                total_income = VALUES(total_income),
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
            ps.setDouble(7, payroll.getTotalIncome());
            ps.setDouble(8, payroll.getSocialInsurance());
            ps.setDouble(9, payroll.getHealthInsurance());
            ps.setDouble(10, payroll.getUnemploymentInsurance());
            ps.setDouble(11, payroll.getIncomeBeforeTax());
            ps.setDouble(12, payroll.getTaxableIncome());
            ps.setDouble(13, payroll.getIncomeTax());
            ps.setDouble(14, payroll.getNetPay());
            ps.setString(15, payroll.getStatus());

            ps.setTimestamp(16, Timestamp.valueOf(java.time.LocalDateTime.now()));

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
                    payroll.setTotalIncome(rs.getDouble("total_income"));
                    payroll.setSocialInsurance(rs.getDouble("social_insurance"));
                    payroll.setHealthInsurance(rs.getDouble("health_insurance"));
                    payroll.setUnemploymentInsurance(rs.getDouble("unemployment_insurance"));
                    payroll.setIncomeBeforeTax(rs.getDouble("income_before_tax"));
                    payroll.setTaxableIncome(rs.getDouble("taxable_income"));
                    payroll.setIncomeTax(rs.getDouble("income_tax"));
                    payroll.setNetPay(rs.getDouble("net_pay"));
                    payroll.setStatus(rs.getString("status"));

                    // Nếu class của bạn có thuộc tính này (LocalDateTime)
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

    public List<Payroll> findAllPayrollByUserId(int userId) {
        List<Payroll> list = new ArrayList<>();

        String sql = "SELECT * FROM payrolls WHERE user_id = ? ORDER BY year DESC, month DESC";

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

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
                    list.add(payroll);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Payroll> findAllPayrolls() {
        List<Payroll> list = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name FROM payrolls p JOIN users u ON p.user_id = u.id ORDER BY p.year DESC, p.month DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
                list.add(payroll);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int payrollId, String status) {
        String sql = "UPDATE payrolls SET status = ? WHERE id = ?";
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

    // 1. Hàm đếm tổng số bản ghi payroll dựa trên quyền (Để tính toán tổng số trang totalPage)
    public int countPayrolls(String roleName, int userId) {
        String sql = "SELECT COUNT(*) FROM payrolls";
        if ("EMPLOYEE".equalsIgnoreCase(roleName)) {
            sql += " WHERE user_id = ?";
        }

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if ("EMPLOYEE".equalsIgnoreCase(roleName)) {
                ps.setInt(1, userId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 2. Hàm lấy danh sách payroll có phân trang và kèm đầy đủ thông tin nhân sự liên kết
    public List<Payroll> findPayrollsWithPaging(String roleName, int userId, int offset, int pageSize) {
        List<Payroll> list = new ArrayList<>();

        // Câu lệnh SQL tối ưu: Kết hợp JOIN để lấy trực tiếp thông tin Employee sang đối tượng tạm trong List
        String sql = "SELECT p.*, u.full_name, d.name AS department_name, pos.name AS position_name " +
                "FROM payrolls p " +
                "JOIN users u ON p.user_id = u.id " +
                "LEFT JOIN departments d ON u.department_id = d.id " +
                "LEFT JOIN positions pos ON u.position_id = pos.id ";

        if ("EMPLOYEE".equalsIgnoreCase(roleName)) {
            sql += "WHERE p.user_id = ? ";
        }

        sql += "ORDER BY p.year DESC, p.month DESC, p.id DESC LIMIT ? OFFSET ?";

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIdx = 1;
            if ("EMPLOYEE".equalsIgnoreCase(roleName)) {
                ps.setInt(paramIdx++, userId);
            }
            ps.setInt(paramIdx++, pageSize);
            ps.setInt(paramIdx, offset);

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

                    // Mẹo tối ưu: Chúng ta sẽ tạm thời lưu thông tin nhân sự đi kèm vào các thuộc tính String nếu Model Payroll của bạn chưa tạo đối tượng User lồng nhau, hoặc gán tạm sang map.
                    // Tuy nhiên để JSP đọc mượt nhất mà không cần sửa Model Payroll, tôi khuyên bạn nên mở file Payroll.java thêm tạm 3 trường String (hoặc tận dụng nếu đã có):
                    // private String employeeName; private String departmentName; private String positionName; kèm getter/setter.
                    // Ở đây tôi gán giả định bạn thêm 3 trường String này vào Model Payroll để JSP dễ gọi:
                    payroll.setExpectedHours(rs.getDouble("expected_hours"));
                    payroll.setActualHours(rs.getDouble("actual_hours"));

                    // Lưu thông tin hiển thị phụ ra danh sách:
                    // Bạn hãy mở file Payroll.java thêm 3 thuộc tính String này vào nhé!
                    payroll.setEmployeeName(rs.getString("full_name"));
                    payroll.setDepartmentName(rs.getString("department_name"));
                    payroll.setPositionName(rs.getString("position_name"));

                    list.add(payroll);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
