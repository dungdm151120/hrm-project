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
    <style>
        /* ── Sick date tags ── */
        .sick-date-list {
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
            margin: 0;
            padding: 0;
        }
        .sick-date-tag {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            background: #e3f2fd;
            color: #1565c0;
            border: 1px solid #90caf9;
            border-radius: 5px;
            padding: 3px 10px;
            font-size: 13px;
            font-weight: 500;
            white-space: nowrap;
        }
        .sick-date-tag svg {
            flex-shrink: 0;
        }
        .sick-date-count {
            font-size: 12px;
            color: #555;
            margin-top: 6px;
        }

        /* ── Attachment box ── */
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
        .attachment-icon.pdf  { background: #fde8e8; color: #c0392b; }
        .attachment-icon.img  { background: #e8f5e9; color: #2e7d32; }
        .attachment-icon.file { background: #e8eaf6; color: #3949ab; }
        .attachment-meta { overflow: hidden; }
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

        /* ── Image preview ── */
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
        .att-open-hint {
            font-size: 11px;
            color: #888;
            margin-top: 4px;
        }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <h1 class="header-title">Request Detail #${request.id}</h1>
        </div>

        <div class="dashboard-content">
            <div class="detail-card">
                <div class="detail-info">
                    <div class="detail-row">
                        <span class="detail-label">Proposer:</span>
                        <span class="detail-value">${request.proposerName}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Department:</span>
                        <span class="detail-value">${not empty request.departmentName ? request.departmentName : 'N/A'}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Type:</span>
                        <span class="detail-value"><strong>${request.readableType}</strong></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Status:</span>
                        <span class="detail-value">
                            <span class="badge badge-${fn:toLowerCase(request.status)}">${request.status}</span>
                        </span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Created At:</span>
                        <span class="detail-value"><fmt:formatDate value="${request.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span>
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
                                    <c:when test="${leaveRequest.leaveType == 'ON_LEAVE'}">On Leave (Paid)</c:when>
                                    <c:when test="${leaveRequest.leaveType == 'LEAVE'}">Leave (Unpaid)</c:when>
                                    <c:otherwise>${leaveRequest.leaveType}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Leave Date:</span>
                            <span class="detail-value"><strong>${leaveRequest.leaveDate}</strong></span>
                        </div>
                    </c:if>

                    <%-- ══════════════ ATTENDANCE ADJUST ══════════════ --%>
                    <c:if test="${request.type == 'ATTENDANCE_ADJUST' && not empty attendanceChangeRequest}">
                        <div class="detail-row">
                            <span class="detail-label">Work Date:</span>
                            <span class="detail-value">${attendanceChangeRequest.workDate}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Desired Check-in:</span>
                            <span class="detail-value">${not empty attendanceChangeRequest.desiredCheckIn ? attendanceChangeRequest.desiredCheckIn : '--'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Desired Check-out:</span>
                            <span class="detail-value">${not empty attendanceChangeRequest.desiredCheckOut ? attendanceChangeRequest.desiredCheckOut : '--'}</span>
                        </div>
                    </c:if>

                    <%-- ══════════════ SICK LEAVE REQUEST ══════════════ --%>
                    <c:if test="${request.type == 'SICK_LEAVE_REQUEST' && not empty sickLeaveRequest}">

                        <%-- Sick Dates --%>
                        <div class="detail-row" style="grid-column: 1 / -1;">
                            <span class="detail-label">Sick Dates:</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${not empty sickLeaveDates}">
                                        <div class="sick-date-list">
                                            <c:forEach items="${sickLeaveDates}" var="d">
                                                <span class="sick-date-tag">
                                                    <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
                                                         stroke="currentColor" stroke-width="2"
                                                         stroke-linecap="round" stroke-linejoin="round">
                                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                                                        <line x1="16" y1="2" x2="16" y2="6"/>
                                                        <line x1="8"  y1="2" x2="8"  y2="6"/>
                                                        <line x1="3"  y1="10" x2="21" y2="10"/>
                                                    </svg>
                                                    ${d}
                                                </span>
                                            </c:forEach>
                                        </div>
                                        <div class="sick-date-count">
                                            Total: <strong>${fn:length(sickLeaveDates)}</strong> day(s)
                                        </div>
                                    </c:when>
                                    <c:otherwise><span style="color:#999;">No dates recorded.</span></c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <%-- Attachment --%>
                        <div class="detail-row" style="grid-column: 1 / -1;">
                            <span class="detail-label">Attachment:</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${not empty sickLeaveRequest.filePath}">
                                        <%-- Xác định loại file qua extension --%>
                                        <c:set var="fp"  value="${sickLeaveRequest.filePath}"/>
                                        <c:set var="ext" value="${fn:toLowerCase(fn:substring(fp, fn:length(fp) - 4, fn:length(fp)))}"/>
                                        <c:set var="fullUrl" value="${pageContext.request.contextPath}${fp}"/>

                                        <%-- Lấy tên file từ path --%>
                                        <c:set var="fileName" value="${fp}"/>
                                        <c:forTokens items="${fp}" delims="/" var="tok">
                                            <c:set var="fileName" value="${tok}"/>
                                        </c:forTokens>

                                        <%-- Phân loại icon --%>
                                        <c:set var="isImage" value="${fn:contains('.jpg.jpeg.png.gif.webp', ext)}"/>
                                        <c:set var="isPdf"   value="${fn:endsWith(fn:toLowerCase(fp), '.pdf')}"/>

                                        <%-- Hộp download --%>
                                        <a href="${fullUrl}" target="_blank" class="attachment-box" download>
                                            <div class="attachment-icon ${isPdf ? 'pdf' : (isImage ? 'img' : 'file')}">
                                                <c:choose>
                                                    <c:when test="${isPdf}">
                                                        <%-- PDF icon --%>
                                                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                                             stroke="currentColor" stroke-width="2"
                                                             stroke-linecap="round" stroke-linejoin="round">
                                                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                                                            <polyline points="14 2 14 8 20 8"/>
                                                            <line x1="9" y1="13" x2="15" y2="13"/>
                                                            <line x1="9" y1="17" x2="15" y2="17"/>
                                                        </svg>
                                                    </c:when>
                                                    <c:when test="${isImage}">
                                                        <%-- Image icon --%>
                                                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                                             stroke="currentColor" stroke-width="2"
                                                             stroke-linecap="round" stroke-linejoin="round">
                                                            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                                                            <circle cx="8.5" cy="8.5" r="1.5"/>
                                                            <polyline points="21 15 16 10 5 21"/>
                                                        </svg>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <%-- Generic file icon --%>
                                                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                                             stroke="currentColor" stroke-width="2"
                                                             stroke-linecap="round" stroke-linejoin="round">
                                                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                                                            <polyline points="14 2 14 8 20 8"/>
                                                        </svg>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="attachment-meta">
                                                <div class="att-name" title="${fileName}">${fileName}</div>
                                                <div class="att-hint">
                                                    <c:choose>
                                                        <c:when test="${isPdf}">PDF Document — click to open</c:when>
                                                        <c:when test="${isImage}">Image file — click to view</c:when>
                                                        <c:otherwise>File attachment — click to download</c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </a>

                                        <%-- Nếu là ảnh: hiện thumbnail preview bên dưới --%>
                                        <c:if test="${isImage}">
                                            <div class="attachment-preview-wrap">
                                                <img src="${fullUrl}" alt="Medical certificate"
                                                     onclick="window.open('${fullUrl}','_blank')"
                                                     title="Click to open full size"/>
                                                <div class="att-open-hint">Click image to open full size</div>
                                            </div>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color:#999;">No attachment uploaded.</span>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </c:if>

                    <%-- ══════════════ DEPENDENT CHANGE REQUEST ══════════════ --%>
                    <c:if test="${request.type == 'DEPENDENT_CHANGE_REQUEST' && not empty dependentChangeRequest}">
                        <div class="detail-row">
                            <span class="detail-label">Change Type:</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${dependentChangeRequest.changeType == 'ADD'}"><strong>Add Dependent</strong></c:when>
                                    <c:when test="${dependentChangeRequest.changeType == 'UPDATE'}"><strong>Update Dependent</strong></c:when>
                                    <c:when test="${dependentChangeRequest.changeType == 'REMOVE'}"><strong>Remove Dependent</strong></c:when>
                                    <c:otherwise><strong>${dependentChangeRequest.changeType}</strong></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <c:if test="${not empty targetDependent}">
                            <div class="detail-row">
                                <span class="detail-label">Target Dependent:</span>
                                <span class="detail-value"><strong>${targetDependent.dependentName} (${targetDependent.relationship})</strong></span>
                            </div>
                        </c:if>
                        
                        <c:choose>
                            <c:when test="${dependentChangeRequest.changeType == 'ADD' || dependentChangeRequest.changeType == 'UPDATE'}">
                                <div class="detail-row">
                                    <span class="detail-label">Proposed Dependent Full Name:</span>
                                    <span class="detail-value"><strong>${dependentChangeRequest.dependentName}</strong></span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Proposed Date of Birth:</span>
                                    <span class="detail-value"><strong>${dependentChangeRequest.dependentDob}</strong></span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Proposed ID Number:</span>
                                    <span class="detail-value"><strong>${dependentChangeRequest.dependentIdNumber}</strong></span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Proposed Relationship:</span>
                                    <span class="detail-value"><strong>${dependentChangeRequest.relationship}</strong></span>
                                </div>
                                <div class="detail-row" style="grid-column: 1 / -1;">
                                    <span class="detail-label">Evidence Image:</span>
                                    <span class="detail-value">
                                        <c:choose>
                                            <c:when test="${not empty dependentChangeRequest.documentPath}">
                                                <c:set var="fullUrl" value="${pageContext.request.contextPath}${dependentChangeRequest.documentPath}"/>
                                                <div class="attachment-preview-wrap" style="margin-top: 10px;">
                                                    <img src="${fullUrl}" alt="Evidence Image"
                                                         onclick="window.open('${fullUrl}','_blank')"
                                                         title="Click to open full size"
                                                         style="max-width: 100%; max-height: 300px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; object-fit: contain;"/>
                                                    <div class="att-open-hint" style="font-size: 12px; color: #666; margin-top: 5px;">Click image to open full size</div>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color:#999;">No image uploaded.</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </c:when>
                            <c:when test="${dependentChangeRequest.changeType == 'REMOVE'}">
                                <div class="detail-row" style="grid-column: 1 / -1;">
                                    <span class="detail-label">Target Dependent Status:</span>
                                    <span class="detail-value" style="color: #dc3545; font-weight: bold;">To Be Marked Inactive</span>
                                </div>
                            </c:when>
                        </c:choose>
                    </c:if>

                    <%-- ══════════════ OVERTIME ══════════════ --%>
                    <c:if test="${request.type == 'OVERTIME' && not empty overtimeRequest}">
                        <div class="detail-row">
                            <span class="detail-label">OT Date:</span>
                            <span class="detail-value"><strong>${overtimeRequest.overtimeDate}</strong></span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Shift:</span>
                            <span class="detail-value">${overtimeRequest.shiftStart} - ${overtimeRequest.shiftEnd}</span>
                        </div>
                        <div class="detail-row" style="grid-column: 1 / -1; margin-top: 15px;">
                            <span class="detail-label">OT Participants:</span>
                            <div class="detail-value" style="width: 100%;">
                                <table class="table" style="width: 100%; border-collapse: collapse; margin-top: 10px;">
                                    <thead>
                                        <tr style="background-color: #f5f5f5;">
                                            <th style="padding: 8px; border: 1px solid #ddd; text-align: left;">Employee</th>
                                            <th style="padding: 8px; border: 1px solid #ddd; text-align: center;">Status</th>
                                            <th style="padding: 8px; border: 1px solid #ddd; text-align: right;">Actual Hours</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${overtimeParticipants}" var="p">
                                            <tr>
                                                <td style="padding: 8px; border: 1px solid #ddd;">${p.userFullName} - ${p.employeeCode}</td>
                                                <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                                    <span class="badge badge-${fn:toLowerCase(p.status)}">${p.status}</span>
                                                </td>
                                                <td style="padding: 8px; border: 1px solid #ddd; text-align: right;">${p.hoursActual}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                                <c:if test="${request.status == 'APPROVED' && (sessionScope.currentUser.id eq request.approverId || fn:contains(sessionScope.currentUser.roleName, 'HR'))}">
                                    <form action="confirm_overtime" method="POST" style="margin-top: 15px;">
                                        <input type="hidden" name="requestId" value="${request.id}">
                                        <button type="submit" class="btn btn-success" style="border: 2px solid #28a745;"
                                                onclick="return confirm('Bạn có chắc chắn muốn xác nhận tính giờ OT cho request này?');">
                                            Confirm Overtime
                                        </button>
                                    </form>
                                </c:if>
                                <c:if test="${request.status == 'CONFIRMED'}">
                                    <div style="margin-top:15px; padding:10px; background-color:#f3e8ff; color:#6b21a8; border-radius:8px; border:1px solid #d8b4fe;">
                                        <strong>
                                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                                 stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                                                 style="vertical-align:text-bottom; margin-right:4px;">
                                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                                                <polyline points="22 4 12 14.01 9 11.01"/>
                                            </svg>
                                            OT Confirmed!
                                        </strong><br/>
                                        The OT hours for this request have been successfully calculated and synced with the attendance records.
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </c:if>

                    <div class="detail-row">
                        <span class="detail-label">Approver:</span>
                        <span class="detail-value">${request.approverName}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Observer(s):</span>
                        <span class="detail-value">
                            <c:choose>
                                <c:when test="${not empty request.observer}">
                                    <ul style="margin: 0; padding-left: 20px;">
                                        <c:forEach items="${request.observer}" var="obs">
                                            <li>${obs.fullName} - ${obs.positionName}</li>
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
                                    <fmt:formatDate value="${request.processedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </c:when>
                                <c:otherwise><span>-</span></c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <c:if test="${request.type == 'ATTENDANCE_ADJUST' && request.status == 'APPROVED' && !attendanceChangeRequest.applied && (sessionScope.currentUser.positionName == 'HR Staff' || sessionScope.currentUser.id eq request.handlerId)}">
                        <div class="detail-row" style="margin-top: 20px; grid-column: 1 / -1;">
                            <span class="detail-label">Apply Attendance Correction:</span>
                            <span class="detail-value">
                                <form action="process_request" method="POST">
                                    <input type="hidden" name="requestId" value="${request.id}">
                                    <button type="submit" name="action" value="APPLY_CHANGES" class="btn btn-success" 
                                            style="border: 2px solid #28a745; padding: 8px 16px; border-radius: 4px; font-weight: bold; background-color: #28a745; color: #fff; cursor: pointer;"
                                            onclick="return confirm('Are you sure you want to apply these attendance changes to the database?');">
                                        Apply Changes
                                    </button>
                                </form>
                            </span>
                        </div>
                    </c:if>

                    <c:if test="${request.type == 'ATTENDANCE_ADJUST' && request.status == 'APPROVED' && attendanceChangeRequest.applied}">
                        <div class="detail-row" style="margin-top: 20px; grid-column: 1 / -1;">
                            <div style="padding:12px; background-color:#d4edda; color:#155724; border-radius:8px; border:1px solid #c3e6cb; width: 100%;">
                                <strong>
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                         stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                                         style="vertical-align:text-bottom; margin-right:4px;">
                                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                                        <polyline points="22 4 12 14.01 9 11.01"/>
                                    </svg>
                                    Changes Applied!
                                </strong><br/>
                                The attendance record adjustments have been successfully applied to the database by HR Staff.
                            </div>
                        </div>
                    </c:if>

                    <div class="detail-row" style="margin-top: 20px;">
                        <span class="detail-label">Approver Comment *:</span>
                        <span class="detail-value">
                            <c:choose>
                                <c:when test="${request.status == 'PENDING' && sessionScope.currentUser.id eq request.approverId}">
                                    <form action="process_request" method="POST">
                                        <input type="hidden" name="requestId" value="${request.id}">
                                        <textarea name="comment" class="form-control" required placeholder="Enter approver comment..."></textarea>
                                        <div style="margin-top: 10px;">
                                            <button type="submit" name="action" value="APPROVE" class="btn btn-primary">Approve</button>
                                            <button type="submit" name="action" value="REJECT" class="btn btn-danger">Reject</button>
                                        </div>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <p>${not empty request.approverComment ? request.approverComment : 'No comment provided.'}</p>
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
