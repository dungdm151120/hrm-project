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