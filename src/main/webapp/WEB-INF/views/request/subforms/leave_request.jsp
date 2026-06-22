<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>

<div class="request-group">
    <label>Proposal Date</label>
    <input type="text" class="request-input" value="${now}" readonly />
</div>

<div class="request-group">
    <label>Approver <span class="required-star">*</span></label>
    <select name="approverId" class="request-select" required>
        <c:choose>
            <c:when test="${not empty approver}">
                <option value="${approver.id}" selected>${approver.fullName} - ${approver.positionName}</option>
            </c:when>
            <c:otherwise>
                <option value="" disabled selected>No department manager available</option>
            </c:otherwise>
        </c:choose>
    </select>
</div>

<div class="request-group">
    <label>Leave Type <span class="required-star">*</span></label>
    <div>
        <label><input type="radio" name="leaveType" value="ON_LEAVE" checked onchange="updateBalanceDisplay()"> On Leave (Paid)</label>
        <label><input type="radio" name="leaveType" value="LEAVE" onchange="updateBalanceDisplay()"> Leave (Absent)</label>
    </div>
</div>

<div class="request-group">
    <label for="leaveDate">Leave Date <span class="required-star">*</span></label>
    <input type="date" name="leaveDate" id="leaveDate" class="request-input"
           min="${today}" required onchange="updateLeaveDays()" />
</div>

<div class="request-group">
    <label>Observer(s)</label>
    <select class="select2-dynamic request-select" name="observerIds" multiple="multiple" style="width:100%">
        <c:forEach items="${observerList}" var="obs">
            <option value="${obs.id}">${obs.fullName} - ${obs.positionName}</option>
        </c:forEach>
    </select>
</div>

<div class="request-group">
    <div class="balance-text-italic-red">
        <span id="balanceLabel">Remaining paid leave:</span>
        <span id="remainingBalance">${remainingLeave}</span> days<br/>
        Requested days: <span id="requestedDays">0</span>
    </div>
</div>

<div class="request-group" style="grid-column: 1;">
    <label for="reasonEditor">Reason <span class="required-star">*</span></label>
    <textarea name="reason" id="reasonEditor" class="request-textarea" rows="5" required></textarea>
</div>

<div class="request-group" style="grid-column: 1 / -1;">
    <label>Notes</label>
    <div class="request-info-box">
        <ul>
            <li>If approved, your attendance for the selected date will be marked accordingly.</li>
            <li>Paid leave grants 8 hours; unpaid leave grants 0 hours and marks absent.</li>
            <li>You will be notified after processing.</li>
        </ul>
    </div>
</div>

<script>
    ClassicEditor
        .create(document.querySelector('#reasonEditor'), {
            toolbar: ['heading', '|', 'bold', 'italic', 'underline', 'link', 'bulletedList', 'numberedList', '|', 'alignment', 'fontColor', 'fontFamily', 'fontSize', '|', 'insertTable', 'blockQuote', 'undo', 'redo']
        })
        .catch(error => { console.error(error); });

    function updateLeaveDays() {
        var dateInput = document.getElementById('leaveDate');
        var daysSpan = document.getElementById('requestedDays');
        if (dateInput.value) {
            daysSpan.innerText = '1';
        } else {
            daysSpan.innerText = '0';
        }
    }

    function updateBalanceDisplay() {
        var radios = document.getElementsByName('leaveType');
        var balanceLabel = document.getElementById('balanceLabel');
        var balanceSpan = document.getElementById('remainingBalance');
        if (radios[0].checked) {
            balanceLabel.innerText = 'Remaining paid leave:';
            balanceSpan.innerText = '${remainingLeave}';
        } else {
            balanceLabel.innerText = 'Remaining absent leave:';
            balanceSpan.innerText = '${remainingAbsent}';
        }
    }

    updateBalanceDisplay();
</script>