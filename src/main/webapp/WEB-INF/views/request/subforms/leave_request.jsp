<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>

<div class="row">
    <div class="col-md-6">
        <div class="form-group">
            <label>Leave Date: *</label>
            <input type="date" name="leaveDate" id="leaveDate" class="form-control"
                   min="${today}" required onchange="updateLeaveDays()" />
        </div>

        <div class="form-group">
            <label>Leave Balance Info:</label>
            <div style="background:#f5f5f5; padding:10px; border-radius:4px;">
                <strong>Remaining leave balance:</strong> <span id="remainingBalance">${remainingLeave}</span> day(s)<br/>
                <strong>Requested leave days:</strong> <span id="requestedDays">0</span>
            </div>
        </div>

        <div class="form-group">
            <label>Observer(s):</label>
            <select class="select2-dynamic form-control" name="observerIds" multiple="multiple" style="width:100%">
                <c:forEach items="${observerList}" var="obs">
                    <option value="${obs.id}">${obs.fullName} - ${obs.positionName}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Reason: *</label>
            <textarea name="reason" id="reasonEditor" class="form-control" rows="6" required></textarea>
        </div>
    </div>

    <div class="col-md-6">
        <div class="form-group">
            <label>Proposal Date:</label>
            <input type="text" class="form-control" value="${now}" readonly />
        </div>

        <div class="form-group">
            <label>Approver: *</label>
            <select name="approverId" class="form-control" required>
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

        <div class="form-group">
            <label>Notes:</label>
            <ul style="font-size:0.9em; color:#666;">
                <li>Your leave request will be reviewed by your department manager.</li>
                <li>If approved, your attendance for the selected date will be marked as "ON_LEAVE" with full 8 hours credited.</li>
                <li>You will be notified after processing.</li>
            </ul>
        </div>
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
</script>