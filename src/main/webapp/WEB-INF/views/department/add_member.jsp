<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <title>Add Member to Department</title>
</head>

<body>
<form action="${pageContext.request.contextPath}/add_member" method="POST">
  <input type="hidden" name="deptId" value="${deptId}">

  <table border="1">
    <tr>
      <th>Select</th>
      <th>Employee Name</th>
    </tr>
    <c:forEach items="${unassignedUsers}" var="u">
      <tr>
        <td><input type="checkbox" name="userIds" value="${u.id}"></td>
        <td>${u.fullName}</td>
      </tr>
    </c:forEach>
  </table>

  <br>
  <button type="submit">Add Selected Members</button>
  <a href="${pageContext.request.contextPath}/department_members?deptId=${deptId}">Cancel</a>
</form>
</body>
