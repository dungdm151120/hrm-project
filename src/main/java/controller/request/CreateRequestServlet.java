package controller.request;

import dao.*;
import model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/create_request")
public class CreateRequestServlet extends HttpServlet {
    private final RequestDAO requestDAO = new RequestDAO();
    private final UserDAO userDAO = new UserDAO();
    private final LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("currentUser");

        String position = userDAO.getPositionNameByUserId(user.getId());
        boolean isManager = (position != null && position.contains("Manager"));
        boolean isSysAdmin = (position != null && position.contains("Admin"));

        Map<String, String> allTypes = Request.getAllType();
        Map<String, String> filteredTypes = new LinkedHashMap<>();

        for (var entry : allTypes.entrySet()) {
            if ("POSITION_HANDOVER".equals(entry.getKey())) {
                if (isManager || isSysAdmin) {
                    filteredTypes.put(entry.getKey(), entry.getValue());
                }
            } else {
                filteredTypes.put(entry.getKey(), entry.getValue());
            }
        }

        request.setAttribute("requestTypes", filteredTypes);

        int deptId = (user.getDepartmentId() != null) ? user.getDepartmentId() : 0;
        request.setAttribute("deptEmployees", userDAO.getAllEmployeesByDepartment(deptId));
        request.setAttribute("businessAdminList", userDAO.getUserByRole("BUSINESS ADMIN"));

        request.getRequestDispatcher("WEB-INF/views/request/create_request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");

        try {
            Request req = new Request();
            req.setUserId(currentUser.getId());
            req.setDepartmentId(currentUser.getDepartmentId());

            req.setType(request.getParameter("type"));
            req.setReason(request.getParameter("reason"));

            String approverIdParam = request.getParameter("approverId");
            if (approverIdParam == null || approverIdParam.trim().isEmpty()) {
                response.sendRedirect("create_request?error=missing_approver");
                return;
            }
            req.setApproverId(Integer.parseInt(approverIdParam));

            if ("LEAVE_REQUEST".equals(req.getType())) {
                if (currentUser.getDepartmentId() != null) {
                    Department dept = departmentDAO.getDepartmentById(currentUser.getDepartmentId());
                    if (dept != null && dept.getManagerUserId() != null) {
                        req.setHandlerId(dept.getManagerUserId());
                    }
                }
            } else {
                String handlerIdParam = request.getParameter("handlerId");
                if (handlerIdParam != null && !handlerIdParam.trim().isEmpty()) {
                    req.setHandlerId(Integer.parseInt(handlerIdParam));
                }
            }

            String[] observerIds = request.getParameterValues("observerIds");
            Set<Integer> uniqueObsIds = new LinkedHashSet<>();
            if (observerIds != null) {
                for (String id : observerIds) {
                    if (id != null && !id.trim().isEmpty()) {
                        uniqueObsIds.add(Integer.parseInt(id));
                    }
                }
            }

            // ---------- VALIDATION CHO LEAVE_REQUEST ----------
            if ("LEAVE_REQUEST".equals(req.getType())) {
                String leaveDateStr = request.getParameter("leaveDate");
                if (leaveDateStr == null || leaveDateStr.trim().isEmpty()) {
                    response.sendRedirect("create_request?error=missing_leave_date");
                    return;
                }
                LocalDate leaveDate = LocalDate.parse(leaveDateStr);


                if (leaveDate.isBefore(LocalDate.now())) {
                    response.sendRedirect("create_request?error=leave_date_past");
                    return;
                }


                DayOfWeek dayOfWeek = leaveDate.getDayOfWeek();
                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                    response.sendRedirect("create_request?error=leave_date_weekend");
                    return;
                }


                AttendanceRecord existingRecord = attendanceDAO.getRecordByUserAndDate(currentUser.getId(), leaveDate);
                if (existingRecord != null && "ON_LEAVE".equals(existingRecord.getStatus())) {
                    response.sendRedirect("create_request?error=leave_date_already_on_leave");
                    return;
                }


                if (leaveRequestDAO.existsLeaveRequestForDate(currentUser.getId(), leaveDate)) {
                    response.sendRedirect("create_request?error=leave_date_duplicate_request");
                    return;
                }


                AttendanceSummary summary = attendanceDAO.getSummaryByUser(
                        currentUser.getId(),
                        LocalDate.of(LocalDate.now().getYear(), 1, 1),
                        LocalDate.now()
                );
                if (summary.getRemainingLeaveDays() <= 0) {
                    response.sendRedirect("create_request?error=leave_balance_exhausted");
                    return;
                }

                int requestId = requestDAO.createRequestAndGetId(req, new ArrayList<>(uniqueObsIds));
                leaveRequestDAO.createLeaveRequest(requestId, leaveDate);
            } else {
                requestDAO.createRequestAndGetId(req, new ArrayList<>(uniqueObsIds));
            }

            response.sendRedirect("view_my_request?success=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("create_request?error=system_error");
        }
    }
}