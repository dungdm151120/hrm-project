<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="form-group">
    <label>Approver: *</label>
    <select name="approverId" class="form-control" required>
        <option value="" disabled selected>-- Select --</option>
        <c:forEach items="${businessAdminList}" var="admin">
            <option value="${admin.id}">${admin.fullName}</option>
        </c:forEach>
    </select>
</div>

<div class="form-group">
    <label>Handler: *</label>
    <select name="handlerId" class="form-control" required>
        <option value="" disabled selected>-- Select --</option>
        <c:forEach items="${hrManagers}" var="hr">
            <option value="${hr.id}">${hr.fullName} - ${hr.positionName}</option>
        </c:forEach>
    </select>
</div>

<div class="form-group">
    <label>Observer(s):</label>
    <select class="select2-dynamic form-control" name="observerIds" multiple="multiple">
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
    <textarea name="reason" class="form-control" rows="5" required placeholder="Enter detailed handover info..."></textarea>
</div>