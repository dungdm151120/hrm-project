package controller.request;

import dao.*;
import model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/create_request")
@MultipartConfig
public class CreateRequestServlet extends HttpServlet {
    private final RequestDAO requestDAO = new RequestDAO();
    private final UserDAO userDAO = new UserDAO();
    private final LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("currentUser");

        String roleName = user.getRoleName();
        boolean isManagerRole = roleName != null && (roleName.contains("MANAGER") || "SYSTEM ADMIN".equals(roleName)
                || "BUSINESS ADMIN".equals(roleName));

        Map<String, String> allTypes = Request.getAllType();
        Map<String, String> filteredTypes = new LinkedHashMap<>();

        for (var entry : allTypes.entrySet()) {
            String key = entry.getKey();
            if ("POSITION_HANDOVER".equals(key)) {
                String position = userDAO.getPositionNameByUserId(user.getId());
                boolean isManager = (position != null && position.contains("Manager"));
                boolean isSysAdmin = (position != null && position.contains("Admin"));
                if (isManager || isSysAdmin) {
                    filteredTypes.put(key, entry.getValue());
                }
            } else if ("OVERTIME".equals(key)) {
                if (isManagerRole) {
                    filteredTypes.put(key, entry.getValue());
                }
            } else {
                filteredTypes.put(key, entry.getValue());
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
            String reason = request.getParameter("reason");
            if (reason != null && reason.trim().length() > 1000) {
                String typeParam = req.getType() != null ? req.getType() : "";
                response.sendRedirect("create_request?type=" + typeParam + "&error=reason_too_long");
                return;
            }
            req.setReason(reason);

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
            } else if ("ATTENDANCE_ADJUST".equals(req.getType())) {
                String handlerIdParam = request.getParameter("handlerId");
                if (handlerIdParam != null && !handlerIdParam.trim().isEmpty()) {
                    req.setHandlerId(Integer.parseInt(handlerIdParam));
                }
            } else if ("SICK_LEAVE_REQUEST".equals(req.getType()) || "DEPENDENT_CHANGE_REQUEST".equals(req.getType())) {
                req.setHandlerId(req.getApproverId());
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

            if ("LEAVE_REQUEST".equals(req.getType())) {
                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");
                String leaveType = request.getParameter("leaveType");

                if (startDateStr == null || startDateStr.trim().isEmpty() ||
                    endDateStr == null || endDateStr.trim().isEmpty() ||
                    leaveType == null || leaveType.trim().isEmpty()) {
                    response.sendRedirect("create_request?type=LEAVE_REQUEST&error=missing_leave_info");
                    return;
                }

                LocalDate startDate = LocalDate.parse(startDateStr);
                LocalDate endDate = LocalDate.parse(endDateStr);

                if (startDate.isAfter(endDate)) {
                    response.sendRedirect("create_request?type=LEAVE_REQUEST&error=invalid_date_range");
                    return;
                }

                List<LocalDate> requestedDates = new ArrayList<>();
                LocalDate curr = startDate;
                while (!curr.isAfter(endDate)) {
                    DayOfWeek dow = curr.getDayOfWeek();
                    if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                        requestedDates.add(curr);
                    }
                    curr = curr.plusDays(1);
                }

                if (requestedDates.isEmpty()) {
                    response.sendRedirect("create_request?type=LEAVE_REQUEST&error=no_working_days");
                    return;
                }

                List<String> conflicts = leaveRequestDAO.checkDateConflicts(currentUser.getId(), requestedDates);
                if (!conflicts.isEmpty()) {
                    String conflictMsg = String.join(" | ", conflicts);
                    session.setAttribute("requestConflictMsg", conflictMsg);
                    response.sendRedirect("create_request?type=LEAVE_REQUEST&error=conflict_detail");
                    return;
                }

                AttendanceSummary summary = attendanceDAO.getSummaryByUser(
                        currentUser.getId(),
                        LocalDate.of(LocalDate.now().getYear(), 1, 1),
                        LocalDate.now());

                if ("ON_LEAVE".equals(leaveType)) {
                    if (summary.getRemainingLeaveDays() < requestedDates.size()) {
                        response.sendRedirect("create_request?type=LEAVE_REQUEST&error=leave_balance_exhausted");
                        return;
                    }
                } else if ("LEAVE".equals(leaveType)) {
                    if (summary.getRemainingAbsentDays() < requestedDates.size()) {
                        response.sendRedirect("create_request?type=LEAVE_REQUEST&error=absent_balance_exhausted");
                        return;
                    }
                } else {
                    response.sendRedirect("create_request?type=LEAVE_REQUEST&error=invalid_leave_type");
                    return;
                }

                int requestId = requestDAO.createRequestAndGetId(req, new ArrayList<>(uniqueObsIds));
                leaveRequestDAO.createLeaveRequest(requestId, startDate, endDate, leaveType, requestedDates);
            } else if ("ATTENDANCE_ADJUST".equals(req.getType())) {
                int currentDay = LocalDate.now().getDayOfMonth();
                if (currentDay > 5 && currentDay <= 10) {
                    response.sendRedirect("create_request?type=ATTENDANCE_ADJUST&error=adjustment_blocked_days_6_to_10");
                    return;
                }

                String workDateStr = request.getParameter("workDate");
                if (workDateStr == null || workDateStr.trim().isEmpty()) {
                    response.sendRedirect("create_request?type=ATTENDANCE_ADJUST&error=missing_work_date");
                    return;
                }
                LocalDate workDate = LocalDate.parse(workDateStr);

                DayOfWeek dayOfWeek = workDate.getDayOfWeek();
                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                    response.sendRedirect("create_request?type=ATTENDANCE_ADJUST&error=adjustment_date_weekend");
                    return;
                }

                HolidayDAO holidayDAO = new HolidayDAO();
                if (holidayDAO.isHoliday(workDate)) {
                    response.sendRedirect("create_request?type=ATTENDANCE_ADJUST&error=adjustment_date_holiday");
                    return;
                }

                AttendanceChangeRequestDAO acrDAO = new AttendanceChangeRequestDAO();
                if (acrDAO.existsRequestForDate(currentUser.getId(), workDate)) {
                    response.sendRedirect("create_request?type=ATTENDANCE_ADJUST&error=adjustment_date_duplicate");
                    return;
                }

                String desiredCheckInStr = request.getParameter("desiredCheckIn");
                String desiredCheckOutStr = request.getParameter("desiredCheckOut");

                AttendanceChangeRequest acr = new AttendanceChangeRequest();
                acr.setWorkDate(workDate);
                if (desiredCheckInStr != null && !desiredCheckInStr.trim().isEmpty()) {
                    acr.setDesiredCheckIn(LocalTime.parse(desiredCheckInStr));
                }
                if (desiredCheckOutStr != null && !desiredCheckOutStr.trim().isEmpty()) {
                    acr.setDesiredCheckOut(LocalTime.parse(desiredCheckOutStr));
                }

                if (acr.getDesiredCheckIn() != null && acr.getDesiredCheckOut() != null) {
                    if (acr.getDesiredCheckOut().isBefore(acr.getDesiredCheckIn())) {
                        response.sendRedirect("create_request?type=ATTENDANCE_ADJUST&error=adjustment_invalid_time");
                        return;
                    }
                }

                int requestId = requestDAO.createRequestAndGetId(req, new ArrayList<>(uniqueObsIds));
                acr.setRequestId(requestId);
                acrDAO.create(acr);
            } else if ("DEPENDENT_CHANGE_REQUEST".equals(req.getType())) {
                String changeType = request.getParameter("changeType");
                if (changeType == null || changeType.trim().isEmpty()) {
                    response.sendRedirect("create_request?type=DEPENDENT_CHANGE_REQUEST&error=missing_change_type");
                    return;
                }

                Integer dependentId = null;
                if ("UPDATE".equals(changeType) || "REMOVE".equals(changeType)) {
                    String dependentIdStr = request.getParameter("dependentId");
                    if (dependentIdStr == null || dependentIdStr.trim().isEmpty()) {
                        response.sendRedirect("create_request?type=DEPENDENT_CHANGE_REQUEST&error=missing_dependent_id");
                        return;
                    }
                    try {
                        dependentId = Integer.parseInt(dependentIdStr);
                    } catch (NumberFormatException e) {
                        response.sendRedirect("create_request?type=DEPENDENT_CHANGE_REQUEST&error=invalid_dependent_id");
                        return;
                    }
                }

                String dependentName = null;
                LocalDate dependentDob = null;
                String dependentIdNumber = null;
                String relationship = null;

                if ("ADD".equals(changeType) || "UPDATE".equals(changeType)) {
                    dependentName = request.getParameter("dependentName");
                    String dependentDobStr = request.getParameter("dependentDob");
                    dependentIdNumber = request.getParameter("dependentIdNumber");
                    relationship = request.getParameter("relationship");

                    if (dependentName == null || dependentName.trim().isEmpty() ||
                        dependentDobStr == null || dependentDobStr.trim().isEmpty() ||
                        dependentIdNumber == null || dependentIdNumber.trim().isEmpty() ||
                        relationship == null || relationship.trim().isEmpty()) {
                        response.sendRedirect("create_request?type=DEPENDENT_CHANGE_REQUEST&error=missing_dependent_info");
                        return;
                    }

                    dependentIdNumber = dependentIdNumber.trim();
                    if (!dependentIdNumber.matches("^[0-9]{12}$")) {
                        response.sendRedirect("create_request?type=DEPENDENT_CHANGE_REQUEST&error=invalid_dependent_id_number");
                        return;
                    }

                    try {
                        dependentDob = LocalDate.parse(dependentDobStr);
                        if (dependentDob.isAfter(LocalDate.now())) {
                            response.sendRedirect("create_request?type=DEPENDENT_CHANGE_REQUEST&error=future_dependent_dob");
                            return;
                        }
                    } catch (Exception e) {
                        response.sendRedirect("create_request?type=DEPENDENT_CHANGE_REQUEST&error=invalid_dependent_dob");
                        return;
                    }
                }

                Part filePart = request.getPart("dependentFile");
                if ("ADD".equals(changeType) || "UPDATE".equals(changeType)) {
                    if (filePart == null || filePart.getSize() == 0) {
                        response.sendRedirect("create_request?type=DEPENDENT_CHANGE_REQUEST&error=missing_evidence_file");
                        return;
                    }
                }

                String relativePath = null;
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
                    String uploadDir = getServletContext().getRealPath("/uploads/dependent_change");
                    File uploadFolder = new File(uploadDir);
                    if (!uploadFolder.exists()) {
                        uploadFolder.mkdirs();
                    }
                    String filePath = uploadDir + File.separator + fileName;
                    filePart.write(filePath);
                    relativePath = "/uploads/dependent_change/" + fileName;
                }

