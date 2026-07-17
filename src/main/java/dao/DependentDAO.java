package dao;

import model.Dependent;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DependentDAO {

    public List<Dependent> getActiveDependentsByUserId(int userId) {
        List<Dependent> list = new ArrayList<>();
        String sql = "SELECT * FROM dependents WHERE user_id = ? AND status = 'ACTIVE' ORDER BY dependent_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Dependent dep = new Dependent();
                    dep.setId(rs.getInt("id"));
                    dep.setUserId(rs.getInt("user_id"));
                    dep.setDependentName(rs.getString("dependent_name"));
                    dep.setDependentDob(rs.getDate("dependent_dob").toLocalDate());
                    dep.setDependentIdNumber(rs.getString("dependent_id_number"));
                    dep.setRelationship(rs.getString("relationship"));
                    dep.setStatus(rs.getString("status"));
                    dep.setEffectiveDate(rs.getDate("effective_date").toLocalDate());
                    dep.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    list.add(dep);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Dependent getById(int id) {
        String sql = "SELECT * FROM dependents WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Dependent dep = new Dependent();
                    dep.setId(rs.getInt("id"));
                    dep.setUserId(rs.getInt("user_id"));
                    dep.setDependentName(rs.getString("dependent_name"));
                    dep.setDependentDob(rs.getDate("dependent_dob").toLocalDate());
                    dep.setDependentIdNumber(rs.getString("dependent_id_number"));
                    dep.setRelationship(rs.getString("relationship"));
                    dep.setStatus(rs.getString("status"));
                    dep.setEffectiveDate(rs.getDate("effective_date").toLocalDate());
                    dep.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return dep;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
