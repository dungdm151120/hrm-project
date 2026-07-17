package controller.request;

import dao.*;
import model.AttendanceSummary;
import model.Department;
import model.Dependent;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/load_sub_form")
public class LoadSubFormServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

            if (!isManager) {
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
                    today);
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
            if (sysAdmins != null)
                allObservers.addAll(sysAdmins);
            if (hrManagers != null)
                allObservers.addAll(hrManagers);
            if (payrollManagers != null)
                allObservers.addAll(payrollManagers);
            if (deptManagers != null)
                allObservers.addAll(deptManagers);
            request.setAttribute("allObservers", allObservers);
        }

        if ("POSITION_HANDOVER".equals(type)) {
            jspPath = "/WEB-INF/views/request/subforms/position_handover.jsp";
        } else if ("EMP_MOVE_REMOVE".equals(type)) {
            jspPath = "/WEB-INF/views/request/subforms/move_remove.jsp";
        } else if ("OVERTIME".equals(type)) {
            User currentUser = userDAO.findById(user.getId());
            int deptId = currentUser != null && currentUser.getDepartmentId() != null ? currentUser.getDepartmentId()
                    : 0;
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
            if (hrManagers != null)
                allManagers.addAll(hrManagers);
            List<User> payrollManagers = userDAO.getUserByPosition("Payroll Manager");
            if (payrollManagers != null)
                allManagers.addAll(payrollManagers);
            for (User mgr : allManagers) {
                if (mgr.getId() != currentUser.getId() && !observers.contains(mgr)) {
                    observers.add(mgr);
                }
            }
            request.setAttribute("observerList", observers);

            jspPath = "/WEB-INF/views/request/subforms/overtime.jsp";
        } else if ("ATTENDANCE_ADJUST".equals(type)) {
            User currentUser = userDAO.findById(user.getId());

            List<User> hrStaffList = userDAO.getUserByPosition("HR Staff");
            request.setAttribute("hrStaffList", hrStaffList);

            List<User> observers = new ArrayList<>();
            List<User> allManagers = userDAO.getAllDeptManager();
            for (User mgr : allManagers) {
                if (mgr.getId() != currentUser.getId() && !observers.contains(mgr)) {
                    observers.add(mgr);
                }
            }
            List<User> hrManagers = userDAO.getUserByPosition("HR Manager");
            for (User hr : hrManagers) {
                if (hr.getId() != currentUser.getId() && !observers.contains(hr)) {
                    observers.add(hr);
                }
            }
            List<User> businessAdmins = userDAO.getUserByRole("BUSINESS ADMIN");
            for (User ba : businessAdmins) {
                if (ba.getId() != currentUser.getId() && !observers.contains(ba)) {
                    observers.add(ba);
                }
            }
            request.setAttribute("observerList", observers);

            LocalDate today = LocalDate.now();
            int currentDay = today.getDayOfMonth();
            if (currentDay > 5 && currentDay <= 10) {
                request.setAttribute("blocked", true);
            } else {
                request.setAttribute("blocked", false);
            }

            LocalDate minDate = LocalDate.of(today.getYear(), 1, 1);
            LocalDate maxDate = today;

            request.setAttribute("minDate", minDate.toString());
            request.setAttribute("maxDate", maxDate.toString());

            User defaultApprover = null;
            if (currentUser.getDepartmentId() != null) {
                Department dept = departmentDAO.getDepartmentById(currentUser.getDepartmentId());
                if (dept != null && dept.getManagerUserId() != null) {
                    defaultApprover = userDAO.findById(dept.getManagerUserId());
                }
            }
            if (defaultApprover == null) {
                defaultApprover = currentUser;
            }
            request.setAttribute("defaultApprover", defaultApprover);

            request.setAttribute("proposer", currentUser);
            request.setAttribute("today", today.toString());
            request.setAttribute("now", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            jspPath = "/WEB-INF/views/request/subforms/attendance_change.jsp";
        } else if ("SICK_LEAVE_REQUEST".equals(type)) {
            User currentUser = userDAO.findById(user.getId());

            List<User> hrStaffList = userDAO.getUserByPosition("HR Staff");
            request.setAttribute("hrStaffList", hrStaffList);

            List<User> observers = new ArrayList<>();
            List<User> hrStaffAll = new ArrayList<>(hrStaffList);
            List<User> payrollStaff = userDAO.getUserByPosition("Payroll Staff");
            List<User> allManagers = userDAO.getAllDeptManager();
            List<User> hrManagers = userDAO.getUserByPosition("HR Manager");
            List<User> payrollManagers = userDAO.getUserByPosition("Payroll Manager");

            hrStaffAll.removeIf(u -> u.getId() == currentUser.getId());
            payrollStaff.removeIf(u -> u.getId() == currentUser.getId());
            allManagers.removeIf(u -> u.getId() == currentUser.getId());
            hrManagers.removeIf(u -> u.getId() == currentUser.getId());
            payrollManagers.removeIf(u -> u.getId() == currentUser.getId());

            observers.addAll(hrStaffAll);
            observers.addAll(payrollStaff);
            observers.addAll(allManagers);
            observers.addAll(hrManagers);
            observers.addAll(payrollManagers);

            Set<User> uniqueObservers = new HashSet<>(observers);
            request.setAttribute("observerList", new ArrayList<>(uniqueObservers));

            int year = LocalDate.now().getYear();
            SickLeaveRequestDAO sickDAO = new SickLeaveRequestDAO();
            int usedDays = sickDAO.countSickLeaveDaysUsed(currentUser.getId(), year);
            int pendingDays = sickDAO.countPendingOrApprovedFuture(currentUser.getId(), year);
            int remainingSickDays = 30 - usedDays - pendingDays;
            request.setAttribute("remainingSickDays", Math.max(0, remainingSickDays));
            request.setAttribute("totalSickDays", 30);

            request.setAttribute("proposer", currentUser);
            request.setAttribute("today", LocalDate.now().toString());
            request.setAttribute("now", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            jspPath = "/WEB-INF/views/request/subforms/sick_leave.jsp";
        } else if ("DEPENDENT_CHANGE_REQUEST".equals(type)) {
            User currentUser = userDAO.findById(user.getId());

            List<User> payrollStaffList = userDAO.getUserByPosition("Payroll Staff");
            request.setAttribute("payrollStaffList", payrollStaffList);

            List<User> observers = new ArrayList<>();
            List<User> deptManagers = userDAO.getAllDeptManager();
            if (deptManagers != null) observers.addAll(deptManagers);
            List<User> hrManagers = userDAO.getUserByPosition("HR Manager");
            if (hrManagers != null) observers.addAll(hrManagers);
            List<User> payrollManagers = userDAO.getUserByPosition("Payroll Manager");
            if (payrollManagers != null) observers.addAll(payrollManagers);

            observers.removeIf(u -> u.getId() == currentUser.getId());

            Set<User> uniqueObservers = new HashSet<>(observers);
            request.setAttribute("observerList", new ArrayList<>(uniqueObservers));

            request.setAttribute("proposer", currentUser);
            request.setAttribute("today", LocalDate.now().toString());
            request.setAttribute("now", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            DependentDAO dependentDAO = new DependentDAO();
            List<Dependent> activeDependents = dependentDAO.getActiveDependentsByUserId(currentUser.getId());
            request.setAttribute("activeDependents", activeDependents);

            jspPath = "/WEB-INF/views/request/subforms/dependent_change.jsp";
        }

        try {
            request.getRequestDispatcher(jspPath).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}