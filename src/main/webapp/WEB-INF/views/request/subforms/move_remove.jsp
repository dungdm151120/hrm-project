<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Cột trái -->
<div class="request-group">
    <label>Approver <span class="required-star">*</span></label>
    <select name="approverId" class="request-select" required>
        <option value="" disabled selected>-- Select --</option>
        <c:forEach items="${businessAdminList}" var="admin">
            <option value="${admin.id}">${admin.fullName}</option>
        </c:forEach>
    </select>
</div>

<div class="request-group">
    <label>Handler <span class="required-star">*</span></label>
    <select name="handlerId" class="request-select" required>
        <option value="" disabled selected>-- Select --</option>
        <c:forEach items="${hrManagers}" var="hr">
            <option value="${hr.id}">${hr.fullName} - ${hr.positionName}</option>
        </c:forEach>
    </select>
</div>

<!-- Cột phải -->
<div class="request-group">
    <label>Observer(s)</label>
    <select class="select2-dynamic request-select" name="observerIds" multiple="multiple">
        <c:forEach items="${allObservers}" var="obs">
            <option value="${obs.id}">${obs.fullName} - ${obs.positionName}</option>
        </c:forEach>
    </select>
</div>

<div class="request-group">
    <label>Reason <span class="required-star">*</span></label>
    <textarea name="reason" class="request-textarea" rows="5" required placeholder="Enter reasons for move/remove action..."></textarea>
</div>