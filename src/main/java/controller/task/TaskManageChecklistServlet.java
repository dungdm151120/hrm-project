package controller.task;

import dao.TaskChecklistItemDAO;
import dao.TaskDAO;
import dao.TaskHistoryDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Task;
import model.TaskChecklistItem;
import model.TaskParticipant;
import model.User;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@WebServlet(urlPatterns = {"/tasks/checklist/add", "/tasks/checklist/assign", "/tasks/checklist/delete"})
public class TaskManageChecklistServlet extends HttpServlet {
    private final TaskDAO taskDAO = new TaskDAO();
    private final TaskChecklistItemDAO checklistItemDAO = new TaskChecklistItemDAO();
    private final TaskHistoryDAO historyDAO = new TaskHistoryDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            String path = request.getServletPath();
            if ("/tasks/checklist/add".equals(path)) {
                addChecklist(request, response);
            } else if ("/tasks/checklist/assign".equals(path)) {
                assignChecklist(request, response);
            } else if ("/tasks/checklist/delete".equals(path)) {
                deleteChecklist(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void addChecklist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long taskId = parseLong(request.getParameter("taskId"), 0);
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }
        if (isPaused(task)) {
            request.getSession().setAttribute("error", "Paused task cannot be changed.");
            response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + taskId);
            return;
        }

        String content = trim(request.getParameter("content"));
        if (content == null) {
            request.getSession().setAttribute("error", "Subtask content is required.");
            response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + taskId);
            return;
        }

        TaskChecklistItem item = new TaskChecklistItem();
        item.setTaskId(taskId);
        item.setContent(content);
        item.setAssignedTo(allowedNullableUserId(request.getParameter("assignedTo"), participantUserIdSet(task)));
        checklistItemDAO.insertChecklistItem(item);
        taskDAO.refreshProgressAndAutoComplete(taskId);
        historyDAO.insertHistory(taskId, currentUserId(request), "Add subtask", "Added subtask: " + content);
        response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + taskId);
    }

    private void assignChecklist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long itemId = parseLong(request.getParameter("itemId"), 0);
        long taskId = parseLong(request.getParameter("taskId"), 0);
        TaskChecklistItem item = checklistItemDAO.getChecklistItemById(itemId);
        if (item == null || item.getTaskId() != taskId) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }

        Task task = taskDAO.getTaskById(item.getTaskId());
        if (isPaused(task)) {
            request.getSession().setAttribute("error", "Paused task cannot be changed.");
            response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + item.getTaskId());
            return;
        }

        Long assignedTo = allowedNullableUserId(request.getParameter("assignedTo"), participantUserIdSet(task));
        item.setAssignedTo(assignedTo);
        checklistItemDAO.updateChecklistItem(item);
        historyDAO.insertHistory(item.getTaskId(), currentUserId(request), "Assign subtask",
                assignedTo == null
                        ? "Cleared assignee for subtask: " + item.getContent()
                        : "Assigned subtask to " + userDisplayName(assignedTo) + ": " + item.getContent());
        response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + item.getTaskId());
    }

    private void deleteChecklist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long itemId = parseLong(request.getParameter("itemId"), 0);
        long taskId = parseLong(request.getParameter("taskId"), 0);
        TaskChecklistItem item = checklistItemDAO.getChecklistItemById(itemId);
        if (item == null || item.getTaskId() != taskId) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }
        Task task = taskDAO.getTaskById(item.getTaskId());
        if (isPaused(task)) {
            request.getSession().setAttribute("error", "Paused task cannot be changed.");
            response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + item.getTaskId());
            return;
        }
        checklistItemDAO.deleteChecklistItem(itemId, taskId);
        taskDAO.refreshProgressAndAutoComplete(item.getTaskId());
        historyDAO.insertHistory(item.getTaskId(), currentUserId(request), "Delete subtask", "Deleted subtask: " + item.getContent());
        response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + item.getTaskId());
    }

    private Set<Long> participantUserIdSet(Task task) {
        Set<Long> ids = new LinkedHashSet<>();
        if (task == null || task.getParticipants() == null) {
            return ids;
        }
        for (TaskParticipant participant : task.getParticipants()) {
            ids.add(participant.getUserId());
        }
        return ids;
    }

    private Long allowedNullableUserId(String value, Set<Long> allowedUserIds) {
        Long userId = parseNullableLong(value);
        if (userId == null || userId <= 0) {
            return null;
        }
        return allowedUserIds.contains(userId) ? userId : null;
    }

    private boolean isPaused(Task task) {
        return task != null && "PAUSED".equals(task.getStatus());
    }

    private String userDisplayName(long userId) {
        User user = userDAO.findById((int) userId);
        return user == null ? String.valueOf(userId) : user.getFullName();
    }

    private long currentUserId(HttpServletRequest request) {
        Object value = request.getSession().getAttribute("userId");
        if (value instanceof Number number) {
            return number.longValue();
        }
        return parseLong(String.valueOf(value), 0);
    }

    private Long parseNullableLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return parseLong(value, 0);
    }

    private long parseLong(String value, long defaultValue) {
        try {
            return value == null || value.isBlank() ? defaultValue : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
