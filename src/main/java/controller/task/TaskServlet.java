package controller.task;

import dao.TaskChecklistItemDAO;
import dao.TaskCommentDAO;
import dao.TaskDAO;
import dao.TaskHistoryDAO;
import dao.TaskObserverDAO;
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
import model.User;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@WebServlet("/tasks")
public class TaskServlet extends HttpServlet {
    private static final int PAGE_SIZE = 10;
    private static final String TASK_CREATE = "TASK_CREATE";
    private static final String TASK_UPDATE = "TASK_UPDATE";
    private static final String TASK_DELETE = "TASK_DELETE";
    private static final String TASK_MANAGE_CHECKLIST = "TASK_MANAGE_CHECKLIST";
    private static final String TASK_UPDATE_STATUS = "TASK_UPDATE_STATUS";

    private final TaskDAO taskDAO = new TaskDAO();
    private final TaskParticipantDAO participantDAO = new TaskParticipantDAO();
    private final TaskObserverDAO observerDAO = new TaskObserverDAO();
    private final TaskChecklistItemDAO checklistItemDAO = new TaskChecklistItemDAO();
    private final TaskCommentDAO commentDAO = new TaskCommentDAO();
    private final TaskHistoryDAO historyDAO = new TaskHistoryDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = getAction(request);

        try {
            switch (action) {
                case "create" -> showCreateForm(request, response);
                case "detail" -> showDetail(request, response);
                case "edit" -> showEditForm(request, response);
                case "delete" -> deleteTask(request, response);
                case "toggleChecklist" -> toggleChecklist(request, response);
                case "deleteChecklist" -> deleteChecklist(request, response);
                default -> listTasks(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = getAction(request);

        try {
            switch (action) {
                case "insert" -> insertTask(request, response);
                case "update" -> updateTask(request, response);
                case "toggleChecklist" -> toggleChecklist(request, response);
                case "addChecklist" -> addChecklist(request, response);
                case "addComment" -> addComment(request, response);
                case "updateStatus" -> updateStatus(request, response);
                default -> listTasks(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void listTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        int page = parseInt(request.getParameter("page"), 1);
        int totalRecords = taskDAO.countTasks(keyword, status);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / PAGE_SIZE));
        if (page > totalPages) {
            page = totalPages;
        }

        request.setAttribute("tasks", taskDAO.getTasks(keyword, status, page, PAGE_SIZE));
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.getRequestDispatcher("/WEB-INF/views/task/task-list.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!hasPermission(request, TASK_CREATE)) {
            forwardForbidden(request, response, TASK_CREATE);
            return;
        }
        List<User> departmentUsers = getDepartmentUsersForCurrentUser(request);
        request.setAttribute("users", userDAO.getActiveUsersForTaskSelection());
        request.setAttribute("departmentUsers", departmentUsers);
        request.setAttribute("participantUsers", departmentUsers);
        request.getRequestDispatcher("/WEB-INF/views/task/task-create.jsp").forward(request, response);
    }

    private void insertTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!hasPermission(request, TASK_CREATE)) {
            forwardForbidden(request, response, TASK_CREATE);
            return;
        }

        Task task = buildTaskFromRequest(request);
        task.setCreatedBy(currentUserId(request));
        task.setStatus("TODO");
        task.setProgress(0);
        List<User> departmentUsers = getDepartmentUsersForCurrentUser(request);
        validateDepartmentAssignee(task.getAssignedTo(), departmentUsers);

        List<String> checklistContents = cleanTextValues(request.getParameterValues("checklistContent"));
        long taskId = taskDAO.insertTask(task);
        participantDAO.insertParticipants(taskId, allowedUserIds(departmentUsers, request.getParameterValues("participantIds")));
        observerDAO.insertObservers(taskId, parseLongValues(request.getParameterValues("observerIds")));
        insertChecklistItems(taskId, checklistContents, request.getParameterValues("checklistAssignedTo"), departmentUsers);
        taskDAO.refreshProgressAndAutoComplete(taskId);
        historyDAO.insertHistory(taskId, task.getCreatedBy(), "Created", "Task was created");

        request.getSession().setAttribute("message", "Task created successfully.");
        response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = parseLong(request.getParameter("id"), 0);
        Task task = taskDAO.getTaskById(id);
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }

        long currentUserId = currentUserId(request);
        request.setAttribute("task", task);
        request.setAttribute("creator", userDAO.findById((int) task.getCreatedBy()));
        request.setAttribute("assignee", userDAO.findById((int) task.getAssignedTo()));
        request.setAttribute("participants", task.getParticipants());
        request.setAttribute("observers", task.getObservers());
        request.setAttribute("checklistItems", task.getChecklistItems());
        request.setAttribute("comments", commentDAO.getCommentsByTaskId(id));
        request.setAttribute("histories", historyDAO.getHistoriesByTaskId(id));
        request.setAttribute("canToggleChecklist", canToggleChecklist(task, currentUserId));
        request.setAttribute("canUpdateTask", canUpdateTask(request, task));
        request.setAttribute("canDeleteTask", canDeleteTask(request, task));
        request.setAttribute("canManageChecklist", canManageChecklist(request, task));
        request.setAttribute("canUpdateTaskStatus", canUpdateTaskStatus(request, task));
        request.setAttribute("departmentUsers", getDepartmentUsersForTask(task));
        request.getRequestDispatcher("/WEB-INF/views/task/task-detail.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = parseLong(request.getParameter("id"), 0);
        Task task = taskDAO.getTaskById(id);
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }
        if (!canUpdateTask(request, task)) {
            forwardForbidden(request, response, TASK_UPDATE);
            return;
        }

        request.setAttribute("task", task);
        List<User> departmentUsers = getDepartmentUsersForTask(task);
        request.setAttribute("users", userDAO.getActiveUsersForTaskSelection());
        request.setAttribute("departmentUsers", departmentUsers);
        request.setAttribute("participantUsers", departmentUsers);
        request.setAttribute("canManageChecklist", canManageChecklist(request, task));
        request.getRequestDispatcher("/WEB-INF/views/task/task-edit.jsp").forward(request, response);
    }

