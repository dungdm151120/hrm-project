<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<body>
<h2>All Requests</h2>

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
    <c:forEach var="req" items="${requestList}">
        <tr>
            <td>${req.id}</td>
            <td>${req.readableType}</td>
            <td>${req.status}</td>
            <td><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
            <td>
                <a href="request_detail?id=${req.id}&from=all">View Detail</a>
                <c:if test="${req.status == 'APPROVED' || req.status == 'REJECTED'}">
                    <form action="process_request" method="POST" onsubmit="return confirm('Close this request?');">
                        <input type="hidden" name="requestId" value="${req.id}">
                        <input type="hidden" name="action" value="CLOSE">
                        <button type="submit">Close</button>
                    </form>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>