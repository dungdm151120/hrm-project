package service;

import dao.AttendanceDAO;
import dao.OvertimeParticipantDAO;
import dao.OvertimeRequestDAO;
import model.AttendanceRecord;
import model.OvertimeParticipant;
import model.OvertimeRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class OvertimeService {
    private final OvertimeRequestDAO overtimeRequestDAO = new OvertimeRequestDAO();
    private final OvertimeParticipantDAO overtimeParticipantDAO = new OvertimeParticipantDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    public boolean createOvertimeRequest(model.User currentUser, LocalDate overtimeDate, String reason, String[] employeeIds, List<Integer> observerIds, int approverId) {
        dao.RequestDAO requestDAO = new dao.RequestDAO();
        try {
            model.Request req = new model.Request();
            req.setUserId(currentUser.getId());
            req.setDepartmentId(currentUser.getDepartmentId());
            req.setType("OVERTIME");
            req.setReason(reason);
            req.setApproverId(approverId);
            req.setHandlerId(approverId);

            int requestId = requestDAO.createRequestAndGetId(req, observerIds);

            if (requestId > 0) {
                OvertimeRequest otReq = new OvertimeRequest();
                otReq.setRequestId(requestId);
                otReq.setDepartmentId(currentUser.getDepartmentId());
                otReq.setOvertimeDate(overtimeDate);
                otReq.setShiftStart(LocalTime.of(17, 0));
                otReq.setShiftEnd(LocalTime.of(19, 0));
                otReq.setTotalHours(employeeIds.length * 2.0);
                otReq.setReason(reason);
                otReq.setCreatedBy(currentUser.getId());

                int otReqId = overtimeRequestDAO.createOvertimeRequest(otReq);

                if (otReqId > 0) {
                    List<OvertimeParticipant> participants = new java.util.ArrayList<>();
                    for (String empIdStr : employeeIds) {
                        OvertimeParticipant p = new OvertimeParticipant();
                        p.setOvertimeRequestId(otReqId);
                        p.setUserId(Integer.parseInt(empIdStr));
                        p.setStatus("PENDING");
                        p.setHoursActual(0.0);
                        participants.add(p);
                    }
                    overtimeParticipantDAO.addParticipants(participants);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkDuplicateOvertime(int userId, LocalDate date) {
        return overtimeRequestDAO.checkDuplicateOvertime(userId, date);
    }

    /**
     * Handles the specific processing logic for Overtime Requests (Calculate hours, update status).
     * This is called AFTER the main Request is updated to APPROVED/REJECTED/CANCELLED.
     */
    public void handleProcessAction(int requestId, String action) {
        OvertimeRequest oreq = overtimeRequestDAO.getByRequestId(requestId);
        if (oreq == null) return;

        List<OvertimeParticipant> parts = overtimeParticipantDAO.getByOvertimeRequestId(oreq.getId());

        if ("APPROVE".equals(action)) {
            for (OvertimeParticipant p : parts) {
                if ("PENDING".equals(p.getStatus())) {
                    overtimeParticipantDAO.updateParticipantStatusAndHours(p.getId(), "REGISTERED", 0.0);
                }
            }
        } else if ("REJECT".equals(action) || "CANCEL".equals(action)) {
            String newStatus = "REJECT".equals(action) ? "REJECTED" : "CANCELLED";
            for (OvertimeParticipant p : parts) {
                overtimeParticipantDAO.updateParticipantStatusAndHours(p.getId(), newStatus, 0.0);
            }
        }
    }

    public boolean confirmOvertime(int requestId) {
        OvertimeRequest oreq = overtimeRequestDAO.getByRequestId(requestId);
        if (oreq == null) return false;

        List<OvertimeParticipant> parts = overtimeParticipantDAO.getByOvertimeRequestId(oreq.getId());
        LocalTime shiftStart = oreq.getShiftStart();
        LocalTime shiftEnd = oreq.getShiftEnd();
        boolean hasUpdates = false;

        for (OvertimeParticipant p : parts) {
            // Only confirm if they are still REGISTERED
            if (!"REGISTERED".equals(p.getStatus())) continue;

            AttendanceRecord ar = attendanceDAO.getRecordByUserAndDate(p.getUserId(), oreq.getOvertimeDate());

            String pStatus = "ABSENT";
            double pHours = 0.0;

            if (ar != null && ar.getCheckOut() != null) {
                LocalTime co = ar.getCheckOut().toLocalTime();
                if (co.isBefore(shiftStart)) {
                    pStatus = "ABSENT";
                    pHours = 0.0;
                } else if (co.isAfter(shiftEnd) || co.equals(shiftEnd)) {
                    pStatus = "COMPLETED";
                    pHours = 2.0;
                } else {
                    pStatus = "PARTIAL";
                    long workedMinutes = Duration.between(shiftStart, co).toMinutes();
                    long blocks = workedMinutes / 30;
                    pHours = blocks * 0.5;
                }

                // Update attendance records immediately without marking as edited
                ar.setOvertimeHours((ar.getOvertimeHours() != null ? ar.getOvertimeHours() : 0.0) + pHours);
                attendanceDAO.updateOvertimeHours(ar.getId(), ar.getOvertimeHours());
            }

            overtimeParticipantDAO.updateParticipantStatusAndHours(p.getId(), pStatus, pHours);
            hasUpdates = true;
        }

        if (hasUpdates) {
            dao.RequestDAO requestDAO = new dao.RequestDAO();
            boolean success = requestDAO.updateRequestStatusOnly(requestId, "CONFIRMED");
            if (!success) {
                // If the update fails (e.g. because of ENUM constraints), return false
                return false;
            }
        }

        return hasUpdates;
    }

    public model.OvertimeDetail getOvertimeDetailByUserAndDate(int userId, LocalDate date) {
        return overtimeRequestDAO.getOvertimeDetailByUserAndDate(userId, date);
    }
}
