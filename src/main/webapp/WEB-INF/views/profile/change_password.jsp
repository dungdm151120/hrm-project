<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Change Password | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Change Password</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty passwordErrors.global}">
                <div class="alert alert-error"><c:out value="${passwordErrors.global}"/></div>
            </c:if>

            <div class="detail-wrapper change-password-wrapper">
                <form class="change-password-form" action="${pageContext.request.contextPath}/change_password" method="post">
                    <div class="form-group">
                        <label for="oldPassword">Old Password <span class="required-star">*</span></label>
                        <input type="password" id="oldPassword" name="oldPassword"
                               placeholder="Enter current password" maxlength="32" required>
                        <c:if test="${not empty passwordErrors.oldPassword}">
                            <small class="field-error"><c:out value="${passwordErrors.oldPassword}"/></small>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="newPassword">New Password <span class="required-star">*</span></label>
                        <input type="password" id="newPassword" name="newPassword"
                               placeholder="Enter new password" minlength="6" maxlength="32" required>
                        <c:if test="${not empty passwordErrors.newPassword}">
                            <small class="field-error"><c:out value="${passwordErrors.newPassword}"/></small>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm New Password <span class="required-star">*</span></label>
                        <input type="password" id="confirmPassword" name="confirmPassword"
                               placeholder="Confirm new password" maxlength="32" required>
                        <c:if test="${not empty passwordErrors.confirmPassword}">
                            <small class="field-error"><c:out value="${passwordErrors.confirmPassword}"/></small>
                        </c:if>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-save">Change Password</button>
                        <a href="${pageContext.request.contextPath}/profile" class="btn-cancel">Back to Profile</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
