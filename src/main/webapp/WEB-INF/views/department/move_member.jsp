<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Move member | HRM</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body>
<div class="container">
  <h2 class="form-title">Move member to a department</h2>
  <p>Move: <strong>${user.fullName}</strong></p>

  <form action="${pageContext.request.contextPath}/move_member" method="POST">
    <input type="hidden" name="userId" value="${user.id}">

    <div class="form-group">
      <label>Select New Department:</label>
      <select name="newDeptId" class="form-control">
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
</body>
</html>
