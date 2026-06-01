<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Contract | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <a class="back-link" href="${pageContext.request.contextPath}/home">Back to home</a>
    <div class="page-header">
        <h2>My Contract</h2>
    </div>
    <div class="empty-state">${message}</div>
</div>

</body>
</html>
