<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Task | HRM</title>
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
        .checklist-row { display: grid; grid-template-columns: 1fr auto; gap: 8px; align-items: center; margin-bottom: 8px; }
        .form-actions { display: flex; gap: 10px; }
    </style>
</head>
<body class="dashboard-body">
<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>
    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Create Task</h1>
            </div>
        </div>
        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>
            <form class="task-form" action="${pageContext.request.contextPath}/tasks/create" method="post">
                <div class="form-row">
                    <label>Task name *</label>
                    <input type="text" name="title" required>
                </div>

                <div class="form-row">
                    <label>Task description</label>
                    <textarea name="description" rows="7"></textarea>
                </div>

                <div class="form-row">
                    <label>Assignee *</label>
                    <div class="user-picker">
                        <input class="user-picker-search" type="text" placeholder="Search assignee" data-target="assigneePicker" oninput="filterUserPicker(this)">
                        <div id="assigneePicker" class="user-picker-list">
                            <c:forEach items="${departmentUsers}" var="user">
                                <label class="user-picker-item" data-search="${user.fullName} ${user.positionName} ${user.roleName}">
                                    <input type="radio" name="assignedTo" value="${user.id}" required>
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
                                <label class="user-picker-item" data-search="${user.fullName} ${user.positionName} ${user.roleName}">
                                    <input type="checkbox" name="participantIds" value="${user.id}">
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
                                <label class="user-picker-item" data-search="${user.fullName} ${user.positionName} ${user.roleName}">
                                    <input type="checkbox" name="observerIds" value="${user.id}">
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
                    <input type="date" name="deadline" min="${deadlineMin}" required>
                </div>

                <label class="inline-checkbox">
                    <input type="checkbox" name="allowParticipantsCompleteChecklist" value="true">
                    Allow assignee and participants to complete checklist
                </label>

                <div class="form-row">
                    <label>Work item list</label>
                    <div id="checklistContainer">
                        <div class="checklist-row">
                            <input type="text" name="checklistContent" placeholder="Work item content">
                            <button type="button" class="btn-reset" onclick="removeChecklistRow(this)">Delete</button>
                        </div>
                    </div>
                    <button type="button" class="btn-secondary" onclick="addChecklistRow()">+ Add work item</button>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-primary">Save</button>
                    <a href="${pageContext.request.contextPath}/tasks" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    function addChecklistRow() {
        const firstRow = document.querySelector('.checklist-row');
        const clone = firstRow.cloneNode(true);
        clone.querySelector('input').value = '';
        document.getElementById('checklistContainer').appendChild(clone);
    }
    function removeChecklistRow(button) {
        const rows = document.querySelectorAll('.checklist-row');
        if (rows.length > 1) {
            button.closest('.checklist-row').remove();
        } else {
            button.closest('.checklist-row').querySelector('input').value = '';
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
</script>
</body>
</html>
