package dao;

import model.DependentChangeRequest;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class DependentChangeRequestDAO {

    public void create(DependentChangeRequest dcr) throws SQLException {
        String sql = "INSERT INTO dependent_change_requests (request_id, number_of_dependents, document_path) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dcr.getRequestId());
            ps.setInt(2, dcr.getNumberOfDependents());
            ps.setString(3, dcr.getDocumentPath());
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
                    dcr.setNumberOfDependents(rs.getInt("number_of_dependents"));
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

    public void updateUserDependentNumber(int userId, int dependentCount) {
        String updateSql = "UPDATE dependent_number SET dependent = ?, effective_date = ? WHERE user_id = ?";
        String insertSql = "INSERT INTO dependent_number (user_id, dependent, effective_date) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, dependentCount);
                psUpdate.setDate(2, Date.valueOf(LocalDate.now()));
                psUpdate.setInt(3, userId);
                
                int rowsUpdated = psUpdate.executeUpdate();
                if (rowsUpdated == 0) {
                    try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                        psInsert.setInt(1, userId);
                        psInsert.setInt(2, dependentCount);
                        psInsert.setDate(3, Date.valueOf(LocalDate.now()));
                        psInsert.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
