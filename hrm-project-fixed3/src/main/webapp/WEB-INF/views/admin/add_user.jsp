<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New User | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-page">
<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

    <main class="main-content">
        <header class="main-header">
            <h1 class="header-title">Add New User</h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/user_list" class="btn btn-secondary">← Back to List</a>
            </div>
        </header>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">⚠ ${error}</div>
            </c:if>

            <div class="form-wrapper">
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
                        <select id="gender" name="gender">
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
                        <input type="text" id="roleId" name="roleId" placeholder="Enter role id" required>
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
        </div>
    </main>
</div>
</body>
</html>