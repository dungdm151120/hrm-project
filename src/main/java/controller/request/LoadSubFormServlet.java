package controller.request;

import dao.AttendanceDAO;
import dao.DepartmentDAO;
import dao.UserDAO;
import model.AttendanceSummary;
import model.Department;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/load_sub_form")
public class LoadSubFormServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Must be logged in");
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        String type = request.getParameter("type");
        String jspPath = "/WEB-INF/views/request/subforms/default.jsp";

        if ("POSITION_HANDOVER".equals(type)) {
            String position = userDAO.getPositionNameByUserId(user.getId());
            boolean isManager = (position != null && position.contains("Manager"));
            boolean isSysAdmin = (position != null && position.contains("Admin"));

            if (!isManager && !isSysAdmin) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền tạo loại đơn này.");
                return;
            }
        }

        if ("LEAVE_REQUEST".equals(type)) {
            User currentUser = userDAO.findById(user.getId());
            int deptId = currentUser.getDepartmentId() != null ? currentUser.getDepartmentId() : 0;

            if (deptId > 0) {
                Department dept = departmentDAO.getDepartmentById(deptId);
                if (dept != null && dept.getManagerUserId() != null) {
                    User approver = userDAO.findById(dept.getManagerUserId());
                    request.setAttribute("approver", approver);
                }
            }

            List<User> observers = new ArrayList<>();
            List<User> allDeptManagers = userDAO.getAllDeptManager();
            User approver = (User) request.getAttribute("approver");
            for (User mgr : allDeptManagers) {
                if (approver == null || mgr.getId() != approver.getId()) {
                    observers.add(mgr);
                }
            }
            List<User> businessAdmins = userDAO.getUserByRole("BUSINESS ADMIN");
            for (User ba : businessAdmins) {
                if (!observers.contains(ba)) {
                    observers.add(ba);
                }
            }
            List<User> hrManagers = userDAO.getUserByPosition("HR Manager");
            for (User hr : hrManagers) {
                if (!observers.contains(hr)) {
                    observers.add(hr);
                }
            }
            List<User> payrollManagers = userDAO.getUserByPosition("Payroll Manager");
            for (User pm : payrollManagers) {
                if (!observers.contains(pm)) {
                    observers.add(pm);
                }
            }
            request.setAttribute("observerList", observers);

            LocalDate today = LocalDate.now();
            AttendanceSummary summary = attendanceDAO.getSummaryByUser(
                    currentUser.getId(),
                    LocalDate.of(today.getYear(), 1, 1),
                    today
            );
            request.setAttribute("remainingLeave", summary.getRemainingLeaveDays());
            request.setAttribute("remainingAbsent", summary.getRemainingAbsentDays());

            request.setAttribute("proposer", currentUser);
            request.setAttribute("today", today.toString());
            request.setAttribute("now", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            jspPath = "/WEB-INF/views/request/subforms/leave_request.jsp";
        } else {
            request.setAttribute("businessAdminList", userDAO.getUserByRole("BUSINESS ADMIN"));

            List<User> hrManagers = userDAO.getUserByPosition("HR Manager");
            request.setAttribute("hrManagers", hrManagers);

            int deptId = (user.getDepartmentId() != null) ? user.getDepartmentId() : 0;
            List<User> deptEmployees = userDAO.getAllEmployeesByDepartment(deptId);
            List<User> deptEmployeesFiltered = new ArrayList<>();
            if (deptEmployees != null) {
                for (User emp : deptEmployees) {
                    if (emp.getId() != user.getId()) {
                        deptEmployeesFiltered.add(emp);
                    }
                }
            }
            request.setAttribute("deptEmployees", deptEmployeesFiltered);

            List<User> allObservers = new ArrayList<>();
            List<User> sysAdmins = userDAO.getUserByPosition("System Administrator");
            List<User> payrollManagers = userDAO.getUserByPosition("Payroll Manager");
            List<User> deptManagers = userDAO.getAllDeptManager();
            if (sysAdmins != null) allObservers.addAll(sysAdmins);
            if (hrManagers != null) allObservers.addAll(hrManagers);
            if (payrollManagers != null) allObservers.addAll(payrollManagers);
            if (deptManagers != null) allObservers.addAll(deptManagers);
            request.setAttribute("allObservers", allObservers);
        }

        if ("POSITION_HANDOVER".equals(type)) {
            jspPath = "/WEB-INF/views/request/subforms/position_handover.jsp";
        } else if ("EMP_MOVE_REMOVE".equals(type)) {
            jspPath = "/WEB-INF/views/request/subforms/move_remove.jsp";
        } else if ("OVERTIME".equals(type)) {
            User currentUser = userDAO.findById(user.getId());
            int deptId = currentUser != null && currentUser.getDepartmentId() != null ? currentUser.getDepartmentId() : 0;
            if (deptId > 0) {
                Department dept = departmentDAO.getDepartmentById(deptId);
                request.setAttribute("departmentName", dept != null ? dept.getName() : "N/A");
            } else {
                request.setAttribute("departmentName", "N/A");
            }

            request.setAttribute("proposer", userDAO.findById(currentUser.getId()));
            request.setAttribute("today", LocalDate.now().toString());
            request.setAttribute("now", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            List<User> hrManagers = userDAO.getUserByPosition("HR Manager");
            request.setAttribute("approverList", hrManagers);

            List<User> deptEmployees = userDAO.getAllEmployeesByDepartment(deptId);
            List<User> deptEmployeesFiltered = new ArrayList<>();
            if (deptEmployees != null) {
                for (User emp : deptEmployees) {
                    if (emp.getId() != currentUser.getId() && emp.isActive()) {
                        deptEmployeesFiltered.add(emp);
                    }
                }
            }
            request.setAttribute("deptEmployees", deptEmployees);

            List<User> observers = new ArrayList<>();
            List<User> allManagers = userDAO.getAllDeptManager();
            if(hrManagers != null) allManagers.addAll(hrManagers);
            List<User> payrollManagers = userDAO.getUserByPosition("Payroll Manager");
            if(payrollManagers != null) allManagers.addAll(payrollManagers);
            for (User mgr : allManagers) {
                if (mgr.getId() != currentUser.getId() && !observers.contains(mgr)) {
                    observers.add(mgr);
                }
            }
            request.setAttribute("observerList", observers);

            jspPath = "/WEB-INF/views/request/subforms/overtime.jsp";
        }

        try {
            request.getRequestDispatcher(jspPath).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}