<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Position | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <a class="back-link" href="${pageContext.request.contextPath}/position/list">Return to position list</a>
    <h2 class="form-title">Update position information</h2>

    <c:if test="${not empty error}">
        <div style="color: red; margin-bottom: 15px; font-weight: bold;">
            ${error}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/position/update" method="post">
        <input type="hidden" name="id" value="${position.id}">

        <div class="form-group">
            <label for="name">Name <span style="color: var(--danger);">*</span></label>
            <input type="text" id="name" name="name" placeholder="Enter position name" value="${position.name}" required>
        </div>

        <div class="form-group">
            <label for="description">Description</label>
            <textarea id="description" name="description" placeholder="Enter description">${position.description}</textarea>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-save">Update position</button>
            <a href="${pageContext.request.contextPath}/position/list" class="btn-cancel">Cancel</a>
        </div>
    </form>
</div>

</body>
</html>