<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update User | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-page">
<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

    <main class="main-content">
        <%
            User userToUpdate = (User) request.getAttribute("userToUpdate");
        %>
        <header class="main-header">
            <h1 class="header-title">Update User: <%= userToUpdate.getFullName() %></h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/user_list" class="btn btn-secondary">← Back to List</a>
            </div>
        </header>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">⚠ ${error}</div>
            </c:if>

            <div class="form-wrapper">
                <form action="${pageContext.request.contextPath}/users/update" method="post">
                    <input type="hidden" name="id" value="<%= userToUpdate.getId() %>">

                    <div class="form-group">
                        <label for="fullName">Full Name <span style="color: var(--danger);">*</span></label>
                        <input type="text" id="fullName" name="fullName" value="<%= userToUpdate.getFullName() %>" required>
                    </div>

                    <div class="form-group">
                        <label for="email">Email <span style="color: var(--danger);">*</span></label>
                        <input type="email" id="email" name="email" value="<%= userToUpdate.getEmail() %>" required>
                    </div>

                    <div class="form-group">
                        <label for="phone">Phone</label>
                        <input type="text" id="phone" name="phone" value="<%= userToUpdate.getPhone() != null ? userToUpdate.getPhone() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="gender">Gender</label>
                        <select id="gender" name="gender">
                            <option value="Male" <%= "Male".equalsIgnoreCase(userToUpdate.getGender()) ? "selected" : "" %>>Male</option>
                            <option value="Female" <%= "Female".equalsIgnoreCase(userToUpdate.getGender()) ? "selected" : "" %>>Female</option>
                            <option value="Other" <%= "Other".equalsIgnoreCase(userToUpdate.getGender()) ? "selected" : "" %>>Other</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="dateOfBirth">Date of Birth</label>
                        <%
                            String dobString = "";
                            if (userToUpdate.getDateOfBirth() != null) {
                                dobString = userToUpdate.getDateOfBirth().toLocalDate().toString();
                            }
                        %>
                        <input type="date" id="dateOfBirth" name="dateOfBirth" value="<%= dobString %>">
                    </div>

                    <div class="form-group">
                        <label for="address">Address</label>
                        <input type="text" id="address" name="address" value="<%= userToUpdate.getAddress() != null ? userToUpdate.getAddress() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="avatarUrl">Avatar URL</label>
                        <input type="text" id="avatarUrl" name="avatarUrl" value="<%= userToUpdate.getAvatarUrl() != null ? userToUpdate.getAvatarUrl() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="roleId">Role ID <span style="color: var(--danger);">*</span></label>
                        <input type="text" id="roleId" name="roleId" value="<%= userToUpdate.getRoleId() %>" required>
                    </div>

                    <div class="form-group">
                        <label>Active Status</label>
                        <div class="radio-group">
                            <label class="radio-label">
                                <input type="radio" name="active" value="true" <%= userToUpdate.isActive() ? "checked" : "" %>>
                                <span>Active</span>
                            </label>
                            <label class="radio-label">
                                <input type="radio" name="active" value="false" <%= !userToUpdate.isActive() ? "checked" : "" %>>
                                <span>Inactive</span>
                            </label>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-save">Update</button>
                        <a href="${pageContext.request.contextPath}/user_list" class="btn-cancel">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>
</body>
</html>