<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Create New Request</title>
  <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
</head>
<body>
<h2>Create New Request</h2>
<form action="create_request" method="POST">

  <p>Proposer:<br>
    <input type="text" value="${sessionScope.fullName}" readonly>
  </p>

  <p>Request Date:<br>
    <input type="text" value="<%= java.time.LocalDate.now() %>" readonly>
  </p>

  <p>Request Type: *<br>
    <select name="type" id="typeSelect" onchange="toggleObserver()" required>
      <option value="" disabled selected>-- Select --</option>
      <c:forEach var="entry" items="${requestType}">
        <option value="${entry.key}">${entry.value}</option>
      </c:forEach>
    </select>
  </p>

  <p>Approver: *<br>
    <select name="approverId" required>
      <option value="" disabled selected>-- Select --</option>
      <c:forEach items="${businessAdminList}" var="admin">
        <option value="${admin.id}">${admin.fullName}</option>
      </c:forEach>
    </select>
  </p>

  <!-- Move emp -->
  <div id="moveObserverSection" class="observer-box" style="display: none;">
    <p>Observer(s):<br>
      <select class="select2-observers" name="observerIds" multiple="multiple" style="width: 300px;">
        <c:forEach items="${allObservers}" var="user">
          <option value="${user.id}">${user.fullName} - ${user.positionName}</option>
        </c:forEach>
      </select>
    </p>
  </div>

  <!-- Position handover -->
  <div id="handoverObserverSection" class="observer-box" style="display: none;">
    <p>Observer(s):<br>
      <select id="handoverSelect" class="select2-observers" name="observerIds" multiple="multiple" style="width: 300px;">
          <c:forEach items="${hrManagers}" var="hr">
            <option value="${hr.id}">${hr.fullName} - ${hr.positionName}</option>
          </c:forEach>
          <c:forEach items="${deptEmployees}" var="emp">
            <option value="${emp.id}">${emp.fullName} - ${emp.positionName}</option>
          </c:forEach>
      </select>
    </p>
  </div>

  <p>Reason *<br>
    <textarea name="reason" rows="5" required></textarea>
  </p>

  <button type="submit">Send Request</button>
  <a href="${pageContext.request.contextPath}/view_my_request">Cancel</a>
</form>

<script>
  $(document).ready(function() {
    $('.select2-observers').select2({
      placeholder: "Select observers...",
      allowClear: true
    });
  });

  function toggleObserver() {
    var type = document.getElementById("typeSelect").value;

    document.querySelectorAll('.observer-box').forEach(div => div.style.display = 'none');

    // Logic hiển thị
    if (type === 'DEPT_MOVE') {
      document.getElementById('moveObserverSection').style.display = 'block';
    } else if (type === 'POSITION_HANDOVER') {
      document.getElementById('handoverObserverSection').style.display = 'block';
    }
  }
</script>

</body>
</html>