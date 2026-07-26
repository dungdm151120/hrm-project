package controller.report;

import dao.DepartmentDAO;
import dao.AttendanceReportDAO;
import model.Department;
import model.User;
import model.AttendanceReportRowDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@WebServlet("/reports/attendance")
public class AttendanceReportServlet extends HttpServlet {
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final AttendanceReportDAO reportDAO = new AttendanceReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        @SuppressWarnings("unchecked")
        Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");
        boolean canViewAll = userPermissions != null && userPermissions.contains("ATTENDANCE_VIEW_ALL");
        boolean canViewDepartment = userPermissions != null && userPermissions.contains("ATTENDANCE_VIEW_DEPARTMENT");
        boolean canViewReport = userPermissions != null && userPermissions.contains("ATTENDANCE_REPORT_VIEW");
        if (!canViewReport || (!canViewAll && !canViewDepartment)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to view reports.");
            return;
        }

        boolean isRestricted = !canViewAll && canViewDepartment;

        List<Department> departments = new ArrayList<>();
        if (isRestricted) {
            Integer userDeptId = currentUser.getDepartmentId();
            if (userDeptId != null) {
                Department dept = departmentDAO.getDepartmentById(userDeptId);
                if (dept != null) {
                    departments.add(dept);
                }
            }
        } else {
            departments = departmentDAO.getActiveDepartments();
        }

        LocalDate today = LocalDate.now();
        
        // Parse filters
        String deptParam = request.getParameter("departmentId");
        Integer selectedDeptId = null;
        if (isRestricted) {
            selectedDeptId = currentUser.getDepartmentId();
        } else if (deptParam != null && !deptParam.isEmpty() && !"all".equalsIgnoreCase(deptParam)) {
            try {
                selectedDeptId = Integer.parseInt(deptParam);
            } catch (NumberFormatException ignored) {}
        }

        String periodType = request.getParameter("periodType");
        periodType = periodType == null || periodType.isEmpty() ? "month" : periodType.toLowerCase();
        if (!"month".equals(periodType) && !"quarter".equals(periodType) && !"year".equals(periodType)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid report period.");
            return;
        }

