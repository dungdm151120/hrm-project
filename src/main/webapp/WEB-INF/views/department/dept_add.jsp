<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Department | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Add Department</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/departments" class="btn-secondary">← Back to List</a>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">⚠ ${error}</div>
            </c:if>

            <div class="detail-wrapper">
                <form action="${pageContext.request.contextPath}/admin/departments/add" method="post">
                    <div class="form-group">
                        <label>Name <span class="required">*</span></label>
                        <input type="text" name="name" value="<c:out value='${name}'/>"
                               required maxlength="100" placeholder="Enter department name">
                    </div>
                    <div class="form-group">
                        <label>Description <span class="required">*</span></label>
                        <textarea name="description" required
                                  placeholder="Enter department description"><c:out value="${description}"/></textarea>
                    </div>
                    <div class="form-group">
                        <label>Status</label>
                        <select name="active">
                            <option value="true" <c:if test="${empty active or active}">selected</c:if>>Active</option>
                            <option value="false" <c:if test="${not empty active and !active}">selected</c:if>>Inactive</option>
                        </select>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn-save">Add</button>
                        <a href="${pageContext.request.contextPath}/admin/departments" class="btn-cancel">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>