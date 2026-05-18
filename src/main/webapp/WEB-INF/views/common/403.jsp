<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>403 - Không có quyền truy cập</title>
</head>
<body>

<h1>403 - Truy cập bị từ chối</h1>

<p>Bạn không có quyền thực hiện hành động này.</p>

<% String permission = (String) request.getAttribute("permissionDenied"); %>
<% if (permission != null) { %>
    <p>Permission yêu cầu: <strong><%= permission %></strong></p>
<% } %>

<p>
    <a href="${pageContext.request.contextPath}/login">Quay về trang chủ</a>
</p>

</body>
</html>
