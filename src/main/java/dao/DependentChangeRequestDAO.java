package dao;

import model.DependentChangeRequest;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class DependentChangeRequestDAO {

    public void create(DependentChangeRequest dcr) throws SQLException {
        String sql = "INSERT INTO dependent_change_requests (request_id, change_type, dependent_id, dependent_name, dependent_dob, dependent_id_number, relationship, document_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dcr.getRequestId());
            ps.setString(2, dcr.getChangeType());
            if (dcr.getDependentId() != null) {
                ps.setInt(3, dcr.getDependentId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, dcr.getDependentName());
            if (dcr.getDependentDob() != null) {
                ps.setDate(5, Date.valueOf(dcr.getDependentDob()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, dcr.getDependentIdNumber());
            ps.setString(7, dcr.getRelationship());
            ps.setString(8, dcr.getDocumentPath());
            ps.executeUpdate();
        }
    }

    public DependentChangeRequest getByRequestId(int requestId) {
        String sql = "SELECT * FROM dependent_change_requests WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DependentChangeRequest dcr = new DependentChangeRequest();
                    dcr.setId(rs.getInt("id"));
                    dcr.setRequestId(rs.getInt("request_id"));
                    dcr.setChangeType(rs.getString("change_type"));
                    int depId = rs.getInt("dependent_id");
                    if (!rs.wasNull()) {
                        dcr.setDependentId(depId);
                    }
                    dcr.setDependentName(rs.getString("dependent_name"));
                    Date dob = rs.getDate("dependent_dob");
                    if (dob != null) {
                        dcr.setDependentDob(dob.toLocalDate());
                    }
                    dcr.setDependentIdNumber(rs.getString("dependent_id_number"));
                    dcr.setRelationship(rs.getString("relationship"));
                    dcr.setDocumentPath(rs.getString("document_path"));
                    dcr.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return dcr;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void approveDependentChange(int userId, DependentChangeRequest dcr) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Apply changes to dependents table
                if ("ADD".equals(dcr.getChangeType())) {
                    String insertDepSql = "INSERT INTO dependents (user_id, dependent_name, dependent_dob, dependent_id_number, relationship, status, effective_date) VALUES (?, ?, ?, ?, ?, 'ACTIVE', ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertDepSql)) {
                        ps.setInt(1, userId);
                        ps.setString(2, dcr.getDependentName());
                        ps.setDate(3, Date.valueOf(dcr.getDependentDob()));
                        ps.setString(4, dcr.getDependentIdNumber());
                        ps.setString(5, dcr.getRelationship());
                        ps.setDate(6, Date.valueOf(LocalDate.now()));
                        ps.executeUpdate();
                    }
                } else if ("UPDATE".equals(dcr.getChangeType())) {
                    String updateDepSql = "UPDATE dependents SET dependent_name = ?, dependent_dob = ?, dependent_id_number = ?, relationship = ?, status = 'ACTIVE', effective_date = ? WHERE id = ? AND user_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(updateDepSql)) {
                        ps.setString(1, dcr.getDependentName());
                        ps.setDate(2, Date.valueOf(dcr.getDependentDob()));
                        ps.setString(3, dcr.getDependentIdNumber());
                        ps.setString(4, dcr.getRelationship());
                        ps.setDate(5, Date.valueOf(LocalDate.now()));
                        ps.setInt(6, dcr.getDependentId());
                        ps.setInt(7, userId);
                        ps.executeUpdate();
                    }
                } else if ("REMOVE".equals(dcr.getChangeType())) {
                    String deleteDepSql = "UPDATE dependents SET status = 'INACTIVE', effective_date = ? WHERE id = ? AND user_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(deleteDepSql)) {
                        ps.setDate(1, Date.valueOf(LocalDate.now()));
                        ps.setInt(2, dcr.getDependentId());
                        ps.setInt(3, userId);
                        ps.executeUpdate();
                    }
                }

                // 2. Count active dependents for this user
                int activeCount = 0;
                String countSql = "SELECT COUNT(*) FROM dependents WHERE user_id = ? AND status = 'ACTIVE'";
                try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                    ps.setInt(1, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            activeCount = rs.getInt(1);
                        }
                    }
                }

                // 3. Update or Insert into dependent_number table (for backward compatibility / active counts history)
                String updateNumSql = "UPDATE dependent_number SET dependent = ?, effective_date = ? WHERE user_id = ?";
                String insertNumSql = "INSERT INTO dependent_number (user_id, dependent, effective_date) VALUES (?, ?, ?)";
                
                try (PreparedStatement psUpdate = conn.prepareStatement(updateNumSql)) {
                    psUpdate.setInt(1, activeCount);
                    psUpdate.setDate(2, Date.valueOf(LocalDate.now()));
                    psUpdate.setInt(3, userId);
                    
                    int rowsUpdated = psUpdate.executeUpdate();
                    if (rowsUpdated == 0) {
                        try (PreparedStatement psInsert = conn.prepareStatement(insertNumSql)) {
                            psInsert.setInt(1, userId);
                            psInsert.setInt(2, activeCount);
                            psInsert.setDate(3, Date.valueOf(LocalDate.now()));
                            psInsert.executeUpdate();
                        }
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
