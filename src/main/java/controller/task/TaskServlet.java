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
import model.TaskParticipant;
import model.User;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
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
                case "toggleChecklist" -> toggleChecklist(request, response);
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
                case "delete" -> deleteTask(request, response);
                case "toggleChecklist" -> toggleChecklist(request, response);
                case "addChecklist" -> addChecklist(request, response);
                case "assignChecklist" -> assignChecklist(request, response);
                case "deleteChecklist" -> deleteChecklist(request, response);
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
        request.setAttribute("deadlineMin", LocalDate.now().toString());
        request.getRequestDispatcher("/WEB-INF/views/task/task-create.jsp").forward(request, response);
    }

    private void insertTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!hasPermission(request, TASK_CREATE)) {
            forwardForbidden(request, response, TASK_CREATE);
            return;
        }

        Task task = buildTaskFromRequest(request, null);
        task.setCreatedBy(currentUserId(request));
        task.setStatus("TODO");
        task.setProgress(0);
        List<User> departmentUsers = getDepartmentUsersForCurrentUser(request);
        validateDepartmentAssignee(task.getAssignedTo(), departmentUsers);

        List<String> checklistContents = cleanTextValues(request.getParameterValues("checklistContent"));
        List<Long> participantIds = allowedUserIds(departmentUsers, request.getParameterValues("participantIds"));
        long taskId = taskDAO.insertTask(task);
        participantDAO.insertParticipants(taskId, participantIds);
        observerDAO.insertObservers(taskId, parseLongValues(request.getParameterValues("observerIds")));
        insertChecklistItems(taskId, checklistContents);
        refreshProgressAndRecordStatusChange(taskId, task.getStatus(), task.getCreatedBy());
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
        request.setAttribute("deadlineMin", deadlineMin(task));
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

        Task task = buildTaskFromRequest(request, existingTask);
        task.setId(taskId);
        task.setStatus(existingTask.getStatus());
        task.setProgress(taskDAO.calculateProgress(taskId));
        List<User> departmentUsers = getDepartmentUsersForTask(existingTask);
        validateDepartmentAssignee(task.getAssignedTo(), departmentUsers);
        List<Long> participantIds = allowedUserIds(departmentUsers, request.getParameterValues("participantIds"));
        taskDAO.updateTask(task);
        taskDAO.replaceTaskRelations(
                task,
                participantIds,
                parseLongValues(request.getParameterValues("observerIds"))
        );

        if (canManageChecklist(request, existingTask) && !isPaused(existingTask)) {
            updateChecklistItems(request, taskId, new LinkedHashSet<>(participantIds));
        }
        refreshProgressAndRecordStatusChange(taskId, existingTask.getStatus(), currentUserId(request));
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
        historyDAO.insertHistory(
                item.getTaskId(),
                currentUserId,
                "Subtask updated",
                (completed ? "Completed subtask: " : "Reopened subtask: ") + item.getContent()
        );
        refreshProgressAndRecordStatusChange(item.getTaskId(), task.getStatus(), currentUserId);
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
        if (isPaused(task)) {
            request.getSession().setAttribute("error", "Paused task cannot be changed.");
            response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
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
        item.setAssignedTo(allowedNullableUserId(request.getParameter("assignedTo"), participantUserIdSet(task)));
        checklistItemDAO.insertChecklistItem(item);
        refreshProgressAndRecordStatusChange(taskId, task.getStatus(), currentUserId(request));
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
        if (isPaused(task)) {
            request.getSession().setAttribute("error", "Paused task cannot be changed.");
            response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + item.getTaskId());
            return;
        }
        checklistItemDAO.deleteChecklistItem(itemId, taskId);
        refreshProgressAndRecordStatusChange(item.getTaskId(), task.getStatus(), currentUserId(request));
        historyDAO.insertHistory(item.getTaskId(), currentUserId(request), "Delete subtask", "Deleted subtask: " + item.getContent());
        response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + item.getTaskId());
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
        if (!canManageChecklist(request, task)) {
            forwardForbidden(request, response, TASK_MANAGE_CHECKLIST);
            return;
        }
        if (isPaused(task)) {
            request.getSession().setAttribute("error", "Paused task cannot be changed.");
            response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + item.getTaskId());
            return;
        }

        Long assignedTo = allowedNullableUserId(request.getParameter("assignedTo"), participantUserIdSet(task));
        item.setAssignedTo(assignedTo);
        checklistItemDAO.updateChecklistItem(item);
        historyDAO.insertHistory(
                item.getTaskId(),
                currentUserId(request),
                "Assign subtask",
                assignedTo == null
                        ? "Cleared assignee for subtask: " + item.getContent()
                        : "Assigned subtask to " + userDisplayName(assignedTo) + ": " + item.getContent()
        );
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

        String requestedStatus = request.getParameter("status");
        String oldStatus = task.getStatus();
        String actionType;
        String historyContent;

        if ("PAUSED".equals(requestedStatus)) {
            if ("COMPLETED".equals(oldStatus)) {
                request.getSession().setAttribute("error", "Completed task cannot be paused.");
                response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
                return;
            }
            taskDAO.updateTaskStatus(taskId, "PAUSED");
            actionType = "Task paused";
            historyContent = "Paused task: " + oldStatus + " -> PAUSED";
        } else if ("RESUME".equals(requestedStatus)) {
            if (!"PAUSED".equals(oldStatus)) {
                request.getSession().setAttribute("error", "Only paused task can be resumed.");
                response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
                return;
            }
            taskDAO.resumeTask(taskId);
            String newStatus = taskDAO.calculateStatusFromChecklist(taskId);
            actionType = "Task resumed";
            historyContent = "Resumed task: PAUSED -> " + newStatus;
        } else {
            request.getSession().setAttribute("error", "Task status can only be paused or resumed manually.");
            response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
            return;
        }

        historyDAO.insertHistory(
                taskId,
                currentUserId(request),
                actionType,
                historyContent
        );
        response.sendRedirect(request.getContextPath() + "/tasks?action=detail&id=" + taskId);
    }

    private Task buildTaskFromRequest(HttpServletRequest request, Task existingTask) {
        Task task = new Task();
        task.setTitle(requiredTrim(request.getParameter("title"), "Task name"));
        task.setDescription(trim(request.getParameter("description")));
        task.setAssignedTo(parseLong(request.getParameter("assignedTo"), 0));
        task.setDeadline(parseRequiredDate(request.getParameter("deadline"), existingTask == null ? null : existingTask.getDeadline()));
        task.setAllowParticipantsCompleteChecklist(request.getParameter("allowParticipantsCompleteChecklist") != null);
        if (task.getAssignedTo() <= 0) {
            throw new IllegalArgumentException("Assignee is required.");
        }
        return task;
    }

    private void insertChecklistItems(long taskId, List<String> contents) throws Exception {
        for (String content : contents) {
            TaskChecklistItem item = new TaskChecklistItem();
            item.setTaskId(taskId);
            item.setContent(content);
            checklistItemDAO.insertChecklistItem(item);
        }
    }

    private void refreshProgressAndRecordStatusChange(long taskId, String oldStatus, long userId) throws Exception {
        taskDAO.refreshProgressAndAutoComplete(taskId);
        Task refreshedTask = taskDAO.getTaskById(taskId);
        if (refreshedTask != null && !Objects.equals(oldStatus, refreshedTask.getStatus())) {
            historyDAO.insertHistory(
                    taskId,
                    userId,
                    "Status changed",
                    "Updated status: " + oldStatus + " -> " + refreshedTask.getStatus()
            );
        }
    }

    private void updateChecklistItems(HttpServletRequest request, long taskId, Set<Long> allowedParticipantIds) throws Exception {
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
                    ? allowedNullableUserId(assignedToValues[i], allowedParticipantIds)
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
        if (isPaused(task)) {
            return false;
        }
        if (task.getCreatedBy() == currentUserId) {
            return true;
        }
        if (!task.isAllowParticipantsCompleteChecklist()) {
            return false;
        }
        return task.getAssignedTo() == currentUserId
                || participantDAO.existsByTaskIdAndUserId(task.getId(), currentUserId);
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

    private Long allowedNullableUserId(String value, Set<Long> allowedUserIds) {
        Long userId = parseNullableLong(value);
        if (userId == null || userId <= 0 || allowedUserIds == null) {
            return null;
        }
        return allowedUserIds.contains(userId) ? userId : null;
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

    private boolean isPaused(Task task) {
        return task != null && "PAUSED".equals(task.getStatus());
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

    private Date parseRequiredDate(String value, Date currentDeadline) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Deadline is required.");
        }
        Date deadline;
        try {
            deadline = Date.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Deadline must be a valid date.");
        }
        if (currentDeadline != null && currentDeadline.equals(deadline)) {
            return deadline;
        }
        if (deadline.toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline cannot be in the past.");
        }
        return deadline;
    }

    private String deadlineMin(Task task) {
        LocalDate today = LocalDate.now();
        Date currentDeadline = task == null ? null : task.getDeadline();
        if (currentDeadline != null && currentDeadline.toLocalDate().isBefore(today)) {
            return currentDeadline.toString();
        }
        return today.toString();
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
