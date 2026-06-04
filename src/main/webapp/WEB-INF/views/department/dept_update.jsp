<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Department | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Update Department</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/departments" class="btn-secondary">← Back to List</a>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <c:if test="${empty department}">
                <div class="alert alert-error">Department not found.</div>
            </c:if>

            <c:if test="${not empty department}">
                <div class="detail-wrapper">
                    <form action="${pageContext.request.contextPath}/admin/departments/update?id=${department.id}" method="post">
                        <div class="form-group">
                            <label for="name">Department Name <span class="required">*</span></label>
                            <input type="text" id="name" name="name" value="${department.name}" required maxlength="100" placeholder="Enter department name">
                        </div>

                        <div class="form-group">
                            <label for="description">Description <span class="required">*</span></label>
                            <textarea id="description" name="description" rows="3" maxlength="255" placeholder="Enter description">${department.description}</textarea>
                        </div>

                        <div class="form-group">
                            <label for="active">Status</label>
                            <select id="active" name="active">
                                <option value="true" ${department.active ? 'selected' : ''}>Active</option>
                                <option value="false" ${!department.active ? 'selected' : ''}>Inactive</option>
                            </select>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn-save">Update</button>
                            <a href="${pageContext.request.contextPath}/admin/departments/detail?id=${department.id}" class="btn-cancel">Cancel</a>
                        </div>
                    </form>
                </div>
            </c:if>
        </div>
    </div>
</div>

</body>
</html>