<%@ page import="model.User" %>
<%@ page import="java.time.LocalDate" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    User user = (User) request.getAttribute("user");
    String dob = "";
    String maxDob = LocalDate.now().minusYears(18).toString();
    if (user != null && user.getDateOfBirth() != null) {
        dob = user.getDateOfBirth().toLocalDate().toString();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">My Profile</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty profileSuccess}">
                <div class="alert alert-success"><c:out value="${profileSuccess}"/></div>
            </c:if>

            <c:if test="${not empty profileError}">
                <div class="alert alert-error"><c:out value="${profileError}"/></div>
            </c:if>
            <c:if test="${not empty profileErrors.global}">
                <div class="alert alert-error"><c:out value="${profileErrors.global}"/></div>
            </c:if>

            <c:if test="${empty user}">
                <div class="empty-state">Profile not found.</div>
            </c:if>

            <c:if test="${not empty user}">
                <form action="${pageContext.request.contextPath}/profile" method="post">
                    <div class="detail-card">
                        <div class="detail-avatar-wrapper">
                            <c:choose>
                                <c:when test="${not empty user.avatarUrl}">
                                    <img src="${fn:escapeXml(user.avatarUrl)}"
                                         alt="Avatar of ${fn:escapeXml(user.fullName)}" class="avatar-circle">
                                </c:when>
                                <c:otherwise>
                                    <div class="avatar-placeholder-circle">
                                        <c:out value="${user.fullName.substring(0,1)}"/>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="detail-info">
                            <div class="form-group">
                                <label for="fullName">Full Name</label>
                                <input type="text" id="fullName" value="${fn:escapeXml(user.fullName)}" readonly class="input-readonly">
                            </div>

                            <div class="form-group">
                                <label for="email">Email</label>
                                <input type="email" id="email" value="${fn:escapeXml(user.email)}" readonly class="input-readonly">
                            </div>

                            <div class="form-group">
                                <label for="phone">Phone <span class="required-star">*</span></label>
                                <input type="text" id="phone" name="phone"
                                       value="${fn:escapeXml(user.phone)}" maxlength="15" required
                                       inputmode="numeric" pattern="\d{10,15}"
                                       title="Phone number must be 10-15 digits.">
                                <c:if test="${not empty profileErrors.phone}">
                                    <small class="field-error"><c:out value="${profileErrors.phone}"/></small>
                                </c:if>
                            </div>

                            <div class="form-group">
                                <label for="gender">Gender <span class="required-star">*</span></label>
                                <select id="gender" name="gender" required>
                                    <option value="" disabled ${empty user.gender ? 'selected' : ''}>Select gender</option>
                                    <option value="Male" ${user.gender == 'Male' ? 'selected' : ''}>Male</option>
                                    <option value="Female" ${user.gender == 'Female' ? 'selected' : ''}>Female</option>
                                    <option value="Other" ${user.gender == 'Other' ? 'selected' : ''}>Other</option>
                                </select>
                                <c:if test="${not empty profileErrors.gender}">
                                    <small class="field-error"><c:out value="${profileErrors.gender}"/></small>
                                </c:if>
                            </div>

                            <div class="form-group">
                                <label for="dateOfBirth">Date of Birth <span class="required-star">*</span></label>
                                <input type="date" id="dateOfBirth" name="dateOfBirth" value="<%= dob %>"
                                       max="<%= maxDob %>" required title="DD/MM/YYYY">
                                <c:if test="${not empty profileErrors.dateOfBirth}">
                                    <small class="field-error"><c:out value="${profileErrors.dateOfBirth}"/></small>
                                </c:if>
                            </div>

                            <div class="form-group">
                                <label for="address">Address</label>
                                <textarea id="address" name="address" maxlength="255" rows="3"><c:out value="${user.address}"/></textarea>
                                <c:if test="${not empty profileErrors.address}">
                                    <small class="field-error"><c:out value="${profileErrors.address}"/></small>
                                </c:if>
                            </div>

                            <div class="form-group">
                                <label for="avatarUrl">Avatar URL</label>
                                <input type="url" id="avatarUrl" name="avatarUrl"
                                       value="${fn:escapeXml(user.avatarUrl)}" maxlength="1000"
                                       title="Use a JPG, JPEG, PNG, or WEBP image URL.">
                                <c:if test="${not empty profileErrors.avatarUrl}">
                                    <small class="field-error"><c:out value="${profileErrors.avatarUrl}"/></small>
                                </c:if>
                            </div>

                            <div class="form-group">
                                <label for="department">Department</label>
                                <input type="text" id="department"
                                       value="${fn:escapeXml(empty user.departmentName ? 'No department' : user.departmentName)}"
                                       readonly class="input-readonly">
                            </div>

                            <div class="form-group">
                                <label for="position">Position</label>
                                <input type="text" id="position"
                                       value="${fn:escapeXml(empty user.positionName ? 'No position' : user.positionName)}"
                                       readonly class="input-readonly">
                            </div>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-save">Update Profile</button>
                        <a href="${pageContext.request.contextPath}/change_password" class="btn-cancel">Change Password</a>
                    </div>
                </form>
            </c:if>
        </div>
    </div>
</div>

</body>
</html>
