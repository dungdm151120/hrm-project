<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Create New Request | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <!-- Thêm request.css -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/request.css">
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <h1 class="header-title">Create New Request</h1>
        </div>

        <div class="dashboard-content">
            <!-- Error / Success alerts -->
            <c:if test="${not empty param.error}">
                <div class="alert alert-error" style="margin-bottom:1.5rem;">
                    <span class="alert-icon">⚠️</span>
                    <span>
                        <c:choose>
                            <c:when test="${param.error == 'missing_leave_date'}">Please select a leave date.</c:when>
                            <c:when test="${param.error == 'leave_date_past'}">Leave date cannot be in the past.</c:when>
                            <c:when test="${param.error == 'leave_date_weekend'}">Weekends are not allowed for leave requests.</c:when>
                            <c:when test="${param.error == 'leave_date_already_on_leave'}">You already have an approved leave on this date.</c:when>
                            <c:when test="${param.error == 'leave_date_duplicate_request'}">A leave request for this date already exists (pending or approved).</c:when>
                            <c:when test="${param.error == 'leave_balance_exhausted'}">You have no remaining leave balance. Cannot submit request.</c:when>
                            <c:when test="${param.error == 'missing_approver'}">Please select an approver.</c:when>
                            <c:when test="${param.error == 'system_error'}">A system error occurred. Please try again later.</c:when>
                            <c:otherwise>${param.error}</c:otherwise>
                        </c:choose>
                    </span>
                </div>
            </c:if>

            <div class="request-card">
                <form action="create_request" method="POST">
                    <!-- Dropdown chọn loại request -->
                    <div class="request-group" style="max-width: 400px;">
                        <label for="typeSelect">Request Type <span class="required-star">*</span></label>
                        <select name="type" id="typeSelect" class="request-select" required>
                            <option value="" disabled selected>-- Select Type --</option>
                            <c:forEach var="entry" items="${requestTypes}">
                                <option value="${entry.key}">${entry.value}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- Container động -->
                    <div id="dynamicFormContainer" class="request-grid"></div>

                    <!-- Nút hành động -->
                    <div class="request-actions">
                        <button type="submit" class="btn-submit-request">Send Request</button>
                        <a href="${pageContext.request.contextPath}/view_my_request" class="btn-cancel-request">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
    $('#typeSelect').change(function() {
        var selectedType = $(this).val();
        if(selectedType) {
            $.ajax({
                url: '${pageContext.request.contextPath}/load_sub_form',
                type: 'GET',
                data: { type: selectedType },
                success: function(htmlResult) {
                    $('#dynamicFormContainer').html(htmlResult);
                    // Re-init Select2
                    $('.select2-dynamic').select2({
                        placeholder: "Select options...",
                        allowClear: true,
                        width: '100%'
                    });
                },
                error: function() {
                    $('#dynamicFormContainer').html('<p style="color:red;">Error loading specific form options.</p>');
                }
            });
        } else {
            $('#dynamicFormContainer').empty();
        }
    });
});
</script>

</body>
</html>