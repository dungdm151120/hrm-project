<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Department | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-page">
<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

    <main class="main-content">
        <header class="main-header">
            <h1 class="header-title">Update Department Info</h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/departments" class="btn btn-secondary">← Back to List</a>
            </div>
        </header>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">⚠ ${error}</div>
            </c:if>

            <c:if test="${empty department}">
                <div class="alert alert-error">⚠ Department not found.</div>
            </c:if>

            <c:if test="${not empty department}">
                <div class="form-wrapper">
                    <form action="${pageContext.request.contextPath}/admin/departments/update?id=${department.id}" method="post">
                        <div class="form-group">
                            <label for="name">Department Name <span class="required">*</span></label>
                            <input type="text" id="name" name="name"
                                   value="${department.name}" required maxlength="100"
                                   placeholder="Enter department name">
                        </div>

                        <div class="form-group">
                            <label for="description">Description <span class="required">*</span></label>
                            <textarea id="description" name="description"
                                      rows="3" maxlength="255"
                                      placeholder="Enter description">${department.description}</textarea>
                        </div>

                        <div class="form-group">
                            <label for="active">Status</label>
                            <select id="active" name="active">
                                <option value="true" ${department.active ? 'selected' : ''}>Active</option>
                                <option value="false" ${!department.active ? 'selected' : ''}>Inactive</option>
                            </select>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">Update</button>
                            <a href="${pageContext.request.contextPath}/admin/departments/detail?id=${department.id}" class="btn btn-cancel">Cancel</a>
                        </div>
                    </form>
                </div>
            </c:if>
        </div>
    </main>
</div>
</body>
</html>