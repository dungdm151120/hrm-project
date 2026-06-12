<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Announcement | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Create Announcement</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${pageContext.request.contextPath}/announcements">Back to announcements</a>
            <h2 class="form-title">New Announcement</h2>

            <c:if test="${not empty error}">
                <div class="alert alert-error"><c:out value="${error}"/></div>
            </c:if>

            <form action="${pageContext.request.contextPath}/announcements/add" method="post">
                <div class="form-group">
                    <label for="title">Title <span class="required-star">*</span></label>
                    <input type="text" id="title" name="title" maxlength="200"
                           value="${fn:escapeXml(announcement.title)}" required>
                </div>

                <div class="form-group">
                    <label for="content">Content <span class="required-star">*</span></label>
                    <textarea id="content" name="content" rows="8" required><c:out value="${announcement.content}"/></textarea>
                </div>

                <div class="form-group">
                    <label for="targetScope">Target Audience <span class="required-star">*</span></label>
                    <select id="targetScope" name="targetScope" required>
                        <option value="ALL" ${announcement.targetScope == 'ALL' || empty announcement.targetScope ? 'selected' : ''}>All employees</option>
                        <option value="DEPARTMENT" ${announcement.targetScope == 'DEPARTMENT' ? 'selected' : ''}>Selected department</option>
                    </select>
                </div>

                <div class="form-group" id="departmentGroup">
                    <label for="departmentId">Department <span class="required-star">*</span></label>
                    <select id="departmentId" name="departmentId">
                        <option value="">Select department</option>
                        <c:forEach items="${departments}" var="department">
                            <option value="${department.id}" ${announcement.departmentId == department.id ? 'selected' : ''}>
                                <c:out value="${department.name}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="publishDate">Publish Date <span class="required-star">*</span></label>
                    <input type="datetime-local" id="publishDate" name="publishDate"
                           value="${announcement.publishDate}" required>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save">Send</button>
                    <a href="${pageContext.request.contextPath}/announcements" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const targetScope = document.getElementById('targetScope');
        const departmentGroup = document.getElementById('departmentGroup');
        const departmentId = document.getElementById('departmentId');
        const publishDate = document.getElementById('publishDate');

        function toggleDepartment() {
            const isDepartment = targetScope.value === 'DEPARTMENT';
            departmentGroup.classList.toggle('hidden', !isDepartment);
            departmentId.required = isDepartment;
            departmentId.disabled = !isDepartment;
            if (!isDepartment) {
                departmentId.value = '';
            }
        }

        if (!publishDate.value) {
            const now = new Date();
            now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
            publishDate.value = now.toISOString().slice(0, 16);
        }

        targetScope.addEventListener('change', toggleDepartment);
        toggleDepartment();
    });
</script>
</body>
</html>
