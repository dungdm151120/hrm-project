<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Task Detail | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .task-detail-page { padding: 24px; }
        .task-detail-header { display: flex; justify-content: space-between; gap: 16px; align-items: flex-start; margin-bottom: 20px; }
        .task-detail-title { margin: 0; font-size: 24px; line-height: 1.35; color: #111827; }
        .task-actions { display: flex; gap: 8px; flex-wrap: wrap; justify-content: flex-end; }
        .task-actions form { margin: 0; }
        .task-detail-grid { display: grid; grid-template-columns: minmax(0, 2fr) minmax(280px, 1fr); gap: 24px; align-items: start; }
        .task-panel { background: #fff; border: 1px solid #e5e7eb; border-radius: 8px; padding: 20px; margin-bottom: 16px; }
        .task-panel h2, .task-panel h3 { margin-top: 0; color: #111827; }
        .task-description { white-space: pre-wrap; color: #374151; line-height: 1.6; }
        .progress { height: 22px; background: #e5e7eb; border-radius: 999px; overflow: hidden; }
        .progress-bar { height: 100%; min-width: 36px; background: #2563eb; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 600; }
        .checklist-list { display: grid; gap: 10px; }
        .checklist-item { display: grid; grid-template-columns: 28px minmax(0, 1fr) auto; gap: 10px; align-items: center; padding: 10px 0; border-bottom: 1px solid #f3f4f6; }
        .checklist-title { color: #111827; }
        .checklist-assignee { color: #6b7280; font-size: 13px; }
        .checklist-actions { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; justify-content: flex-end; }
        .assign-subtask-form { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
        .assign-subtask-form select { min-width: 180px; padding: 7px 8px; border: 1px solid #d1d5db; border-radius: 6px; }
        .inline-form { display: flex; gap: 8px; flex-wrap: wrap; align-items: center; }
        .inline-form input[type="text"], .inline-form textarea, .inline-form select { padding: 9px 10px; border: 1px solid #d1d5db; border-radius: 6px; }
        .inline-form input[type="text"] { min-width: 260px; flex: 1; }
        .sidebar-section { padding: 14px 0; border-bottom: 1px solid #f3f4f6; }
        .sidebar-section:last-child { border-bottom: 0; }
        .sidebar-label { display: block; color: #6b7280; font-size: 13px; margin-bottom: 6px; }
        .person-name { font-weight: 600; color: #111827; }
        .person-meta { color: #6b7280; font-size: 13px; margin-top: 2px; }
        .people-list { display: grid; gap: 8px; }
        .tabs { display: flex; gap: 8px; border-bottom: 1px solid #e5e7eb; margin-bottom: 16px; }
        .tab-button { border: 0; background: transparent; padding: 10px 12px; cursor: pointer; color: #4b5563; border-bottom: 2px solid transparent; }
        .tab-button.active { color: #2563eb; border-bottom-color: #2563eb; font-weight: 600; }
        .tab-content { display: none; }
        .tab-content.active { display: block; }
        .comment-list { display: grid; gap: 12px; margin-bottom: 16px; }
        .comment-item { padding: 12px; border: 1px solid #e5e7eb; border-radius: 8px; }
        .comment-meta { color: #6b7280; font-size: 13px; margin-bottom: 6px; }
        .history-table { width: 100%; border-collapse: collapse; }
        .history-table th, .history-table td { padding: 10px; border-bottom: 1px solid #e5e7eb; text-align: left; vertical-align: top; }
        .history-table th { color: #374151; background: #f9fafb; }
        .empty-state { color: #6b7280; padding: 12px 0; }
        .button-row { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 16px; }
        @media (max-width: 900px) {
            .task-detail-grid { grid-template-columns: 1fr; }
            .task-detail-header { flex-direction: column; }
            .task-actions { justify-content: flex-start; }
        }
    </style>
</head>
<body class="dashboard-body">
<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>
    <div class="dashboard-main">
        <div class="task-detail-page">
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success">${sessionScope.message}</div>
                <c:remove var="message" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-error">${sessionScope.error}</div>
                <c:remove var="error" scope="session"/>
            </c:if>

            <div class="task-detail-header">
                <h1 class="task-detail-title"><c:out value="${task.title}"/></h1>
                <div class="task-actions">
                    <c:if test="${canUpdateTask}">
                        <a href="${pageContext.request.contextPath}/tasks?action=edit&id=${task.id}" class="btn-primary">Edit</a>
                    </c:if>
                    <c:if test="${canDeleteTask}">
                        <form action="${pageContext.request.contextPath}/tasks?action=delete" method="post"
                              onsubmit="return confirm('Delete this task?')">
                            <input type="hidden" name="id" value="${task.id}">
                            <button type="submit" class="btn-reset">Delete</button>
                        </form>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/tasks?action=list" class="btn-cancel">Back to list</a>
                </div>
            </div>

            <div class="container-fluid">
            <div class="row task-detail-grid">
                <main class="col-md-8">
                    <section class="task-panel">
                        <h2>Description</h2>
                        <c:choose>
                            <c:when test="${not empty task.description}">
                                <div class="task-description"><c:out value="${task.description}"/></div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">No description yet.</div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <section class="task-panel">
                        <h2>Subtasks</h2>
                        <div class="checklist-list">
                            <c:forEach items="${checklistItems}" var="item">
                                <div class="checklist-item">
                                    <div>
                                        <c:choose>
                                            <c:when test="${canToggleChecklist}">
                                                <input type="checkbox"
                                                       onchange="location.href='${pageContext.request.contextPath}/tasks?action=toggleChecklist&itemId=${item.id}&taskId=${task.id}'"
                                                       ${item.completed ? 'checked' : ''}>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" disabled ${item.completed ? 'checked' : ''}>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div>
                                        <div class="checklist-title"><c:out value="${item.content}"/></div>
                                        <div class="checklist-assignee">
                                            <c:choose>
                                                <c:when test="${not empty item.assignedToName}">
                                                    Assignee: <c:out value="${item.assignedToName}"/>
                                                </c:when>
                                                <c:otherwise>No specific assignee</c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div class="checklist-actions">
                                        <c:if test="${canManageChecklist and task.status != 'PAUSED'}">
                                            <form class="assign-subtask-form" action="${pageContext.request.contextPath}/tasks?action=assignChecklist" method="post">
                                                <input type="hidden" name="taskId" value="${task.id}">
                                                <input type="hidden" name="itemId" value="${item.id}">
                                                <select name="assignedTo">
                                                    <option value="">No specific assignee</option>
                                                    <c:forEach items="${participants}" var="participant">
                                                        <option value="${participant.userId}" ${item.assignedTo == participant.userId ? 'selected' : ''}>
                                                            <c:out value="${participant.userName}"/>
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                                <button type="submit" class="btn-secondary">Assign</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/tasks?action=deleteChecklist" method="post"
                                                  onsubmit="return confirm('Delete this subtask?')">
                                                <input type="hidden" name="itemId" value="${item.id}">
                                                <input type="hidden" name="taskId" value="${task.id}">
                                                <button type="submit" class="btn-reset">Delete</button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                            <c:if test="${empty checklistItems}">
                                <div class="empty-state">No subtasks yet.</div>
                            </c:if>
                        </div>
                    </section>

                    <c:if test="${canManageChecklist and task.status != 'PAUSED'}">
                        <section class="task-panel">
                            <h3>Add subtask</h3>
                            <form class="inline-form" action="${pageContext.request.contextPath}/tasks?action=addChecklist" method="post">
                                <input type="hidden" name="taskId" value="${task.id}">
                                <input type="text" name="content" placeholder="Subtask content" required>
                                <select name="assignedTo">
                                    <option value="">No specific assignee</option>
                                    <c:forEach items="${participants}" var="participant">
                                        <option value="${participant.userId}"><c:out value="${participant.userName}"/></option>
                                    </c:forEach>
                                </select>
                                <button type="submit" class="btn-secondary">Add</button>
                            </form>
                        </section>
                    </c:if>

                    <section class="task-panel">
                        <div class="button-row">
                            <c:if test="${canUpdateTaskStatus and task.status != 'COMPLETED'}">
                                <form class="inline-form" action="${pageContext.request.contextPath}/tasks?action=updateStatus" method="post">
                                    <input type="hidden" name="taskId" value="${task.id}">
                                    <c:choose>
                                        <c:when test="${task.status == 'PAUSED'}">
                                            <input type="hidden" name="status" value="RESUME">
                                            <button type="submit" class="btn-secondary">Resume</button>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="hidden" name="status" value="PAUSED">
                                            <button type="submit" class="btn-secondary">Pause</button>
                                        </c:otherwise>
                                    </c:choose>
                                </form>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/tasks?action=list" class="btn-cancel">Back to list</a>
                        </div>
                    </section>

                    <section class="task-panel">
                        <div class="tabs">
                            <button type="button" class="tab-button active" data-tab="comments">Discussion</button>
                            <button type="button" class="tab-button" data-tab="histories">History</button>
                        </div>

                        <div id="comments" class="tab-content active">
                            <div class="comment-list">
                                <c:forEach items="${comments}" var="comment">
                                    <div class="comment-item">
                                        <div class="comment-meta">
                                            <strong><c:out value="${comment.userName}"/></strong>
                                            <c:if test="${not empty comment.userPositionName}">
                                                - <c:out value="${comment.userPositionName}"/>
                                            </c:if>
                                            - <fmt:formatDate value="${comment.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                        </div>
                                        <div class="task-description"><c:out value="${comment.content}"/></div>
                                    </div>
                                </c:forEach>
                                <c:if test="${empty comments}">
                                    <div class="empty-state">No comments yet.</div>
                                </c:if>
                            </div>
                            <form class="inline-form" action="${pageContext.request.contextPath}/tasks?action=addComment" method="post">
                                <input type="hidden" name="taskId" value="${task.id}">
                                <textarea name="content" rows="3" placeholder="Write a comment" required style="width: 100%;"></textarea>
                                <button type="submit" class="btn-primary">Send comment</button>
                            </form>
                        </div>

                        <div id="histories" class="tab-content">
                            <table class="history-table">
                                <thead>
                                <tr>
                                    <th>Time</th>
                                    <th>Actor</th>
                                    <th>Type</th>
                                    <th>Update</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${histories}" var="history">
                                    <tr>
                                        <td><fmt:formatDate value="${history.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                        <td><c:out value="${history.userName}"/></td>
                                        <td><c:out value="${history.actionType}"/></td>
                                        <td><c:out value="${history.content}"/></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty histories}">
                                    <tr>
                                        <td colspan="4" class="empty-state">No update history yet.</td>
                                    </tr>
                                </c:if>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </main>

                <aside class="col-md-4 task-panel">
                    <div class="sidebar-section">
                        <span class="sidebar-label">Deadline</span>
                        <strong><fmt:formatDate value="${task.deadline}" pattern="dd/MM/yyyy"/></strong>
                    </div>

                    <div class="sidebar-section">
                        <span class="sidebar-label">Progress: ${task.progress}%</span>
                        <div class="progress">
                            <div class="progress-bar" style="width: ${task.progress}%">${task.progress}%</div>
                        </div>
                    </div>

                    <div class="sidebar-section">
                        <span class="sidebar-label">Created by</span>
                        <div class="person-name">ID ${creator.id} - <c:out value="${creator.fullName}"/></div>
                        <c:if test="${not empty creator.positionName}">
                            <div class="person-meta"><c:out value="${creator.positionName}"/></div>
                        </c:if>
                    </div>

                    <div class="sidebar-section">
                        <span class="sidebar-label">Assigned to</span>
                        <div class="person-name">ID ${assignee.id} - <c:out value="${assignee.fullName}"/></div>
                        <c:if test="${not empty assignee.positionName}">
                            <div class="person-meta"><c:out value="${assignee.positionName}"/></div>
                        </c:if>
                        <c:if test="${canUpdateTask}">
                            <div class="person-meta">
                                <a href="${pageContext.request.contextPath}/tasks?action=edit&id=${task.id}">Change assignee</a>
                            </div>
                        </c:if>
                    </div>

                    <div class="sidebar-section">
                        <span class="sidebar-label">Participants</span>
                        <div class="people-list">
                            <c:forEach items="${participants}" var="participant">
                                <div><c:out value="${participant.userName}"/></div>
                            </c:forEach>
                            <c:if test="${empty participants}">
                                <div class="empty-state">No participants yet.</div>
                            </c:if>
                        </div>
                    </div>

                    <div class="sidebar-section">
                        <span class="sidebar-label">Observers</span>
                        <div class="people-list">
                            <c:forEach items="${observers}" var="observer">
                                <div><c:out value="${observer.userName}"/></div>
                            </c:forEach>
                            <c:if test="${empty observers}">
                                <div class="empty-state">No observers yet.</div>
                            </c:if>
                        </div>
                    </div>
                </aside>
            </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.querySelectorAll('.tab-button').forEach(function(button) {
        button.addEventListener('click', function() {
            document.querySelectorAll('.tab-button').forEach(function(item) {
                item.classList.remove('active');
            });
            document.querySelectorAll('.tab-content').forEach(function(item) {
                item.classList.remove('active');
            });
            button.classList.add('active');
            document.getElementById(button.dataset.tab).classList.add('active');
        });
    });
</script>
</body>
</html>
