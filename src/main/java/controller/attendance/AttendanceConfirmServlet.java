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

        LocalDate today = LocalDate.now();
        int month = parseInt(request.getParameter("month"), today.getMonthValue());
        int year = parseInt(request.getParameter("year"), today.getYear());

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

        int month = parseInt(request.getParameter("month"), LocalDate.now().getMonthValue());
        int year = parseInt(request.getParameter("year"), LocalDate.now().getYear());
        String action = request.getParameter("action");

        try {
            if ("dept_confirm".equals(action)) {
                int deptId = Integer.parseInt(request.getParameter("departmentId"));
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
            } else if ("hr_finalize".equals(action)) {
                Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");
                boolean isHRManager = "HR_MANAGER".equalsIgnoreCase(currentUser.getRoleName());
                if (isHRManager && userPermissions != null && canFinalizeAttendance(userPermissions)) {
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

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private boolean canFinalizeAttendance(Set<String> userPermissions) {
        return userPermissions.contains("ATTENDANCE_FINALIZE_HR");
    }

}
