<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Password Reset Requests</title>
</head>
<body>
<h2>Password Reset Requests</h2>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
    <tr>
        <th>ID</th>
        <th>User</th>
        <th>Email</th>
        <th>Reason</th>
        <th>Status</th>
        <th>Created At</th>
        <th>Handled At</th>
        <th>Admin Note</th>
        <th>Action</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${resetRequests}" var="resetRequest">
        <tr>
            <td>${resetRequest.id}</td>
            <td>${resetRequest.fullName}</td>
            <td>${resetRequest.email}</td>
            <td>${resetRequest.reason}</td>
            <td>${resetRequest.status}</td>
            <td>${resetRequest.createdAt}</td>
            <td>${resetRequest.handledAt}</td>
            <td>${resetRequest.adminNote}</td>
            <td>
                <c:if test="${resetRequest.status == 'PENDING'}">
                    <form action="${pageContext.request.contextPath}/admin/password-reset/approve" method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${resetRequest.id}">
                        <button type="submit">Reset password</button>
                    </form>

                    <form action="${pageContext.request.contextPath}/admin/password-reset/reject" method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${resetRequest.id}">
                        <input type="hidden" name="adminNote" value="Rejected by admin">
                        <button type="submit">Reject</button>
                    </form>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<br>
<a href="${pageContext.request.contextPath}/home">Back to homepage</a>
</body>
</html>
