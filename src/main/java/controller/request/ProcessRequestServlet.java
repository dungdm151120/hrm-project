package controller.request;

import dao.RequestDAO;
import model.Request;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/process_request")
public class ProcessRequestServlet extends HttpServlet {
    private final RequestDAO dao = new RequestDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");
        String requestIdStr = request.getParameter("requestId");
        String comment = request.getParameter("comment");
        String from = request.getParameter("from"); // Nhận trang nguồn: 'my', 'dept', hoặc 'all'

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
            // Xác định trang đích để quay về sau khi xử lý
            String returnUrl = "view_all_request";
            if ("my".equals(from)) returnUrl = "view_my_request";
            else if ("dept".equals(from)) returnUrl = "view_department_request";

            switch (action) {
                case "APPROVE":
                case "REJECT":
                    // Chỉ Approver mới được Approve/Reject
                    if (req.getApproverId() == userId && "PENDING".equals(req.getStatus())) {
                        if (comment == null || comment.trim().isEmpty()) {
                            response.sendRedirect("request_detail?id=" + requestId + "&from=" + from + "&error=comment_required");
                            return;
                        }
                        success = dao.updateRequestStatus(requestId, action.equals("APPROVE") ? "APPROVED" : "REJECTED", comment);
                    }
                    break;

                case "CLOSE":
                    if (req.getApproverId() == userId && ("APPROVED".equals(req.getStatus()) || "REJECTED".equals(req.getStatus()))) {
                        success = dao.updateRequestStatus(requestId, "CLOSED", req.getApproverComment());
                    }
                    break;

                case "CANCEL":
                    // Chỉ chủ sở hữu mới được Cancel
                    if (req.getUserId() == userId && "PENDING".equals(req.getStatus())) {
                        success = dao.updateRequestStatus(requestId, "CANCELLED", null);
                        returnUrl = "view_my_request";
                    }
                    break;
            }

            if (success) {
                response.sendRedirect(returnUrl + "?success=true");
            } else {
                response.sendRedirect("request_detail?id=" + requestId + "&from=" + from + "&error=action_failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("view_all_request?error=system_error");
        }
    }
}