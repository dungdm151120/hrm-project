package controller.admin;

import dao.PasswordResetRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/password-reset-requests")
public class PasswordResetRequestListServlet extends HttpServlet {
    private final PasswordResetRequestDAO requestDAO = new PasswordResetRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = trimToNull(request.getParameter("search"));
        String status = normalizeStatus(request.getParameter("status"));
        int pageSize = 10;
        int currentPage = parsePositiveInt(request.getParameter("page"), 1);

        int totalRecords = requestDAO.count(search, status);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * pageSize;

        request.setAttribute("resetRequests", requestDAO.search(search, status, offset, pageSize));
        request.setAttribute("search", search);
        request.setAttribute("status", status);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("pageSize", pageSize);
        request.getRequestDispatcher("/WEB-INF/views/admin/password_reset_requests.jsp")
                .forward(request, response);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeStatus(String status) {
        String cleanStatus = trimToNull(status);
        if (cleanStatus == null || "all".equalsIgnoreCase(cleanStatus)) {
            return null;
        }

        cleanStatus = cleanStatus.toUpperCase();
        if ("PENDING".equals(cleanStatus) || "APPROVED".equals(cleanStatus) || "REJECTED".equals(cleanStatus)) {
            return cleanStatus;
        }

        return null;
    }

    private int parsePositiveInt(String value, int defaultValue) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
