package controller.request;

import dao.RequestDAO;
import jakarta.servlet.ServletException;
import model.Request;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/process_request")
public class ProcessRequestServlet extends HttpServlet {
    private RequestDAO dao = new RequestDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");
        String requestIdStr = request.getParameter("requestId");
        String comment = request.getParameter("comment");

        if (requestIdStr == null || requestIdStr.isEmpty()) {
            response.sendRedirect("view_all_requests?error=invalid_id");
            return;
        }

        try {
            int requestId = Integer.parseInt(requestIdStr);
            Request req = dao.getRequestById(requestId);

            if (req == null) {
                response.sendRedirect("view_all_requests?error=not_found");
                return;
            }

            boolean success;

            switch (action) {
                case "APPROVE":
                    if (req.getApproverId() == userId && "PENDING".equals(req.getStatus())) {
                        if (comment == null || comment.trim().isEmpty()) {
                            response.sendRedirect("view_request_detail?id=" + requestId + "&error=comment_required");
                            return;
                        }
                        success = dao.updateRequestStatus(requestId, "APPROVED", comment);
                        if (success) {
                            response.sendRedirect("view_all_requests");
                            return;
                        }
                    }
                    break;

                case "REJECT":
                    if (req.getApproverId() == userId && "PENDING".equals(req.getStatus())) {
                        if (comment == null || comment.trim().isEmpty()) {
                            response.sendRedirect("view_request_detail?id=" + requestId + "&error=comment_required");
                            return;
                        }
                        success = dao.updateRequestStatus(requestId, "REJECTED", comment);
                        if (success) {
                            response.sendRedirect("view_all_requests");
                            return;
                        }
                    }
                    break;

                case "CLOSE":
                    if (req.getApproverId() == userId && ("APPROVED".equals(req.getStatus()) || "REJECTED".equals(req.getStatus()))) {
                        success = dao.updateRequestStatus(requestId, "CLOSED", req.getApproverComment());
                        if (success) {
                            response.sendRedirect("view_all_requests");
                            return;
                        }
                    }
                    break;

                case "CANCEL":
                    if (req.getUserId() == userId && "PENDING".equals(req.getStatus())) {
                        success = dao.updateRequestStatus(requestId, "CANCELLED", null);
                        if (success) {
                            response.sendRedirect("view_my_request");
                            return;
                        }
                    }
                    break;

                default:
                    response.sendRedirect("view_all_requests?error=invalid_action");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("view_all_requests?error=invalid_id_format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("view_all_requests?error=system_error");
        }
    }
}