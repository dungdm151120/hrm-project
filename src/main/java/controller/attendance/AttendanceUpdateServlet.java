package controller.attendance;

import dao.AttendanceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AttendanceRecord;
import model.AttendanceRecordDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@WebServlet("/attendance/update")
public class AttendanceUpdateServlet extends HttpServlet {
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parsePositiveInteger(request.getParameter("id"));
        if (id == null) {
            redirectToRecords(request, response, "invalid_id");
            return;
        }

        AttendanceRecordDTO record = attendanceDAO.getAttendanceRecordDetailById(id);
        if (record == null) {
            redirectToRecords(request, response, "record_not_found");
            return;
        }

        forwardToForm(request, response, record, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parsePositiveInteger(request.getParameter("id"));
        if (id == null) {
            redirectToRecords(request, response, "invalid_id");
            return;
        }

        AttendanceRecord record = attendanceDAO.getAttendanceRecordById(id);
        AttendanceRecordDTO recordDetail = attendanceDAO.getAttendanceRecordDetailById(id);
        if (record == null || recordDetail == null) {
            redirectToRecords(request, response, "record_not_found");
            return;
        }

        String checkInText = trimToEmpty(request.getParameter("checkIn"));
        String checkOutText = trimToEmpty(request.getParameter("checkOut"));
        String note = trimToEmpty(request.getParameter("note"));

        recordDetail.setCheckInText(checkInText);
        recordDetail.setCheckOutText(checkOutText);
        recordDetail.setNote(note);

        if (note.isEmpty()) {
            forwardToForm(
                    request,
                    response,
                    recordDetail,
                    "Reason for change is required."
            );
            return;
        }
        if (note.length() > 1000) {
            forwardToForm(
                    request,
                    response,
                    recordDetail,
                    "Reason for change must not exceed 1000 characters."
            );
            return;
        }

        LocalDateTime checkIn;
        LocalDateTime checkOut;
        try {
            checkIn = parseNullableTime(checkInText, record.getWorkDate());
            checkOut = parseNullableTime(checkOutText, record.getWorkDate());
        } catch (DateTimeParseException e) {
            forwardToForm(
                    request,
                    response,
                    recordDetail,
                    "Check-in or check-out has an invalid time format."
            );
            return;
        }

        if (checkIn != null && checkOut != null && checkOut.isBefore(checkIn)) {
            forwardToForm(
                    request,
                    response,
                    recordDetail,
                    "Check-out time cannot be before check-in time."
            );
            return;
        }

        record.setCheckIn(checkIn);
        record.setCheckOut(checkOut);
        record.setNote(note);
        attendanceDAO.calculateWorkingHours(record);
        record.setStatus(attendanceDAO.determineStatus(record));

        if (!attendanceDAO.updateAttendanceRecord(record)) {
            recordDetail.setStatus(record.getStatus());
            recordDetail.setTotalWorkHours(record.getTotalWorkHours());
            forwardToForm(
                    request,
                    response,
                    recordDetail,
                    "Unable to update the attendance record. Please try again."
            );
            return;
        }

        response.sendRedirect(
                request.getContextPath()
                        + "/attendance/records?month=" + record.getWorkDate().getMonthValue()
                        + "&year=" + record.getWorkDate().getYear()
                        + "&message=updated"
        );
    }

    private void forwardToForm(
            HttpServletRequest request,
            HttpServletResponse response,
            AttendanceRecordDTO record,
            String error
    ) throws ServletException, IOException {
        request.setAttribute("record", record);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/WEB-INF/views/attendance/update_attendance_record.jsp")
                .forward(request, response);
    }

    private void redirectToRecords(
            HttpServletRequest request,
            HttpServletResponse response,
            String error
    ) throws IOException {
        response.sendRedirect(
                request.getContextPath() + "/attendance/records?error=" + error
        );
    }

    private LocalDateTime parseNullableTime(String value, LocalDate workDate) {
        return value.isEmpty()
                ? null
                : LocalDateTime.of(workDate, LocalTime.parse(value));
    }

    private Integer parsePositiveInteger(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}