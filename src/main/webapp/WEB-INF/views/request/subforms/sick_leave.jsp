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

<div class="request-group" style="grid-column: 1 / -1; max-width: 50%;">
    <label>Sick Dates <span class="required-star">*</span></label>
    <div id="sickDatesContainer">
        <div class="sick-calendar" id="sickCalendar"></div>
        <div id="selectedDatesTags" style="margin-top:10px; display:flex; flex-wrap:wrap; gap:5px;"></div>
        <small style="color:#666;">
            Only past or today dates are allowed. Weekends cannot be selected.
            Maximum <span id="maxDaysHint">${remainingSickDays}</span> days remaining.
        </small>
    </div>
</div>

<script>
(function () {
    function tryInit() {
        var calEl     = document.getElementById('sickCalendar');
        var tagsEl    = document.getElementById('selectedDatesTags');
        var container = document.getElementById('sickDatesContainer');
        var remEl     = document.getElementById('remainingSickDays');

        if (!calEl || !tagsEl || !container) {
            setTimeout(tryInit, 50);
            return;
        }

        var rawMax  = parseInt('${remainingSickDays}', 10);
        var maxDays = (isNaN(rawMax) || rawMax <= 0) ? 999 : rawMax;

        var selectedDates = [];
        var today = new Date();
        today.setHours(0, 0, 0, 0);
        var viewYear  = today.getFullYear();
        var viewMonth = today.getMonth();

        function pad2(n) { return n < 10 ? '0' + n : '' + n; }

        function toStr(date) {
            return date.getFullYear() + '-' + pad2(date.getMonth() + 1) + '-' + pad2(date.getDate());
        }

        function fromStr(s) {
            var p = s.split('-');
            return new Date(+p[0], +p[1] - 1, +p[2]);
        }

        function isWeekend(date) {
            var day = date.getDay();
            return day === 0 || day === 6;
        }

        function renderCalendar() {
            var firstDay    = new Date(viewYear, viewMonth, 1);
            var lastDay     = new Date(viewYear, viewMonth + 1, 0);
            var startDOW    = (firstDay.getDay() + 6) % 7;
            var monthLabel  = firstDay.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
            var totalDays   = lastDay.getDate();

            var h = '';

            h += '<div class="cal-header">';
            h += '<button type="button" id="calPrev" aria-label="Previous month">&lt;</button>';
            h += '<span>' + monthLabel + '</span>';
            h += '<button type="button" id="calNext" aria-label="Next month">&gt;</button>';
            h += '</div>';

            h += '<div class="cal-weekdays">';
            ['Mon','Tue','Wed','Thu','Fri','Sat','Sun'].forEach(function(d){ h += '<div>' + d + '</div>'; });
            h += '</div>';

            h += '<div class="cal-days">';

            var prevLast = new Date(viewYear, viewMonth, 0).getDate();
            for (var i = startDOW - 1; i >= 0; i--) {
                h += '<div class="cal-day other-month">' + (prevLast - i) + '</div>';
            }

            for (var d = 1; d <= totalDays; d++) {
                var date    = new Date(viewYear, viewMonth, d);
                var dateStr = toStr(date);
                var cls     = 'cal-day';

                if (date.getTime() > today.getTime()) {
                    cls += ' future';
                } else if (isWeekend(date)) {
                    cls += ' weekend';
                } else if (selectedDates.indexOf(dateStr) !== -1) {
                    cls += ' selected';
                }
                if (date.getTime() === today.getTime()) cls += ' today';

                h += '<div class="' + cls + '" data-d="' + dateStr + '">' + d + '</div>';
            }

            var totalCells = startDOW + totalDays;
            var trailing   = totalCells % 7 === 0 ? 0 : 7 - (totalCells % 7);
            for (var j = 1; j <= trailing; j++) {
                h += '<div class="cal-day other-month">' + j + '</div>';
            }

            h += '</div>';
            calEl.innerHTML = h;

            calEl.querySelectorAll('.cal-day[data-d]:not(.future):not(.weekend)').forEach(function(el) {
                el.addEventListener('click', function () { toggleDate(this.getAttribute('data-d')); });
            });
            document.getElementById('calPrev').addEventListener('click', function () { changeMonth(-1); });
            document.getElementById('calNext').addEventListener('click', function () { changeMonth(1); });
        }

        function toggleDate(dateStr) {
            var date = fromStr(dateStr);
            if (date.getTime() > today.getTime()) return;
            if (isWeekend(date)) return;

            var idx = selectedDates.indexOf(dateStr);
            if (idx !== -1) {
                selectedDates.splice(idx, 1);
            } else {
                if (selectedDates.length >= maxDays) {
                    alert('You can only select up to ' + maxDays + ' day(s).');
                    return;
                }
                selectedDates.push(dateStr);
                selectedDates.sort();
            }

            renderCalendar();
            renderTags();
            syncHiddenInputs();
            updateRemaining();
        }

        function changeMonth(delta) {
            viewMonth += delta;
            if (viewMonth > 11) { viewMonth = 0;  viewYear++; }
            if (viewMonth < 0)  { viewMonth = 11; viewYear--; }
            if (viewYear > today.getFullYear() ||
                (viewYear === today.getFullYear() && viewMonth > today.getMonth())) {
                viewYear  = today.getFullYear();
                viewMonth = today.getMonth();
            }
            renderCalendar();
        }

        function renderTags() {
            tagsEl.innerHTML = selectedDates.map(function (d) {
                return '<span class="date-tag">' + d +
                       ' <button type="button" data-rm="' + d + '">&times;</button></span>';
            }).join('');

            tagsEl.querySelectorAll('[data-rm]').forEach(function (btn) {
                btn.addEventListener('click', function () { removeDate(this.getAttribute('data-rm')); });
            });
        }

        function removeDate(dateStr) {
            var idx = selectedDates.indexOf(dateStr);
            if (idx !== -1) selectedDates.splice(idx, 1);
            renderCalendar();
            renderTags();
            syncHiddenInputs();
            updateRemaining();
        }

        function syncHiddenInputs() {
            container.querySelectorAll('input[name="sickDates"]').forEach(function (el) { el.remove(); });
            selectedDates.forEach(function (s) {
                var inp   = document.createElement('input');
                inp.type  = 'hidden';
                inp.name  = 'sickDates';
                inp.value = s;
                container.appendChild(inp);
            });
        }

        function updateRemaining() {
            if (remEl) remEl.textContent = Math.max(0, maxDays - selectedDates.length);
        }

        renderCalendar();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', tryInit);
    } else {
        tryInit();
    }
})();
</script>

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
        Remaining sick leave: <span id="remainingSickDays">${remainingSickDays}</span> / ${totalSickDays} days
    </div>
</div>

<div class="request-group" style="grid-column: 1 / -1;">
    <label for="reasonEditor">Reason <span class="required-star">*</span></label>
    <textarea name="reason" id="reasonEditor" class="request-textarea" rows="5" required></textarea>
</div>

<script>
    ClassicEditor
        .create(document.querySelector('#reasonEditor'), {
            toolbar: ['heading', '|', 'bold', 'italic', 'underline', 'link',
                      'bulletedList', 'numberedList', '|',
                      'alignment', 'fontColor', 'fontFamily', 'fontSize', '|',
                      'insertTable', 'blockQuote', 'undo', 'redo']
        })
        .catch(function(error) { console.error(error); });
</script>