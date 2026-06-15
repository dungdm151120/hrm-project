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
            <input type="text" class="form-control" value="${sessionScope.fullName}" readonly>
          </div>

          <div class="form-group">
            <label>Request Date:</label>
            <input type="text" class="form-control" value="<%= java.time.LocalDate.now() %>" readonly>
          </div>

          <div class="form-group">
            <label>Request Type: *</label>
            <select name="type" id="typeSelect" class="form-control" onchange="toggleObserver()" required>
              <option value="" disabled selected>-- Select --</option>
              <c:forEach var="entry" items="${requestType}">
                <option value="${entry.key}">${entry.value}</option>
              </c:forEach>
            </select>
          </div>

          <div class="form-group">
            <label>Approver: *</label>
            <select name="approverId" class="form-control" required>
              <option value="" disabled selected>-- Select --</option>
              <c:forEach items="${businessAdminList}" var="admin">
                <option value="${admin.id}">${admin.fullName}</option>
              </c:forEach>
            </select>
          </div>

          <div id="moveObserverSection" class="observer-box form-group" style="display: none;">
            <label>Observer(s):</label>
            <select class="select2-observers form-control" name="observerIds" multiple="multiple">
              <c:forEach items="${allObservers}" var="user">
                <option value="${user.id}">${user.fullName} - ${user.positionName}</option>
              </c:forEach>
            </select>
          </div>

          <div id="handoverObserverSection" class="observer-box form-group" style="display: none;">
            <label>Observer(s):</label>
            <select class="select2-observers form-control" name="observerIds" multiple="multiple">
              <c:forEach items="${hrManagers}" var="hr">
                <option value="${hr.id}">${hr.fullName} - ${hr.positionName}</option>
              </c:forEach>
              <c:forEach items="${deptEmployees}" var="emp">
                <option value="${emp.id}">${emp.fullName} - ${emp.positionName}</option>
              </c:forEach>
            </select>
          </div>

          <div class="form-group">
            <label>Reason: *</label>
            <textarea name="reason" class="form-control" rows="5" required></textarea>
          </div>

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
    $('.select2-observers').select2({
      placeholder: "Select observers...",
      allowClear: true,
      width: '100%'
    });

    $('.select2-observers').on('change', function() {
      $(this).trigger('select2:resize');
    });
  });

  function toggleObserver() {
    var type = document.getElementById("typeSelect").value;
    document.querySelectorAll('.observer-box').forEach(div => div.style.display = 'none');
    if (type === 'DEPT_MOVE') {
      document.getElementById('moveObserverSection').style.display = 'block';
    } else if (type === 'POSITION_HANDOVER') {
      document.getElementById('handoverObserverSection').style.display = 'block';
    }

    $('.select2-observers').trigger('change');
  }
</script>

</body>
</html>