<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New User | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <a class="back-link" href="${pageContext.request.contextPath}/user_list">Return to user list</a>
    <h2 class="form-title">Add new user</h2>

    <c:if test="${not empty error}">
        <div>${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/users/add" method="post">
        <div class="form-group">
            <label for="fullName">Full Name <span style="color: var(--danger);">*</span></label>
            <input type="text" id="fullName" name="fullName" placeholder="Enter full name" required>
        </div>

        <div class="form-group">
            <label for="email">Email <span style="color: var(--danger);">*</span></label>
            <input type="email" id="email" name="email" placeholder="Enter email address" required>
        </div>

        <div class="form-group">
            <label for="password">Password <span style="color: var(--danger);">*</span></label>
            <input type="password" id="password" name="password" placeholder="Enter password" required>
        </div>

        <div class="form-group">
            <label for="phone">Phone</label>
            <input type="text" id="phone" name="phone" placeholder="Enter phone number">
        </div>

        <div class="form-group">
            <label for="gender">Gender</label>
            <select id="gender" name="gender" class="form-select">
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
            </select>
        </div>

        <div class="form-group">
            <label for="dateOfBirth">Date of Birth</label>
            <input type="date" id="dateOfBirth" name="dateOfBirth">
        </div>

        <div class="form-group">
            <label for="address">Address</label>
            <input type="text" id="address" name="address" placeholder="Enter address">
        </div>

        <div class="form-group">
            <label for="avatarUrl">Avatar URL</label>
            <input type="text" id="avatarUrl" name="avatarUrl" placeholder="Enter photo url">
        </div>

        <div class="form-group">
            <label for="roleId">Role <span style="color: var(--danger);">*</span></label>
            <select id="roleId" name="roleId" required style="width: 100%; height: 38px; border-radius: 4px; border: 1px solid #ccc; padding: 0 10px;">
                <option value="" disabled selected>-- Select a role --</option>

                <c:forEach items="${roles}" var="role">
                    <option value="${role.id}">${role.name}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Active Status</label>
            <div class="radio-group">
                <label class="radio-label">
                    <input type="radio" name="active" value="true" checked>
                    <span>Active</span>
                </label>
                <label class="radio-label">
                    <input type="radio" name="active" value="false">
                    <span>Inactive</span>
                </label>
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-save">Add user</button>
            <a href="${pageContext.request.contextPath}/user_list" class="btn-cancel">Cancel</a>
        </div>
    </form>
</div>

</body>
</html>