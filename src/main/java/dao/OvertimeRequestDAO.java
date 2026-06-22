package dao;

import model.OvertimeRequest;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class OvertimeRequestDAO {

    public int createOvertimeRequest(OvertimeRequest req) {
        String sql = "INSERT INTO overtime_requests (request_id, department_id, overtime_date, shift_start, shift_end, total_hours, reason, created_by, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, req.getRequestId());
            ps.setInt(2, req.getDepartmentId());
            ps.setDate(3, Date.valueOf(req.getOvertimeDate()));
            ps.setTime(4, Time.valueOf(req.getShiftStart()));
            ps.setTime(5, Time.valueOf(req.getShiftEnd()));
            ps.setDouble(6, req.getTotalHours());
            ps.setString(7, req.getReason());
            ps.setInt(8, req.getCreatedBy());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public OvertimeRequest getByRequestId(int requestId) {
        String sql = "SELECT * FROM overtime_requests WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OvertimeRequest req = new OvertimeRequest();
                    req.setId(rs.getInt("id"));
                    req.setRequestId(rs.getInt("request_id"));
                    req.setDepartmentId(rs.getInt("department_id"));
                    req.setOvertimeDate(rs.getDate("overtime_date").toLocalDate());
                    req.setShiftStart(rs.getTime("shift_start").toLocalTime());
                    req.setShiftEnd(rs.getTime("shift_end").toLocalTime());
                    req.setTotalHours(rs.getDouble("total_hours"));
                    req.setReason(rs.getString("reason"));
                    req.setCreatedBy(rs.getInt("created_by"));
                    if(rs.getTimestamp("created_at") != null) req.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    if(rs.getTimestamp("updated_at") != null) req.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return req;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkDuplicateOvertime(int userId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM overtime_participants op " +
                     "JOIN overtime_requests oreq ON op.overtime_request_id = oreq.id " +
                     "WHERE op.user_id = ? AND oreq.overtime_date = ? " +
                     "AND op.status IN ('PENDING', 'REGISTERED', 'COMPLETED', 'PARTIAL')";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public model.OvertimeDetail getOvertimeDetailByUserAndDate(int userId, LocalDate date) {
        String sql = "SELECT op.status, op.hours_actual, " +
                "oreq.id AS overtime_request_id, oreq.overtime_date, oreq.shift_start, oreq.shift_end, oreq.reason, " +
                "u.full_name, u.employee_code " +
                "FROM overtime_participants op " +
                "JOIN overtime_requests oreq ON op.overtime_request_id = oreq.id " +
                "JOIN users u ON op.user_id = u.id " +
                "WHERE op.user_id = ? AND oreq.overtime_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    model.OvertimeDetail detail = new model.OvertimeDetail();
                    detail.setUserId(userId);
                    detail.setUserFullName(rs.getString("full_name"));
                    detail.setEmployeeCode(rs.getString("employee_code"));
                    detail.setOvertimeDate(rs.getDate("overtime_date").toLocalDate());
                    detail.setShiftStart(rs.getTime("shift_start").toLocalTime());
                    detail.setShiftEnd(rs.getTime("shift_end").toLocalTime());
                    detail.setReason(rs.getString("reason"));
                    detail.setRequestStatus(rs.getString("status"));
                    detail.setHoursActual(rs.getDouble("hours_actual"));

                    // Fetch participants for the same request
                    int overtimeRequestId = rs.getInt("overtime_request_id");
                    OvertimeParticipantDAO participantDAO = new OvertimeParticipantDAO();
                    detail.setParticipants(participantDAO.getByOvertimeRequestId(overtimeRequestId));

                    return detail;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
