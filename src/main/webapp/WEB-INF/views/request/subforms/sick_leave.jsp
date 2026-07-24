<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>

<style>
    .sick-calendar {
        width: 100%;
        max-width: 400px;
        border: 1px solid #ddd;
        border-radius: 6px;
        overflow: hidden;
        user-select: none;
    }
    .sick-calendar .cal-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        background: #f5f5f5;
        padding: 8px 10px;
        font-weight: 600;
    }
    .sick-calendar .cal-header button {
        background: none;
        border: none;
        cursor: pointer;
        font-size: 16px;
        padding: 2px 8px;
    }
    .sick-calendar .cal-weekdays {
        display: grid;
        grid-template-columns: repeat(7, 1fr);
        text-align: center;
        background: #fafafa;
        padding: 4px 0;
        font-size: 12px;
        color: #666;
    }
    .sick-calendar .cal-days {
        display: grid;
        grid-template-columns: repeat(7, 1fr);
        gap: 2px;
        padding: 4px;
    }
    .sick-calendar .cal-day {
        aspect-ratio: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 4px;
        font-size: 13px;
        cursor: pointer;
        border: 1px solid transparent;
        box-sizing: border-box;
    }
    .sick-calendar .cal-day.other-month {
        color: #ccc;
        cursor: default;
        pointer-events: none;
    }
    .sick-calendar .cal-day.future {
        color: #ccc;
        cursor: not-allowed;
        pointer-events: none;
    }
    .sick-calendar .cal-day.weekend {
        color: #ccc;
        cursor: not-allowed;
        pointer-events: none;
        background: #fafafa;
    }
    .sick-calendar .cal-day:not(.other-month):not(.future):not(.weekend):hover {
        background: #e3f2fd;
    }
    .sick-calendar .cal-day.selected {
        background: #1976d2;
        color: white;
        border-color: #1565c0;
    }
    .sick-calendar .cal-day.today {
        font-weight: bold;
        border: 2px solid #1976d2;
    }
    .sick-calendar .cal-day.today.selected {
        border: 2px solid #1565c0;
    }
    .date-tag {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        background: #e3f2fd;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 13px;
    }
    .date-tag button {
        background: none;
        border: none;
        color: #c00;
        cursor: pointer;
        font-weight: bold;
        line-height: 1;
        padding: 0 2px;
    }
</style>

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
        <option value="" disabled selected>-- Select HR Staff --</option>
        <c:forEach items="${hrStaffList}" var="hr">
            <option value="${hr.id}">${hr.fullName} - ${hr.positionName}</option>
        </c:forEach>
    </select>
</div>

<div class="request-group">
    <label for="startDate">Start Date <span class="required-star">*</span></label>
    <input type="date" name="startDate" id="startDate" class="request-input"
           max="${today}" required onchange="updateSickDays()" />
</div>

<div class="request-group">
    <label for="endDate">End Date <span class="required-star">*</span></label>
    <input type="date" name="endDate" id="endDate" class="request-input"
           max="${today}" required onchange="updateSickDays()" />
</div>

<div class="request-group">
    <label>Attachment (Medical Certificate) <span class="required-star">*</span></label>
    <input type="file" name="sickFile" class="request-input" accept=".jpg,.png,.pdf" required />
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
        Remaining sick leave: <span id="remainingSickDays">${remainingSickDays}</span> / ${totalSickDays} days<br/>
        Requested workdays (excl. weekends): <span id="requestedSickDays">0</span>
    </div>
</div>

<div class="request-group" style="grid-column: 1 / -1;">
    <label for="reasonEditor">Reason <span class="required-star">*</span> <small style="color:#666; font-weight:normal;">(Max 1000 characters)</small></label>
    <textarea name="reason" id="reasonEditor" class="request-textarea" rows="5" maxlength="1000" required></textarea>
    <div style="font-size:12px; color:#666; text-align:right; margin-top:2px;">
        <span id="charCount">0</span>/1000 characters
    </div>
</div>

<script>
    var reasonEditorInstance = null;
    ClassicEditor
        .create(document.querySelector('#reasonEditor'), {
            toolbar: ['heading', '|', 'bold', 'italic', 'underline', 'link',
                      'bulletedList', 'numberedList', '|',
                      'alignment', 'fontColor', 'fontFamily', 'fontSize', '|',
                      'insertTable', 'blockQuote', 'undo', 'redo']
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
        .catch(function(error) { console.error(error); });

    function updateSickDays() {
        var startInput = document.getElementById('startDate');
        var endInput = document.getElementById('endDate');
        var daysSpan = document.getElementById('requestedSickDays');
        var remSpan = document.getElementById('remainingSickDays');

        var rawMax = parseInt('${remainingSickDays}', 10);
        var maxDays = (isNaN(rawMax) || rawMax <= 0) ? 0 : rawMax;

        if (!startInput.value || !endInput.value) {
            daysSpan.innerText = '0';
            if (remSpan) remSpan.innerText = maxDays;
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
        if (remSpan) remSpan.innerText = Math.max(0, maxDays - count);
    }

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

        var rawMax = parseInt('${remainingSickDays}', 10);
        var maxDays = (isNaN(rawMax) || rawMax <= 0) ? 0 : rawMax;
        if (count > maxDays) {
            if (window.showGlobalError) window.showGlobalError('Requested days (' + count + ') exceed remaining sick leave (' + maxDays + ').');
            e.preventDefault();
            return false;
        }
    });
</script>