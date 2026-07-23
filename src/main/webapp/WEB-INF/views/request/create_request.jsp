<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <title>Create New Request | HRM</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/request.css">
            <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
            <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
        </head>

        <body class="dashboard-body">

            <div class="dashboard-wrapper">
                <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

                <div class="dashboard-main">
                    <div class="dashboard-header">
                        <h1 class="header-title">Create New Request</h1>
                    </div>

                    <div class="dashboard-content">
                        <c:if test="${not empty param.error}">
                            <div class="alert alert-error" style="margin-bottom:1.5rem;">
                                <span class="alert-icon">⚠️</span>
                                <span>
                                    <c:choose>
                                        <c:when test="${param.error == 'missing_leave_date'}">Please select a leave
                                            date.</c:when>
                                        <c:when test="${param.error == 'leave_date_past'}">Leave date cannot be in the
                                            past.</c:when>
                                        <c:when test="${param.error == 'leave_date_weekend'}">Weekends are not allowed
                                            for leave requests.</c:when>
                                        <c:when test="${param.error == 'leave_date_holiday'}">Leave date cannot be a
                                            holiday.</c:when>
                                        <c:when test="${param.error == 'leave_date_already_on_leave'}">You already have
                                            an approved leave on this date.</c:when>
                                        <c:when test="${param.error == 'leave_date_already_marked'}">This date is
                                            already marked as leave or absent.</c:when>
                                        <c:when test="${param.error == 'leave_date_duplicate_request'}">A leave request
                                            for this date already exists (pending or approved).</c:when>
                                        <c:when test="${param.error == 'leave_balance_exhausted'}">You have no remaining
                                            leave balance. Cannot submit request.</c:when>
                                        <c:when test="${param.error == 'absent_balance_exhausted'}">You have no
                                            remaining unpaid leave balance. Cannot submit request.</c:when>
                                        <c:when test="${param.error == 'missing_leave_info'}">Please fill in all leave
                                            request information.</c:when>
                                        <c:when test="${param.error == 'invalid_leave_type'}">Invalid leave type.
                                        </c:when>
                                        <c:when test="${param.error == 'missing_approver'}">Please select an approver.
                                        </c:when>
                                        <c:when test="${param.error == 'invalid_approver'}">The selected approver must
                                            be an active HR Manager.</c:when>
                                        <c:when test="${param.error == 'missing_department'}">You must be assigned to a
                                            department to create this request.</c:when>
                                        <c:when test="${param.error == 'missing_date'}">Please select an overtime date.
                                        </c:when>
                                        <c:when test="${param.error == 'date_past'}">Overtime date cannot be in the
                                            past.</c:when>
                                        <c:when test="${param.error == 'date_weekend'}">Sick leave and overtime are only
                                            allowed from Monday to Friday.</c:when>
                                        <c:when test="${param.error == 'overtime_date_holiday'}">Overtime date cannot be
                                            a holiday.</c:when>
                                        <c:when test="${param.error == 'missing_reason'}">Please enter a reason.</c:when>
                                        <c:when test="${param.error == 'reason_too_long'}">The reason must not exceed
                                            500 characters.</c:when>
                                        <c:when test="${param.error == 'missing_employees'}">Please select at least one
                                            employee to work overtime.</c:when>
                                        <c:when test="${param.error == 'invalid_employee'}">One or more selected
                                            employees are invalid or outside your department.</c:when>
                                        <c:when test="${param.error == 'duplicate_overtime'}">One or more selected
                                            employees already have an overtime request (pending or approved) on this
                                            date.</c:when>
                                        <c:when test="${param.error == 'missing_work_date'}">Please select a work date.
                                        </c:when>
                                        <c:when test="${param.error == 'adjustment_blocked_days_6_to_10'}">Cannot submit
                                            adjustment requests between the 6th and 10th day of the month.</c:when>
                                        <c:when test="${param.error == 'adjustment_limit_exceeded'}">You have reached
                                            the maximum of 2 adjustment requests for this month.</c:when>
                                        <c:when test="${param.error == 'adjustment_date_weekend'}">Attendance adjustment is not allowed on weekends.</c:when>
                                        <c:when test="${param.error == 'adjustment_date_holiday'}">Attendance adjustment is not allowed on holidays.</c:when>
                                        <c:when test="${param.error == 'adjustment_date_duplicate'}">An adjustment request for this date already exists (pending or approved).</c:when>
                                        <c:when test="${param.error == 'adjustment_invalid_time'}">Desired check-out time cannot be earlier than check-in time.</c:when>
                                        <c:when test="${param.error == 'missing_evidence_file'}">Please upload a proof document (image).</c:when>
                                        <c:when test="${param.error == 'missing_dependents_count'}">Please enter the number of dependents.</c:when>
                                        <c:when test="${param.error == 'invalid_dependents_count'}">Invalid number of dependents.</c:when>
                                        <c:when test="${param.error == 'missing_dependent_info'}">Please fill in all dependent details.</c:when>
                                        <c:when test="${param.error == 'future_dependent_dob'}">Dependent's date of birth cannot be in the future.</c:when>
                                        <c:when test="${param.error == 'invalid_dependent_dob'}">Invalid date of birth format.</c:when>
                                        <c:when test="${param.error == 'missing_change_type'}">Please select a change type.</c:when>
                                        <c:when test="${param.error == 'missing_dependent_id'}">Please select a dependent to modify.</c:when>
                                        <c:when test="${param.error == 'invalid_dependent_id'}">Invalid dependent selected.</c:when>
                                        <c:when test="${param.error == 'invalid_dependent_id_number'}">Personal Identification Number must be exactly 12 digits.</c:when>
                                        <c:when test="${param.error == 'missing_file'}">Please attach a medical
                                            certificate.</c:when>
                                        <c:when test="${param.error == 'missing_sick_dates'}">Please select at least one
                                            sick date.</c:when>
                                        <c:when test="${param.error == 'invalid_date'}">Sick leave cannot be requested
                                            for future dates.</c:when>
                                        <c:when test="${param.error == 'insufficient_sick_days'}">You do not have enough
                                            remaining sick leave days.</c:when>
                                        <c:when test="${param.error == 'duplicate_sick_request'}">One or more selected
                                            dates are already covered by an existing sick leave request.</c:when>
                                        <c:when test="${param.error == 'sick_date_holiday'}">Sick leave dates cannot
                                            include holidays.</c:when>
                                        <c:when test="${param.error == 'system_error'}">A system error occurred. Please
                                            try again later.</c:when>
                                        <c:otherwise>${param.error}</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </c:if>

                        <div class="request-card">
                            <form action="create_request" method="POST">
                                <div class="request-group" style="max-width: 400px;">
                                    <label for="typeSelect">Request Type <span class="required-star">*</span></label>
                                    <select name="type" id="typeSelect" class="request-select" required>
                                        <option value="" disabled selected>-- Select Type --</option>
                                        <c:forEach var="entry" items="${requestTypes}">
                                            <option value="${entry.key}">${entry.value}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div id="dynamicFormContainer" class="request-grid"></div>

                                <div class="request-actions">
                                    <button type="submit" class="btn-submit-request">Send Request</button>
                                    <a href="${pageContext.request.contextPath}/view_my_request"
                                        class="btn-cancel-request">Cancel</a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                $(document).ready(function () {
                    function loadSubForm(selectedType) {
                        if (selectedType) {
                            $.ajax({
                                url: '${pageContext.request.contextPath}/load_sub_form',
                                type: 'GET',
                                data: { type: selectedType },
                                success: function (htmlResult) {
                                    $('#dynamicFormContainer').html(htmlResult);
                                    if (selectedType === 'OVERTIME') {
                                        $('form').attr('action', 'create_overtime_request');
                                    } else {
                                        $('form').attr('action', 'create_request');
                                    }
                                    if (selectedType === 'SICK_LEAVE_REQUEST' || selectedType === 'DEPENDENT_CHANGE_REQUEST') {
                                        $('form').attr('enctype', 'multipart/form-data');
                                    } else {
                                        $('form').removeAttr('enctype');
                                    }
                                    $('.select2-dynamic').select2({
                                        placeholder: "Select options...",
                                        allowClear: true,
                                        width: '100%'
                                    });
                                },
                                error: function () {
                                    $('#dynamicFormContainer').html('<p style="color:red;">Error loading specific form options.</p>');
                                }
                            });
                        } else {
                            $('#dynamicFormContainer').empty();
                        }
                    }

                    var urlParams = new URLSearchParams(window.location.search);
                    var typeParam = urlParams.get('type');
                    if (typeParam) {
                        $('#typeSelect').val(typeParam);
                        loadSubForm(typeParam);
                    }

                    $('#typeSelect').change(function () {
                        loadSubForm($(this).val());
                    });
                });
            </script>

        </body>

        </html>
