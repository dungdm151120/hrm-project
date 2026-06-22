package controller.request;

import dao.LeaveRequestDAO;
import dao.AttendanceChangeRequestDAO;
import dao.RequestDAO;
import model.AttendanceChangeRequest;
import model.LeaveRequest;
import model.Request;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
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
            } else if ("OVERTIME".equals(req.getType())) {
                // xử lý overtime nếu có
            }

            request.setAttribute("request", req);
            request.getRequestDispatcher("/WEB-INF/views/request/request_detail.jsp").forward(request, response);
        } else {
            response.sendRedirect("view_my_request");
        }
    }
}