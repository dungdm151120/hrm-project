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
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Add New User</h1>
            </div>
        </div>

        <div class="dashboard-content">

            <a class="back-link" href="${pageContext.request.contextPath}/user_list">Return to user list</a>
            <h2 class="form-title">Add new user</h2>

            <c:if test="${not empty error}">
                <div class="alert alert-error"><c:out value="${error}" escapeXml="false"/></div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/users/add" method="post">
                <div class="form-group">
                    <label for="fullName">Full Name <span class="required-star">*</span></label>
                    <input type="text" id="fullName" name="fullName" placeholder="Enter full name"
                           minlength="2" maxlength="100" value="<c:out value='${newUser.fullName}'/>" required>
                </div>

                <div class="form-group">
                    <label for="email">Email <span class="required-star">*</span></label>
                    <input type="email" id="email" name="email" placeholder="Enter email address"
                           maxlength="100" value="<c:out value='${newUser.email}'/>" required>
                </div>

                <div class="form-group">
                    <label for="password">Password <span class="required-star">*</span></label>
                    <input type="password" id="password" name="password" placeholder="Enter password (6-32 chars)"
                           minlength="6" maxlength="32" required>
                </div>

                <div class="form-group">
                    <label for="phone">Phone</label>
                    <input type="text" id="phone" name="phone" placeholder="Enter phone number (9-11 digits)"
                           pattern="\d{9,11}" title="Phone number must contain 9 to 11 digits only"
                           value="<c:out value='${newUser.phone}'/>">
                </div>

                <div class="form-group">
                    <label for="gender">Gender</label>
                    <select id="gender" name="gender">
                        <option value="Male" ${newUser.gender == 'Male' ? 'selected' : ''}>Male</option>
                        <option value="Female" ${newUser.gender == 'Female' ? 'selected' : ''}>Female</option>
                        <option value="Other" ${newUser.gender == 'Other' ? 'selected' : ''}>Other</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="dateOfBirth">Date of Birth</label>
                    <input type="date" id="dateOfBirth" name="dateOfBirth" value="${newUser.dateOfBirth != null ? newUser.dateOfBirth.toString().substring(0,10) : ''}">
                </div>

                <div class="form-group">
                    <label for="address">Address</label>
                    <input type="text" id="address" name="address" placeholder="Enter address"
                           maxlength="255" value="<c:out value='${newUser.address}'/>">
                </div>

                <div class="form-group">
                    <label for="avatarUrl">Avatar URL</label>
                    <input type="text" id="avatarUrl" name="avatarUrl" placeholder="Enter photo url"
                           maxlength="255" value="<c:out value='${newUser.avatarUrl}'/>">
                </div>

                <div class="form-group">
                    <label for="roleId">Role <span class="required-star">*</span></label>
                    <select id="roleId" name="roleId" required>
                        <option value="" disabled ${newUser.roleId == null || newUser.roleId == 0 ? 'selected' : ''}>-- Select a role --</option>
                        <c:forEach items="${roles}" var="role">
                            <option value="${role.id}" ${newUser.roleId == role.id ? 'selected' : ''}>${role.name}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label>Active Status</label>
                    <div class="radio-group">
                        <label class="radio-label">
                            <input type="radio" name="active" value="true" ${newUser == null || newUser.active ? 'checked' : ''}>
                            <span>Active</span>
                        </label>
                        <label class="radio-label">
                            <input type="radio" name="active" value="false" ${newUser != null && !newUser.active ? 'checked' : ''}>
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
</div>

<script>
    // Frontend Validation: Ngăn người dùng chọn ngày sinh ở tương lai
    document.getElementById('dateOfBirth').max = new Date().toISOString().split("T")[0];
</script>

</body>
</html>