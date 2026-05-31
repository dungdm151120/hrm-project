<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <title>Move Member</title>
</head>
<body>
<h2>Move: ${user.fullName}</h2>

<form action="${pageContext.request.contextPath}/move_member" method="POST">
  <input type="hidden" name="userId" value="${user.id}">

  <label>Select New Department:</label>
  <select name="newDeptId">
    <c:forEach items="${deptList}" var="d">
      <option value="${d.id}" ${d.id == user.departmentId ? 'selected' : ''}>
          ${d.name}
      </option>
    </c:forEach>
  </select>

  <br><br>
  <button type="submit">Move</button>
  <a href="${pageContext.request.contextPath}/department_list">Cancel</a>
</form>
</body>
</html>
