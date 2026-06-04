<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New User | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <a class="back-link" href="${pageContext.request.contextPath}/user_list">Return to user list</a>
    <h2 class="form-title">Add new user</h2>

    <c:if test="${not empty error}">
        <div>${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/users/add" method="post">
        <div class="form-group">
            <label for="fullName">Full Name <span style="color: var(--danger);">*</span></label>
            <input type="text" id="fullName" name="fullName" placeholder="Enter full name" required>
        </div>

        <div class="form-group">
            <label for="email">Email <span style="color: var(--danger);">*</span></label>
            <input type="email" id="email" name="email" placeholder="Enter email address" required>
        </div>

        <div class="form-group">
            <label for="password">Password <span style="color: var(--danger);">*</span></label>
            <input type="password" id="password" name="password" placeholder="Enter password" required>
        </div>

        <div class="form-group">
            <label for="phone">Phone</label>
            <input type="text" id="phone" name="phone" placeholder="Enter phone number">
        </div>

        <div class="form-group">
            <label for="gender">Gender</label>
            <select id="gender" name="gender" class="form-select">
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
            </select>
        </div>

        <div class="form-group">
            <label for="dateOfBirth">Date of Birth</label>
            <input type="date" id="dateOfBirth" name="dateOfBirth">
        </div>

        <div class="form-group">
            <label for="address">Address</label>
            <input type="text" id="address" name="address" placeholder="Enter address">
        </div>

        <div class="form-group">
            <label for="avatarUrl">Avatar URL</label>
            <input type="text" id="avatarUrl" name="avatarUrl" placeholder="Enter photo url">
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

        <input type="hidden" id="positionId" name="positionId" value="">
        <input type="hidden" id="roleId" name="roleId" value="">

        <script>
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

        function updatePositions() {
            const deptSelect = document.getElementById("departmentSelect");
            const posSelectDisplay = document.getElementById("positionSelectDisplay");
            const posHiddenInput = document.getElementById("positionId");
            const roleHiddenInput = document.getElementById("roleId");

            const selectedDept = deptSelect.value;

            // Reset sạch các ô nhập ngầm
            posSelectDisplay.innerHTML = '<option value="" disabled selected>-- Select a position --</option>';
            posSelectDisplay.disabled = true;
            posHiddenInput.value = "";
            roleHiddenInput.value = "";

            if (selectedDept && departmentData[selectedDept]) {
                departmentData[selectedDept].forEach(pos => {
                    const option = document.createElement("option");
                    option.value = pos.id;
                    option.textContent = pos.name;
                    option.setAttribute("data-role-id", pos.roleId);
                    posSelectDisplay.appendChild(option);
                });
                posSelectDisplay.disabled = false;
            }
        }

        // Hàm này vừa kích hoạt sẽ ghi đồng thời cả positionId và roleId thực tế vào thẻ hidden
        function autoSetRoleAndPosition() {
            const posSelectDisplay = document.getElementById("positionSelectDisplay");
            const posHiddenInput = document.getElementById("positionId");
            const roleHiddenInput = document.getElementById("roleId");

            const selectedOption = posSelectDisplay.options[posSelectDisplay.selectedIndex];
            if (selectedOption) {
                const targetRoleId = selectedOption.getAttribute("data-role-id");

                // Gán giá trị thẳng vào 2 thẻ hidden
                posHiddenInput.value = posSelectDisplay.value;
                roleHiddenInput.value = targetRoleId;

                console.log("Đã gán ngầm lên Form -> Position ID:", posHiddenInput.value, " | Role ID:", roleHiddenInput.value);
            }
        }
        </script>

        <div class="form-group">
            <label>Active Status</label>
            <div class="radio-group">
                <label class="radio-label">
                    <input type="radio" name="active" value="true" checked>
                    <span>Active</span>
                </label>
                <label class="radio-label">
                    <input type="radio" name="active" value="false">
                    <span>Inactive</span>
                </label>
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-save">Add user</button>
            <a href="${pageContext.request.contextPath}/user_list" class="btn-cancel">Cancel</a>
        </div>
    </form>
</div>

</body>
</html>