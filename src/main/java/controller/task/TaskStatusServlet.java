package controller.task;

import dao.TaskDAO;
import dao.TaskHistoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Task;

import java.io.IOException;

@WebServlet("/tasks/status")
public class TaskStatusServlet extends HttpServlet {
    private final TaskDAO taskDAO = new TaskDAO();
    private final TaskHistoryDAO historyDAO = new TaskHistoryDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            long taskId = parseLong(request.getParameter("taskId"), parseLong(request.getParameter("id"), 0));
            Task task = taskDAO.getTaskById(taskId);
            if (task == null) {
                response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
                return;
            }

            String requestedStatus = request.getParameter("status");
            String oldStatus = task.getStatus();
            String actionType;
            String historyContent;

            if ("PAUSED".equals(requestedStatus)) {
                if ("COMPLETED".equals(oldStatus)) {
                    request.getSession().setAttribute("error", "Completed task cannot be paused.");
                    response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + taskId);
                    return;
                }
                taskDAO.updateTaskStatus(taskId, "PAUSED");
                actionType = "Task paused";
                historyContent = "Paused task: " + oldStatus + " -> PAUSED";
            } else if ("RESUME".equals(requestedStatus)) {
                if (!"PAUSED".equals(oldStatus)) {
                    request.getSession().setAttribute("error", "Only paused task can be resumed.");
                    response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + taskId);
                    return;
                }
                taskDAO.resumeTask(taskId);
                String newStatus = taskDAO.calculateStatusFromChecklist(taskId);
                actionType = "Task resumed";
                historyContent = "Resumed task: PAUSED -> " + newStatus;
            } else {
                request.getSession().setAttribute("error", "Task status can only be paused or resumed manually.");
                response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + taskId);
                return;
            }

            historyDAO.insertHistory(taskId, currentUserId(request), actionType, historyContent);
            response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + taskId);
        } catch (Exception e) {
            throw new ServletException(e);
        }
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
