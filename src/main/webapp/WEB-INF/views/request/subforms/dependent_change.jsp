<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>

<div class="request-group">
    <label>Proposer</label>
    <input type="text" class="request-input" value="${sessionScope.currentUser.fullName}" readonly />
</div>

<div class="request-group">
    <label>Proposal Date</label>
    <input type="text" class="request-input" value="${now}" readonly />
</div>

<div class="request-group">
    <label>Approver <span class="required-star">*</span></label>
    <select name="approverId" class="request-select" required>
        <option value="" disabled selected>-- Select Payroll Staff --</option>
        <c:forEach items="${payrollStaffList}" var="pr">
            <option value="${pr.id}">${pr.fullName} - ${pr.positionName}</option>
        </c:forEach>
    </select>
</div>

<div class="request-group">
    <label for="numberOfDependents">New Number of Dependents <span class="required-star">*</span></label>
    <input type="number" name="numberOfDependents" id="numberOfDependents" class="request-input" min="0" step="1" required placeholder="Enter number of dependents" onkeypress="return event.charCode >= 48 && event.charCode <= 57" />
</div>

<div class="request-group">
    <label for="dependentFile">Proof Image <span class="required-star">*</span></label>
    <input type="file" name="dependentFile" id="dependentFile" class="request-input" accept="image/*" required />
</div>

<div class="request-group">
    <label>Observer(s)</label>
    <select class="select2-dynamic request-select" name="observerIds" multiple="multiple" style="width:100%">
        <c:forEach items="${observerList}" var="obs">
            <option value="${obs.id}">${obs.fullName} - ${obs.positionName}</option>
        </c:forEach>
    </select>
</div>

<div class="request-group" style="grid-column: 1 / -1;">
    <label for="reasonEditor">Reason <span class="required-star">*</span></label>
    <textarea name="reason" id="reasonEditor" class="request-textarea" rows="5" required></textarea>
</div>

<script>
    ClassicEditor
        .create(document.querySelector('#reasonEditor'), {
            toolbar: ['heading', '|', 'bold', 'italic', 'underline', 'link', 'bulletedList', 'numberedList', '|', 'alignment', 'fontColor', 'fontFamily', 'fontSize', '|', 'insertTable', 'blockQuote', 'undo', 'redo']
        })
        .catch(error => { console.error(error); });
</script>
