package dao;

import model.OvertimeParticipant;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OvertimeParticipantDAO {

    public void addParticipants(List<OvertimeParticipant> participants) {
        String sql = "INSERT INTO overtime_participants (overtime_request_id, user_id, status, hours_actual, created_at) " +
                     "VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            for (OvertimeParticipant p : participants) {
                ps.setInt(1, p.getOvertimeRequestId());
                ps.setInt(2, p.getUserId());
                ps.setString(3, p.getStatus() != null ? p.getStatus() : "PENDING");
                ps.setDouble(4, p.getHoursActual());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<OvertimeParticipant> getByOvertimeRequestId(int overtimeRequestId) {
        List<OvertimeParticipant> list = new ArrayList<>();
        String sql = "SELECT op.*, u.full_name, u.employee_code, pos.name AS position_name " +
                     "FROM overtime_participants op " +
                     "JOIN users u ON op.user_id = u.id " +
                     "LEFT JOIN positions pos ON u.position_id = pos.id " +
                     "WHERE op.overtime_request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, overtimeRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OvertimeParticipant p = new OvertimeParticipant();
                    p.setId(rs.getInt("id"));
                    p.setOvertimeRequestId(rs.getInt("overtime_request_id"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setStatus(rs.getString("status"));
                    p.setHoursActual(rs.getDouble("hours_actual"));
                    if(rs.getTimestamp("confirmed_at") != null) p.setConfirmedAt(rs.getTimestamp("confirmed_at").toLocalDateTime());
                    if(rs.getTimestamp("created_at") != null) p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    
                    p.setUserFullName(rs.getString("full_name"));
                    p.setEmployeeCode(rs.getString("employee_code"));
                    p.setPositionName(rs.getString("position_name"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateParticipantStatusAndHours(int participantId, String status, double hoursActual) {
        String sql = "UPDATE overtime_participants SET status = ?, hours_actual = ?, confirmed_at = NOW() WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setDouble(2, hoursActual);
            ps.setInt(3, participantId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
