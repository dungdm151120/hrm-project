<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Create New Request | HRM</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
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

      <!-- Hiển thị thông báo lỗi nếu có -->
      <c:if test="${not empty param.error}">
        <div style="color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 12px 16px; border-radius: 6px; margin-bottom: 20px; display: flex; align-items: center; gap: 8px;">
          <span style="font-size: 18px;">⚠️</span>
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

      <div class="detail-card">
        <form action="create_request" method="POST">

          <div class="form-group">
            <label>Proposer:</label>
            <input type="text" class="form-control" value="${sessionScope.currentUser.fullName}" readonly>
          </div>

          <div class="form-group">
            <label>Request Date:</label>
            <input type="text" class="form-control" value="<%= java.time.LocalDate.now() %>" readonly>
          </div>

          <div class="form-group">
            <label>Request Type: *</label>
            <select name="type" id="typeSelect" class="form-control" required>
              <option value="" disabled selected>-- Select Type --</option>
              <c:forEach var="entry" items="${requestTypes}">
                <option value="${entry.key}">${entry.value}</option>
              </c:forEach>
            </select>
          </div>

          <div id="dynamicFormContainer"></div>

          <div class="form-actions" style="margin-top: 20px;">
            <button type="submit" class="btn-primary">Send Request</button>
            <a href="${pageContext.request.contextPath}/view_my_request" class="btn-secondary">Cancel</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>

<script>
  $(document).ready(function() {
    // Lắng nghe sự kiện thay đổi của Dropdown Request Type
    $('#typeSelect').change(function() {
      var selectedType = $(this).val();
      if(selectedType) {
        // Gọi AJAX lấy cấu trúc JSP con tương ứng
        $.ajax({
          url: '${pageContext.request.contextPath}/load_sub_form',
          type: 'GET',
          data: { type: selectedType },
          success: function(htmlResult) {
            // Đổ HTML nhận được vào vùng chứa
            $('#dynamicFormContainer').html(htmlResult);

            // Re-init lại Select2 cho các element mới được load động vào DOM
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