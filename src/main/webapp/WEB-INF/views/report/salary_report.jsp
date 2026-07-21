<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Báo Cáo Lương | HRM</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 20px;
            align-items: flex-end;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .form-group label {
            font-weight: 600;
            font-size: 13px;
            color: var(--text-secondary);
        }

        .form-group select {
            padding: 10px 14px;
            border-radius: var(--radius-sm);
            border: 1px solid var(--border-color);
            background-color: var(--white);
            color: var(--text-primary);
            font-size: 14px;
            font-family: inherit;
            outline: none;
            width: 100%;
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
            height: 42px;
        }

        .salary-tabs {
            display: flex;
            gap: 28px;
            border-bottom: 1px solid var(--border-color);
            margin-bottom: 18px;
            padding: 18px 24px 0;
            overflow-x: auto;
        }

        .salary-tab {
            appearance: none;
            background: transparent;
            border: 0;
            border-bottom: 2px solid transparent;
            color: var(--text-secondary);
            cursor: pointer;
            font-weight: 700;
            padding: 0 0 12px;
            white-space: nowrap;
        }

        .salary-tab.active {
            border-color: #22c55e;
            color: #16a34a;
        }

        .summary-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 16px;
            margin-bottom: 24px;
        }

        .summary-card {
            background: var(--white);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-sm);
            box-shadow: var(--shadow-sm);
            padding: 18px;
        }

        .summary-title {
            color: var(--text-secondary);
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .summary-value {
            color: var(--text-primary);
            font-size: 20px;
            font-weight: 800;
            margin-top: 10px;
        }

        .report-panel {
            background: var(--white);
            border: 1px solid var(--border-color);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 24px;
            overflow: hidden;
        }

        .chart-wrap {
            padding: 18px 24px 8px;
        }

        .chart-title {
            color: var(--text-primary);
            font-size: 16px;
            font-weight: 800;
            margin: 0 0 14px;
        }

        .analytics-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(420px, 1fr));
            gap: 24px;
            margin-bottom: 24px;
        }

        .analytics-grid .report-panel { margin-bottom: 0; }

        @media (max-width: 640px) {
            .analytics-grid { grid-template-columns: 1fr; }
        }

        .chart-scroll {
            width: 100%;
            overflow-x: auto;
            overflow-y: hidden;
            padding-bottom: 8px;
        }

        .chart-inner {
            height: 320px;
            min-width: 100%;
        }

        .table-wrap {
            overflow-x: auto;
        }

        .salary-table {
            width: 100%;
            border-collapse: collapse;
            min-width: 980px;
        }

        .salary-table th,
        .salary-table td {
            border: 1px solid var(--border-color);
            padding: 13px 14px;
            text-align: left;
            font-size: 13px;
        }

        .salary-table th {
            background: #f8fafc;
            color: var(--text-primary);
            font-weight: 700;
        }

        .salary-table td.number,
        .salary-table th.number {
            text-align: right;
        }

        .empty-state {
            text-align: center;
            padding: 48px 24px;
            background: var(--white);
            border-radius: var(--radius);
            border: 1px solid var(--border-color);
            color: var(--text-secondary);
        }
    </style>
