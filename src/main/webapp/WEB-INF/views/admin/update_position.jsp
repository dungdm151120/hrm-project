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
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Update Position</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${pageContext.request.contextPath}/position/list">Return to position list</a>
            <h2 class="form-title">Update position information</h2>

            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/position/update" method="post">
                <input type="hidden" name="id" value="${position.id}">

                <div class="form-group">
                    <label for="departmentId">Department <span class="required-star">*</span></label>

                    <select id="departmentIdDisplay" class="form-control" disabled>
                        <c:forEach items="${departments}" var="dept">
                            <option value="${dept.id}" ${dept.id == position.departmentId ? 'selected' : ''}>
                                ${dept.name}
                            </option>
                        </c:forEach>
                    </select>
                    <input type="hidden" name="departmentId" value="${position.departmentId}" />
                </div>

                <div class="form-group">
                    <label for="name">Name <span class="required-star">*</span></label>
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
    </div>
</div>

</body>
</html>