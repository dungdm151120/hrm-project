package controller.task;

import dao.TaskDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Task;

import java.io.IOException;
import java.util.Set;

@WebServlet("/tasks/delete")
public class TaskDeleteServlet extends HttpServlet {
    private static final String TASK_VIEW_ALL = "TASK_VIEW_ALL";

    private final TaskDAO taskDAO = new TaskDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            long id = parseLong(request.getParameter("id"), 0);
            Task task = taskDAO.getTaskById(id);
            if (task == null) {
                response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
                return;
            }
            if (!canModifyTask(task, request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            taskDAO.deleteTask(id);
            request.getSession().setAttribute("message", "Task deleted successfully.");
            response.sendRedirect(request.getContextPath() + "/tasks");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private boolean canModifyTask(Task task, HttpServletRequest request) {
        long currentUserId = currentUserId(request);
        return task != null
                && currentUserId > 0
                && (task.getCreatedBy() == currentUserId || hasPermission(request, TASK_VIEW_ALL));
    }

    private boolean hasPermission(HttpServletRequest request, String permission) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object permissions = session.getAttribute("userPermissions");
        return permissions instanceof Set<?> permissionSet && permissionSet.contains(permission);
    }

    private long currentUserId(HttpServletRequest request) {
        Object value = request.getSession().getAttribute("userId");
        if (value instanceof Number number) {
            return number.longValue();
        }
        return parseLong(String.valueOf(value), 0);
    }

    private long parseLong(String value, long defaultValue) {
        try {
            return value == null || value.isBlank() ? defaultValue : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
