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
import jakarta.servlet.http.HttpSession;
import model.Task;
import model.TaskChecklistItem;
import model.User;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@WebServlet("/tasks/edit")
public class TaskUpdateServlet extends HttpServlet {
    private static final String TASK_MANAGE_CHECKLIST = "TASK_MANAGE_CHECKLIST";

    private final TaskDAO taskDAO = new TaskDAO();
    private final TaskChecklistItemDAO checklistItemDAO = new TaskChecklistItemDAO();
    private final TaskHistoryDAO historyDAO = new TaskHistoryDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        long id = parseLong(request.getParameter("id"), 0);
        Task task = taskDAO.getTaskById(id);
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
            return;
        }

        List<User> departmentUsers = getDepartmentUsersForTask(task);
        request.setAttribute("task", task);
        request.setAttribute("users", userDAO.getActiveUsersForTaskSelection());
        request.setAttribute("departmentUsers", departmentUsers);
        request.setAttribute("participantUsers", departmentUsers);
        request.setAttribute("canManageChecklist", hasPermission(request, TASK_MANAGE_CHECKLIST));
        request.setAttribute("deadlineMin", deadlineMin(task));
        request.getRequestDispatcher("/WEB-INF/views/task/task-edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            long taskId = parseLong(request.getParameter("id"), 0);
            Task existingTask = taskDAO.getTaskById(taskId);
            if (existingTask == null) {
                response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
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
            taskDAO.replaceTaskRelations(task, participantIds, parseLongValues(request.getParameterValues("observerIds")));

            if (hasPermission(request, TASK_MANAGE_CHECKLIST) && !"PAUSED".equals(existingTask.getStatus())) {
                updateChecklistItems(request, taskId, new LinkedHashSet<>(participantIds));
            }
            taskDAO.refreshProgressAndAutoComplete(taskId);
            recordTaskUpdateHistory(existingTask, task, currentUserId(request));
            request.getSession().setAttribute("message", "Task updated successfully.");
            response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + taskId);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Task buildTaskFromRequest(HttpServletRequest request, Task existingTask) {
        Task task = new Task();
        task.setTitle(requiredTrim(request.getParameter("title"), "Task name"));
        task.setDescription(trim(request.getParameter("description")));
        task.setAssignedTo(parseLong(request.getParameter("assignedTo"), 0));
        task.setDeadline(parseRequiredDate(request.getParameter("deadline"), existingTask.getDeadline()));
        task.setAllowParticipantsCompleteChecklist(request.getParameter("allowParticipantsCompleteChecklist") != null);
        if (task.getAssignedTo() <= 0) {
            throw new IllegalArgumentException("Assignee is required.");
        }
        return task;
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
            historyDAO.insertHistory(newTask.getId(), userId, "Assignee changed",
                    "Updated assignee: " + userDisplayName(oldTask.getAssignedTo()) + " -> " + userDisplayName(newTask.getAssignedTo()));
        }
        if (!Objects.equals(oldTask.getDeadline(), newTask.getDeadline())) {
            historyDAO.insertHistory(newTask.getId(), userId, "Deadline changed",
                    "Updated deadline: " + dateText(oldTask.getDeadline()) + " -> " + dateText(newTask.getDeadline()));
        }
    }

    private List<User> getDepartmentUsersForTask(Task task) {
        return getDepartmentUsersForUser(userDAO.findById((int) task.getCreatedBy()));
    }

    private List<User> getDepartmentUsersForUser(User user) {
        if (user == null || user.getDepartmentId() == null || isBusinessOrSystemAdmin(user)) {
            return userDAO.getActiveUsersForTaskSelection();
        }
        return userDAO.findActiveByDepartmentId(user.getDepartmentId());
    }

    private boolean isBusinessOrSystemAdmin(User user) {
        String roleName = user.getRoleName();
        return "BUSINESS ADMIN".equalsIgnoreCase(roleName) || "SYSTEM ADMIN".equalsIgnoreCase(roleName);
    }

    private List<Long> allowedUserIds(List<User> allowedUsers, String[] values) {
        Set<Long> requestedIds = new LinkedHashSet<>(parseLongValues(values));
        requestedIds.retainAll(allowedUserIdSet(allowedUsers));
        return new ArrayList<>(requestedIds);
    }

    private Long allowedNullableUserId(String value, Set<Long> allowedUserIds) {
        Long userId = parseNullableLong(value);
        if (userId == null || userId <= 0) {
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
        for (User user : allowedUsers) {
            ids.add((long) user.getId());
        }
        return ids;
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

    private Date parseRequiredDate(String value, Date currentDeadline) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Deadline is required.");
        }
        Date deadline = Date.valueOf(value);
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

    private String userDisplayName(long userId) {
        User user = userDAO.findById((int) userId);
        return user == null ? String.valueOf(userId) : user.getFullName();
    }

    private String dateText(Date date) {
        return date == null ? "-" : date.toString();
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
}
