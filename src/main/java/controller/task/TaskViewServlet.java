package controller.task;

import dao.TaskChecklistItemDAO;
import dao.TaskCommentDAO;
import dao.TaskDAO;
import dao.TaskHistoryDAO;
import dao.TaskParticipantDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Task;
import model.TaskChecklistItem;
import model.TaskObserver;
import model.TaskParticipant;

import java.io.IOException;
import java.util.Set;

@WebServlet(urlPatterns = {"/tasks", "/tasks/detail", "/tasks/comment", "/tasks/checklist/toggle"})
public class TaskViewServlet extends HttpServlet {
    private static final int PAGE_SIZE = 10;
    private static final String TASK_UPDATE = "TASK_UPDATE";
    private static final String TASK_DELETE = "TASK_DELETE";
    private static final String TASK_MANAGE_CHECKLIST = "TASK_MANAGE_CHECKLIST";
    private static final String TASK_UPDATE_STATUS = "TASK_UPDATE_STATUS";
    private static final String TASK_VIEW_ALL = "TASK_VIEW_ALL";

    private final TaskDAO taskDAO = new TaskDAO();
    private final TaskParticipantDAO participantDAO = new TaskParticipantDAO();
    private final TaskChecklistItemDAO checklistItemDAO = new TaskChecklistItemDAO();
    private final TaskCommentDAO commentDAO = new TaskCommentDAO();
    private final TaskHistoryDAO historyDAO = new TaskHistoryDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            String path = request.getServletPath();
            if ("/tasks/detail".equals(path)) {
                showDetail(request, response);
            } else if ("/tasks/checklist/toggle".equals(path)) {
                toggleChecklist(request, response);
            } else {
                listRelatedTasks(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            String path = request.getServletPath();
            if ("/tasks/comment".equals(path)) {
                addComment(request, response);
            } else if ("/tasks/checklist/toggle".equals(path)) {
                toggleChecklist(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void listRelatedTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        long currentUserId = currentUserId(request);
        int page = parseInt(request.getParameter("page"), 1);
        int totalRecords = taskDAO.countTasks(keyword, status, currentUserId, true);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / PAGE_SIZE));
        if (page > totalPages) {
            page = totalPages;
        }

        request.setAttribute("tasks", taskDAO.getTasks(keyword, status, page, PAGE_SIZE, currentUserId, true));
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("activeView", "mine");
        request.setAttribute("listAction", "/tasks");
        request.setAttribute("canViewAllTasks", hasPermission(request, TASK_VIEW_ALL));
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.getRequestDispatcher("/WEB-INF/views/task/task-list.jsp").forward(request, response);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = parseLong(request.getParameter("id"), 0);
        Task task = taskDAO.getTaskById(id);
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }

        long currentUserId = currentUserId(request);
        if (!canViewTask(task, currentUserId, request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        request.setAttribute("task", task);
        request.setAttribute("creator", userDAO.findById((int) task.getCreatedBy()));
        request.setAttribute("assignee", userDAO.findById((int) task.getAssignedTo()));
        request.setAttribute("participants", task.getParticipants());
        request.setAttribute("observers", task.getObservers());
        request.setAttribute("checklistItems", task.getChecklistItems());
        request.setAttribute("comments", commentDAO.getCommentsByTaskId(id));
        request.setAttribute("histories", historyDAO.getHistoriesByTaskId(id));
        request.setAttribute("canToggleChecklist", canToggleChecklist(task, currentUserId));
        request.setAttribute("canUpdateTask", canModifyTask(task, currentUserId, request, TASK_UPDATE));
        request.setAttribute("canDeleteTask", canModifyTask(task, currentUserId, request, TASK_DELETE));
        request.setAttribute("canManageChecklist", canModifyTask(task, currentUserId, request, TASK_MANAGE_CHECKLIST));
        request.setAttribute("canUpdateTaskStatus", canModifyTask(task, currentUserId, request, TASK_UPDATE_STATUS));
        request.getRequestDispatcher("/WEB-INF/views/task/task-detail.jsp").forward(request, response);
    }

    private void toggleChecklist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long itemId = parseLong(request.getParameter("itemId"), 0);
        long taskId = parseLong(request.getParameter("taskId"), 0);
        TaskChecklistItem item = checklistItemDAO.getChecklistItemById(itemId);
        if (item == null || item.getTaskId() != taskId) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }

        Task task = taskDAO.getTaskById(item.getTaskId());
        long currentUserId = currentUserId(request);
        if (!canToggleChecklist(task, currentUserId)) {
            request.getSession().setAttribute("error", "You do not have permission to complete this checklist item.");
            response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + item.getTaskId());
            return;
        }

        String completedParameter = request.getParameter("completed");
        boolean completed = completedParameter == null ? !item.isCompleted() : "true".equals(completedParameter);
        checklistItemDAO.toggleChecklistItem(itemId, taskId, completed);
        historyDAO.insertHistory(item.getTaskId(), currentUserId, "Subtask updated",
                (completed ? "Completed subtask: " : "Reopened subtask: ") + item.getContent());
        taskDAO.refreshProgressAndAutoComplete(item.getTaskId());
        response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + item.getTaskId());
    }

    private void addComment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long taskId = parseLong(request.getParameter("taskId"), 0);
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }
        if (!canViewTask(task, currentUserId(request), request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String content = trim(request.getParameter("content"));
        if (content == null) {
            request.getSession().setAttribute("error", "Comment content is required.");
            response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + taskId);
            return;
        }

        long currentUserId = currentUserId(request);
        commentDAO.insertComment(taskId, currentUserId, content);
        historyDAO.insertHistory(taskId, currentUserId, "Comment", "Added a comment");
        response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + taskId);
    }

    private boolean canViewTask(Task task, long currentUserId, HttpServletRequest request) {
        if (task == null || currentUserId <= 0) {
            return false;
        }
        if (hasPermission(request, TASK_VIEW_ALL)) {
            return true;
        }
        if (task.getCreatedBy() == currentUserId || task.getAssignedTo() == currentUserId) {
            return true;
        }
        for (TaskParticipant participant : task.getParticipants()) {
            if (participant.getUserId() == currentUserId) {
                return true;
            }
        }
        for (TaskObserver observer : task.getObservers()) {
            if (observer.getUserId() == currentUserId) {
                return true;
            }
        }
        for (TaskChecklistItem item : task.getChecklistItems()) {
            Long assignedTo = item.getAssignedTo();
            if (assignedTo != null && assignedTo == currentUserId) {
                return true;
            }
        }
        return false;
    }

    private boolean canToggleChecklist(Task task, long currentUserId) {
        if (task == null || currentUserId <= 0 || "PAUSED".equals(task.getStatus())) {
            return false;
        }
        if (task.getCreatedBy() == currentUserId) {
            return true;
        }
        return task.isAllowParticipantsCompleteChecklist()
                && (task.getAssignedTo() == currentUserId
                || participantDAO.existsByTaskIdAndUserId(task.getId(), currentUserId));
    }

    private boolean canModifyTask(Task task, long currentUserId, HttpServletRequest request, String permission) {
        if (!hasPermission(request, permission) || task == null || currentUserId <= 0) {
            return false;
        }
        return task.getCreatedBy() == currentUserId || hasPermission(request, TASK_VIEW_ALL);
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

    private int parseInt(String value, int defaultValue) {
        try {
            return value == null || value.isBlank() ? defaultValue : Math.max(1, Integer.parseInt(value));
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
