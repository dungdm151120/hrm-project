package controller.announcement;

import dao.AnnouncementDAO;
import dao.DepartmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Announcement;
import model.User;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/announcements/add")
public class AddAnnouncementServlet extends HttpServlet {
    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_CONTENT_LENGTH = 5000;
    private final AnnouncementDAO announcementDAO = new AnnouncementDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardForm(request, response, new Announcement(), null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Announcement announcement;
        try {
            announcement = mapFromRequest(request, currentUser.getId());
        } catch (IllegalArgumentException e) {
            Announcement invalidAnnouncement = new Announcement();
            invalidAnnouncement.setTitle(request.getParameter("title"));
            invalidAnnouncement.setContent(request.getParameter("content"));
            invalidAnnouncement.setTargetScope(request.getParameter("targetScope"));
            invalidAnnouncement.setDepartmentId(parseNullablePositiveInt(request.getParameter("departmentId")));
            invalidAnnouncement.setPublishDate(parseNullableDateTime(request.getParameter("publishDate")));
            forwardForm(request, response, invalidAnnouncement, e.getMessage());
            return;
        }

        if (announcementDAO.add(announcement)) {
            response.sendRedirect(request.getContextPath() + "/announcements");
        } else {
            forwardForm(request, response, announcement, "Create announcement failed.");
        }
    }

    private Announcement mapFromRequest(HttpServletRequest request, int createdBy) {
        String title = trimToNull(request.getParameter("title"));
        String content = trimToNull(request.getParameter("content"));
        String targetScope = trimToNull(request.getParameter("targetScope"));
        String publishDateRaw = trimToNull(request.getParameter("publishDate"));

        if (title == null) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Title must not exceed 200 characters.");
        }
        if (content == null) {
            throw new IllegalArgumentException("Content is required.");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("Content must not exceed 5000 characters.");
        }
        if (!"ALL".equals(targetScope) && !"DEPARTMENT".equals(targetScope)) {
            throw new IllegalArgumentException("Target audience is invalid.");
        }
        if (publishDateRaw == null) {
            throw new IllegalArgumentException("Publish date is required.");
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setTargetScope(targetScope);
        announcement.setPublishDate(parseDateTime(publishDateRaw));
        announcement.setCreatedBy(createdBy);

        if ("DEPARTMENT".equals(targetScope)) {
            int departmentId = parsePositiveInt(request.getParameter("departmentId"), "Department is required.");
            if (!departmentDAO.isDepartmentActive(departmentId)) {
                throw new IllegalArgumentException("Selected department is not active.");
            }
            announcement.setDepartmentId(departmentId);
        }

        return announcement;
    }

    private void forwardForm(HttpServletRequest request, HttpServletResponse response,
                             Announcement announcement, String error)
            throws ServletException, IOException {
        request.setAttribute("announcement", announcement);
        request.setAttribute("departments", departmentDAO.getActiveDepartments());
        request.setAttribute("error", error);
        request.getRequestDispatcher("/WEB-INF/views/announcement/add_announcement.jsp").forward(request, response);
    }

    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("currentUser");
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Publish date is invalid.");
        }
    }

    private LocalDateTime parseNullableDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private int parsePositiveInt(String value, String message) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed > 0) {
                return parsed;
            }
        } catch (Exception ignored) {
        }
        throw new IllegalArgumentException(message);
    }

    private Integer parseNullablePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
