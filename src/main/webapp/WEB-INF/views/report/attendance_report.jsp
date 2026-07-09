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

        .form-group select, .form-group input {
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

        .form-group select:focus, .form-group input:focus {
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
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Báo Cáo Chuyên Cần</h1>
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
                            <label for="departmentId">Phòng ban</label>
                            <c:choose>
                                <c:when test="${isRestricted}">
                                    <select name="departmentId" id="departmentId" readonly>
                                        <c:forEach var="dept" items="${departments}">
                                            <option value="${dept.id}" selected>${dept.name}</option>
                                        </c:forEach>
                                    </select>
                                </c:when>
                                <c:otherwise>
                                    <select name="departmentId" id="departmentId">
                                        <option value="all" ${empty selectedDeptId ? 'selected' : ''}>Tất cả phòng ban</option>
                                        <c:forEach var="dept" items="${departments}">
                                            <option value="${dept.id}" ${selectedDeptId == dept.id ? 'selected' : ''}>${dept.name}</option>
                                        </c:forEach>
                                    </select>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Period Type -->
                        <div class="form-group">
                            <label for="periodType">Kỳ báo cáo</label>
                            <select name="periodType" id="periodType">
                                <option value="month" ${periodType == 'month' ? 'selected' : ''}>Theo Tháng</option>
                                <option value="quarter" ${periodType == 'quarter' ? 'selected' : ''}>Theo Quý</option>
                                <option value="year" ${periodType == 'year' ? 'selected' : ''}>Theo Năm</option>
                            </select>
                        </div>

                        <!-- Monthly Input Option -->
                        <div class="form-group" id="monthGroup">
                            <label for="month">Tháng</label>
                            <select name="month" id="month">
                                <c:forEach var="m" begin="1" end="12">
                                    <option value="${m}" ${selectedMonth == m ? 'selected' : ''}>Tháng ${m}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Quarterly Input Option -->
                        <div class="form-group" id="quarterGroup">
                            <label for="quarter">Quý</label>
                            <select name="quarter" id="quarter">
                                <c:forEach var="q" begin="1" end="4">
                                    <option value="${q}" ${selectedQuarter == q ? 'selected' : ''}>Quý ${q}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Year (needed for month, quarter, and year periods) -->
                        <div class="form-group" id="yearGroup">
                            <label for="year">Năm</label>
                            <select name="year" id="year">
                                <c:forEach var="y" items="${years}">
                                    <option value="${y}" ${selectedYear == y ? 'selected' : ''}>${y}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Action Button -->
                        <div class="form-group">
                            <button type="submit" class="btn-generate">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                    <line x1="18" y1="20" x2="18" y2="10"></line>
                                    <line x1="12" y1="20" x2="12" y2="4"></line>
                                    <line x1="6" y1="20" x2="6" y2="14"></line>
                                </svg>
                                Tạo báo cáo
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
                            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                <polyline points="14 2 14 8 20 8"></polyline>
                                <line x1="16" y1="13" x2="8" y2="13"></line>
                                <line x1="16" y1="17" x2="8" y2="17"></line>
                                <polyline points="10 9 9 9 8 9"></polyline>
                            </svg>
                        </div>
                        <h3>Chọn bộ lọc và click "Tạo báo cáo" để hiển thị dữ liệu</h3>
                        <p>Bạn có thể lọc báo cáo chuyên cần theo Phòng ban và chọn kỳ báo cáo mong muốn.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    
                    <!-- Statistics table -->
                    <div class="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th class="align-center">Mã NV</th>
                                    <th>Họ tên</th>
                                    <th>Chức vụ</th>
                                    <th>Phòng ban</th>
                                    <th class="align-center">Tổng ngày công</th>
                                    <th class="align-center">Ngày có mặt</th>
                                    <th class="align-center">Ngày vắng</th>
                                    <th class="align-center">Đi muộn</th>
                                    <th class="align-center">Về sớm</th>
                                    <th class="align-center">Quên check-in</th>
                                    <th class="align-center">Quên check-out</th>
                                    <th class="align-right">Tổng giờ công</th>
                                    <th class="align-right">Tổng giờ OT</th>
                                    <th class="align-center">Tổng Phép</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="row" items="${reportRows}">
                                    <tr>
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
                                            <fmt:formatNumber value="${row.totalWorkHours}" pattern="#,##0.0"/>
                                        </td>
                                        <td class="align-right bold">
                                            <fmt:formatNumber value="${row.totalOvertimeHours}" pattern="#,##0.0"/>
                                        </td>
                                        <td class="align-center">${row.leaveDays}</td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty reportRows}">
                                    <tr>
                                        <td colspan="14" class="align-center" style="padding: 40px; color: var(--text-secondary);">
                                            Không tìm thấy dữ liệu nhân viên nào phù hợp với bộ lọc đã chọn.
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>

                    <!-- Bottom Summaries and Highlights -->
                    <c:if test="${not empty reportRows}">
                        
                        <div class="dashboard-grid">
                            
                            <!-- Stat 1: Total Work Hours vs Expected -->
                            <div class="stat-card">
                                <div class="stat-header">
                                    <span class="stat-title">Hiệu suất giờ công</span>
                                    <div class="stat-icon">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <circle cx="12" cy="12" r="10"></circle>
                                            <polyline points="12 6 12 12 16 14"></polyline>
                                        </svg>
                                    </div>
                                </div>
                                <div class="stat-value">
                                    <c:set var="workPercent" value="${totalExpectedWorkHours > 0 ? (totalActualWorkHours / totalExpectedWorkHours) * 100 : 0}" />
                                    <fmt:formatNumber value="${workPercent}" pattern="#,##0.0"/>%
                                </div>
                                <div class="stat-ratio">
                                    <fmt:formatNumber value="${totalActualWorkHours}" pattern="#,##0.0"/> / 
                                    <fmt:formatNumber value="${totalExpectedWorkHours}" pattern="#,##0.0"/> giờ công
                                </div>
                                <div class="progress-bar-container">
                                    <div class="progress-bar-fill" style="width: ${workPercent > 100 ? 100 : workPercent}%;"></div>
                                </div>
                            </div>

                            <!-- Stat 2: Total OT Hours vs Registered -->
                            <div class="stat-card">
                                <div class="stat-header">
                                    <span class="stat-title">Hiệu suất làm thêm (OT)</span>
                                    <div class="stat-icon" style="background-color: #FEF3C7; color: #D97706;">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                                        </svg>
                                    </div>
                                </div>
                                <div class="stat-value">
                                    <c:set var="otPercent" value="${totalRegisteredOvertimeHours > 0 ? (totalActualOvertimeHours / totalRegisteredOvertimeHours) * 100 : 100.0}" />
                                    <c:choose>
                                        <c:when test="${totalRegisteredOvertimeHours > 0}">
                                            <fmt:formatNumber value="${otPercent}" pattern="#,##0.0"/>%
                                        </c:when>
                                        <c:otherwise>100%</c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="stat-ratio">
                                    <fmt:formatNumber value="${totalActualOvertimeHours}" pattern="#,##0.0"/> / 
                                    <fmt:formatNumber value="${totalRegisteredOvertimeHours}" pattern="#,##0.0"/> giờ OT
                                </div>
                                <div class="progress-bar-container">
                                    <div class="progress-bar-fill" style="background-color: #D97706; width: ${otPercent > 100 ? 100 : otPercent}%;"></div>
                                </div>
                            </div>

                            <!-- Stat 3: Total Leave Days -->
                            <div class="stat-card">
                                <div class="stat-header">
                                    <span class="stat-title">Tổng ngày nghỉ phép</span>
                                    <div class="stat-icon" style="background-color: #ECFDF5; color: #10B981;">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                                            <polyline points="9 22 9 12 15 12 15 22"></polyline>
                                        </svg>
                                    </div>
                                </div>
                                <div class="stat-value" style="color: #10B981;">
                                    <fmt:formatNumber value="${totalLeaveDays}" pattern="#,##0.0"/> ngày
                                </div>
                                <div class="stat-ratio">
                                    Nghỉ phép thường và nghỉ ốm
                                </div>
                            </div>

                            <!-- Stat 4: Total Absent Days -->
                            <div class="stat-card">
                                <div class="stat-header">
                                    <span class="stat-title">Tổng ngày vắng mặt</span>
                                    <div class="stat-icon" style="background-color: #FEF2F2; color: #EF4444;">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M18.36 6.64a9 9 0 1 1-12.73 0"></path>
                                            <line x1="12" y1="2" x2="12" y2="12"></line>
                                        </svg>
                                    </div>
                                </div>
                                <div class="stat-value" style="color: #EF4444;">
                                    <fmt:formatNumber value="${totalAbsentDays}" pattern="#,##0"/> ngày
                                </div>
                                <div class="stat-ratio">
                                    Vắng mặt không phép / nghỉ không lương
                                </div>
                            </div>

                        </div>

                        <!-- Highlights Section -->
                        <div class="dashboard-grid" style="grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));">
                            
                            <!-- Highlight: Hardest Working Employee -->
                            <c:if test="${not empty hardestWorking}">
                                <div class="stat-card highlight-card">
                                    <div class="stat-header">
                                        <span class="stat-title" style="color: #1E3A8A;">🏆 Nhân viên chăm chỉ nhất</span>
                                        <div class="stat-icon">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2">
                                                <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"></path>
                                                <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"></path>
                                                <path d="M4 22h16"></path>
                                                <path d="M10 14.66V17c0 .55-.45 1-1 1H4v2h16v-2h-5c-.55 0-1-.45-1-1v-2.34"></path>
                                                <path d="M12 2a7.7 7.7 0 0 1 7.54 8H4.46A7.7 7.7 0 0 1 12 2z"></path>
                                            </svg>
                                        </div>
                                    </div>
                                    <div class="stat-value" style="color: #1E3A8A; font-size: 24px; margin-top: 4px;">
                                        ${hardestWorking.employeeName}
                                    </div>
                                    <div class="highlight-meta">
                                        Mã NV: <span class="bold">${hardestWorking.employeeCode}</span> | Phòng ban: <span class="bold">${hardestWorking.departmentName}</span>
                                    </div>
                                    <div class="stat-ratio" style="color: #2563EB; font-weight: 600; margin-top: 8px;">
                                        Tổng giờ làm + OT: 
                                        <fmt:formatNumber value="${hardestWorking.totalWorkHours + hardestWorking.totalOvertimeHours}" pattern="#,##0.0"/> giờ
                                    </div>
                                </div>
                            </c:if>

                            <!-- Highlight: Most Punctual Employee -->
                            <c:if test="${not empty mostPunctual}">
                                <div class="stat-card highlight-card punctual">
                                    <div class="stat-header">
                                        <span class="stat-title" style="color: #064E3B;">⏱️ Nhân viên đúng giờ nhất</span>
                                        <div class="stat-icon">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#059669" stroke-width="2">
                                                <circle cx="12" cy="12" r="10"></circle>
                                                <polyline points="12 6 12 12 16 14"></polyline>
                                            </svg>
                                        </div>
                                    </div>
                                    <div class="stat-value" style="color: #064E3B; font-size: 24px; margin-top: 4px;">
                                        ${mostPunctual.employeeName}
                                    </div>
                                    <div class="highlight-meta">
                                        Mã NV: <span class="bold">${mostPunctual.employeeCode}</span> | Phòng ban: <span class="bold">${mostPunctual.departmentName}</span>
                                    </div>
                                    <div class="stat-ratio" style="color: #059669; font-weight: 600; margin-top: 8px;">
                                        Đi muộn: ${mostPunctual.lateDays} lần | Về sớm: ${mostPunctual.earlyLeaveDays} lần | Quên check-in: ${mostPunctual.forgotCheckInDays} lần
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

<script>
    document.addEventListener("DOMContentLoaded", function() {
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
    });
</script>

</body>
</html>
