package controller.attendance;

import dao.AttendanceConfirmDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.DepartmentConfirmStatusDTO;
import model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

@WebServlet("/attendance/confirm")
public class AttendanceConfirmServlet extends HttpServlet {
    private final AttendanceConfirmDAO confirmDAO = new AttendanceConfirmDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        Integer month = parseOptionalInteger(request.getParameter("month"), previousMonth.getMonthValue());
        Integer year = parseOptionalInteger(request.getParameter("year"), previousMonth.getYear());
        if (month == null || year == null || !isValidMonthYear(month, year)) {
            session.setAttribute("errorMsg", "Invalid month or year.");
            response.sendRedirect(request.getContextPath() + "/attendance/confirm");
            return;
        }

        List<DepartmentConfirmStatusDTO> deptStatuses = confirmDAO.getDepartmentLockStatuses(month, year);
        String overallStatus = confirmDAO.getOverallStatus(month, year);

        boolean allConfirmed = true;
        if (deptStatuses.isEmpty()) {
            allConfirmed = false;
        } else {
            for (DepartmentConfirmStatusDTO dept : deptStatuses) {
                if (!"CONFIRMED".equals(dept.getStatus())) {
                    allConfirmed = false;
                    break;
                }
            }
        }

        boolean isHRManager = "HR_MANAGER".equalsIgnoreCase(currentUser.getRoleName());

        Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");
        if (userPermissions != null) {
            isHRManager = isHRManager && canFinalizeAttendance(userPermissions);
        }

        request.setAttribute("deptStatuses", deptStatuses);
        request.setAttribute("overallStatus", overallStatus);
        request.setAttribute("allConfirmed", allConfirmed);
        request.setAttribute("confirmationAllowed", isConfirmationAllowed(month, year));
        request.setAttribute("selectedMonth", month);
        request.setAttribute("selectedYear", year);
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("isHRManager", isHRManager);

        request.getRequestDispatcher("/WEB-INF/views/attendance/attendance_confirm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer month = parseInteger(request.getParameter("month"));
        Integer year = parseInteger(request.getParameter("year"));
        String action = request.getParameter("action");

        if (month == null || year == null || !isValidMonthYear(month, year)) {
            session.setAttribute("errorMsg", "Invalid month or year.");
            response.sendRedirect(request.getContextPath() + "/attendance/confirm");
            return;
        }
        if (!isConfirmationAllowed(month, year)) {
            session.setAttribute("errorMsg", "Attendance can only be confirmed for the previous month from day 5 to day 10.");
            response.sendRedirect(request.getContextPath() + "/attendance/confirm?month=" + month + "&year=" + year);
            return;
        }

        try {
            if ("dept_confirm".equals(action)) {
                int deptId = Integer.parseInt(request.getParameter("departmentId"));
                if ("APPROVED".equals(confirmDAO.getOverallStatus(month, year))) {
                    session.setAttribute("errorMsg", "Attendance has already been finalized for this month.");
                } else {
                    List<DepartmentConfirmStatusDTO> deptStatuses = confirmDAO.getDepartmentLockStatuses(month, year);
                    boolean isManager = false;
                    for (DepartmentConfirmStatusDTO dept : deptStatuses) {
                        if (dept.getDepartmentId() == deptId && dept.getManagerUserId() == currentUser.getId()) {
                            isManager = true;
                            break;
                        }
                    }
                    if (isManager) {
                        confirmDAO.logAction(month, year, "DEPT_CONFIRM", currentUser.getId(), deptId, "Confirmed by Dept Manager");
                        session.setAttribute("successMsg", "Department attendance confirmed successfully.");
                    } else {
                        session.setAttribute("errorMsg", "You are not the manager of this department.");
                    }
                }
            } else if ("hr_finalize".equals(action)) {
                Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");
                boolean isHRManager = "HR_MANAGER".equalsIgnoreCase(currentUser.getRoleName());
                if (isHRManager && userPermissions != null && canFinalizeAttendance(userPermissions)) {
                    if ("APPROVED".equals(confirmDAO.getOverallStatus(month, year))) {
                        session.setAttribute("errorMsg", "Attendance has already been finalized for this month.");
                    } else {
                        List<DepartmentConfirmStatusDTO> deptStatuses = confirmDAO.getDepartmentLockStatuses(month, year);
                        boolean allConfirmed = !deptStatuses.isEmpty()
                                && deptStatuses.stream().allMatch(dept -> "CONFIRMED".equals(dept.getStatus()));
                        if (!allConfirmed) {
                            session.setAttribute("errorMsg", "All departments must confirm attendance before HR can finalize it.");
                        } else if (confirmDAO.finalizeAttendance(month, year, currentUser.getId())) {
                            session.setAttribute("successMsg", "Attendance finalized and snapshot created successfully.");
                        } else {
                            session.setAttribute("errorMsg", "Attendance has already been finalized for this month.");
                        }
                    }
                } else {
                    session.setAttribute("errorMsg", "Permission denied.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "An error occurred: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/attendance/confirm?month=" + month + "&year=" + year);
    }

    private Integer parseOptionalInteger(String value, int defaultValue) {
        return value == null || value.isBlank() ? defaultValue : parseInteger(value);
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean canFinalizeAttendance(Set<String> userPermissions) {
        return userPermissions.contains("ATTENDANCE_FINALIZE_HR");
    }

    private boolean isValidMonthYear(int month, int year) {
        return month >= 1 && month <= 12 && year >= 2000 && year <= 9999;
    }

    private boolean isConfirmationAllowed(int month, int year) {
        LocalDate today = LocalDate.now();
        YearMonth previousMonth = YearMonth.from(today).minusMonths(1);
        return today.getDayOfMonth() >= 5 && today.getDayOfMonth() <= 10
                && previousMonth.getMonthValue() == month
                && previousMonth.getYear() == year;
    }

}
