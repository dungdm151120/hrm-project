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
    <label for="startDate">Start Date <span class="required-star">*</span></label>
    <input type="date" name="startDate" id="startDate" class="request-input"
           min="${today}" required onchange="updateLeaveDays()" />
</div>

<div class="request-group">
    <label for="endDate">End Date <span class="required-star">*</span></label>
    <input type="date" name="endDate" id="endDate" class="request-input"
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
        Requested workdays (excl. weekends): <span id="requestedDays">0</span>
    </div>
</div>

<div class="request-group" style="grid-column: 1 / -1;">
    <label for="reasonEditor">Reason <span class="required-star">*</span> <small style="color:#666; font-weight:normal;">(Max 1000 characters)</small></label>
    <textarea name="reason" id="reasonEditor" class="request-textarea" rows="5" maxlength="1000" required></textarea>
    <div style="font-size:12px; color:#666; text-align:right; margin-top:2px;">
        <span id="charCount">0</span>/1000 characters
    </div>
</div>

<div class="request-group" style="grid-column: 1 / -1;">
    <label>Notes</label>
    <div class="request-info-box">
        <ul>
            <li>If approved, your attendance for working days in the selected date range (excluding weekends) will be marked accordingly.</li>
            <li>Paid leave grants 8 hours; unpaid leave grants 0 hours and marks absent.</li>
            <li>You will be notified after processing.</li>
        </ul>
    </div>
</div>

<script>
    var reasonEditorInstance = null;
    ClassicEditor
        .create(document.querySelector('#reasonEditor'), {
            toolbar: ['heading', '|', 'bold', 'italic', 'underline', 'link', 'bulletedList', 'numberedList', '|', 'alignment', 'fontColor', 'fontFamily', 'fontSize', '|', 'insertTable', 'blockQuote', 'undo', 'redo']
        })
        .then(editor => {
            reasonEditorInstance = editor;
            editor.model.document.on('change:data', () => {
                var text = editor.getData().replace(/<[^>]*>/g, '').trim();
                var count = text.length;
                var charCountEl = document.getElementById('charCount');
                if (charCountEl) charCountEl.innerText = count;
                if (count > 1000) {
                    charCountEl.style.color = 'red';
                    charCountEl.style.fontWeight = 'bold';
                } else {
                    charCountEl.style.color = '#666';
                    charCountEl.style.fontWeight = 'normal';
                }
            });
        })
        .catch(error => { console.error(error); });

    function updateLeaveDays() {
        var startInput = document.getElementById('startDate');
        var endInput = document.getElementById('endDate');
        var daysSpan = document.getElementById('requestedDays');

        if (!startInput.value || !endInput.value) {
            daysSpan.innerText = '0';
            return;
        }

        var start = new Date(startInput.value);
        var end = new Date(endInput.value);

        if (start > end) {
            daysSpan.innerText = '0';
            return;
        }

        var count = 0;
        var cur = new Date(start);
        while (cur <= end) {
            var day = cur.getDay();
            if (day !== 0 && day !== 6) { // Exclude Sunday (0) and Saturday (6)
                count++;
            }
            cur.setDate(cur.getDate() + 1);
        }

        daysSpan.innerText = count;
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

    // Form submission validation displaying errors to create_request.jsp global alert
    $('form').on('submit', function(e) {
        if (window.clearGlobalError) window.clearGlobalError();

        var reasonText = reasonEditorInstance ? reasonEditorInstance.getData().replace(/<[^>]*>/g, '').trim() : $('#reasonEditor').val().trim();
        if (reasonText.length > 1000) {
            if (window.showGlobalError) window.showGlobalError('Reason must not exceed 1000 characters.');
            e.preventDefault();
            return false;
        }

        var startInput = document.getElementById('startDate');
        var endInput = document.getElementById('endDate');
        if (!startInput || !endInput || !startInput.value || !endInput.value) {
            if (window.showGlobalError) window.showGlobalError('Please select start date and end date.');
            e.preventDefault();
            return false;
        }

        var start = new Date(startInput.value);
        var end = new Date(endInput.value);
        if (start > end) {
            if (window.showGlobalError) window.showGlobalError('Start date cannot be after end date.');
            e.preventDefault();
            return false;
        }

        var count = 0;
        var cur = new Date(start);
        while (cur <= end) {
            var day = cur.getDay();
            if (day !== 0 && day !== 6) count++;
            cur.setDate(cur.getDate() + 1);
        }

        if (count === 0) {
            if (window.showGlobalError) window.showGlobalError('Selected date range contains no working days (weekends only).');
            e.preventDefault();
            return false;
        }

        var isPaid = $('input[name="leaveType"]:checked').val() === 'ON_LEAVE';
        var maxDays = isPaid ? parseInt('${remainingLeave}', 10) : parseInt('${remainingAbsent}', 10);
        maxDays = isNaN(maxDays) ? 0 : maxDays;
        if (count > maxDays) {
            var label = isPaid ? 'paid leave' : 'unpaid leave';
            if (window.showGlobalError) window.showGlobalError('Requested working days (' + count + ') exceed remaining ' + label + ' balance (' + maxDays + ').');
            e.preventDefault();
            return false;
        }
    });
</script>