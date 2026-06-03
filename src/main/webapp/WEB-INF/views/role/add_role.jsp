<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Role | HRM</title>
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
            <h1 class="header-title">Add New Role</h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/roles" class="btn btn-secondary">← Back to List</a>
            </div>
        </header>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">⚠ ${error}</div>
            </c:if>

            <div class="form-wrapper">
                <form action="${pageContext.request.contextPath}/admin/roles/add" method="post">
                    <div class="form-group">
                        <label for="name">Role Name <span class="required">*</span></label>
                        <input type="text" id="name" name="name"
                               value="${not empty name ? name : ''}"
                               required maxlength="50"
                               placeholder="Enter role name">
                    </div>

                    <div class="form-group">
                        <label for="description">Description <span class="required">*</span></label>
                        <textarea id="description" name="description"
                                  rows="3" maxlength="255"
                                  placeholder="Enter description">${not empty description ? description : ''}</textarea>
                    </div>

                    <div class="form-group">
                        <label for="active">Status</label>
                        <select id="active" name="active">
                            <option value="true" ${active ? 'selected' : ''}>Active</option>
                            <option value="false" ${!active ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Add Role</button>
                        <a href="${pageContext.request.contextPath}/admin/roles" class="btn btn-cancel">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>
</body>
</html>