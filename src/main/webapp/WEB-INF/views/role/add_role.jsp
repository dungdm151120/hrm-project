<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Role | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Add New Role</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/roles" class="btn-secondary">← Back to List</a>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <div class="detail-wrapper">
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
                        <button type="submit" class="btn-save">Add Role</button>
                        <a href="${pageContext.request.contextPath}/admin/roles" class="btn-cancel">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>