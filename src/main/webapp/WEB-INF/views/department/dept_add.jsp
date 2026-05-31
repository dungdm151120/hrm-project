<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Department</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>
<div class="container" style="margin-top: 2rem;">
    <h2>Add Department</h2>
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    <div class="form-wrapper">
        <form action="${pageContext.request.contextPath}/admin/departments/add" method="post">
            <div class="form-group">
                <label>Name <span class="required">*</span></label>
                <input type="text" name="name" value="${name}" required maxlength="100">
            </div>
            <div class="form-group">
                <label>Description <span class="required">*</span></label>
                <textarea name="description" required>${description}</textarea>
            </div>
            <div class="form-group">
                <label>Manager</label>
                <select name="managerUserId">
                    <option value="">-- No manager --</option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" ${managerUserId == user.id ? 'selected' : ''}>${user.fullName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label>Status</label>
                <select name="active">
                    <option value="true" ${active ? 'selected' : ''}>Active</option>
                    <option value="false" ${!active ? 'selected' : ''}>Inactive</option>
                </select>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Add</button>
                <a href="${pageContext.request.contextPath}/admin/departments" class="btn btn-cancel">Cancel</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>