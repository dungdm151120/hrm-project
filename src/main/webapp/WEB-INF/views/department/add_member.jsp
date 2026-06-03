<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Thêm thành viên | HRM</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<div class="container">
  <h2 class="form-title">Thêm thành viên vào phòng ban</h2>

  <form action="${pageContext.request.contextPath}/add_member" method="POST">
    <input type="hidden" name="deptId" value="${deptId}">

    <div class="toolbar">
      <button type="button" class="btn-secondary" onclick="selectAll()">Chọn tất cả</button>
      <button type="button" class="btn-secondary" onclick="clearAll()">Bỏ chọn tất cả</button>
      <span class="selected-count-wrapper">
                Đã chọn: <span id="selectedCount" class="selected-count">0</span> nhân viên
            </span>
    </div>

    <div class="table-wrapper">
      <table>
        <thead>
        <tr>
          <th style="width: 60px; text-align: center;">Chọn</th>
          <th>Tên nhân viên</th>
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
            <td colspan="3" class="empty-state">Không có nhân viên nào khả dụng.</td>
          </tr>
        </c:if>
        </tbody>
      </table>
    </div>

    <div class="form-actions">
      <button type="submit" class="btn-save">Xác nhận thêm</button>
      <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${deptId}" class="btn-cancel">Hủy</a>
    </div>
  </form>
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
