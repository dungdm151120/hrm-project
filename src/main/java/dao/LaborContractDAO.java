package dao;

import model.LaborContract;
import model.LaborContractChangeLog;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        return add(contract, null);
    }

    public boolean add(LaborContract contract, Integer changedBy) {
        expireEndedActiveContracts();
        String sql = """
                INSERT INTO labor_contracts (
                    user_id, contract_code, contract_type, start_date, end_date,
                    base_salary, working_time, work_location, status, note, union_member
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            fillEditableFields(ps, contract);
            boolean inserted = ps.executeUpdate() > 0;
            if (!inserted) {
                conn.rollback();
                return false;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    contract.setId(rs.getInt(1));
                }
            }
            insertCreateLogs(conn, contract, changedBy);
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(LaborContract contract) {
        return update(contract, null);
    }

    public boolean update(LaborContract contract, Integer changedBy) {
        expireEndedActiveContracts();
        LaborContract current = findById(contract.getId());
        if (current == null) {
            return false;
        }

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
                    union_member = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            fillEditableFields(ps, contract);
            ps.setInt(12, contract.getId());
            boolean updated = ps.executeUpdate() > 0;
            if (!updated) {
                conn.rollback();
                return false;
            }
            insertUpdateLogs(conn, current, contract, changedBy);
            conn.commit();
            return true;
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
            conn.setAutoCommit(false);
            if (terminatedBy == null || terminatedBy <= 0) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, terminatedBy);
            }
            ps.setString(2, trimToNull(reason));
            ps.setInt(3, id);
            boolean updated = ps.executeUpdate() > 0;
            if (!updated) {
                conn.rollback();
                return false;
            }
            insertTerminateLogs(conn, current, reason, terminatedBy);
            conn.commit();
            return true;
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
        String insertLogSql = """
                INSERT INTO labor_contract_change_logs (
                    contract_id, action, field_name, old_value, new_value, changed_by
                )
                SELECT id, 'AUTO_EXPIRE', 'status', status, 'EXPIRED', NULL
                FROM labor_contracts
                WHERE status = 'ACTIVE'
                  AND end_date IS NOT NULL
                  AND end_date < CURRENT_DATE
                """;
        String sql = """
                UPDATE labor_contracts
                SET status = 'EXPIRED',
                    updated_at = CURRENT_TIMESTAMP
                WHERE status = 'ACTIVE'
                  AND end_date IS NOT NULL
                  AND end_date < CURRENT_DATE
                """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement logPs = conn.prepareStatement(insertLogSql);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                logPs.executeUpdate();
                int expired = ps.executeUpdate();
                conn.commit();
                return expired;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<LaborContractChangeLog> findChangeLogsByContractId(int contractId) {
        List<LaborContractChangeLog> logs = new ArrayList<>();
        String sql = """
                SELECT l.*, lc.contract_code, u.full_name AS employee_name, changed_user.full_name AS changed_by_name
                FROM labor_contract_change_logs l
                JOIN labor_contracts lc ON l.contract_id = lc.id
                JOIN users u ON lc.user_id = u.id
                LEFT JOIN users changed_user ON l.changed_by = changed_user.id
                WHERE l.contract_id = ?
                ORDER BY l.changed_at DESC, l.id DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapChangeLog(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return logs;
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
        ps.setBoolean(11, contract.isUnionMember());
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
        contract.setUnionMember(rs.getBoolean("union_member"));
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

    private LaborContractChangeLog mapChangeLog(ResultSet rs) throws Exception {
        LaborContractChangeLog log = new LaborContractChangeLog();
        log.setId(rs.getInt("id"));
        log.setContractId(rs.getInt("contract_id"));
        log.setContractCode(rs.getString("contract_code"));
        log.setEmployeeName(rs.getString("employee_name"));
        log.setAction(rs.getString("action"));
        log.setFieldName(rs.getString("field_name"));
        log.setOldValue(rs.getString("old_value"));
        log.setNewValue(rs.getString("new_value"));
        int changedBy = rs.getInt("changed_by");
        log.setChangedBy(rs.wasNull() ? null : changedBy);
        log.setChangedByName(rs.getString("changed_by_name"));
        log.setChangedAt(rs.getTimestamp("changed_at"));
        return log;
    }

    private void insertCreateLogs(Connection conn, LaborContract contract, Integer changedBy) throws Exception {
        insertLog(conn, contract.getId(), "CREATE", "user_id", null, String.valueOf(contract.getUserId()), changedBy);
        insertLog(conn, contract.getId(), "CREATE", "contract_code", null, contract.getContractCode(), changedBy);
        insertLog(conn, contract.getId(), "CREATE", "contract_type", null, contract.getContractType(), changedBy);
        insertLog(conn, contract.getId(), "CREATE", "start_date", null, formatValue(contract.getStartDate()), changedBy);
        insertLog(conn, contract.getId(), "CREATE", "end_date", null, formatValue(contract.getEndDate()), changedBy);
        insertLog(conn, contract.getId(), "CREATE", "base_salary", null, formatValue(contract.getBaseSalary()), changedBy);
        insertLog(conn, contract.getId(), "CREATE", "working_time", null, contract.getWorkingTime(), changedBy);
        insertLog(conn, contract.getId(), "CREATE", "work_location", null, contract.getWorkLocation(), changedBy);
        insertLog(conn, contract.getId(), "CREATE", "union_member", null,
                String.valueOf(contract.isUnionMember()), changedBy);
        insertLog(conn, contract.getId(), "CREATE", "status", null, contract.getStatus(), changedBy);
        insertLog(conn, contract.getId(), "CREATE", "note", null, contract.getNote(), changedBy);
    }

    private void insertUpdateLogs(Connection conn, LaborContract oldContract, LaborContract newContract,
                                  Integer changedBy) throws Exception {
        insertIfChanged(conn, newContract.getId(), "UPDATE", "contract_type",
                oldContract.getContractType(), newContract.getContractType(), changedBy);
        insertIfChanged(conn, newContract.getId(), "UPDATE", "start_date",
                formatValue(oldContract.getStartDate()), formatValue(newContract.getStartDate()), changedBy);
        insertIfChanged(conn, newContract.getId(), "UPDATE", "end_date",
                formatValue(oldContract.getEndDate()), formatValue(newContract.getEndDate()), changedBy);
        insertIfChanged(conn, newContract.getId(), "UPDATE", "base_salary",
                formatValue(oldContract.getBaseSalary()), formatValue(newContract.getBaseSalary()), changedBy);
        insertIfChanged(conn, newContract.getId(), "UPDATE", "working_time",
                oldContract.getWorkingTime(), newContract.getWorkingTime(), changedBy);
        insertIfChanged(conn, newContract.getId(), "UPDATE", "work_location",
                oldContract.getWorkLocation(), newContract.getWorkLocation(), changedBy);
        insertIfChanged(conn, newContract.getId(), "UPDATE", "union_member",
                String.valueOf(oldContract.isUnionMember()),
                String.valueOf(newContract.isUnionMember()), changedBy);
        insertIfChanged(conn, newContract.getId(), "UPDATE", "status",
                oldContract.getStatus(), newContract.getStatus(), changedBy);
        insertIfChanged(conn, newContract.getId(), "UPDATE", "note",
                oldContract.getNote(), newContract.getNote(), changedBy);
    }

    private void insertTerminateLogs(Connection conn, LaborContract oldContract, String reason,
                                     Integer changedBy) throws Exception {
        insertLog(conn, oldContract.getId(), "TERMINATE", "status", oldContract.getStatus(), "TERMINATED", changedBy);
        insertLog(conn, oldContract.getId(), "TERMINATE", "end_date",
                formatValue(oldContract.getEndDate()), formatValue(LocalDate.now()), changedBy);
        insertLog(conn, oldContract.getId(), "TERMINATE", "termination_reason",
                oldContract.getTerminationReason(), trimToNull(reason), changedBy);
    }

    private void insertIfChanged(Connection conn, int contractId, String action, String fieldName,
                                 String oldValue, String newValue, Integer changedBy) throws Exception {
        if (!Objects.equals(trimToNull(oldValue), trimToNull(newValue))) {
            insertLog(conn, contractId, action, fieldName, oldValue, newValue, changedBy);
        }
    }

    private void insertLog(Connection conn, int contractId, String action, String fieldName,
                           String oldValue, String newValue, Integer changedBy) throws Exception {
        String sql = """
                INSERT INTO labor_contract_change_logs (
                    contract_id, action, field_name, old_value, new_value, changed_by
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ps.setString(2, action);
            ps.setString(3, fieldName);
            ps.setString(4, trimToNull(oldValue));
            ps.setString(5, trimToNull(newValue));
            if (changedBy == null || changedBy <= 0) {
                ps.setNull(6, java.sql.Types.INTEGER);
            } else {
                ps.setInt(6, changedBy);
            }
            ps.executeUpdate();
        }
    }

    private String formatValue(Object value) {
        return value == null ? null : value.toString();
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
