package controller.request;

import dao.RequestDAO;
import model.Request;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/process_request")
public class ProcessRequestServlet extends HttpServlet {
    private final RequestDAO dao = new RequestDAO();

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

            switch (action) {
                case "APPROVE":
                case "REJECT":
                    if (req.getApproverId() == userId && "PENDING".equals(req.getStatus())) {
                        if (comment == null || comment.trim().isEmpty()) {
                            response.sendRedirect("request_detail?id=" + requestId + "&error=comment_required");
                            return;
                        }
                        success = dao.updateRequestStatus(requestId, action.equals("APPROVE") ? "APPROVED" : "REJECTED", comment);
                    }
                    break;

                case "CANCEL":
                    if (String.valueOf(req.getUserId()).equals(String.valueOf(userId)) && "PENDING".equals(req.getStatus())) {
                        success = dao.updateRequestStatus(requestId, "CANCELLED", null);
                        returnUrl = "view_my_request";
                    }
                    break;
            }

            if (success) {
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