<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Update Task | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .task-form { display: grid; gap: 18px; max-width: 980px; }
        .form-row { display: grid; gap: 8px; }
        .task-form input, .task-form select, .task-form textarea { width: 100%; padding: 10px 12px; border: 1px solid #d1d5db; border-radius: 6px; }
        .task-form select[multiple] { min-height: 120px; }
        .user-picker { border: 1px solid #d1d5db; border-radius: 8px; background: #fff; overflow: hidden; }
        .user-picker-search { border: 0 !important; border-bottom: 1px solid #e5e7eb !important; border-radius: 0 !important; }
        .user-picker-list { max-height: 220px; overflow-y: auto; padding: 8px; display: grid; gap: 6px; }
        .user-picker-item { display: flex; gap: 10px; align-items: flex-start; padding: 8px; border-radius: 6px; cursor: pointer; }
        .user-picker-item:hover { background: #f3f4f6; }
        .user-picker-item input { width: auto; margin-top: 2px; }
        .user-picker-name { color: #111827; font-weight: 600; }
        .user-picker-meta { color: #6b7280; font-size: 13px; margin-top: 2px; display: block; }
        .inline-checkbox { display: flex; align-items: center; gap: 8px; width: fit-content; cursor: pointer; }
        .inline-checkbox input { width: auto; padding: 0; border: 0; margin: 0; }
        .checklist-row { display: grid; grid-template-columns: 1fr 260px auto; gap: 8px; align-items: center; margin-bottom: 8px; }
        .form-actions { display: flex; gap: 10px; }
    </style>
</head>
<body class="dashboard-body">
<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>
    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Update Task</h1>
            </div>
        </div>
        <div class="dashboard-content">
            <fmt:formatDate var="createdAtText" value="${task.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
            <form class="task-form" action="${pageContext.request.contextPath}/tasks?action=update&id=${task.id}" method="post">
                <div class="form-row">
                    <label>Task name *</label>
                    <input type="text" name="title" value="${task.title}" required>
                </div>

                <div class="form-row">
                    <label>Description</label>
                    <textarea name="description" rows="7">${task.description}</textarea>
                </div>

                <div class="form-row">
                    <label>Created by</label>
                    <input type="text" value="${task.createdByName}" readonly>
                </div>

                <div class="form-row">
                    <label>Created at</label>
                    <input type="text" value="${createdAtText}" readonly>
                </div>

                <div class="form-row">
                    <label>Assignee *</label>
                    <div class="user-picker">
                        <input class="user-picker-search" type="text" placeholder="Search assignee" data-target="assigneePicker" oninput="filterUserPicker(this)">
                        <div id="assigneePicker" class="user-picker-list">
                            <c:forEach items="${departmentUsers}" var="user">
                                <label class="user-picker-item" data-search="${user.fullName} ${user.positionName} ${user.roleName}">
                                    <input type="radio" name="assignedTo" value="${user.id}" required ${task.assignedTo == user.id ? 'checked' : ''}>
                                    <span>
                                        <span class="user-picker-name">${user.fullName}</span>
                                        <span class="user-picker-meta">${not empty user.positionName ? user.positionName : user.roleName}</span>
                                    </span>
                                </label>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <div class="form-row">
                    <label>Participants</label>
                    <div class="user-picker">
                        <input class="user-picker-search" type="text" placeholder="Search participants" data-target="participantPicker" oninput="filterUserPicker(this)">
                        <div id="participantPicker" class="user-picker-list">
                            <c:forEach items="${participantUsers}" var="user">
                                <c:set var="selectedParticipant" value="false"/>
                                <c:forEach items="${task.participants}" var="participant">
                                    <c:if test="${participant.userId == user.id}">
                                        <c:set var="selectedParticipant" value="true"/>
                                    </c:if>
                                </c:forEach>
                                <label class="user-picker-item" data-search="${user.fullName} ${user.positionName} ${user.roleName}">
                                    <input type="checkbox" name="participantIds" value="${user.id}" ${selectedParticipant ? 'checked' : ''}>
                                    <span>
                                        <span class="user-picker-name">${user.fullName}</span>
                                        <span class="user-picker-meta">${not empty user.positionName ? user.positionName : user.roleName}</span>
                                    </span>
                                </label>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <div class="form-row">
                    <label>Observers</label>
                    <div class="user-picker">
                        <input class="user-picker-search" type="text" placeholder="Search observers" data-target="observerPicker" oninput="filterUserPicker(this)">
                        <div id="observerPicker" class="user-picker-list">
                            <c:forEach items="${users}" var="user">
                                <c:set var="selectedObserver" value="false"/>
                                <c:forEach items="${task.observers}" var="observer">
                                    <c:if test="${observer.userId == user.id}">
                                        <c:set var="selectedObserver" value="true"/>
                                    </c:if>
                                </c:forEach>
                                <label class="user-picker-item" data-search="${user.fullName} ${user.positionName} ${user.roleName}">
                                    <input type="checkbox" name="observerIds" value="${user.id}" ${selectedObserver ? 'checked' : ''}>
                                    <span>
                                        <span class="user-picker-name">${user.fullName}</span>
                                        <span class="user-picker-meta">${not empty user.positionName ? user.positionName : user.roleName}</span>
                                    </span>
                                </label>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <div class="form-row">
                    <label>Deadline *</label>
                    <input type="date" name="deadline" value="${task.deadline}" required>
                </div>

                <label class="inline-checkbox">
                    <input type="checkbox" name="allowParticipantsCompleteChecklist" value="true" ${task.allowParticipantsCompleteChecklist ? 'checked' : ''}>
                    Allow participants to complete checklist
                </label>

                <c:if test="${canManageChecklist and task.status != 'PAUSED'}">
                    <div class="form-row">
                        <label>Work items</label>
                        <div id="checklistContainer">
                            <c:forEach items="${task.checklistItems}" var="item">
                                <div class="checklist-row">
                                    <input type="hidden" name="checklistId" value="${item.id}">
                                    <input type="text" name="checklistContent" value="${item.content}" placeholder="Work item content">
                                    <select name="checklistAssignedTo">
                                        <option value="">No specific assignee</option>
                                        <c:forEach items="${task.participants}" var="participant">
                                            <option value="${participant.userId}" ${item.assignedTo == participant.userId ? 'selected' : ''}>${participant.userName}</option>
                                        </c:forEach>
                                    </select>
                                    <button type="submit" class="btn-reset"
                                            formaction="${pageContext.request.contextPath}/tasks?action=deleteChecklist&itemId=${item.id}&taskId=${task.id}"
                                            formmethod="post"
                                            formnovalidate
                                            onclick="return confirm('Delete this work item?')">Delete</button>
                                </div>
                            </c:forEach>
                            <c:if test="${empty task.checklistItems}">
                                <div class="checklist-row">
                                    <input type="hidden" name="checklistId" value="">
                                    <input type="text" name="checklistContent" placeholder="Work item content">
                                    <select name="checklistAssignedTo">
                                        <option value="">No specific assignee</option>
                                        <c:forEach items="${task.participants}" var="participant">
                                            <option value="${participant.userId}">${participant.userName}</option>
                                        </c:forEach>
                                    </select>
                                    <button type="button" class="btn-reset" onclick="removeChecklistRow(this)">Delete</button>
                                </div>
                            </c:if>
                        </div>
                        <button type="button" class="btn-secondary" onclick="addChecklistRow()">+ Add work item</button>
                    </div>
                </c:if>

                <div class="form-actions">
                    <button type="submit" class="btn-primary">Save</button>
                    <a href="${pageContext.request.contextPath}/tasks?action=detail&id=${task.id}" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    function addChecklistRow() {
        const container = document.getElementById('checklistContainer');
        const firstRow = document.querySelector('.checklist-row');
        const clone = firstRow.cloneNode(true);
        clone.querySelector('input[name="checklistId"]').value = '';
        clone.querySelector('input[name="checklistContent"]').value = '';
        clone.querySelector('select').value = '';
        const action = clone.querySelector('button');
        if (action.type === 'submit') {
            const button = document.createElement('button');
            button.type = 'button';
            button.className = 'btn-reset';
            button.textContent = 'Delete';
            button.onclick = function() { removeChecklistRow(button); };
            action.replaceWith(button);
        }
        container.appendChild(clone);
    }
    function removeChecklistRow(button) {
        const rows = document.querySelectorAll('.checklist-row');
        if (rows.length > 1) {
            button.closest('.checklist-row').remove();
        } else {
            button.closest('.checklist-row').querySelector('input[name="checklistId"]').value = '';
            button.closest('.checklist-row').querySelector('input[name="checklistContent"]').value = '';
            button.closest('.checklist-row').querySelector('select').value = '';
        }
    }
    function filterUserPicker(input) {
        const keyword = input.value.trim().toLowerCase();
        const target = document.getElementById(input.dataset.target);
        target.querySelectorAll('.user-picker-item').forEach(function(item) {
            const text = (item.dataset.search || item.textContent).toLowerCase();
            item.style.display = text.includes(keyword) ? 'flex' : 'none';
        });
    }
    const deadlineInput = document.querySelector('input[name="deadline"]');
    if (deadlineInput) {
        const today = new Date().toISOString().split('T')[0];
        deadlineInput.min = deadlineInput.value && deadlineInput.value < today ? deadlineInput.value : today;
    }
</script>
</body>
</html>
