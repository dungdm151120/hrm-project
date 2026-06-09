<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Add member | HRM</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
  <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

  <div class="dashboard-main">
    <div class="dashboard-header">
      <div class="header-left">
        <h1 class="header-title">Add Member</h1>
      </div>
    </div>

    <div class="dashboard-content">
      <div class="container">
  <form action="${pageContext.request.contextPath}/add_member" method="POST">
    <input type="hidden" name="deptId" value="${deptId}">

    <div class="toolbar">
      <button type="button" class="btn-secondary" onclick="selectAll()">Select</button>
      <button type="button" class="btn-secondary" onclick="clearAll()">Clear All</button>
      <span class="selected-count-wrapper">
                Selected: <span id="selectedCount" class="selected-count">0</span> Employee
            </span>
    </div>

    <div class="table-wrapper">
      <table>
        <thead>
        <tr>
          <th style="width: 60px; text-align: center;">Select</th>
          <th>Full Name</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${unassignedUsers}" var="u">
          <tr>
            <td style="text-align: center;">
              <input type="checkbox" name="userIds" value="${u.id}" class="perm-checkbox">
            </td>
            <td><strong>${u.fullName}</strong></td>
          </tr>
        </c:forEach>
        <c:if test="${empty unassignedUsers}">
          <tr>
            <td colspan="2" class="empty-state">No employees found.</td>
          </tr>
        </c:if>
        </tbody>
      </table>
    </div>

    <div class="form-actions">
      <button type="submit" class="btn-save">Confirm</button>
      <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${deptId}" class="btn-cancel">Cancel</a>
    </div>
  </form>
      </div>
    </div>
  </div>
</div>

<script>
  function updateCount() {
    var checked = document.querySelectorAll('.perm-checkbox:checked').length;
    document.getElementById('selectedCount').textContent = checked;
  }
  function selectAll() {
    document.querySelectorAll('.perm-checkbox').forEach(function(cb) { cb.checked = true; });
    updateCount();
  }
  function clearAll() {
    document.querySelectorAll('.perm-checkbox').forEach(function(cb) { cb.checked = false; });
    updateCount();
  }
  document.querySelectorAll('.perm-checkbox').forEach(function(cb) {
    cb.addEventListener('change', updateCount);
  });
</script>

</body>
</html>
