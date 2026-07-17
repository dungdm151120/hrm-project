package controller.request;

import dao.LeaveRequestDAO;
import dao.AttendanceChangeRequestDAO;
import dao.RequestDAO;
import dao.SickLeaveRequestDAO;
import model.AttendanceChangeRequest;
import model.LeaveRequest;
import model.Request;
import model.SickLeaveRequest;
import model.DependentChangeRequest;
import model.User;
import dao.DependentChangeRequestDAO;
import dao.DependentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/request_detail")
public class RequestDetailServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("view_my_request");
            return;
        }

        String cleanId = idParam.split("&")[0].split("\\?")[0];
        int id = Integer.parseInt(cleanId);
        RequestDAO dao = new RequestDAO();

        Request req = dao.getRequestById(id);

        if (req != null) {
            HttpSession session = request.getSession(false);
            User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
            if (currentUser != null) {
                dao.markRequestNotificationsRead(id, currentUser.getId());
            }

            List<User> observers = dao.getObserversByRequestId(id);
            req.setObserver(observers);

            if ("LEAVE_REQUEST".equals(req.getType())) {
                LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
                LeaveRequest lr = leaveRequestDAO.getByRequestId(id);
                request.setAttribute("leaveRequest", lr);
            } else if ("ATTENDANCE_ADJUST".equals(req.getType())) {
                AttendanceChangeRequestDAO acrDAO = new AttendanceChangeRequestDAO();
                AttendanceChangeRequest acr = acrDAO.getByRequestId(id);
                request.setAttribute("attendanceChangeRequest", acr);
            } else if ("SICK_LEAVE_REQUEST".equals(req.getType())) {
                SickLeaveRequestDAO sickDAO = new SickLeaveRequestDAO();
                SickLeaveRequest sickReq = sickDAO.getByRequestId(id);
                if (sickReq != null) {
                    request.setAttribute("sickLeaveRequest", sickReq);
                    List<LocalDate> dates = sickDAO.getDatesBySickRequestId(sickReq.getId());
                    request.setAttribute("sickLeaveDates", dates);
                }
            } else if ("OVERTIME".equals(req.getType())) {
                dao.OvertimeRequestDAO overtimeRequestDAO = new dao.OvertimeRequestDAO();
                dao.OvertimeParticipantDAO overtimeParticipantDAO = new dao.OvertimeParticipantDAO();
                model.OvertimeRequest oreq = overtimeRequestDAO.getByRequestId(id);
                if (oreq != null) {
                    request.setAttribute("overtimeRequest", oreq);
                    request.setAttribute("overtimeParticipants", overtimeParticipantDAO.getByOvertimeRequestId(oreq.getId()));
                }
            } else if ("DEPENDENT_CHANGE_REQUEST".equals(req.getType())) {
                DependentChangeRequestDAO dcrDAO = new DependentChangeRequestDAO();
                DependentChangeRequest dcr = dcrDAO.getByRequestId(id);
                request.setAttribute("dependentChangeRequest", dcr);
                if (dcr != null && dcr.getDependentId() != null) {
                    DependentDAO dependentDAO = new DependentDAO();
                    request.setAttribute("targetDependent", dependentDAO.getById(dcr.getDependentId()));
                }
            }

            request.setAttribute("request", req);
            request.getRequestDispatcher("/WEB-INF/views/request/request_detail.jsp").forward(request, response);
        } else {
            response.sendRedirect("view_my_request");
        }
    }
}
