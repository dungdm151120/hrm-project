<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="form-grid">
    <div class="request-group">
        <label>Proposer</label>
        <input type="text" class="request-input" value="${proposer.fullName} - ${proposer.employeeCode}" disabled>
    </div>

    <div class="request-group">
        <label>Request Date</label>
        <input type="text" class="request-input" value="${today}" disabled>
    </div>

    <div class="request-group">
        <label>Department</label>
        <input type="text" class="request-input" value="${departmentName}" disabled>
    </div>
    
    <div class="request-group">
        <label>Approver <span class="required-star">*</span></label>
        <select name="approverId" class="request-select" required>
            <option value="" disabled>-- Select Approver (HR Manager) --</option>
            <c:forEach var="mgr" items="${approverList}">
                <option value="${mgr.id}" ${mgr.id == defaultApproverId ? 'selected' : ''}>
                    <c:out value="${mgr.fullName}"/> - <c:out value="${mgr.positionName}"/>
                </option>
            </c:forEach>
        </select>
    </div>

    <div class="request-group">
        <label>OT Date <span class="required-star">*</span></label>
        <input type="date" name="overtimeDate" class="request-input" id="overtimeDate" min="${today}" required>
    </div>
    
    <div class="request-group">
        <label>Shift Time</label>
        <input type="text" class="request-input" value="17:00 - 19:00" disabled>
    </div>
</div>

<div class="request-group" style="margin-top: 15px;">
    <label>Employees (Select who will work OT) <span class="required-star">*</span></label>
    <div id="overtimeEmployeeList" class="checkbox-list-container" style="max-height: 200px; overflow-y: auto; border: 1px solid #ccc; padding: 10px; border-radius: 4px;">
        <c:forEach var="emp" items="${deptEmployees}">
            <label style="display: block; margin-bottom: 5px;">
                <input type="checkbox" name="employeeIds" value="${emp.id}">
                <c:out value="${emp.fullName}"/> - <c:out value="${emp.positionName}"/>
            </label>
        </c:forEach>
        <c:if test="${empty deptEmployees}">
            <p style="color: #888;">No employees found in your department.</p>
        </c:if>
    </div>
</div>

<div class="request-group" style="margin-top: 15px;">
    <label>Observers (Managers who can view this request)</label>
    <select name="observerIds" class="request-select select2-dynamic" multiple="multiple">
        <c:forEach var="obs" items="${observerList}">
            <option value="${obs.id}">${obs.fullName} - ${obs.positionName}</option>
        </c:forEach>
    </select>
</div>

<div class="request-group full-width" style="margin-top: 15px;">
    <label>Reason <span class="required-star">*</span></label>
    <textarea name="reason" class="request-textarea" rows="4"
              placeholder="Enter reason" maxlength="500" required></textarea>
</div>

<script>
    document.getElementById('overtimeDate').addEventListener('change', async function() {
        var employeeList = document.getElementById('overtimeEmployeeList');
        if (!this.value) {
            return;
        }

        var date = new Date(this.value + 'T00:00:00Z');
        var day = date.getUTCDay();
        if (day === 0 || day === 6) {
            alert('OT Date must be from Monday to Friday.');
            this.value = '';
            employeeList.replaceChildren();
            return;
        }

        try {
            var response = await fetch('${pageContext.request.contextPath}/overtime/available-employees?date=' + this.value);
            var employeeHtml = await response.text();
            if (!response.ok) {
                throw new Error(employeeHtml || 'Unable to load available employees.');
            }
            employeeList.innerHTML = employeeHtml;
        } catch (error) {
            alert(error.message);
            this.value = '';
            employeeList.replaceChildren();
        }
    });
</script>
