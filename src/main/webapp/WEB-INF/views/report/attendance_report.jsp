<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

                <c:set var="ctx" value="${pageContext.request.contextPath}" />

                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Attendance Report | HRM</title>
                    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
                    <style>
                        .report-filter-card {
                            background: var(--card-bg);
                            border-radius: var(--radius);
                            box-shadow: var(--shadow);
                            padding: 24px;
                            margin-bottom: 24px;
                            border: 1px solid var(--border-color);
                        }

                        .filter-form-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                            gap: 20px;
                            align-items: flex-end;
                        }

                        .form-group {
                            display: flex;
                            vertical-align: middle;
                            flex-direction: column;
                            gap: 8px;
                        }

                        .form-group label {
                            font-weight: 600;
                            font-size: 13px;
                            color: var(--text-secondary);
                        }

                        .form-group select,
                        .form-group input {
                            padding: 10px 14px;
                            border-radius: var(--radius-sm);
                            border: 1px solid var(--border-color);
                            background-color: var(--white);
                            color: var(--text-primary);
                            font-size: 14px;
                            font-family: inherit;
                            outline: none;
                            transition: border-color 0.2s, box-shadow 0.2s;
                            width: 100%;
                        }

                        .form-group select:focus,
                        .form-group input:focus {
                            border-color: var(--primary);
                            box-shadow: 0 0 0 3px var(--primary-light);
                        }

                        .btn-generate {
                            background: var(--primary);
                            color: var(--white);
                            border: none;
                            padding: 11px 24px;
                            border-radius: var(--radius-sm);
                            font-weight: 600;
                            font-size: 14px;
                            cursor: pointer;
                            transition: background-color 0.2s, transform 0.1s;
                            display: inline-flex;
                            align-items: center;
                            justify-content: center;
                            gap: 8px;
                            height: 42px;
                        }

                        .btn-generate:hover {
                            background: var(--primary-dark);
                        }

                        .btn-generate:active {
                            transform: scale(0.98);
                        }

                        .dashboard-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
                            gap: 20px;
                            margin-top: 28px;
                            margin-bottom: 28px;
                        }

                        .stat-card {
                            background: var(--white);
                            border-radius: var(--radius-sm);
                            border: 1px solid var(--border-color);
                            padding: 20px;
                            box-shadow: var(--shadow-sm);
                            display: flex;
                            flex-direction: column;
                            gap: 12px;
                            transition: transform 0.2s, box-shadow 0.2s;
                        }

                        .stat-card:hover {
                            transform: translateY(-2px);
                            box-shadow: var(--shadow);
                        }

                        .stat-header {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                        }

                        .stat-title {
                            font-size: 13px;
                            font-weight: 600;
                            color: var(--text-secondary);
                            text-transform: uppercase;
                            letter-spacing: 0.5px;
                        }

                        .stat-icon {
                            width: 36px;
                            height: 36px;
                            border-radius: 50%;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            background: var(--primary-light);
                            color: var(--primary);
                        }

                        .stat-value {
                            font-size: 22px;
                            font-weight: 700;
                            color: var(--text-primary);
                        }

                        .stat-ratio {
                            font-size: 14px;
                            color: var(--text-secondary);
                            font-weight: 500;
                        }

                        .progress-bar-container {
                            width: 100%;
                            height: 6px;
                            background: #E5E7EB;
                            border-radius: 10px;
                            overflow: hidden;
                        }

                        .progress-bar-fill {
                            height: 100%;
                            background: var(--primary);
                            border-radius: 10px;
                            transition: width 0.3s ease;
                        }

                        .highlight-card {
                            background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%);
                            border: 1px solid #BFDBFE;
                        }

                        .highlight-card.punctual {
                            background: linear-gradient(135deg, #ECFDF5 0%, #D1FAE5 100%);
                            border: 1px solid #A7F3D0;
                        }

                        .highlight-card .stat-icon {
                            background: var(--white);
                        }

                        .highlight-card.punctual .stat-icon {
                            color: #10B981;
                        }

                        .highlight-meta {
                            font-size: 12px;
                            color: var(--text-secondary);
                            margin-top: -4px;
                        }

                        .empty-state {
                            text-align: center;
                            padding: 48px 24px;
                            background: var(--white);
                            border-radius: var(--radius);
                            border: 1px solid var(--border-color);
                            color: var(--text-secondary);
                        }

                        .empty-icon {
                            margin-bottom: 16px;
                            color: var(--text-muted);
                        }

                        /* Adjust Table Style for Report */
                        .table-wrapper {
                            background: var(--white);
                            border-radius: var(--radius);
                            border: 1px solid var(--border-color);
                            box-shadow: var(--shadow);
                            overflow-x: auto;
                            margin-bottom: 24px;
                        }

                        .table-wrapper table {
                            width: 100%;
                            border-collapse: collapse;
                            text-align: left;
                            font-size: 13px;
                        }

                        .table-wrapper th {
                            background: #F9FAFB;
                            color: #374151;
                            font-weight: 600;
                            padding: 14px 16px;
                            border-bottom: 1px solid var(--border-color);
                            white-space: nowrap;
                        }

                        .table-wrapper td {
                            padding: 12px 16px;
                            border-bottom: 1px solid var(--border-color);
                            color: #4B5563;
                            white-space: nowrap;
                        }

                        .table-wrapper tr:last-child td {
                            border-bottom: none;
                        }

                        .table-wrapper tr:hover td {
                            background-color: #F9FAFB;
                        }

                        .align-right {
                            text-align: right;
                        }

                        .align-center {
                            text-align: center;
                        }

                        .bold {
                            font-weight: 600;
                            color: var(--text-primary);
                        }
                    </style>
                </head>

                <body class="dashboard-body">

                    <div class="dashboard-wrapper">

                        <!-- Sidebar component -->
                        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

                        <div class="dashboard-main">
                            <div class="dashboard-header">
                                <div class="header-left">
                                    <h1 class="header-title">Attendance Report</h1>
                                </div>
                                <div class="header-right">
                                    <!-- Additional controls can go here -->
                                </div>
                            </div>

                            <div class="dashboard-content">

                                <!-- Filters Card -->
                                <div class="report-filter-card">
                                    <form action="${ctx}/reports/attendance" method="GET" id="reportForm">
                                        <input type="hidden" name="action" value="generate">

                                        <div class="filter-form-grid">

                                            <!-- Department Selection -->
                                            <div class="form-group">
                                                <label for="departmentId">Department</label>
                                                <c:choose>
                                                    <c:when test="${isRestricted}">
                                                        <select name="departmentId" id="departmentId" readonly>
                                                            <c:forEach var="dept" items="${departments}">
                                                                <option value="${dept.id}" selected>${dept.name}
                                                                </option>
                                                            </c:forEach>
                                                        </select>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <select name="departmentId" id="departmentId">
                                                            <option value="all" ${empty selectedDeptId ? 'selected' : ''}>All Departments</option>
                                                            <c:forEach var="dept" items="${departments}">
                                                                <option value="${dept.id}" ${selectedDeptId==dept.id
                                                                    ? 'selected' : '' }>${dept.name}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>

                                            <!-- Period Type -->
                                            <div class="form-group">
                                                <label for="periodType">Report Period</label>
                                                <select name="periodType" id="periodType">
                                                    <option value="month" ${periodType=='month' ? 'selected' : ''}>Monthly</option>
                                                    <option value="quarter" ${periodType=='quarter' ? 'selected' : ''}>Quarterly</option>
                                                    <option value="year" ${periodType=='year' ? 'selected' : ''}>Yearly</option>
                                                </select>
                                            </div>

                                            <!-- Monthly Input Option -->
                                            <div class="form-group" id="monthGroup">
                                                <label for="month">Month</label>
                                                <select name="month" id="month">
                                                    <c:forEach var="m" begin="1" end="12">
                                                        <option value="${m}" ${selectedMonth==m ? 'selected' : '' }>Month ${m}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>

                                            <!-- Quarterly Input Option -->
                                            <div class="form-group" id="quarterGroup">
                                                <label for="quarter">Quarter</label>
                                                <select name="quarter" id="quarter">
                                                    <c:forEach var="q" begin="1" end="4">
                                                        <option value="${q}" ${selectedQuarter==q ? 'selected' : '' }>Quarter ${q}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>

                                            <!-- Year (needed for month, quarter, and year periods) -->
                                            <div class="form-group" id="yearGroup">
                                                <label for="year">Year</label>
                                                <select name="year" id="year">
                                                    <c:forEach var="y" items="${years}">
                                                        <option value="${y}" ${selectedYear==y ? 'selected' : '' }>${y}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </div>

                                            <!-- Action Button -->
                                            <div class="form-group">
                                                <button type="submit" class="btn-generate">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                                        viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                                        stroke-width="2.5" stroke-linecap="round"
                                                        stroke-linejoin="round">
                                                        <line x1="18" y1="20" x2="18" y2="10"></line>
                                                        <line x1="12" y1="20" x2="12" y2="4"></line>
                                                        <line x1="6" y1="20" x2="6" y2="14"></line>
                                                    </svg>
                                                    Generate Report
                                                </button>
                                            </div>

                                            <!-- Export Button -->
                                            <div class="form-group">
                                                <button type="button" class="btn-generate" style="background-color: #10b981;" onclick="exportToExcel()">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                                        viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                                        stroke-width="2.5" stroke-linecap="round"
                                                        stroke-linejoin="round">
                                                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                                        <polyline points="14 2 14 8 20 8"></polyline>
                                                        <line x1="16" y1="13" x2="8" y2="13"></line>
                                                        <line x1="16" y1="17" x2="8" y2="17"></line>
                                                        <polyline points="10 9 9 9 8 9"></polyline>
                                                    </svg>
                                                    Export to Excel
                                                </button>
                                            </div>

                                        </div>
                                    </form>
                                </div>

                                <!-- Report Results Section -->
                                <c:choose>
                                    <c:when test="${not isGenerated}">
                                        <div class="empty-state">
                                            <div class="empty-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48"
                                                    viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                                    stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                                                    <path
                                                        d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z">
                                                    </path>
                                                    <polyline points="14 2 14 8 20 8"></polyline>
                                                    <line x1="16" y1="13" x2="8" y2="13"></line>
                                                    <line x1="16" y1="17" x2="8" y2="17"></line>
                                                    <polyline points="10 9 9 9 8 9"></polyline>
                                                </svg>
                                            </div>
                                            <h3>Select filters and click "Generate Report" to display data</h3>
                                            <p>Filter the attendance report by department and reporting period.</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <!-- Table Filter & Search Controls -->
                                         <div style="display: flex; flex-wrap: wrap; gap: 16px; margin-bottom: 16px; align-items: center; background: var(--white); padding: 16px; border-radius: var(--radius); border: 1px solid var(--border-color);">
                                             <div style="flex: 1; display: flex; flex-direction: column; gap: 6px;">
                                                 <label style="font-weight: 600; font-size: 13px; color: var(--text-secondary);">Search by Employee Name</label>
                                                 <input type="text" id="localSearchName" placeholder="Enter employee name..."
                                                        style="padding: 10px 14px; border-radius: var(--radius-sm); border: 1px solid var(--border-color); outline: none; font-size: 14px;" />
                                             </div>
                                             <div style="width: 250px; display: flex; flex-direction: column; gap: 6px;">
                                                 <label style="font-weight: 600; font-size: 13px; color: var(--text-secondary);">Filter by Department</label>
                                                 <select id="localFilterDept" style="padding: 10px 14px; border-radius: var(--radius-sm); border: 1px solid var(--border-color); outline: none; font-size: 14px; background-color: var(--white); color: var(--text-primary);">
                                                     <option value="all">All Departments</option>
                                                     <c:forEach var="dept" items="${departments}">
                                                         <option value="${dept.name}">${dept.name}</option>
                                                     </c:forEach>
                                                 </select>
                                             </div>
                                             <div style="width: 230px; display: flex; flex-direction: column; gap: 6px;">
                                                 <label for="localSortBy" style="font-weight: 600; font-size: 13px; color: var(--text-secondary);">Sort by</label>
                                                 <select id="localSortBy" style="padding: 10px 14px; border-radius: var(--radius-sm); border: 1px solid var(--border-color); outline: none; font-size: 14px; background-color: var(--white); color: var(--text-primary);">
                                                     <option value="workHours">Total Work Hours</option>
                                                     <option value="absentDays">Total Absent Days</option>
                                                     <option value="lateEarly">Late Arrivals + Early Leaves</option>
                                                     <option value="missingCheck">Missed Check-ins + outs</option>
                                                 </select>
                                             </div>
                                             <div style="width: 170px; display: flex; flex-direction: column; gap: 6px;">
                                                 <label for="localSortOrder" style="font-weight: 600; font-size: 13px; color: var(--text-secondary);">Order</label>
                                                 <select id="localSortOrder" style="padding: 10px 14px; border-radius: var(--radius-sm); border: 1px solid var(--border-color); outline: none; font-size: 14px; background-color: var(--white); color: var(--text-primary);">
                                                     <option value="desc">Descending</option>
                                                     <option value="asc">Ascending</option>
                                                 </select>
                                             </div>
                                         </div>

                                        <!-- Statistics table -->
                                        <div class="table-wrapper">
                                            <table>
                                                <thead>
                                                    <tr>
                                                        <th class="align-center">Employee ID</th>
                                                        <th>Employee Name</th>
                                                        <th>Position</th>
                                                        <th>Department</th>
                                                        <th class="align-center">Expected Workdays</th>
                                                        <th class="align-center">Present Days</th>
                                                        <th class="align-center">Absent Days</th>
                                                        <th class="align-center">Late Arrivals</th>
                                                        <th class="align-center">Early Leaves</th>
                                                        <th class="align-center">Missed Check-ins</th>
                                                        <th class="align-center">Missed Check-outs</th>
                                                        <th class="align-right">Total Work Hours</th>
                                                        <th class="align-right">Total OT Hours</th>
                                                        <th class="align-center">Leave Days</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="row" items="${reportRows}">
                                                        <tr class="employee-row"
                                                            data-name="${fn:toLowerCase(row.employeeName)}"
                                                            data-dept="${row.departmentName}"
                                                            data-work-hours="${row.totalWorkHours}"
                                                            data-absent-days="${row.absentDays}"
                                                            data-late-early="${row.lateDays + row.earlyLeaveDays}"
                                                            data-missing-check="${row.forgotCheckInDays + row.forgotCheckOutDays}">
                                                            <td class="align-center bold">${row.employeeCode}</td>
                                                            <td class="bold">${row.employeeName}</td>
                                                            <td>${row.positionName}</td>
                                                            <td>${row.departmentName}</td>
                                                            <td class="align-center">${row.expectedWorkdays}</td>
                                                            <td class="align-center">${row.presentDays}</td>
                                                            <td class="align-center">${row.absentDays}</td>
                                                            <td class="align-center">${row.lateDays}</td>
                                                            <td class="align-center">${row.earlyLeaveDays}</td>
                                                            <td class="align-center">${row.forgotCheckInDays}</td>
                                                            <td class="align-center">${row.forgotCheckOutDays}</td>
                                                            <td class="align-right bold">
                                                                <fmt:formatNumber value="${row.totalWorkHours}"
                                                                    pattern="#,##0.0" />
                                                            </td>
                                                            <td class="align-right bold">
                                                                <fmt:formatNumber value="${row.totalOvertimeHours}"
                                                                    pattern="#,##0.0" />
                                                            </td>
                                                            <td class="align-center">${row.leaveDays}</td>
                                                        </tr>
                                                    </c:forEach>
                                                    <c:if test="${empty reportRows}">
                                                        <tr>
                                                            <td colspan="14" class="align-center"
                                                                style="padding: 40px; color: var(--text-secondary);">
                                                                No employee data matches the selected filters.
                                                            </td>
                                                        </tr>
                                                    </c:if>
                                                </tbody>
                                            </table>
                                        </div>

                                        <c:if test="${empty selectedDeptId}">
                                            <!-- Chart container -->
                                            <div class="report-filter-card"
                                                style="margin-bottom: 24px; padding: 24px; border: 1px solid var(--border-color); background: var(--card-bg); border-radius: var(--radius);">
                                                <h3
                                                    style="margin-top: 0; margin-bottom: 24px; font-size: 16px; font-weight: 700; color: var(--text-primary);">
                                                    Attendance Overview by Department</h3>
                                                <div style="width: 100%; height: 350px; position: relative;">
                                                    <canvas id="deptAttendanceChart"></canvas>
                                                </div>
                                            </div>
                                        </c:if>


                                        <!-- Bottom Summaries and Highlights -->
                                        <c:if test="${not empty reportRows}">

                                            <div class="dashboard-grid">

                                                <!-- Stat 1: Total Work Hours vs Expected -->
                                                <div class="stat-card">
                                                    <div class="stat-header">
                                                        <span class="stat-title">Work Hours Performance</span>
                                                        <div class="stat-icon">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="18"
                                                                height="18" viewBox="0 0 24 24" fill="none"
                                                                stroke="currentColor" stroke-width="2">
                                                                <circle cx="12" cy="12" r="10"></circle>
                                                                <polyline points="12 6 12 12 16 14"></polyline>
                                                            </svg>
                                                        </div>
                                                    </div>
                                                    <div class="stat-value">
                                                        <c:set var="workPercent"
                                                            value="${totalExpectedWorkHours > 0 ? (totalActualWorkHours / totalExpectedWorkHours) * 100 : 0}" />
                                                        <fmt:formatNumber value="${workPercent}" pattern="#,##0.0" />%
                                                    </div>
                                                    <div class="stat-ratio">
                                                        <fmt:formatNumber value="${totalActualWorkHours}"
                                                            pattern="#,##0.0" /> /
                                                        <fmt:formatNumber value="${totalExpectedWorkHours}"
                                                            pattern="#,##0.0" /> work hours
                                                    </div>
                                                    <div class="progress-bar-container">
                                                        <div class="progress-bar-fill"
                                                            style="width: ${workPercent > 100 ? 100 : workPercent}%;">
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Stat 2: Total OT Hours vs Registered -->
                                                <div class="stat-card">
                                                    <div class="stat-header">
                                                        <span class="stat-title">Overtime Performance</span>
                                                        <div class="stat-icon"
                                                            style="background-color: #FEF3C7; color: #D97706;">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="18"
                                                                height="18" viewBox="0 0 24 24" fill="none"
                                                                stroke="currentColor" stroke-width="2">
                                                                <polygon
                                                                    points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2">
                                                                </polygon>
                                                            </svg>
                                                        </div>
                                                    </div>
                                                    <div class="stat-value">
                                                        <c:set var="otPercent"
                                                            value="${totalRegisteredOvertimeHours > 0 ? (totalActualOvertimeHours / totalRegisteredOvertimeHours) * 100 : 0.0}" />
                                                        <fmt:formatNumber value="${otPercent}"
                                                            pattern="#,##0.0" />%
                                                    </div>
                                                    <div class="stat-ratio">
                                                        <fmt:formatNumber value="${totalActualOvertimeHours}"
                                                            pattern="#,##0.0" /> /
                                                        <fmt:formatNumber value="${totalRegisteredOvertimeHours}"
                                                            pattern="#,##0.0" /> OT hours
                                                    </div>
                                                    <div class="progress-bar-container">
                                                        <div class="progress-bar-fill"
                                                            style="background-color: #D97706; width: ${otPercent > 100 ? 100 : otPercent}%;">
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Stat 3: Total Leave Days -->
                                                <div class="stat-card">
                                                    <div class="stat-header">
                                                        <span class="stat-title">Total Leave Days</span>
                                                        <div class="stat-icon"
                                                            style="background-color: #ECFDF5; color: #10B981;">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="18"
                                                                height="18" viewBox="0 0 24 24" fill="none"
                                                                stroke="currentColor" stroke-width="2">
                                                                <path
                                                                    d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z">
                                                                </path>
                                                                <polyline points="9 22 9 12 15 12 15 22"></polyline>
                                                            </svg>
                                                        </div>
                                                    </div>
                                                    <div class="stat-value" style="color: #10B981;">
                                                        <fmt:formatNumber value="${totalLeaveDays}" pattern="#,##0.0" /> days
                                                    </div>
                                                    <div class="stat-ratio">
                                                        Annual leave and sick leave
                                                    </div>
                                                </div>

                                                <!-- Stat 4: Total Absent Days -->
                                                <div class="stat-card">
                                                    <div class="stat-header">
                                                        <span class="stat-title">Total Absent Days</span>
                                                        <div class="stat-icon"
                                                            style="background-color: #FEF2F2; color: #EF4444;">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="18"
                                                                height="18" viewBox="0 0 24 24" fill="none"
                                                                stroke="currentColor" stroke-width="2">
                                                                <path d="M18.36 6.64a9 9 0 1 1-12.73 0"></path>
                                                                <line x1="12" y1="2" x2="12" y2="12"></line>
                                                            </svg>
                                                        </div>
                                                    </div>
                                                    <div class="stat-value" style="color: #EF4444;">
                                                        <fmt:formatNumber value="${totalAbsentDays}" pattern="#,##0" /> days
                                                    </div>
                                                    <div class="stat-ratio">
                                                        Unauthorized absence / unpaid leave
                                                    </div>
                                                </div>

                                            </div>

                                            <!-- Highlights Section -->
                                            <div class="dashboard-grid"
                                                style="grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));">

                                                <!-- Highlight: Hardest Working Employee -->
                                                <c:if test="${not empty hardestWorking}">
                                                    <div class="stat-card highlight-card">
                                                        <div class="stat-header">
                                                            <span class="stat-title" style="color: #1E3A8A;">Top Performing Employee</span>
                                                            <div class="stat-icon">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="18"
                                                                    height="18" viewBox="0 0 24 24" fill="none"
                                                                    stroke="#2563EB" stroke-width="2">
                                                                    <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"></path>
                                                                    <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"></path>
                                                                    <path d="M4 22h16"></path>
                                                                    <path
                                                                        d="M10 14.66V17c0 .55-.45 1-1 1H4v2h16v-2h-5c-.55 0-1-.45-1-1v-2.34">
                                                                    </path>
                                                                    <path
                                                                        d="M12 2a7.7 7.7 0 0 1 7.54 8H4.46A7.7 7.7 0 0 1 12 2z">
                                                                    </path>
                                                                </svg>
                                                            </div>
                                                        </div>
                                                        <div class="stat-value"
                                                            style="color: #1E3A8A; font-size: 24px; margin-top: 4px;">
                                                            ${hardestWorking.employeeName}
                                                        </div>
                                                        <div class="highlight-meta">
                                                            Employee ID: <span class="bold">${hardestWorking.employeeCode}</span> |
                                                            Department: <span class="bold">${hardestWorking.departmentName}</span>
                                                        </div>
                                                        <div class="stat-ratio"
                                                            style="color: #2563EB; font-weight: 600; margin-top: 8px;">
                                                            Total work + OT hours:
                                                            <fmt:formatNumber value="${hardestWorking.totalWorkHours + hardestWorking.totalOvertimeHours}" pattern="#,##0.0" /> hours
                                                        </div>
                                                    </div>
                                                </c:if>

                                                <!-- Highlight: Most Punctual Employee -->
                                                <c:if test="${not empty mostPunctual}">
                                                    <div class="stat-card highlight-card punctual">
                                                        <div class="stat-header">
                                                            <span class="stat-title" style="color: #064E3B;">Most Punctual Employee</span>
                                                            <div class="stat-icon">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="18"
                                                                    height="18" viewBox="0 0 24 24" fill="none"
                                                                    stroke="#059669" stroke-width="2">
                                                                    <circle cx="12" cy="12" r="10"></circle>
                                                                    <polyline points="12 6 12 12 16 14"></polyline>
                                                                </svg>
                                                            </div>
                                                        </div>
                                                        <div class="stat-value"
                                                            style="color: #064E3B; font-size: 24px; margin-top: 4px;">
                                                            ${mostPunctual.employeeName}
                                                        </div>
                                                        <div class="highlight-meta">
                                                            Employee ID: <span class="bold">${mostPunctual.employeeCode}</span> |
                                                            Department: <span class="bold">${mostPunctual.departmentName}</span>
                                                        </div>
                                                        <div class="stat-ratio"
                                                            style="color: #059669; font-weight: 600; margin-top: 8px;">
                                                            Late: ${mostPunctual.lateDays} times | Early leave: ${mostPunctual.earlyLeaveDays} times |
                                                            Missed check-in: ${mostPunctual.forgotCheckInDays} times
                                                        </div>
                                                    </div>
                                                </c:if>

                                            </div>

                                        </c:if>

                                    </c:otherwise>
                                </c:choose>

                            </div>
                        </div>
                    </div>

                    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
                    <script>
                        function exportToExcel() {
                            const form = document.getElementById("reportForm");
                            const originalAction = form.action;
                            const originalMethod = form.method;

                            form.action = "${ctx}/reports/attendance/export";
                            form.method = "GET";
                            form.submit();

                            form.action = originalAction;
                            form.method = originalMethod;
                        }

                        document.addEventListener("DOMContentLoaded", function () {
                            const periodTypeSelect = document.getElementById("periodType");
                            const monthGroup = document.getElementById("monthGroup");
                            const quarterGroup = document.getElementById("quarterGroup");

                            function updateFieldsVisibility() {
                                const val = periodTypeSelect.value;
                                if (val === "month") {
                                    monthGroup.style.display = "flex";
                                    quarterGroup.style.display = "none";
                                } else if (val === "quarter") {
                                    monthGroup.style.display = "none";
                                    quarterGroup.style.display = "flex";
                                } else if (val === "year") {
                                    monthGroup.style.display = "none";
                                    quarterGroup.style.display = "none";
                                }
                            }

                            periodTypeSelect.addEventListener("change", updateFieldsVisibility);
                            // Run once on load
                            updateFieldsVisibility();

                            <c:if test="${isGenerated && empty selectedDeptId}">
                                const reportData = [
                                <c:forEach var="row" items="${reportRows}" varStatus="status">
                                    {
                                        departmentName: "${fn:escapeXml(row.departmentName)}",
                                    lateDays: ${row.lateDays},
                                    earlyLeaveDays: ${row.earlyLeaveDays},
                                    absentDays: ${row.absentDays},
                                    leaveDays: ${row.leaveDays},
                                    totalOvertimeHours: ${row.totalOvertimeHours}
                }${!status.last ? ',' : ''}
                                </c:forEach>
                                ];

                                // Group and aggregate by department
                                const deptData = {};
                                reportData.forEach(row => {
                                    const dept = row.departmentName;
                                    if (!dept || dept === "N/A" || dept.trim() === "") return;
                                    if (!deptData[dept]) {
                                        deptData[dept] = {
                                            lateDays: 0,
                                            earlyLeaveDays: 0,
                                            absentDays: 0,
                                            leaveDays: 0,
                                            totalOvertimeHours: 0
                                        };
                                    }
                                    deptData[dept].lateDays += row.lateDays;
                                    deptData[dept].earlyLeaveDays += row.earlyLeaveDays;
                                    deptData[dept].absentDays += row.absentDays;
                                    deptData[dept].leaveDays += row.leaveDays;
                                    deptData[dept].totalOvertimeHours += row.totalOvertimeHours;
                                });

                                const labels = Object.keys(deptData);
                                const lateArrivals = [];
                                const earlyCheckouts = [];
                                const absences = [];
                                const leaveDays = [];
                                const overtimeHours = [];

            labels.forEach(dept => {
                                    lateArrivals.push(deptData[dept].lateDays);
                                earlyCheckouts.push(deptData[dept].earlyLeaveDays);
                                absences.push(deptData[dept].absentDays);
                                leaveDays.push(deptData[dept].leaveDays);
                                overtimeHours.push(deptData[dept].totalOvertimeHours);
            });

                                const ctxChart = document.getElementById('deptAttendanceChart').getContext('2d');
                                new Chart(ctxChart, {
                                    type: 'bar',
                                data: {
                                    labels: labels,
                                datasets: [
                                {
                                    label: 'Late Arrivals',
                                data: lateArrivals,
                                backgroundColor: '#3b82f6', // blue
                                borderColor: '#3b82f6',
                                borderWidth: 1
                        },
                                {
                                    label: 'Early Check-outs',
                                data: earlyCheckouts,
                                backgroundColor: '#f59e0b', // orange
                                borderColor: '#f59e0b',
                                borderWidth: 1
                        },
                                {
                                    label: 'Absences',
                                data: absences,
                                backgroundColor: '#ef4444', // red
                                borderColor: '#ef4444',
                                borderWidth: 1
                        },
                                {
                                    label: 'Leave Days',
                                data: leaveDays,
                                backgroundColor: '#10b981', // green
                                borderColor: '#10b981',
                                borderWidth: 1
                        },
                                {
                                    label: 'Overtime (Hours)',
                                data: overtimeHours,
                                backgroundColor: '#8b5cf6', // purple
                                borderColor: '#8b5cf6',
                                borderWidth: 1
                        }
                                ]
                },
                                options: {
                                    responsive: true,
                                maintainAspectRatio: false,
                                scales: {
                                    y: {
                                    beginAtZero: true
                        }
                    },
                                plugins: {
                                    legend: {
                                    position: 'bottom',
                                labels: {
                                    boxWidth: 12,
                                padding: 20
                            }
                        }
                    }
                }
            });
                            </c:if>

                            // Client-side search, department filtering, and sorting
                            const searchInput = document.getElementById("localSearchName");
                            const deptSelect = document.getElementById("localFilterDept");
                            const sortSelect = document.getElementById("localSortBy");
                            const sortOrderSelect = document.getElementById("localSortOrder");
                            const tableBody = document.querySelector(".table-wrapper tbody");

                            if (searchInput && deptSelect && sortSelect && sortOrderSelect && tableBody) {
                                function filterTable() {
                                    const query = searchInput.value.toLowerCase().trim();
                                    const selectedDept = deptSelect.value;

                                    tableBody.querySelectorAll(".employee-row").forEach(row => {
                                        const empName = row.dataset.name || "";
                                        const empDept = row.dataset.dept || "";
                                        const matchesName = empName.includes(query);
                                        const matchesDept = selectedDept === "all" || empDept === selectedDept;

                                        row.style.display = matchesName && matchesDept ? "" : "none";
                                    });
                                }

                                function sortTable() {
                                    const sortKey = sortSelect.value;
                                    const direction = sortOrderSelect.value === "asc" ? 1 : -1;
                                    const rows = Array.from(tableBody.querySelectorAll(".employee-row"));

                                    rows.sort((rowA, rowB) => {
                                        const valueA = Number.parseFloat(rowA.dataset[sortKey]) || 0;
                                        const valueB = Number.parseFloat(rowB.dataset[sortKey]) || 0;
                                        const difference = valueA - valueB;

                                        if (difference !== 0) {
                                            return difference * direction;
                                        }

                                        return (rowA.dataset.name || "").localeCompare(rowB.dataset.name || "");
                                    });

                                    rows.forEach(row => tableBody.appendChild(row));
                                }

                                searchInput.addEventListener("input", filterTable);
                                deptSelect.addEventListener("change", filterTable);
                                sortSelect.addEventListener("change", sortTable);
                                sortOrderSelect.addEventListener("change", sortTable);
                                sortTable();
                            }
                        });
                    </script>

                </body>

                </html>