        int selectedMonth = today.getMonthValue();
        Integer requestedMonth = parseInteger(request.getParameter("month"));
        if (request.getParameter("month") != null && !request.getParameter("month").isBlank()) {
            if (requestedMonth == null || requestedMonth < 1 || requestedMonth > 12) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid month.");
                return;
            }
            selectedMonth = requestedMonth;
        }

        int selectedQuarter = (today.getMonthValue() - 1) / 3 + 1;
        Integer requestedQuarter = parseInteger(request.getParameter("quarter"));
        if (request.getParameter("quarter") != null && !request.getParameter("quarter").isBlank()) {
            if (requestedQuarter == null || requestedQuarter < 1 || requestedQuarter > 4) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid quarter.");
                return;
            }
            selectedQuarter = requestedQuarter;
        }

        int selectedYear = today.getYear();
        Integer requestedYear = parseInteger(request.getParameter("year"));
        if (request.getParameter("year") != null && !request.getParameter("year").isBlank()) {
            if (requestedYear == null || requestedYear < 2000 || requestedYear > today.getYear() + 1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid year.");
                return;
            }
            selectedYear = requestedYear;
        }

        // Opening Attendance Report should immediately show the current month's report.
        String action = request.getParameter("action");
        boolean isGenerated = action == null || action.isEmpty()
                || "generate".equalsIgnoreCase(action);

        if (isGenerated) {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now();

            if ("month".equalsIgnoreCase(periodType)) {
                startDate = LocalDate.of(selectedYear, selectedMonth, 1);
                endDate = startDate.plusMonths(1).minusDays(1);
            } else if ("quarter".equalsIgnoreCase(periodType)) {
                int startMonth = (selectedQuarter - 1) * 3 + 1;
                startDate = LocalDate.of(selectedYear, startMonth, 1);
                endDate = startDate.plusMonths(3).minusDays(1);
            } else if ("year".equalsIgnoreCase(periodType)) {
                startDate = LocalDate.of(selectedYear, 1, 1);
                endDate = LocalDate.of(selectedYear, 12, 31);
            }

            int expectedWorkdays = calculateExpectedWorkdays(startDate, endDate);

            List<AttendanceReportRowDTO> reportRows = reportDAO.generateAttendanceReport(startDate, endDate, selectedDeptId);

            // Compute aggregations
            double totalActualWorkHours = 0;
            double totalExpectedWorkHours = 0;
            double totalActualOvertimeHours = 0;
            double totalRegisteredOvertimeHours = 0;
            double totalLeaveDays = 0;
            double totalAbsentDays = 0;

            AttendanceReportRowDTO hardestWorking = null;
            double maxHours = -1.0;

            AttendanceReportRowDTO mostPunctual = null;
            double minIrregularities = Double.MAX_VALUE;

            AttendanceReportRowDTO lowestWorking = null;
            double minHours = Double.MAX_VALUE;
            AttendanceReportRowDTO leastPunctual = null;
            int maxIrregularities = 0;
            int punctualLeaveLimit = getPunctualLeaveLimit(periodType);

            for (AttendanceReportRowDTO row : reportRows) {
                row.setExpectedWorkdays(expectedWorkdays);
                
                totalActualWorkHours += row.getTotalWorkHours();
                totalExpectedWorkHours += expectedWorkdays * 8.0;
                totalActualOvertimeHours += row.getTotalOvertimeHours();
                totalRegisteredOvertimeHours += row.getRegisteredOvertimeHours();
                totalLeaveDays += row.getLeaveDays();
                totalAbsentDays += row.getAbsentDays();

                // Hardest working check
                double workAndOt = row.getOnsiteWorkAndOtHours();
                if (workAndOt > maxHours) {
                    maxHours = workAndOt;
                    hardestWorking = row;
                }

                // Most punctual check: min (lateDays + earlyLeaveDays + forgotCheckInDays)
                // Filter out employees who never worked in this period (presentDays = 0) to avoid false punctuality
                if (row.getPresentDays() > 0
                        && row.getAbsentDays() + row.getLeaveDays() <= punctualLeaveLimit) {
                    double irregularities = row.getLateDays() + row.getEarlyLeaveDays()
                            + row.getForgotCheckInDays() + row.getForgotCheckOutDays();
                    if (irregularities < minIrregularities) {
                        minIrregularities = irregularities;
                        mostPunctual = row;
                    }

                    if (workAndOt < minHours) {
                        minHours = workAndOt;
                        lowestWorking = row;
                    }
                }

                int irregularities = row.getLateDays() + row.getEarlyLeaveDays()
                        + row.getForgotCheckInDays() + row.getForgotCheckOutDays();
                if (irregularities > maxIrregularities) {
                    maxIrregularities = irregularities;
                    leastPunctual = row;
                }
            }

            request.setAttribute("reportRows", reportRows);
            request.setAttribute("expectedWorkdays", expectedWorkdays);
            request.setAttribute("totalActualWorkHours", totalActualWorkHours);
            request.setAttribute("totalExpectedWorkHours", totalExpectedWorkHours);
            request.setAttribute("totalActualOvertimeHours", totalActualOvertimeHours);
            request.setAttribute("totalRegisteredOvertimeHours", totalRegisteredOvertimeHours);
            request.setAttribute("totalLeaveDays", totalLeaveDays);
            request.setAttribute("totalAbsentDays", totalAbsentDays);
            request.setAttribute("hardestWorking", hardestWorking);
            request.setAttribute("mostPunctual", mostPunctual);
            request.setAttribute("lowestWorking", lowestWorking);
            request.setAttribute("leastPunctual", leastPunctual);
        }

        // Available years for dropdown option (e.g. current year +/- 3 years)
        List<Integer> years = new ArrayList<>();
        for (int y = today.getYear() - 3; y <= today.getYear() + 3; y++) {
            years.add(y);
        }

        request.setAttribute("departments", departments);
        request.setAttribute("selectedDeptId", selectedDeptId);
        request.setAttribute("periodType", periodType);
        request.setAttribute("selectedMonth", selectedMonth);
        request.setAttribute("selectedQuarter", selectedQuarter);
        request.setAttribute("selectedYear", selectedYear);
        request.setAttribute("years", years);
        request.setAttribute("isRestricted", isRestricted);
        request.setAttribute("isGenerated", isGenerated);

        request.getRequestDispatcher("/WEB-INF/views/report/attendance_report.jsp").forward(request, response);
    }

    private int calculateExpectedWorkdays(LocalDate start, LocalDate end) {
        int count = 0;
        LocalDate curr = start;
        while (!curr.isAfter(end)) {
            if (curr.getDayOfWeek().getValue() < 6) { // Mon-Fri
                count++;
            }
            curr = curr.plusDays(1);
        }
        return count;
    }

    private Integer parseInteger(String value) {
        try {
            return value == null || value.isBlank() ? null : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int getPunctualLeaveLimit(String periodType) {
        if ("quarter".equals(periodType)) {
            return 4;
        }
        if ("year".equals(periodType)) {
            return 12;
        }
        return 1;
    }
}
