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

@WebServlet("/announcements/detail")
public class AnnouncementDetailServlet extends HttpServlet {
    private final AnnouncementDAO announcementDAO = new AnnouncementDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int id = parsePositiveInt(request.getParameter("id"));
        if (id <= 0) {
            response.sendRedirect(request.getContextPath() + "/announcements");
            return;
        }

        Announcement announcement = announcementDAO.findVisibleById(id, currentUser.getId());
        if (announcement == null) {
            response.sendRedirect(request.getContextPath() + "/announcements");
            return;
        }

        if (!announcement.isRead()) {
            announcementDAO.markAsRead(id, currentUser.getId());
            announcement.setReadAt(java.time.LocalDateTime.now());
        }

        request.setAttribute("announcement", announcement);
        request.getRequestDispatcher("/WEB-INF/views/announcement/announcement_detail.jsp").forward(request, response);
    }

    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("currentUser");
    }

    private int parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : -1;
        } catch (Exception e) {
            return -1;
        }
    }
}