    private void updateTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long taskId = parseLong(request.getParameter("id"), 0);
        Task existingTask = taskDAO.getTaskById(taskId);
        if (existingTask == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }
        if (!canUpdateTask(request, existingTask)) {
            forwardForbidden(request, response, TASK_UPDATE);
            return;
        }

        Task task = buildTaskFromRequest(request);
        task.setId(taskId);
        task.setProgress("COMPLETED".equals(task.getStatus()) ? 100 : taskDAO.calculateProgress(taskId));
        List<User> departmentUsers = getDepartmentUsersForTask(existingTask);
        validateDepartmentAssignee(task.getAssignedTo(), departmentUsers);
        taskDAO.updateTask(task);
        taskDAO.replaceTaskRelations(
                task,
                allowedUserIds(departmentUsers, request.getParameterValues("participantIds")),
                parseLongValues(request.getParameterValues("observerIds"))
        );

        if (canManageChecklist(request, existingTask)) {
            updateChecklistItems(request, taskId, departmentUsers);
        }
        if ("COMPLETED".equals(task.getStatus())) {
            taskDAO.updateTaskProgress(taskId, 100);
        } else {
            taskDAO.refreshProgressAndAutoComplete(taskId);
        }
        recordTaskUpdateHistory(existingTask, task, currentUserId(request));
        request.getSession().setAttribute("message", "Task updated successfully.");
        response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
    }

    private void deleteTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long id = parseLong(request.getParameter("id"), 0);
        Task task = taskDAO.getTaskById(id);
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }
        if (!canDeleteTask(request, task)) {
            forwardForbidden(request, response, TASK_DELETE);
            return;
        }
        taskDAO.deleteTask(id);
        request.getSession().setAttribute("message", "Task deleted successfully.");
        response.sendRedirect(request.getContextPath() + "/tasks");
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
            response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + item.getTaskId());
            return;
        }

        String completedParameter = request.getParameter("completed");
        boolean completed = completedParameter == null ? !item.isCompleted() : "true".equals(completedParameter);
        checklistItemDAO.toggleChecklistItem(itemId, taskId, completed);
        taskDAO.refreshProgressAndAutoComplete(item.getTaskId());
        historyDAO.insertHistory(
                item.getTaskId(),
                currentUserId,
                "Subtask updated",
                (completed ? "Completed subtask: " : "Reopened subtask: ") + item.getContent()
        );
        response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + item.getTaskId());
    }

    private void addChecklist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long taskId = parseLong(request.getParameter("taskId"), 0);
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }
        if (!canManageChecklist(request, task)) {
            forwardForbidden(request, response, TASK_MANAGE_CHECKLIST);
            return;
        }

        String content = trim(request.getParameter("content"));
        if (content == null) {
            request.getSession().setAttribute("error", "Subtask content is required.");
            response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
            return;
        }

        TaskChecklistItem item = new TaskChecklistItem();
        item.setTaskId(taskId);
        item.setContent(content);
        item.setAssignedTo(allowedNullableUserId(request.getParameter("assignedTo"), getDepartmentUsersForTask(task)));
        checklistItemDAO.insertChecklistItem(item);
        taskDAO.refreshProgressAndAutoComplete(taskId);
        historyDAO.insertHistory(taskId, currentUserId(request), "Add subtask", "Added subtask: " + content);
        response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
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
        if (!canManageChecklist(request, task)) {
            forwardForbidden(request, response, TASK_MANAGE_CHECKLIST);
            return;
        }
        checklistItemDAO.deleteChecklistItem(itemId, taskId);
        taskDAO.refreshProgressAndAutoComplete(item.getTaskId());
        historyDAO.insertHistory(item.getTaskId(), currentUserId(request), "Delete subtask", "Deleted subtask: " + item.getContent());
        response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + item.getTaskId());
    }

    private void addComment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long taskId = parseLong(request.getParameter("taskId"), 0);
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }

        String content = trim(request.getParameter("content"));
        if (content == null) {
            request.getSession().setAttribute("error", "Comment content is required.");
            response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
            return;
        }

        long currentUserId = currentUserId(request);
        commentDAO.insertComment(taskId, currentUserId, content);
        historyDAO.insertHistory(taskId, currentUserId, "Comment", "Added a comment");
        response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
    }

    private void updateStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long taskId = parseLong(request.getParameter("id"), 0);
        if (taskId <= 0) {
            taskId = parseLong(request.getParameter("taskId"), 0);
        }
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }
        if (!canUpdateTaskStatus(request, task)) {
            forwardForbidden(request, response, TASK_UPDATE_STATUS);
            return;
        }

        String status = normalizeStatus(request.getParameter("status"));
        taskDAO.updateTaskStatus(taskId, status);
        if ("COMPLETED".equals(status)) {
            taskDAO.updateTaskProgress(taskId, 100);
        } else {
            taskDAO.refreshProgressAndAutoComplete(taskId);
        }
        historyDAO.insertHistory(
                taskId,
                currentUserId(request),
                "Status changed",
                "Updated status: " + task.getStatus() + " -> " + status
        );
        response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
    }

    private Task buildTaskFromRequest(HttpServletRequest request) {
        Task task = new Task();
        task.setTitle(requiredTrim(request.getParameter("title"), "Task name"));
        task.setDescription(trim(request.getParameter("description")));
        task.setAssignedTo(parseLong(request.getParameter("assignedTo"), 0));
        task.setDeadline(parseRequiredDate(request.getParameter("deadline")));
        task.setStatus(normalizeStatus(request.getParameter("status")));
        task.setAllowParticipantsCompleteChecklist(request.getParameter("allowParticipantsCompleteChecklist") != null);
        if (task.getAssignedTo() <= 0) {
            throw new IllegalArgumentException("Assignee is required.");
        }
        return task;
    }

    private void insertChecklistItems(long taskId, List<String> contents, String[] assignedValues, List<User> allowedUsers) throws Exception {
        for (int i = 0; i < contents.size(); i++) {
            TaskChecklistItem item = new TaskChecklistItem();
            item.setTaskId(taskId);
            item.setContent(contents.get(i));
            if (assignedValues != null && i < assignedValues.length) {
                item.setAssignedTo(allowedNullableUserId(assignedValues[i], allowedUsers));
            }
            checklistItemDAO.insertChecklistItem(item);
        }
    }

    private void updateChecklistItems(HttpServletRequest request, long taskId, List<User> allowedUsers) throws Exception {
        String[] itemIds = request.getParameterValues("checklistId");
        String[] contents = request.getParameterValues("checklistContent");
        String[] assignedToValues = request.getParameterValues("checklistAssignedTo");
        if (contents == null) {
            return;
        }

        for (int i = 0; i < contents.length; i++) {
            String content = trim(contents[i]);
            if (content == null) {
                continue;
            }
            Long assignedTo = assignedToValues != null && i < assignedToValues.length
                    ? allowedNullableUserId(assignedToValues[i], allowedUsers)
                    : null;
            long itemId = itemIds != null && i < itemIds.length ? parseLong(itemIds[i], 0) : 0;

            TaskChecklistItem item = new TaskChecklistItem();
            item.setId(itemId);
            item.setTaskId(taskId);
            item.setContent(content);
            item.setAssignedTo(assignedTo);
            if (itemId > 0) {
                checklistItemDAO.updateChecklistItem(item);
            } else {
                checklistItemDAO.insertChecklistItem(item);
            }
        }
    }

    private void recordTaskUpdateHistory(Task oldTask, Task newTask, long userId) throws Exception {
        historyDAO.insertHistory(newTask.getId(), userId, "Update", "Task information was updated");

        if (oldTask.getAssignedTo() != newTask.getAssignedTo()) {
            historyDAO.insertHistory(
                    newTask.getId(),
                    userId,
                    "Assignee changed",
                    "Updated assignee: " + userDisplayName(oldTask.getAssignedTo()) + " -> " + userDisplayName(newTask.getAssignedTo())
            );
        }

        if (!Objects.equals(oldTask.getDeadline(), newTask.getDeadline())) {
            historyDAO.insertHistory(
                    newTask.getId(),
                    userId,
                    "Deadline changed",
                    "Updated deadline: " + dateText(oldTask.getDeadline()) + " -> " + dateText(newTask.getDeadline())
            );
        }

        if (!Objects.equals(oldTask.getStatus(), newTask.getStatus())) {
            historyDAO.insertHistory(
                    newTask.getId(),
                    userId,
                    "Status changed",
                    "Updated status: " + oldTask.getStatus() + " -> " + newTask.getStatus()
            );
        }
    }

    private String userDisplayName(long userId) {
        User user = userDAO.findById((int) userId);
        if (user == null) {
            return String.valueOf(userId);
        }
        return user.getFullName();
    }

    private String dateText(Date date) {
        return date == null ? "-" : date.toString();
    }

    private boolean canUpdateTask(HttpServletRequest request, Task task) {
        return task != null && hasPermission(request, TASK_UPDATE);
    }

    private boolean canDeleteTask(HttpServletRequest request, Task task) {
        return task != null && hasPermission(request, TASK_DELETE);
    }

    private boolean canManageChecklist(HttpServletRequest request, Task task) {
        return task != null && hasPermission(request, TASK_MANAGE_CHECKLIST);
    }

    private boolean canUpdateTaskStatus(HttpServletRequest request, Task task) {
        return task != null && hasPermission(request, TASK_UPDATE_STATUS);
    }

    private boolean canToggleChecklist(Task task, long currentUserId) {
        if (task == null || currentUserId <= 0) {
            return false;
        }
        if (task.getAssignedTo() == currentUserId) {
            return true;
        }
        boolean participant = participantDAO.existsByTaskIdAndUserId(task.getId(), currentUserId);
        return task.isAllowParticipantsCompleteChecklist() && participant;
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

    private long currentUserId(HttpServletRequest request) {
        Object value = request.getSession().getAttribute("userId");
        if (value instanceof Number number) {
            return number.longValue();
        }
        return parseLong(String.valueOf(value), 0);
    }

    private String getAction(HttpServletRequest request) {
        String action = request.getParameter("action");
        return action == null || action.isBlank() ? "list" : action;
    }

    private List<Long> parseLongValues(String[] values) {
        Set<Long> ids = new LinkedHashSet<>();
        if (values == null) {
            return new ArrayList<>();
        }
        for (String value : values) {
            Long id = parseNullableLong(value);
            if (id != null && id > 0) {
                ids.add(id);
            }
        }
        return new ArrayList<>(ids);
    }

    private List<User> getDepartmentUsersForCurrentUser(HttpServletRequest request) {
        return getDepartmentUsersForUser(currentUser(request));
    }

    private List<User> getDepartmentUsersForTask(Task task) {
        if (task == null) {
            return userDAO.getActiveUsersForTaskSelection();
        }
        return getDepartmentUsersForUser(userDAO.findById((int) task.getCreatedBy()));
    }

    private List<User> getDepartmentUsersForUser(User user) {
        if (user == null || user.getDepartmentId() == null || isBusinessOrSystemAdmin(user)) {
            return userDAO.getActiveUsersForTaskSelection();
        }
        return userDAO.findActiveByDepartmentId(user.getDepartmentId());
    }

    private List<Long> allowedUserIds(List<User> allowedUsers, String[] values) {
        Set<Long> requestedIds = new LinkedHashSet<>(parseLongValues(values));
        if (requestedIds.isEmpty()) {
            return new ArrayList<>();
        }

        requestedIds.retainAll(allowedUserIdSet(allowedUsers));
        return new ArrayList<>(requestedIds);
    }

    private Long allowedNullableUserId(String value, List<User> allowedUsers) {
        Long userId = parseNullableLong(value);
        if (userId == null || userId <= 0) {
            return null;
        }
        return allowedUserIdSet(allowedUsers).contains(userId) ? userId : null;
    }

    private void validateDepartmentAssignee(long assigneeId, List<User> allowedUsers) {
        if (!allowedUserIdSet(allowedUsers).contains(assigneeId)) {
            throw new IllegalArgumentException("Assignee must belong to the task creator's department.");
        }
    }

    private Set<Long> allowedUserIdSet(List<User> allowedUsers) {
        Set<Long> ids = new LinkedHashSet<>();
        if (allowedUsers == null) {
            return ids;
        }
        for (User user : allowedUsers) {
            ids.add((long) user.getId());
        }
        return ids;
    }

    private User currentUser(HttpServletRequest request) {
        Object value = request.getSession().getAttribute("currentUser");
        if (value instanceof User user) {
            return user;
        }
        return userDAO.findById((int) currentUserId(request));
    }

    private boolean isBusinessOrSystemAdmin(User user) {
        String roleName = user.getRoleName();
        return "BUSINESS ADMIN".equalsIgnoreCase(roleName) || "SYSTEM ADMIN".equalsIgnoreCase(roleName);
    }

    private List<String> cleanTextValues(String[] values) {
        List<String> result = new ArrayList<>();
        if (values == null) {
            return result;
        }
        for (String value : values) {
            String trimmed = trim(value);
            if (trimmed != null) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private Date parseRequiredDate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Deadline is required.");
        }
        return Date.valueOf(value);
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank() || "OVERDUE".equals(status)) {
            return "TODO";
        }
        return switch (status) {
            case "TODO", "IN_PROGRESS", "COMPLETED", "PAUSED" -> status;
            default -> "TODO";
        };
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

    private int parseInt(String value, int defaultValue) {
        try {
            return value == null || value.isBlank() ? defaultValue : Math.max(1, Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String requiredTrim(String value, String fieldName) {
        String trimmed = trim(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return trimmed;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void forwardForbidden(HttpServletRequest request, HttpServletResponse response, String permission)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.setAttribute("permissionDenied", permission);
        request.getRequestDispatcher("/WEB-INF/views/common/403.jsp").forward(request, response);
    }
}
