package dao;

import model.Holiday;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HolidayDAO {

    // Lấy tất cả ngày lễ
    public List<Holiday> getAllHolidays() {
        List<Holiday> list = new ArrayList<>();
        String sql = "SELECT holiday_date, holiday_name FROM holidays ORDER BY holiday_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocalDate date = rs.getDate("holiday_date").toLocalDate();
                String name = rs.getString("holiday_name");
                list.add(new Holiday(date, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách ngày lễ trong một khoảng thời gian (thường là tháng)
    public List<Holiday> getHolidaysBetween(LocalDate start, LocalDate end) {
        List<Holiday> list = new ArrayList<>();
        String sql = "SELECT holiday_date, holiday_name FROM holidays WHERE holiday_date BETWEEN ? AND ? ORDER BY holiday_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("holiday_date").toLocalDate();
                    String name = rs.getString("holiday_name");
                    list.add(new Holiday(date, name));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Kiểm tra một ngày có phải là ngày lễ không
    public boolean isHoliday(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM holidays WHERE holiday_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
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

    // Lấy tên ngày lễ
    public String getHolidayName(LocalDate date) {
        String sql = "SELECT holiday_name FROM holidays WHERE holiday_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("holiday_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm mới một ngày lễ (nếu sau này cần CRUD)
    public boolean addHoliday(Holiday holiday) {
        String sql = "INSERT INTO holidays (holiday_date, holiday_name) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(holiday.getHolidayDate()));
            ps.setString(2, holiday.getHolidayName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa một ngày lễ
    public boolean deleteHoliday(LocalDate date) {
        String sql = "DELETE FROM holidays WHERE holiday_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}