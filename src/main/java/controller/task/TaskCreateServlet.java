package controller.task;

import dao.TaskChecklistItemDAO;
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
import model.Task;
import model.TaskChecklistItem;
import model.User;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/tasks/create")
public class TaskCreateServlet extends HttpServlet {
    private final TaskDAO taskDAO = new TaskDAO();
    private final TaskParticipantDAO participantDAO = new TaskParticipantDAO();
    private final TaskObserverDAO observerDAO = new TaskObserverDAO();
    private final TaskChecklistItemDAO checklistItemDAO = new TaskChecklistItemDAO();
    private final TaskHistoryDAO historyDAO = new TaskHistoryDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        List<User> departmentUsers = getDepartmentUsersForCurrentUser(request);
        request.setAttribute("users", userDAO.getActiveUsersForTaskSelection());
        request.setAttribute("departmentUsers", departmentUsers);
        request.setAttribute("participantUsers", departmentUsers);
        request.setAttribute("deadlineMin", LocalDate.now().toString());
        request.getRequestDispatcher("/WEB-INF/views/task/task-create.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            Task task = buildTaskFromRequest(request);
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
            taskDAO.refreshProgressAndAutoComplete(taskId);
            historyDAO.insertHistory(taskId, task.getCreatedBy(), "Created", "Task was created");

            request.getSession().setAttribute("message", "Task created successfully.");
            response.sendRedirect(request.getContextPath() + "/tasks/detail?id=" + taskId);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Task buildTaskFromRequest(HttpServletRequest request) {
        Task task = new Task();
        task.setTitle(requiredTrim(request.getParameter("title"), "Task name"));
        task.setDescription(trim(request.getParameter("description")));
        task.setAssignedTo(parseLong(request.getParameter("assignedTo"), 0));
        task.setDeadline(parseRequiredDate(request.getParameter("deadline")));
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

    private List<User> getDepartmentUsersForCurrentUser(HttpServletRequest request) {
        return getDepartmentUsersForUser(currentUser(request));
    }

    private List<User> getDepartmentUsersForUser(User user) {
        if (user == null || user.getDepartmentId() == null || isBusinessOrSystemAdmin(user)) {
            return userDAO.getActiveUsersForTaskSelection();
        }
        return userDAO.findActiveByDepartmentId(user.getDepartmentId());
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

    private List<Long> allowedUserIds(List<User> allowedUsers, String[] values) {
        Set<Long> requestedIds = new LinkedHashSet<>(parseLongValues(values));
        requestedIds.retainAll(allowedUserIdSet(allowedUsers));
        return new ArrayList<>(requestedIds);
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
            long id = parseLong(value, 0);
            if (id > 0) {
                ids.add(id);
            }
        }
        return new ArrayList<>(ids);
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
        Date deadline = Date.valueOf(value);
        if (deadline.toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline cannot be in the past.");
        }
        return deadline;
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
