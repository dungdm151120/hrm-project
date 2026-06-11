<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Import chấm công</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <h1>Import dữ liệu chấm công từ Excel</h1>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/attendance/import"
                  method="post"
                  enctype="multipart/form-data">
                <div class="form-group">
                    <label>Chọn file Excel (.xlsx):</label>
                    <input type="file" name="excelFile" accept=".xlsx" required>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn-save">Upload và Import</button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>