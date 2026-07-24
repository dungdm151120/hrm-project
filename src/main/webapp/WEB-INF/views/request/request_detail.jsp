<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8">
                    <title>Request Detail - #${request.id} | HRM</title>
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
                    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
                    <style>
                        .attachment-box {
                            display: inline-flex;
                            align-items: center;
                            gap: 10px;
                            border: 1px solid #ddd;
                            border-radius: 8px;
                            padding: 10px 14px;
                            background: #fafafa;
                            text-decoration: none;
                            color: inherit;
                            max-width: 340px;
                            transition: background .15s;
                        }

                        .attachment-box:hover {
                            background: #f0f0f0;
                        }

                        .attachment-icon {
                            width: 36px;
                            height: 36px;
                            border-radius: 6px;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            flex-shrink: 0;
                            font-size: 18px;
                        }

                        .attachment-icon.pdf {
                            background: #fde8e8;
                            color: #c0392b;
                        }

                        .attachment-icon.img {
                            background: #e8f5e9;
                            color: #2e7d32;
                        }

                        .attachment-icon.file {
                            background: #e8eaf6;
                            color: #3949ab;
                        }

                        .attachment-meta {
                            overflow: hidden;
                        }

                        .attachment-meta .att-name {
                            font-weight: 600;
                            font-size: 13px;
                            white-space: nowrap;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            max-width: 220px;
                        }

                        .attachment-meta .att-hint {
                            font-size: 11px;
                            color: #888;
                            margin-top: 1px;
                        }

                        .attachment-preview-wrap {
                            margin-top: 8px;
                        }

                        .attachment-preview-wrap img {
                            max-width: 320px;
                            max-height: 220px;
                            border-radius: 8px;
                            border: 1px solid #ddd;
                            object-fit: contain;
                            display: block;
                            cursor: pointer;
                        }
                    </style>
                </head>

                <body class="dashboard-body">

                    <div class="dashboard-wrapper">
                        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

                        <div class="dashboard-main">
                            <div class="dashboard-header">
                                <div class="header-left">
                                    <h1 class="header-title">Request Detail #${request.id}</h1>
                                </div>
                                <div class="header-right">
                                    <a href="${pageContext.request.contextPath}/view_my_request" class="btn-primary">←
                                        Back to My Requests</a>
                                </div>
                            </div>

                            <div class="dashboard-content">
                                <c:if test="${not empty param.error}">
                                    <div class="alert alert-error" style="margin-bottom: 1.5rem;">
                                        <span class="alert-icon">⚠️</span>
                                        <span>
                                            <c:choose>
                                                <c:when test="${param.error == 'comment_required'}">Approver comment is
                                                    required when processing a request.</c:when>
                                                <c:when test="${param.error == 'comment_too_long'}">Approver comment
                                                    must not exceed 1000 characters.</c:when>
                                                <c:otherwise>${param.error}</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                </c:if>

                                <div class="detail-card">
                                    <div class="detail-info">
                                        <div class="detail-row">
                                            <span class="detail-label">Proposer:</span>
                                            <span class="detail-value">${request.proposerName}</span>
                                        </div>
                                        <div class="detail-row">
                                            <span class="detail-label">Department:</span>
                                            <span class="detail-value">${not empty request.departmentName ?
                                                request.departmentName : 'N/A'}</span>
                                        </div>
                                        <div class="detail-row">
                                            <span class="detail-label">Type:</span>
                                            <span class="detail-value"><strong>${request.readableType}</strong></span>
                                        </div>
                                        <div class="detail-row">
                                            <span class="detail-label">Status:</span>
                                            <span class="detail-value">
                                                <span
                                                    class="badge badge-${fn:toLowerCase(request.status)}">${request.status}</span>
                                            </span>
                                        </div>
                                        <div class="detail-row">
                                            <span class="detail-label">Created At:</span>
                                            <span class="detail-value">
                                                <fmt:formatDate value="${request.createdAt}"
                                                    pattern="dd/MM/yyyy HH:mm" />
                                            </span>
                                        </div>
                                        <div class="detail-row">
                                            <span class="detail-label">Handler:</span>
                                            <span class="detail-value"><strong>${request.handlerName}</strong></span>
                                        </div>
                                        <div class="detail-row">
                                            <span class="detail-label">Reason:</span>
                                            <span class="detail-value">${request.reason}</span>
                                        </div>

                                        <%-- ══════════════ LEAVE REQUEST ══════════════ --%>
                                            <c:if test="${request.type == 'LEAVE_REQUEST' && not empty leaveRequest}">
                                                <div class="detail-row">
                                                    <span class="detail-label">Leave Type:</span>
                                                    <span class="detail-value">
                                                        <c:choose>
                                                            <c:when test="${leaveRequest.leaveType == 'ON_LEAVE'}">On
                                                                Leave (Paid)</c:when>
                                                            <c:when test="${leaveRequest.leaveType == 'LEAVE'}">Leave
                                                                (Unpaid)</c:when>
                                                            <c:otherwise>${leaveRequest.leaveType}</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </div>
                                                <div class="detail-row">
                                                    <span class="detail-label">Start Date:</span>
                                                    <span
                                                        class="detail-value"><strong>${leaveRequest.startDate}</strong></span>
                                                </div>
                                                <div class="detail-row">
                                                    <span class="detail-label">End Date:</span>
                                                    <span
                                                        class="detail-value"><strong>${leaveRequest.endDate}</strong></span>
                                                </div>
                                                <div class="detail-row">
                                                    <span class="detail-label">Requested Days:</span>
                                                    <span class="detail-value"><strong>${fn:length(leaveDates)}
                                                            day(s)</strong></span>
                                                </div>
                                            </c:if>

                                            <%-- ══════════════ ATTENDANCE ADJUST ══════════════ --%>
                                                <c:if
                                                    test="${request.type == 'ATTENDANCE_ADJUST' && not empty attendanceChangeRequest}">
                                                    <div class="detail-row">
                                                        <span class="detail-label">Work Date:</span>
                                                        <span
                                                            class="detail-value">${attendanceChangeRequest.workDate}</span>
                                                    </div>
                                                    <div class="detail-row">
                                                        <span class="detail-label">Desired Check-in:</span>
                                                        <span class="detail-value">${not empty
                                                            attendanceChangeRequest.desiredCheckIn ?
                                                            attendanceChangeRequest.desiredCheckIn : '--'}</span>
                                                    </div>
                                                    <div class="detail-row">
                                                        <span class="detail-label">Desired Check-out:</span>
                                                        <span class="detail-value">${not empty
                                                            attendanceChangeRequest.desiredCheckOut ?
                                                            attendanceChangeRequest.desiredCheckOut : '--'}</span>
                                                    </div>
                                                </c:if>

                                                <%-- ══════════════ SICK LEAVE REQUEST ══════════════ --%>
                                                    <c:if
                                                        test="${request.type == 'SICK_LEAVE_REQUEST' && not empty sickLeaveRequest}">
                                                        <c:set var="sickStartDate"
                                                            value="${not empty sickLeaveDates ? sickLeaveDates[0] : '--'}" />
                                                        <c:set var="sickEndDate"
                                                            value="${not empty sickLeaveDates ? sickLeaveDates[fn:length(sickLeaveDates)-1] : '--'}" />
                                                        <div class="detail-row">
                                                            <span class="detail-label">Start Date:</span>
                                                            <span
                                                                class="detail-value"><strong>${sickStartDate}</strong></span>
                                                        </div>
                                                        <div class="detail-row">
                                                            <span class="detail-label">End Date:</span>
                                                            <span
                                                                class="detail-value"><strong>${sickEndDate}</strong></span>
                                                        </div>
                                                        <div class="detail-row">
                                                            <span class="detail-label">Requested Days:</span>
                                                            <span class="detail-value"><strong>${fn:length(sickLeaveDates)}
                                                                    day(s)</strong></span>
                                                        </div>
                                                        <div class="detail-row" style="grid-column: 1 / -1;">
                                                            <span class="detail-label">Attachment:</span>
                                                            <span class="detail-value">
                                                                <c:choose>
                                                                    <c:when
                                                                        test="${not empty sickLeaveRequest.filePath}">
                                                                        <c:set var="fp"
                                                                            value="${sickLeaveRequest.filePath}" />
                                                                        <c:set var="ext"
                                                                            value="${fn:toLowerCase(fn:substring(fp, fn:length(fp) - 4, fn:length(fp)))}" />
                                                                        <c:set var="fullUrl"
                                                                            value="${pageContext.request.contextPath}${fp}" />
                                                                        <c:set var="fileName" value="${fp}" />
                                                                        <c:forTokens items="${fp}" delims="/" var="tok">
                                                                            <c:set var="fileName" value="${tok}" />
                                                                        </c:forTokens>
                                                                        <c:set var="isImage"
                                                                            value="${fn:contains('.jpg.jpeg.png.gif.webp', ext)}" />
                                                                        <c:set var="isPdf"
                                                                            value="${fn:endsWith(fn:toLowerCase(fp), '.pdf')}" />

                                                                        <a href="${fullUrl}" target="_blank"
                                                                            class="attachment-box" download>
                                                                            <div
                                                                                class="attachment-icon ${isPdf ? 'pdf' : (isImage ? 'img' : 'file')}">
                                                                                <c:choose>
                                                                                    <c:when test="${isPdf}">📄</c:when>
                                                                                    <c:when test="${isImage}">🖼️
                                                                                    </c:when>
                                                                                    <c:otherwise>📎</c:otherwise>
                                                                                </c:choose>
                                                                            </div>
                                                                            <div class="attachment-meta">
                                                                                <div class="att-name"
                                                                                    title="${fileName}">${fileName}
                                                                                </div>
                                                                                <div class="att-hint">Click to
                                                                                    view/download</div>
                                                                            </div>
                                                                        </a>
                                                                        <c:if test="${isImage}">
                                                                            <div class="attachment-preview-wrap">
                                                                                <img src="${fullUrl}"
                                                                                    alt="Medical certificate"
                                                                                    onclick="window.open('${fullUrl}','_blank')" />
                                                                            </div>
                                                                        </c:if>
                                                                    </c:when>
                                                                    <c:otherwise><span style="color:#999;">No attachment
                                                                            uploaded.</span></c:otherwise>
                                                                </c:choose>
                                                            </span>
                                                        </div>
                                                    </c:if>

                                                    <%-- ══════════════ DEPENDENT CHANGE REQUEST ══════════════ --%>
                                                        <c:if
                                                            test="${request.type == 'DEPENDENT_CHANGE_REQUEST' && not empty dependentChangeRequest}">
                                                            <div class="detail-row">
                                                                <span class="detail-label">Change Type:</span>
                                                                <span class="detail-value">
                                                                    <c:choose>
                                                                        <c:when
                                                                            test="${dependentChangeRequest.changeType == 'ADD'}">
                                                                            <strong>Add Dependent</strong></c:when>
                                                                        <c:when
                                                                            test="${dependentChangeRequest.changeType == 'UPDATE'}">
                                                                            <strong>Update Dependent</strong></c:when>
                                                                        <c:when
                                                                            test="${dependentChangeRequest.changeType == 'REMOVE'}">
                                                                            <strong>Remove Dependent</strong></c:when>
                                                                        <c:otherwise>
                                                                            <strong>${dependentChangeRequest.changeType}</strong>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </span>
                                                            </div>
                                                            <c:if test="${not empty targetDependent}">
                                                                <div class="detail-row">
                                                                    <span class="detail-label">Target Dependent:</span>
                                                                    <span class="detail-value"><strong>${targetDependent.dependentName}
                                                                            (${targetDependent.relationship})</strong></span>
                                                                </div>
                                                            </c:if>
                                                            <c:if
                                                                test="${dependentChangeRequest.changeType == 'ADD' || dependentChangeRequest.changeType == 'UPDATE'}">
                                                                <div class="detail-row">
                                                                    <span class="detail-label">Proposed Name:</span>
                                                                    <span
                                                                        class="detail-value"><strong>${dependentChangeRequest.dependentName}</strong></span>
                                                                </div>
                                                                <div class="detail-row">
                                                                    <span class="detail-label">Proposed DOB:</span>
                                                                    <span
                                                                        class="detail-value"><strong>${dependentChangeRequest.dependentDob}</strong></span>
                                                                </div>
                                                                <div class="detail-row">
                                                                    <span class="detail-label">Proposed ID:</span>
                                                                    <span
                                                                        class="detail-value"><strong>${dependentChangeRequest.dependentIdNumber}</strong></span>
                                                                </div>
                                                                <div class="detail-row">
                                                                    <span class="detail-label">Proposed
                                                                        Relationship:</span>
                                                                    <span
                                                                        class="detail-value"><strong>${dependentChangeRequest.relationship}</strong></span>
                                                                </div>
                                                                <div class="detail-row" style="grid-column: 1 / -1;">
                                                                    <span class="detail-label">Evidence Image:</span>
                                                                    <span class="detail-value">
                                                                        <c:choose>
                                                                            <c:when
                                                                                test="${not empty dependentChangeRequest.documentPath}">
                                                                                <c:set var="fullUrl"
                                                                                    value="${pageContext.request.contextPath}${dependentChangeRequest.documentPath}" />
                                                                                <div class="attachment-preview-wrap">
                                                                                    <img src="${fullUrl}"
                                                                                        alt="Evidence Image"
                                                                                        onclick="window.open('${fullUrl}','_blank')" />
                                                                                </div>
                                                                            </c:when>
                                                                            <c:otherwise><span style="color:#999;">No
                                                                                    image uploaded.</span></c:otherwise>
                                                                        </c:choose>
                                                                    </span>
                                                                </div>
                                                            </c:if>
                                                        </c:if>

                                                        <%-- ══════════════ OVERTIME ══════════════ --%>
                                                            <c:if
                                                                test="${request.type == 'OVERTIME' && not empty overtimeRequest}">
                                                                <div class="detail-row">
                                                                    <span class="detail-label">OT Date:</span>
                                                                    <span
                                                                        class="detail-value"><strong>${overtimeRequest.overtimeDate}</strong></span>
                                                                </div>
                                                                <div class="detail-row">
                                                                    <span class="detail-label">Shift:</span>
                                                                    <span
                                                                        class="detail-value">${overtimeRequest.shiftStart}
                                                                        - ${overtimeRequest.shiftEnd}</span>
                                                                </div>
                                                                <div class="detail-row"
                                                                    style="grid-column: 1 / -1; margin-top: 15px;">
                                                                    <span class="detail-label">OT Participants:</span>
                                                                    <div class="detail-value" style="width: 100%;">
                                                                        <table class="table"
                                                                            style="width: 100%; border-collapse: collapse; margin-top: 10px;">
                                                                            <thead>
                                                                                <tr style="background-color: #f5f5f5;">
                                                                                    <th
                                                                                        style="padding: 8px; border: 1px solid #ddd; text-align: left;">
                                                                                        Employee</th>
                                                                                    <th
                                                                                        style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                                                                        Status</th>
                                                                                    <th
                                                                                        style="padding: 8px; border: 1px solid #ddd; text-align: right;">
                                                                                        Actual Hours</th>
                                                                                </tr>
                                                                            </thead>
                                                                            <tbody>
                                                                                <c:forEach
                                                                                    items="${overtimeParticipants}"
                                                                                    var="p">
                                                                                    <tr>
                                                                                        <td
                                                                                            style="padding: 8px; border: 1px solid #ddd;">
                                                                                            ${p.userFullName} -
                                                                                            ${p.employeeCode}</td>
                                                                                        <td
                                                                                            style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                                                                            <span
                                                                                                class="badge badge-${fn:toLowerCase(p.status)}">${p.status}</span>
                                                                                        </td>
                                                                                        <td
                                                                                            style="padding: 8px; border: 1px solid #ddd; text-align: right;">
                                                                                            ${p.hoursActual}</td>
                                                                                    </tr>
                                                                                </c:forEach>
                                                                            </tbody>
                                                                        </table>
                                                                    </div>
                                                                </div>
                                                            </c:if>

                                                            <div class="detail-row">
                                                                <span class="detail-label">Approver:</span>
                                                                <span
                                                                    class="detail-value">${request.approverName}</span>
                                                            </div>
                                                            <div class="detail-row">
                                                                <span class="detail-label">Observer(s):</span>
                                                                <span class="detail-value">
                                                                    <c:choose>
                                                                        <c:when test="${not empty request.observer}">
                                                                            <ul style="margin: 0; padding-left: 20px;">
                                                                                <c:forEach items="${request.observer}"
                                                                                    var="obs">
                                                                                    <li>${obs.fullName} -
                                                                                        ${obs.positionName}</li>
                                                                                </c:forEach>
                                                                            </ul>
                                                                        </c:when>
                                                                        <c:otherwise>N/A</c:otherwise>
                                                                    </c:choose>
                                                                </span>
                                                            </div>
                                                            <div class="detail-row">
                                                                <span class="detail-label">Processed At:</span>
                                                                <span class="detail-value">
                                                                    <c:choose>
                                                                        <c:when test="${not empty request.processedAt}">
                                                                            <fmt:formatDate
                                                                                value="${request.processedAt}"
                                                                                pattern="dd/MM/yyyy HH:mm" />
                                                                        </c:when>
                                                                        <c:otherwise><span>-</span></c:otherwise>
                                                                    </c:choose>
                                                                </span>
                                                            </div>

                                                            <div class="detail-row"
                                                                style="margin-top: 20px; grid-column: 1 / -1;">
                                                                <span class="detail-label">Approver Comment *:</span>
                                                                <span class="detail-value">
                                                                    <c:choose>
                                                                        <c:when
                                                                            test="${request.status == 'PENDING' && sessionScope.currentUser.id eq request.approverId}">
                                                                            <form action="process_request" method="POST"
                                                                                id="processRequestForm">
                                                                                <input type="hidden" name="requestId"
                                                                                    value="${request.id}">
                                                                                <textarea name="comment"
                                                                                    id="approverComment"
                                                                                    class="request-textarea" rows="4"
                                                                                    maxlength="1000" required
                                                                                    placeholder="Enter approver comment..."></textarea>
                                                                                <div
                                                                                    style="display: flex; justify-content: space-between; align-items: center; margin-top: 4px;">
                                                                                    <div id="commentErrorMsg"
                                                                                        style="color: red; font-weight: bold; display: none;">
                                                                                    </div>
                                                                                    <div
                                                                                        style="font-size: 12px; color: #666; margin-left: auto;">
                                                                                        <span
                                                                                            id="commentCharCount">0</span>/1000
                                                                                        characters
                                                                                    </div>
                                                                                </div>
                                                                                <div
                                                                                    style="margin-top: 10px; display: flex; gap: 10px;">
                                                                                    <button type="submit" name="action"
                                                                                        value="APPROVE"
                                                                                        class="btn btn-primary"
                                                                                        onclick="return validateCommentForm();">Approve</button>
                                                                                    <button type="submit" name="action"
                                                                                        value="REJECT"
                                                                                        class="btn btn-danger"
                                                                                        onclick="return validateCommentForm();">Reject</button>
                                                                                </div>
                                                                            </form>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <p>${not empty request.approverComment ?
                                                                                request.approverComment : 'No comment
                                                                                provided.'}</p>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </span>
                                                            </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <script>
                        $(document).ready(function () {
                            var commentArea = $('#approverComment');
                            if (commentArea.length > 0) {
                                commentArea.on('input propertychange', function () {
                                    var len = $(this).val().length;
                                    $('#commentCharCount').text(len);
                                    if (len > 1000) {
                                        $('#commentCharCount').css({ 'color': 'red', 'font-weight': 'bold' });
                                    } else {
                                        $('#commentCharCount').css({ 'color': '#666', 'font-weight': 'normal' });
                                    }
                                });
                            }
                        });

                        function validateCommentForm() {
                            var commentVal = $('#approverComment').val().trim();
                            var errorMsg = $('#commentErrorMsg');
                            errorMsg.hide().text('');

                            if (!commentVal) {
                                errorMsg.text('Approver comment is required.').show();
                                return false;
                            }
                            if (commentVal.length > 1000) {
                                errorMsg.text('Approver comment must not exceed 1000 characters.').show();
                                return false;
                            }
                            return true;
                        }
                    </script>
                </body>

                </html>