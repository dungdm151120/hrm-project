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
    <label>Approver</label>
    <input type="text" class="request-input" value="${defaultApprover.fullName} - ${defaultApprover.positionName}" readonly style="background-color: #f8f9fa;" />
    <input type="hidden" name="approverId" value="${defaultApprover.id}" />
</div>

<div class="request-group">
    <label for="handlerId">Request Handler (HR Staff) <span class="required-star">*</span></label>
    <select name="handlerId" id="handlerId" class="request-select" required>
        <option value="" disabled selected>-- Select HR Staff --</option>
        <c:forEach items="${hrStaffList}" var="hr">
            <option value="${hr.id}">${hr.fullName} - ${hr.positionName}</option>
        </c:forEach>
    </select>
</div>

<div class="request-group">
    <label for="workDate">Work Date <span class="required-star">*</span></label>
    <input type="date" name="workDate" id="workDate" class="request-input" min="${minDate}" max="${maxDate}" required />
</div>

<div class="request-group">
    <label for="desiredCheckIn">Desired Check-in</label>
    <input type="time" name="desiredCheckIn" id="desiredCheckIn" class="request-input" />
</div>

<div class="request-group">
    <label for="desiredCheckOut">Desired Check-out</label>
    <input type="time" name="desiredCheckOut" id="desiredCheckOut" class="request-input" />
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

<c:if test="${blocked}">
    <div class="request-group" style="grid-column: 1 / -1;">
        <div class="request-info-box" style="color: #d32f2f;">
            Attendance adjustment requests cannot be submitted from the 6th to the 10th day of the month.
        </div>
    </div>
</c:if>

<script>
    ClassicEditor
        .create(document.querySelector('#reasonEditor'), {
            toolbar: ['heading', '|', 'bold', 'italic', 'underline', 'link', 'bulletedList', 'numberedList', '|', 'alignment', 'fontColor', 'fontFamily', 'fontSize', '|', 'insertTable', 'blockQuote', 'undo', 'redo']
        })
        .catch(error => { console.error(error); });

    $(document).ready(function() {
        // Namespace the submit event handler to avoid duplicate triggers if reloaded
        $('form').off('submit.attendance_change').on('submit.attendance_change', function(e) {
            if ($('#typeSelect').val() === 'ATTENDANCE_ADJUST') {
                const workDateVal = $('#workDate').val();
                if (workDateVal) {
                    const date = new Date(workDateVal);
                    const day = date.getDay(); // 0: Sunday, 6: Saturday
                    if (day === 0 || day === 6) {
                        alert('Attendance adjustment is not allowed on weekends. / Không được xin điều chỉnh công cho ngày cuối tuần.');
                        e.preventDefault();
                        return false;
                    }
                }

                const checkInVal = $('#desiredCheckIn').val();
                const checkOutVal = $('#desiredCheckOut').val();
                if (checkInVal && checkOutVal) {
                    if (checkOutVal < checkInVal) {
                        alert('Desired check-out time cannot be earlier than check-in time. / Giờ check-out mong muốn phải sau hoặc bằng giờ check-in mong muốn.');
                        e.preventDefault();
                        return false;
                    }
                }
            }
        });

        // Alert on input change for immediate feedback
        $('#workDate').on('change', function() {
            const val = $(this).val();
            if (val) {
                const date = new Date(val);
                const day = date.getDay();
                if (day === 0 || day === 6) {
                    alert('Attendance adjustment is not allowed on weekends. / Không được xin điều chỉnh công cho ngày cuối tuần.');
                    $(this).val('');
                }
            }
        });

        $('#desiredCheckIn, #desiredCheckOut').on('change', function() {
            const checkIn = $('#desiredCheckIn').val();
            const checkOut = $('#desiredCheckOut').val();
            if (checkIn && checkOut && checkOut < checkIn) {
                alert('Desired check-out time cannot be earlier than check-in time. / Giờ check-out mong muốn phải sau hoặc bằng giờ check-in mong muốn.');
                $('#desiredCheckOut').val('');
            }
        });
    });
</script>