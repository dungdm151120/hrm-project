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
<body class="navbar-layout">
    <%-- Include navbar thay cho sidebar --%>
    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <main class="main-container">
        <div class="container">
            <%-- Header với nút quay lại --%>
            <div class="page-header">
                <h1 class="page-title">Add Department</h1>
                <a href="${pageContext.request.contextPath}/admin/departments" class="btn btn-secondary">
                    ← Back to List
                </a>
            </div>

            <%-- Hiển thị lỗi từ server --%>
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    ⚠ ${error}
                </div>
            </c:if>

            <div class="form-wrapper">
                <form action="${pageContext.request.contextPath}/admin/departments/add" method="post">
                    <div class="form-group">
                        <label>Name <span class="required">*</span></label>
                        <input type="text" name="name" value="<c:out value='${name}'/>"
                               required maxlength="100" placeholder="Nhập tên phòng ban">
                    </div>
                    <div class="form-group">
                        <label>Description <span class="required">*</span></label>
                        <textarea name="description" required
                                  placeholder="Nhập mô tả phòng ban"><c:out value="${description}"/></textarea>
                    </div>
                    <div class="form-group">
                        <label>Status</label>
                        <select name="active">
                            <option value="true"
                                    <c:if test="${empty active or active}">selected</c:if>>Active</option>
                            <option value="false"
                                    <c:if test="${not empty active and !active}">selected</c:if>>Inactive</option>
                        </select>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Add</button>
                        <a href="${pageContext.request.contextPath}/admin/departments" class="btn btn-cancel">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</body>
</html>