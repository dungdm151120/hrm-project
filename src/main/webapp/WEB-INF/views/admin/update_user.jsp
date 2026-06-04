<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update User - <%= ((User) request.getAttribute("userToUpdate")).getFullName() %> | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<%
    User userToUpdate = (User) request.getAttribute("userToUpdate");
%>

<div class="container" style="margin-top: 2rem;">
    <a class="back-link" href="${pageContext.request.contextPath}/user_list">Return to user list</a>
    <h2 class="form-title">Update info: <%= userToUpdate.getFullName() %></h2>

    <c:if test="${not empty error}">
        <div>${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/users/update" method="post">
        <input type="hidden" name="id" value="<%= userToUpdate.getId() %>">

        <div class="form-group">
            <label for="fullName">Full Name <span style="color: var(--danger);">*</span></label>
            <input type="text" id="fullName" name="fullName" value="<%= userToUpdate.getFullName() %>" required>
        </div>

        <div class="form-group">
            <label for="email">Email <span style="color: var(--danger);">*</span></label>
            <input type="email" id="email" name="email" value="<%= userToUpdate.getEmail() %>" required>
        </div>

        <div class="form-group">
            <label for="phone">Phone</label>
            <input type="text" id="phone" name="phone" value="<%= userToUpdate.getPhone() %>">
        </div>

        <div class="form-group">
            <label for="gender">Gender</label>
            <select id="gender" name="gender" class="form-select">
                <option value="Male" <%= "Male".equalsIgnoreCase(userToUpdate.getGender()) ? "selected" : "" %>>Male</option>
                <option value="Female" <%= "Female".equalsIgnoreCase(userToUpdate.getGender()) ? "selected" : "" %>>Female</option>
                <option value="Other" <%= "Other".equalsIgnoreCase(userToUpdate.getGender()) ? "selected" : "" %>>Other</option>
            </select>
        </div>

        <div class="form-group">
            <label for="dateOfBirth">Date of Birth</label>
            <%
                String dobString = "";
                if (userToUpdate.getDateOfBirth() != null) {
                    dobString = userToUpdate.getDateOfBirth().toLocalDate().toString();
                }
            %>
            <input type="date" id="dateOfBirth" name="dateOfBirth" value="<%= dobString %>">
        </div>

        <div class="form-group">
            <label for="address">Address</label>
            <input type="text" id="address" name="address" value="<%= userToUpdate.getAddress() %>">
        </div>

        <div class="form-group">
            <label for="avatarUrl">Avatar URL</label>
            <input type="text" id="avatarUrl" name="avatarUrl" value="<%= userToUpdate.getAvatarUrl() %>">
        </div>

       <div class="form-group">
           <label for="departmentSelect">Department <span style="color: var(--danger);">*</span></label>
           <select id="departmentSelect" name="departmentId" required class="form-select" onchange="updatePositions()">
               <option value="" disabled selected>-- Select a department --</option>
               <option value="1">Human Resources</option>
               <option value="2">Information Technology</option>
               <option value="3">Finance</option>
               <option value="4">Sales</option>
           </select>
       </div>

       <div class="form-group">
           <label for="positionSelectDisplay">Position <span style="color: var(--danger);">*</span></label>
           <select id="positionSelectDisplay" required class="form-select" disabled onchange="autoSetRoleAndPosition()">
               <option value="" disabled selected>-- Select a position --</option>
           </select>
       </div>

       <input type="hidden" id="positionId" name="positionId" value="${userToUpdate.positionId}">
       <input type="hidden" id="roleId" name="roleId" value="${userToUpdate.roleId}">

       <script>
       // Khối dữ liệu hardcode đồng bộ y hệt bên trang Add
       const departmentData = {
           "1": [
               { id: 2, name: "HR Manager", roleId: 2 },
               { id: 3, name: "HR Staff", roleId: 3 },
               { id: 9, name: "Employee", roleId: 6 }
           ],
           "2": [
               { id: 4, name: "Department Manager", roleId: 4 },
               { id: 6, name: "Software Developer", roleId: 6 },
               { id: 9, name: "Employee", roleId: 6 }
           ],
           "3": [
               { id: 4, name: "Department Manager", roleId: 4 },
               { id: 5, name: "Payroll Staff", roleId: 5 },
               { id: 7, name: "Accountant", roleId: 6 },
               { id: 9, name: "Employee", roleId: 6 }
           ],
           "4": [
               { id: 4, name: "Department Manager", roleId: 4 },
               { id: 8, name: "Sales Staff", roleId: 6 },
               { id: 9, name: "Employee", roleId: 6 }
           ]
       };

       // Hàm 1: Đổ dữ liệu chức vụ dựa trên phòng ban được chọn
       function updatePositions(targetPositionId = null) {
           const deptSelect = document.getElementById("departmentSelect");
           const posSelectDisplay = document.getElementById("positionSelectDisplay");
           const posHiddenInput = document.getElementById("positionId");
           const roleHiddenInput = document.getElementById("roleId");

           const selectedDept = deptSelect.value;

           // Reset giao diện hiển thị vị trí
           posSelectDisplay.innerHTML = '<option value="" disabled selected>-- Select a position --</option>';
           posSelectDisplay.disabled = true;

           if (selectedDept && departmentData[selectedDept]) {
               departmentData[selectedDept].forEach(pos => {
                   const option = document.createElement("option");
                   option.value = pos.id;
                   option.textContent = pos.name;
                   option.setAttribute("data-role-id", pos.roleId);

                   // Nếu trùng với ID chức vụ cần chọn sẵn (khi load trang)
                   if (targetPositionId && pos.id == targetPositionId) {
                       option.selected = true;
                   }
                   posSelectDisplay.appendChild(option);
               });
               posSelectDisplay.disabled = false;
           }
       }

       // Hàm 2: Kích hoạt khi người dùng chủ động click đổi Chức vụ
       function autoSetRoleAndPosition() {
           const posSelectDisplay = document.getElementById("positionSelectDisplay");
           const posHiddenInput = document.getElementById("positionId");
           const roleHiddenInput = document.getElementById("roleId");

           const selectedOption = posSelectDisplay.options[posSelectDisplay.selectedIndex];
           if (selectedOption) {
               const targetRoleId = selectedOption.getAttribute("data-role-id");
               posHiddenInput.value = posSelectDisplay.value;
               roleHiddenInput.value = targetRoleId;
           }
       }

       // ĐOẠN KHÁC BIỆT: Tự động chạy ngay khi vừa mở trang Update lên
       window.onload = function() {
           // Lấy giá trị ID phòng ban và chức vụ hiện tại của User từ EL biểu thức JSP
           const currentDeptId = "${userToUpdate.departmentId}";
           const currentPosId = "${userToUpdate.positionId}";

           if (currentDeptId && currentDeptId !== "0") {
               // 1. Chọn sẵn phòng ban trên UI
               document.getElementById("departmentSelect").value = currentDeptId;
               // 2. Kích hoạt đổ chức vụ của phòng ban đó và chọn sẵn chức vụ hiện tại
               updatePositions(currentPosId);
           }
       };
       </script>

        <div class="form-group">
            <label>Active Status</label>
            <div class="radio-group">
                <label class="radio-label">
                    <input type="radio" name="active" value="true" <%= userToUpdate.isActive() ? "checked" : "" %>>
                    <span>Active</span>
                </label>
                <label class="radio-label">
                    <input type="radio" name="active" value="false" <%= !userToUpdate.isActive() ? "checked" : "" %>>
                    <span>Inactive</span>
                </label>
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-save">Update</button>
            <a href="${pageContext.request.contextPath}/user_list" class="btn-cancel">Cancel</a>
        </div>
    </form>
</div>

</body>
</html>