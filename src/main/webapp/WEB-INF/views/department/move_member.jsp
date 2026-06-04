<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Move member | HRM</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

  <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

  <div class="dashboard-main">
    <div class="dashboard-header">
      <div class="header-left">
        <h1 class="header-title">Move member</h1>
      </div>
      <div class="header-right">
        <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${user.departmentId}" class="btn-secondary">← Back to employees</a>
      </div>
    </div>

    <div class="dashboard-content">
      <p>Move: <strong>${user.fullName}</strong></p>

      <form action="${pageContext.request.contextPath}/move_member" method="POST">
        <input type="hidden" name="userId" value="${user.id}">
        <input type="hidden" name="currentDeptId" value="${user.departmentId}">

        <div class="form-group">
          <label>Select New Department:</label>
          <select name="newDeptId">
            <c:forEach items="${deptList}" var="d">
              <option value="${d.id}">${d.name}</option>
            </c:forEach>
          </select>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-save">Confirm</button>
          <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${user.departmentId}" class="btn-cancel">Cancel</a>
        </div>
      </form>
    </div>
  </div>
</div>

</body>
</html>