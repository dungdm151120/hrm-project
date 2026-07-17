package controller.request;

import dao.*;
import model.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/process_request")
public class ProcessRequestServlet extends HttpServlet {
    private final RequestDAO dao = new RequestDAO();
    private final LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final AttendanceChangeRequestDAO attendanceChangeRequestDAO = new AttendanceChangeRequestDAO();
    private final SickLeaveRequestDAO sickLeaveRequestDAO = new SickLeaveRequestDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = currentUser.getId();
        String position = currentUser.getPositionName();

        String action = request.getParameter("action");
        String requestIdStr = request.getParameter("requestId");
        String comment = request.getParameter("comment");

        if (requestIdStr == null || requestIdStr.isEmpty()) {
            response.sendRedirect("view_all_request?error=invalid_id");
            return;
        }

        try {
            int requestId = Integer.parseInt(requestIdStr);
            Request req = dao.getRequestById(requestId);

            if (req == null) {
                response.sendRedirect("view_all_request?error=not_found");
                return;
            }

            boolean success = false;
            String returnUrl = "request_detail?id=" + requestId;
            String notificationEventType = null;

            switch (action) {
                case "APPROVE":
                case "REJECT":
                    if (req.getApproverId() == userId && "PENDING".equals(req.getStatus())) {
                        if (comment == null || comment.trim().isEmpty()) {
                            response.sendRedirect("request_detail?id=" + requestId + "&error=comment_required");
                            return;
                        }

                        String newStatus = action.equals("APPROVE") ? "APPROVED" : "REJECTED";
                        notificationEventType = newStatus;
                        if ("APPROVE".equals(action) && "OVERTIME".equals(req.getType())) {
                            success = dao.updateRequestStatusAndHandler(requestId, newStatus, comment, userId);
                        } else {
                            success = dao.updateRequestStatus(requestId, newStatus, comment);
                        }

                        if (success) {
                            if ("APPROVE".equals(action) && "LEAVE_REQUEST".equals(req.getType())) {
                                LeaveRequest lr = leaveRequestDAO.getByRequestId(requestId);
                                if (lr != null) {
                                    if ("ON_LEAVE".equals(lr.getLeaveType())) {
                                        attendanceDAO.markOnLeave(req.getUserId(), lr.getLeaveDate());
                                    } else if ("LEAVE".equals(lr.getLeaveType())) {
                                        attendanceDAO.markAbsent(req.getUserId(), lr.getLeaveDate());
                                    }
                                }
                            } else if ("APPROVE".equals(action) && "SICK_LEAVE_REQUEST".equals(req.getType())) {
                                SickLeaveRequest sickReq = sickLeaveRequestDAO.getByRequestId(requestId);
                                if (sickReq != null) {
                                    List<LocalDate> dates = sickLeaveRequestDAO.getDatesBySickRequestId(sickReq.getId());
                                    for (LocalDate date : dates) {
                                        attendanceDAO.markSickLeave(req.getUserId(), date);
                                    }
                                }
                            } else if ("APPROVE".equals(action) && "DEPENDENT_CHANGE_REQUEST".equals(req.getType())) {
                                DependentChangeRequestDAO dcrDAO = new DependentChangeRequestDAO();
                                DependentChangeRequest dcr = dcrDAO.getByRequestId(requestId);
                                if (dcr != null) {
                                    dcrDAO.approveDependentChange(req.getUserId(), dcr);
                                }
                            } else if ("OVERTIME".equals(req.getType())) {
                                service.OvertimeService overtimeService = new service.OvertimeService();
                                overtimeService.handleProcessAction(requestId, action);
                            }
                        }
                    }
                    break;

                case "APPLY_CHANGES":
                    if (("HR Staff".equals(position) || currentUser.getId() == req.getHandlerId()) && "APPROVED".equals(req.getStatus()) && "ATTENDANCE_ADJUST".equals(req.getType())) {
                        AttendanceChangeRequest acr = attendanceChangeRequestDAO.getByRequestId(requestId);
                        if (acr != null) {
                            AttendanceRecord record = attendanceDAO.getRecordByUserAndDate(req.getUserId(), acr.getWorkDate());
                            if (record == null) {
                                record = new AttendanceRecord();
                                record.setUserId(req.getUserId());
                                record.setWorkDate(acr.getWorkDate());
                            }
                            if (acr.getDesiredCheckIn() != null) {
                                record.setCheckIn(LocalDateTime.of(acr.getWorkDate(), acr.getDesiredCheckIn()));
                            } else {
                                record.setCheckIn(null);
                            }
                            if (acr.getDesiredCheckOut() != null) {
                                record.setCheckOut(LocalDateTime.of(acr.getWorkDate(), acr.getDesiredCheckOut()));
                            } else {
                                record.setCheckOut(null);
                            }
                            record.setNote("Attendance adjusted via request #" + requestId);
                            attendanceDAO.calculateWorkingHours(record);
                            record.setStatus(attendanceDAO.determineStatus(record));
                            attendanceDAO.saveAttendanceRecord(record);

                            success = attendanceChangeRequestDAO.markApplied(requestId);
                            notificationEventType = null;
                        }
                    }
                    break;

                case "CANCEL":
                    if (String.valueOf(req.getUserId()).equals(String.valueOf(userId)) && "PENDING".equals(req.getStatus())) {
                        success = dao.updateRequestStatus(requestId, "CANCELLED", null);
                        notificationEventType = "CANCELLED";

                        if (success && "OVERTIME".equals(req.getType())) {
                            service.OvertimeService overtimeService = new service.OvertimeService();
                            overtimeService.handleProcessAction(requestId, "CANCEL");
                        }

                        String statusFilter = request.getParameter("status");
                        String typeFilter = request.getParameter("type");
                        String sortFilter = request.getParameter("sort");
                        String pageFilter = request.getParameter("page");

                        StringBuilder redirectParams = new StringBuilder("view_my_request?success=true");
                        if (statusFilter != null && !statusFilter.isEmpty()) redirectParams.append("&status=").append(statusFilter);
                        if (typeFilter != null && !typeFilter.isEmpty()) redirectParams.append("&type=").append(typeFilter);
                        if (sortFilter != null && !sortFilter.isEmpty()) redirectParams.append("&sort=").append(sortFilter);
                        if (pageFilter != null && !pageFilter.isEmpty()) redirectParams.append("&page=").append(pageFilter);

                        returnUrl = redirectParams.toString();
                    }
                    break;
            }

            if (success) {
                if (notificationEventType != null) {
                    dao.notifyRequestChanged(requestId, userId, notificationEventType);
                }
                response.sendRedirect(returnUrl + "&success=true");
            } else {
                String encodedComment = URLEncoder.encode(comment != null ? comment : "", "UTF-8");
                response.sendRedirect("request_detail?id=" + requestId + "&error=action_failed&oldComment=" + encodedComment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("view_all_request?error=system_error");
        }
    }
}