                int requestId = requestDAO.createRequestAndGetId(req, new ArrayList<>(uniqueObsIds));

                DependentChangeRequest dcr = new DependentChangeRequest();
                dcr.setRequestId(requestId);
                dcr.setChangeType(changeType);
                dcr.setDependentId(dependentId);
                if (dependentName != null) dcr.setDependentName(dependentName.trim());
                dcr.setDependentDob(dependentDob);
                if (dependentIdNumber != null) dcr.setDependentIdNumber(dependentIdNumber.trim());
                if (relationship != null) dcr.setRelationship(relationship.trim());
                dcr.setDocumentPath(relativePath);

                DependentChangeRequestDAO dcrDAO = new DependentChangeRequestDAO();
                dcrDAO.create(dcr);
            } else if ("SICK_LEAVE_REQUEST".equals(req.getType())) {
                Part filePart = request.getPart("sickFile");
                if (filePart == null || filePart.getSize() == 0) {
                    response.sendRedirect("create_request?type=SICK_LEAVE_REQUEST&error=missing_file");
                    return;
                }

                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");
                if (startDateStr == null || startDateStr.trim().isEmpty() ||
                    endDateStr == null || endDateStr.trim().isEmpty()) {
                    response.sendRedirect("create_request?type=SICK_LEAVE_REQUEST&error=missing_sick_dates");
                    return;
                }

                LocalDate startDate = LocalDate.parse(startDateStr);
                LocalDate endDate = LocalDate.parse(endDateStr);

                if (startDate.isAfter(endDate)) {
                    response.sendRedirect("create_request?type=SICK_LEAVE_REQUEST&error=invalid_date_range");
                    return;
                }

                List<LocalDate> requestedDates = new ArrayList<>();
                LocalDate curr = startDate;
                LocalDate today = LocalDate.now();
                while (!curr.isAfter(endDate)) {
                    if (curr.isAfter(today)) {
                        response.sendRedirect("create_request?type=SICK_LEAVE_REQUEST&error=invalid_date");
                        return;
                    }
                    DayOfWeek dow = curr.getDayOfWeek();
                    if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                        requestedDates.add(curr);
                    }
                    curr = curr.plusDays(1);
                }

                if (requestedDates.isEmpty()) {
                    response.sendRedirect("create_request?type=SICK_LEAVE_REQUEST&error=no_working_days");
                    return;
                }

                List<String> conflicts = leaveRequestDAO.checkDateConflicts(currentUser.getId(), requestedDates);
                if (!conflicts.isEmpty()) {
                    String conflictMsg = String.join(" | ", conflicts);
                    session.setAttribute("requestConflictMsg", conflictMsg);
                    response.sendRedirect("create_request?type=SICK_LEAVE_REQUEST&error=conflict_detail");
                    return;
                }

                SickLeaveRequestDAO sickDAO = new SickLeaveRequestDAO();
                int year = today.getYear();
                int usedDays = sickDAO.countSickLeaveDaysUsed(currentUser.getId(), year);
                int pendingDays = sickDAO.countPendingSickLeaveDays(currentUser.getId(), year);
                int remaining = 30 - usedDays - pendingDays;
                if (remaining < requestedDates.size()) {
                    response.sendRedirect("create_request?type=SICK_LEAVE_REQUEST&error=insufficient_sick_days");
                    return;
                }

                String fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
                String uploadDir = getServletContext().getRealPath("/uploads/sick_leave");
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists())
                    uploadFolder.mkdirs();
                String filePath = uploadDir + File.separator + fileName;
                filePart.write(filePath);
                String relativePath = "/uploads/sick_leave/" + fileName;

                int requestId = requestDAO.createRequestAndGetId(req, new ArrayList<>(uniqueObsIds));
                sickDAO.createSickLeaveRequest(requestId, relativePath, requestedDates);
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