</head>
<body class="dashboard-body">
<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Báo Cáo Lương</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <div class="report-filter-card">
                <form action="${ctx}/reports/salary" method="GET" id="salaryReportForm">
                    <input type="hidden" name="action" value="generate">
                    <input type="hidden" name="groupBy" id="groupBy" value="${groupBy}">

                    <div class="filter-form-grid">
                        <div class="form-group">
                            <label for="periodType">Kỳ báo cáo</label>
                            <select name="periodType" id="periodType">
                                <option value="month" ${periodType == 'month' ? 'selected' : ''}>Theo Tháng</option>
                                <option value="quarter" ${periodType == 'quarter' ? 'selected' : ''}>Theo Quý</option>
                                <option value="year" ${periodType == 'year' ? 'selected' : ''}>Theo Năm</option>
                            </select>
                        </div>

                        <div class="form-group" id="monthGroup">
                            <label for="month">Tháng</label>
                            <select name="month" id="month">
                                <c:forEach var="m" begin="1" end="12">
                                    <option value="${m}" ${selectedMonth == m ? 'selected' : ''}>Tháng ${m}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group" id="quarterGroup">
                            <label for="quarter">Quý</label>
                            <select name="quarter" id="quarter">
                                <c:forEach var="q" begin="1" end="4">
                                    <option value="${q}" ${selectedQuarter == q ? 'selected' : ''}>Quý ${q}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="year">Năm</label>
                            <select name="year" id="year">
                                <c:forEach var="y" items="${years}">
                                    <option value="${y}" ${selectedYear == y ? 'selected' : ''}>${y}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="salarySort">Sắp xếp theo thực lĩnh</label>
                            <select name="salarySort" id="salarySort">
                                <option value="default" ${salarySort == 'default' ? 'selected' : ''}>Mặc định</option>
                                <option value="salaryAsc" ${salarySort == 'salaryAsc' ? 'selected' : ''}>Thấp → cao</option>
                                <option value="salaryDesc" ${salarySort == 'salaryDesc' ? 'selected' : ''}>Cao → thấp</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <button type="submit" class="btn-generate">Tạo báo cáo</button>
                        </div>
                    </div>
                </form>
            </div>

            <c:choose>
                <c:when test="${not isGenerated}">
                    <div class="empty-state">
                        <h3>Chọn bộ lọc và click "Tạo báo cáo" để hiển thị dữ liệu lương</h3>
                        <p>Báo cáo hỗ trợ xem theo nhân viên, vị trí công việc hoặc phòng ban.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="summary-grid">
                        <div class="summary-card">
                            <div class="summary-title">Tổng nhân sự</div>
                            <div class="summary-value">${totalEmployees}</div>
                        </div>
                        <div class="summary-card">
                            <div class="summary-title">Lương ngày công</div>
                            <div class="summary-value"><fmt:formatNumber value="${totalWorkdayIncome}" type="number" maxFractionDigits="0"/></div>
                        </div>
                        <div class="summary-card">
                            <div class="summary-title">Lương làm thêm</div>
                            <div class="summary-value"><fmt:formatNumber value="${totalOvertimeIncome}" type="number" maxFractionDigits="0"/></div>
                        </div>
                        <div class="summary-card">
                            <div class="summary-title">Tổng thực lĩnh</div>
                            <div class="summary-value"><fmt:formatNumber value="${totalIncome}" type="number" maxFractionDigits="0"/></div>
                        </div>
                    </div>

                    <div class="analytics-grid">
                        <c:if test="${periodType != 'month'}">
                            <div class="report-panel">
                                <div class="chart-wrap">
                                    <h2 class="chart-title">Tổng lương toàn công ty theo tháng</h2>
                                    <div class="chart-inner"><canvas id="companySalaryTrendChart"></canvas></div>
                                    <div id="monthlySalaryData" hidden>
                                        <c:forEach var="item" items="${monthlySalaryTotals}">
                                            <span data-label="Tháng ${item.month}/${item.year}" data-value="${item.totalNetPay}"></span>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <div class="report-panel">
                            <div class="chart-wrap">
                                <h2 class="chart-title">Cơ cấu lương giữa các phòng ban</h2>
                                <div class="chart-inner"><canvas id="departmentSalaryChart"></canvas></div>
                                <div id="departmentSalaryData" hidden>
                                    <c:forEach var="item" items="${departmentRows}">
                                        <span data-label="${item.groupName}"
                                              data-workday="${item.workdayIncome}"
                                              data-bonus="${item.productIncome}"
                                              data-overtime="${item.overtimeIncome}"></span>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="report-panel">
                        <div class="salary-tabs">
                            <button type="submit" form="salaryReportForm" class="salary-tab ${groupBy == 'employee' ? 'active' : ''}" onclick="document.getElementById('groupBy').value='employee'">Nhân viên</button>
                            <button type="submit" form="salaryReportForm" class="salary-tab ${groupBy == 'position' ? 'active' : ''}" onclick="document.getElementById('groupBy').value='position'">Vị trí công việc</button>
                            <button type="submit" form="salaryReportForm" class="salary-tab ${groupBy == 'department' ? 'active' : ''}" onclick="document.getElementById('groupBy').value='department'">Phòng ban</button>
                        </div>

                        <div class="chart-wrap">
                            <div class="chart-scroll">
                                <div class="chart-inner" id="salaryChartInner">
                                    <canvas id="salaryChart"></canvas>
                                </div>
                            </div>
                        </div>

                        <div class="table-wrap">
                            <table class="salary-table" id="salaryTable">
                                <thead>
                                <tr>
                                    <th rowspan="2">STT</th>
                                    <th rowspan="2">
                                        <c:choose>
                                            <c:when test="${groupBy == 'employee'}">Nhân viên</c:when>
                                            <c:when test="${groupBy == 'department'}">Phòng ban</c:when>
                                            <c:otherwise>Vị trí công việc</c:otherwise>
                                        </c:choose>
                                    </th>
                                    <th rowspan="2">Đơn vị công tác</th>
                                    <c:if test="${groupBy != 'employee'}">
                                        <th rowspan="2" class="number">Số lượng nhân sự</th>
                                    </c:if>
                                    <th colspan="4" class="number">Thu nhập</th>
                                </tr>
                                <tr>
                                    <th class="number">Lương ngày công</th>
                                    <th class="number">Lương thưởng</th>
                                    <th class="number">Lương làm thêm</th>
                                    <th class="number">Thực lĩnh</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="row" items="${reportRows}" varStatus="status">
                                    <tr data-label="${row.groupName}" data-value="${row.totalIncome}">
                                        <td>${status.index + 1}</td>
                                        <td><c:out value="${row.groupName}"/></td>
                                        <td><c:out value="${row.departmentName}"/></td>
                                        <c:if test="${groupBy != 'employee'}">
                                            <td class="number">${row.employeeCount}</td>
                                        </c:if>
                                        <td class="number"><fmt:formatNumber value="${row.workdayIncome}" type="number" maxFractionDigits="0"/></td>
                                        <td class="number"><fmt:formatNumber value="${row.productIncome}" type="number" maxFractionDigits="0"/></td>
                                        <td class="number"><fmt:formatNumber value="${row.overtimeIncome}" type="number" maxFractionDigits="0"/></td>
                                        <td class="number"><fmt:formatNumber value="${row.totalIncome}" type="number" maxFractionDigits="0"/></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty reportRows}">
                                    <tr>
                                        <td colspan="${groupBy == 'employee' ? 7 : 8}" style="text-align: center; color: var(--text-secondary);">Không có dữ liệu lương trong kỳ đã chọn.</td>
                                    </tr>
                                </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
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
            monthGroup.style.display = val === "month" ? "flex" : "none";
            quarterGroup.style.display = val === "quarter" ? "flex" : "none";
        }

        periodTypeSelect.addEventListener("change", updateFieldsVisibility);
        updateFieldsVisibility();

        const currency = value => new Intl.NumberFormat("vi-VN").format(value) + " VND";
        const commonMoneyAxis = {
            beginAtZero: true,
            grid: { color: "#eef2f7" },
            ticks: { callback: value => new Intl.NumberFormat("vi-VN").format(value) }
        };

        const monthlyData = Array.from(document.querySelectorAll("#monthlySalaryData span"));
        const trendCanvas = document.getElementById("companySalaryTrendChart");
        if (trendCanvas && monthlyData.length && window.Chart) {
            new Chart(trendCanvas.getContext("2d"), {
                type: "line",
                data: {
                    labels: monthlyData.map(item => item.dataset.label),
                    datasets: [{
                        label: "Tổng thực lĩnh",
                        data: monthlyData.map(item => Number(item.dataset.value || 0)),
                        borderColor: "#16a34a",
                        backgroundColor: "rgba(34, 197, 94, 0.14)",
                        pointBackgroundColor: "#16a34a",
                        pointRadius: 4,
                        borderWidth: 3,
                        tension: 0.28,
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: { y: commonMoneyAxis },
                    plugins: { tooltip: { callbacks: { label: context => currency(context.parsed.y) } } }
                }
            });
        }

        const departmentData = Array.from(document.querySelectorAll("#departmentSalaryData span"));
        const departmentCanvas = document.getElementById("departmentSalaryChart");
        if (departmentCanvas && departmentData.length && window.Chart) {
            new Chart(departmentCanvas.getContext("2d"), {
                type: "bar",
                data: {
                    labels: departmentData.map(item => item.dataset.label),
                    datasets: [
                        { label: "Lương ngày công", data: departmentData.map(item => Number(item.dataset.workday || 0)), backgroundColor: "#86efac" },
                        { label: "Lương thưởng", data: departmentData.map(item => Number(item.dataset.bonus || 0)), backgroundColor: "#facc15" },
                        { label: "Lương làm thêm", data: departmentData.map(item => Number(item.dataset.overtime || 0)), backgroundColor: "#0f766e" }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    indexAxis: "y",
                    scales: {
                        x: { ...commonMoneyAxis, stacked: true },
                        y: { stacked: true, grid: { display: false } }
                    },
                    plugins: {
                        tooltip: { callbacks: { label: context => context.dataset.label + ": " + currency(context.parsed.x) } }
                    }
                }
            });
        }

        const rows = Array.from(document.querySelectorAll("#salaryTable tbody tr[data-label]"));
        if (rows.length > 0 && window.Chart) {
            const labels = rows.map(row => row.dataset.label);
            const values = rows.map(row => Number(row.dataset.value || 0));
            const groupBy = "${groupBy}";
            const chartInner = document.getElementById("salaryChartInner");

            if (groupBy === "employee") {
                chartInner.style.width = Math.max(900, rows.length * 130) + "px";
            } else {
                chartInner.style.width = "100%";
            }

            new Chart(document.getElementById("salaryChart").getContext("2d"), {
                type: "bar",
                data: {
                    labels,
                    datasets: [{
                        label: "Tổng thực lĩnh",
                        data: values,
                        backgroundColor: "#4ade80",
                        borderRadius: 4,
                        barPercentage: 0.45
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        x: {
                            grid: { display: false },
                            afterFit: function(scaleInstance) {
                                scaleInstance.height = groupBy === "employee" ? 80 : 58;
                            },
                            ticks: {
                                maxRotation: 0,
                                minRotation: 0,
                                callback: function(value) {
                                    const label = this.getLabelForValue(value);
                                    const maxLength = groupBy === "employee" ? 18 : 14;
                                    if (label.length <= maxLength) return label;
                                    const words = label.split(" ");
                                    const lines = [];
                                    let line = "";
                                    words.forEach(function(word) {
                                        if ((line + " " + word).trim().length > maxLength) {
                                            if (line) lines.push(line.trim());
                                            line = word;
                                        } else {
                                            line = (line + " " + word).trim();
                                        }
                                    });
                                    if (line) lines.push(line);
                                    return lines.slice(0, 3);
                                }
                            }
                        },
                        y: {
                            beginAtZero: true,
                            grid: { color: "#eef2f7" },
                            ticks: {
                                callback: function(value) {
                                    return new Intl.NumberFormat("vi-VN").format(value);
                                }
                            }
                        }
                    },
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return new Intl.NumberFormat("vi-VN").format(context.parsed.y) + " VND";
                                }
                            }
                        }
                    }
                }
            });
        }
    });
</script>
</body>
</html>
