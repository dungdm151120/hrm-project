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
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <div class="page-header">
        <h2>Update Department Info</h2>
        <a href="${pageContext.request.contextPath}/admin/departments" class="btn btn-secondary">← Back to List</a>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-error">
            <span>⚠</span> ${error}
        </div>
    </c:if>

    <c:if test="${empty department}">
        <div class="alert alert-error">
            <span>⚠</span> Department not found.
        </div>
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

</body>
</html>