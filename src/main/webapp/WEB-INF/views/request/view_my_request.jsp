<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<body>
<h2>My Requests</h2>

<table border="1">
  <thead>
  <tr>
    <th>ID</th>
    <th>Type</th>
    <th>Status</th>
    <th>Created At</th>
    <th>Actions</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach items="${myRequests}" var="req">
    <tr>
      <td>${req.id}</td>
      <td>${req.readableType}</td>
      <td>${req.status}</td>
      <td><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
      <td>
        <a href="request_detail?id=${req.id}&from=my">View Detail</a>
        <c:if test="${req.status == 'PENDING'}">
          <form action="process_request" method="POST" onsubmit="return confirm('Cancel this request?');">
            <input type="hidden" name="requestId" value="${req.id}">
            <input type="hidden" name="action" value="CANCEL">
            <button type="submit">Cancel</button>
          </form>
        </c:if>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>

<br>
<a href="create_request">Create New Request</a>
</body>
</html>