<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body>
<h2>Create New Request</h2>
<form action="create_request" method="POST">

  <p>Proposer:<br>
    <input type="text" value="${sessionScope.fullName}" readonly>
  </p>

  <p>Request Date:<br>
    <input type="text" value="<%= java.time.LocalDate.now() %>" readonly>
  </p>

  <p>Request Type: *<br>
    <select name="type" required>
      <option value="" disabled selected>-- Select --</option>

      <c:forEach var="entry" items="${requestType}">
        <option value="${entry.key}">${entry.value}</option>
      </c:forEach>
    </select>
  </p>

  <p>Approver: *<br>
    <select name="approverId" required>
      <option value="" disabled selected>-- Select --</option>
      <c:forEach items="${businessAdminList}" var="admin">
        <option value="${admin.id}">${admin.fullName}</option>
      </c:forEach>
    </select>
  </p>

  <p>Observer<br>
    <select name="observerId">
      <option value="" disabled selected>-- Select --</option>
      <option value="${deptManager.id}">${deptManager.fullName}</option>
    </select>
  </p>

  <p>Reason *<br>
    <textarea name="reason" rows="5" required></textarea>
  </p>

  <button type="submit">Send Request</button>
  <a href="${pageContext.request.contextPath}/view_my_request" class="btn-cancel">Cancel</a>
</form>
</body>
</html>