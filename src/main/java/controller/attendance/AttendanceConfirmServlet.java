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

        boolean isHR = false;
        boolean isBusinessAdmin = false;

        Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");
        if (userPermissions != null) {
            isHR = userPermissions.contains("ATTENDANCE_SEND_TO_BUSINESS");
            isBusinessAdmin = userPermissions.contains("ATTENDANCE_APPROVE_BUSINESS");
        }

        request.setAttribute("deptStatuses", deptStatuses);
        request.setAttribute("overallStatus", overallStatus);
        request.setAttribute("allConfirmed", allConfirmed);
        request.setAttribute("selectedMonth", month);
        request.setAttribute("selectedYear", year);
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("isHR", isHR);
        request.setAttribute("isBusinessAdmin", isBusinessAdmin);

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
            } else if ("hr_send".equals(action)) {
                Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");
                if (userPermissions != null && userPermissions.contains("ATTENDANCE_SEND_TO_BUSINESS")) {
                    confirmDAO.logAction(month, year, "HR_SEND", currentUser.getId(), null, "Sent to Business Admin by HR");
                    session.setAttribute("successMsg", "Request sent to Business Admin successfully.");
                } else {
                    session.setAttribute("errorMsg", "Permission denied.");
                }
            } else if ("business_approve".equals(action)) {
                Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");
                if (userPermissions != null && userPermissions.contains("ATTENDANCE_APPROVE_BUSINESS")) {
                    confirmDAO.logAction(month, year, "BUSINESS_APPROVE", currentUser.getId(), null, "Approved by Business Admin");
                    confirmDAO.createSnapshot(month, year, currentUser.getId());
                    session.setAttribute("successMsg", "Attendance Approved and Snapshot Created successfully.");
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
}
