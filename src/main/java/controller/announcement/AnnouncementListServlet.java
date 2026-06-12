package controller.announcement;

import dao.AnnouncementDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Announcement;
import model.User;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet("/announcements")
public class AnnouncementListServlet extends HttpServlet {
    private static final int PAGE_SIZE = 10;
    private final AnnouncementDAO announcementDAO = new AnnouncementDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String search = trimToNull(request.getParameter("search"));
        String scope = normalizeScope(request.getParameter("scope"));
        String readStatus = normalizeReadStatus(request.getParameter("readStatus"));
        int currentPage = parsePositiveInt(request.getParameter("page"), 1);
        Integer departmentId = currentUser.getDepartmentId();

        int totalRecords = announcementDAO.countVisible(currentUser.getId(), departmentId, search, scope, readStatus);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / PAGE_SIZE));
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * PAGE_SIZE;
        List<Announcement> announcements =
                announcementDAO.searchVisible(currentUser.getId(), departmentId, search, scope, readStatus, offset, PAGE_SIZE);

        request.setAttribute("announcements", announcements);
        request.setAttribute("search", search);
        request.setAttribute("scope", scope);
        request.setAttribute("readStatus", readStatus);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("pageSize", PAGE_SIZE);
        request.setAttribute("canCreateAnnouncement", hasPermission(request, "ANNOUNCEMENT_CREATE"));
        request.getRequestDispatcher("/WEB-INF/views/announcement/announcement_list.jsp").forward(request, response);
    }

    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("currentUser");
    }

    @SuppressWarnings("unchecked")
    private boolean hasPermission(HttpServletRequest request, String permission) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Set<String> permissions = (Set<String>) session.getAttribute("userPermissions");
        return permissions != null && permissions.contains(permission);
    }

    private String normalizeScope(String value) {
        return "MY_DEPARTMENT".equalsIgnoreCase(value) ? "MY_DEPARTMENT" : "ALL";
    }

    private String normalizeReadStatus(String value) {
        if ("READ".equalsIgnoreCase(value)) {
            return "READ";
        }
        if ("UNREAD".equalsIgnoreCase(value)) {
            return "UNREAD";
        }
        return "ALL";
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
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
