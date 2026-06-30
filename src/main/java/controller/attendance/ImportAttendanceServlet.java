package controller.attendance;

import dao.AttendanceDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.AttendanceLog;
import model.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/admin/attendance/import")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class ImportAttendanceServlet extends HttpServlet {


    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/attendance/import.jsp").forward(req, resp);
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Part filePart = req.getPart("excelFile");
            InputStream fileContent = filePart.getInputStream();

            Workbook workbook = new XSSFWorkbook(fileContent);
            Sheet sheet = workbook.getSheetAt(0);

            List<AttendanceLog> logs = new ArrayList<>();
            UserDAO userDAO = new UserDAO();

            java.util.Set<Integer> activeUserIds = new java.util.HashSet<>();
            try (java.sql.Connection conn = util.DBConnection.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE active = TRUE");
                 java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    activeUserIds.add(rs.getInt("id"));
                }
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;


                int employeeId;
                Cell cell0 = row.getCell(0);
                if (cell0 == null) continue;
                if (cell0.getCellType() == CellType.NUMERIC) {
                    employeeId = (int) cell0.getNumericCellValue();
                } else {

                    try {
                        employeeId = Integer.parseInt(cell0.getStringCellValue().trim());
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }


                if (!activeUserIds.contains(employeeId)) {
                    continue;
                }

                LocalDate workDate = getCellLocalDate(row.getCell(1));
                LocalDateTime checkIn = getCellLocalDateTime(row.getCell(2));
                LocalDateTime checkOut = getCellLocalDateTime(row.getCell(3));

                AttendanceLog log = new AttendanceLog();
                log.setEmployeeId(employeeId);
                log.setWorkDate(workDate);
                log.setCheckIn(checkIn);
                log.setCheckOut(checkOut);
                logs.add(log);
            }

            workbook.close();


            AttendanceDAO dao = new AttendanceDAO();
            int count = dao.saveAllAttendanceLogs(logs);


            dao.processAllPendingLogs();


            req.getSession().setAttribute("success", "Đã import thành công " + count + " bản ghi!");
            resp.sendRedirect(req.getContextPath() + "/admin/attendance/import");

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Import thất bại: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/attendance/import.jsp").forward(req, resp);
        }
    }


    private LocalDate getCellLocalDate(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else {
                return LocalDate.parse(cell.getStringCellValue());
            }
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime getCellLocalDateTime(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else {
                String str = cell.getStringCellValue().replace(" ", "T");
                return LocalDateTime.parse(str);
            }
        } catch (Exception e) {
            return null;
        }
    }
}