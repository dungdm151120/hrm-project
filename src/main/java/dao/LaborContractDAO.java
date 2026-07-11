package dao;

import model.LaborContract;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LaborContractDAO {
    public List<LaborContract> search(Integer userId, String keyword, String contractType, String status,
                                      int offset, int limit) {
        expireEndedActiveContracts();
        List<LaborContract> contracts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(baseSelect()).append(" WHERE 1=1");
        appendSearchFilters(sql, params, userId, keyword, contractType, status);
        sql.append(" ORDER BY lc.start_date DESC, lc.id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contracts.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contracts;
    }

    public int count(Integer userId, String keyword, String contractType, String status) {
        expireEndedActiveContracts();
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) AS total
                FROM labor_contracts lc
                JOIN users u ON lc.user_id = u.id
                WHERE 1=1
                """);
        appendSearchFilters(sql, params, userId, keyword, contractType, status);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public LaborContract findById(int id) {
        expireEndedActiveContracts();
        String sql = baseSelect() + " WHERE lc.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean add(LaborContract contract) {
        expireEndedActiveContracts();
        String sql = """
                INSERT INTO labor_contracts (
                    user_id, contract_code, contract_type, start_date, end_date,
                    base_salary, working_time, work_location, status, note
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillEditableFields(ps, contract);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(LaborContract contract) {
        expireEndedActiveContracts();
        String sql = """
                UPDATE labor_contracts
                SET user_id = ?,
                    contract_code = ?,
                    contract_type = ?,
                    start_date = ?,
                    end_date = ?,
                    base_salary = ?,
                    working_time = ?,
                    work_location = ?,
                    status = ?,
                    note = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillEditableFields(ps, contract);
            ps.setInt(11, contract.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean terminate(int id, String reason, Integer terminatedBy) {
        expireEndedActiveContracts();
        LaborContract current = findById(id);
        if (current == null || !"ACTIVE".equals(current.getStatus())) {
            return false;
        }

        String sql = """
                UPDATE labor_contracts
                SET status = 'TERMINATED',
                    end_date = CURRENT_DATE,
                    terminated_at = CURRENT_TIMESTAMP,
                    terminated_by = ?,
                    termination_reason = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                  AND status = 'ACTIVE'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (terminatedBy == null || terminatedBy <= 0) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, terminatedBy);
            }
            ps.setString(2, trimToNull(reason));
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsOverlappingActiveContract(int userId, LocalDate startDate, LocalDate endDate, Integer exceptId) {
        expireEndedActiveContracts();
        String sql = """
                SELECT id
                FROM labor_contracts
                WHERE user_id = ?
                  AND status = 'ACTIVE'
                  AND (? IS NULL OR start_date <= ?)
                  AND (end_date IS NULL OR end_date >= ?)
                """ + (exceptId == null ? "" : " AND id <> ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            setNullableDate(ps, 2, endDate);
            setNullableDate(ps, 3, endDate);
            ps.setDate(4, Date.valueOf(startDate));
            if (exceptId != null) {
                ps.setInt(5, exceptId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isActiveUser(int userId) {
        String sql = "SELECT id FROM users WHERE id = ? AND active = TRUE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsByContractCode(String contractCode, Integer exceptId) {
        String sql = "SELECT id FROM labor_contracts WHERE contract_code = ?"
                + (exceptId == null ? "" : " AND id <> ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contractCode);
            if (exceptId != null) {
                ps.setInt(2, exceptId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean canDeactivateUser(int userId) {
        expireEndedActiveContracts();
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM labor_contracts
                    WHERE user_id = ?
                      AND status = 'ACTIVE'
                ) AS has_active_contract
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return !rs.getBoolean("has_active_contract");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int expireEndedActiveContracts() {
        String sql = """
                UPDATE labor_contracts
                SET status = 'EXPIRED',
                    updated_at = CURRENT_TIMESTAMP
                WHERE status = 'ACTIVE'
                  AND end_date IS NOT NULL
                  AND end_date < CURRENT_DATE
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private String baseSelect() {
        return """
                SELECT lc.*, u.employee_code, u.full_name AS employee_name, u.email AS employee_email,
                       terminated_by_user.full_name AS terminated_by_name
                FROM labor_contracts lc
                JOIN users u ON lc.user_id = u.id
                LEFT JOIN users terminated_by_user ON lc.terminated_by = terminated_by_user.id
                """;
    }

    private void fillEditableFields(PreparedStatement ps, LaborContract contract) throws Exception {
        ps.setInt(1, contract.getUserId());
        ps.setString(2, contract.getContractCode());
        ps.setString(3, contract.getContractType());
        setNullableDate(ps, 4, contract.getStartDate());
        setNullableDate(ps, 5, contract.getEndDate());
        ps.setBigDecimal(6, contract.getBaseSalary());
        ps.setString(7, contract.getWorkingTime());
        ps.setString(8, contract.getWorkLocation());
        ps.setString(9, contract.getStatus());
        ps.setString(10, contract.getNote());
    }

    private void appendSearchFilters(StringBuilder sql, List<Object> params, Integer userId, String keyword,
                                     String contractType, String status) {
        if (userId != null) {
            sql.append(" AND lc.user_id = ?");
            params.add(userId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                     AND (
                        LOWER(lc.contract_code) LIKE ?
                        OR LOWER(u.full_name) LIKE ?
                        OR LOWER(u.employee_code) LIKE ?
                        OR LOWER(u.email) LIKE ?
                     )
                    """);
            String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }
        if (contractType != null && !contractType.trim().isEmpty()) {
            sql.append(" AND lc.contract_type = ?");
            params.add(contractType.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND lc.status = ?");
            params.add(status.trim());
        }
    }

    private void bindParams(PreparedStatement ps, List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    private LaborContract mapRow(ResultSet rs) throws Exception {
        LaborContract contract = new LaborContract();
        contract.setId(rs.getInt("id"));
        contract.setUserId(rs.getInt("user_id"));
        contract.setEmployeeCode(rs.getString("employee_code"));
        contract.setEmployeeName(rs.getString("employee_name"));
        contract.setEmployeeEmail(rs.getString("employee_email"));
        contract.setContractCode(rs.getString("contract_code"));
        contract.setContractType(rs.getString("contract_type"));
        contract.setStartDate(getNullableLocalDate(rs, "start_date"));
        contract.setEndDate(getNullableLocalDate(rs, "end_date"));
        contract.setBaseSalary(rs.getBigDecimal("base_salary"));
        contract.setWorkingTime(rs.getString("working_time"));
        contract.setWorkLocation(rs.getString("work_location"));
        contract.setStatus(rs.getString("status"));
        contract.setNote(rs.getString("note"));
        contract.setTerminatedAt(getNullableLocalDateTime(rs, "terminated_at"));
        int terminatedBy = rs.getInt("terminated_by");
        contract.setTerminatedBy(rs.wasNull() ? null : terminatedBy);
        contract.setTerminatedByName(rs.getString("terminated_by_name"));
        contract.setTerminationReason(rs.getString("termination_reason"));
        contract.setCreatedAt(getNullableLocalDateTime(rs, "created_at"));
        contract.setUpdatedAt(getNullableLocalDateTime(rs, "updated_at"));
        return contract;
    }

    private LocalDate getNullableLocalDate(ResultSet rs, String columnName) throws Exception {
        Date date = rs.getDate(columnName);
        return date == null ? null : date.toLocalDate();
    }

    private LocalDateTime getNullableLocalDateTime(ResultSet rs, String columnName) throws Exception {
        Timestamp timestamp = rs.getTimestamp(columnName);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private void setNullableDate(PreparedStatement ps, int parameterIndex, LocalDate value) throws Exception {
        if (value == null) {
            ps.setDate(parameterIndex, null);
        } else {
            ps.setDate(parameterIndex, Date.valueOf(value));
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    public BigDecimal findActiveSalaryByUserId(int userId) {
        expireEndedActiveContracts();
        String sql = "SELECT base_salary FROM labor_contracts WHERE user_id = ? AND status = 'ACTIVE' LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("base_salary");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}
