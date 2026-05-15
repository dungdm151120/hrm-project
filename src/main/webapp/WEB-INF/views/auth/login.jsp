<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<div class="form-container">
    <h2>Login</h2>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <label>Email</label>
        <input type="email" name="email" required>

        <label>Password</label>
        <input type="password" name="password" required>

        <button type="submit">Login</button>
    </form>

    <p class="error">${error}</p>

    <p>
        <a href="${pageContext.request.contextPath}/landing">Back to landing page</a>
    </p>
</div>

</body>
</html>