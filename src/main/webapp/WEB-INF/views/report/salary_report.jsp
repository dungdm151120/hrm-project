<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payroll Report | HRM</title>
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
                <h1 class="header-title">Payroll Report</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <div class="report-filter-card">
                <form action="${ctx}/reports/salary" method="GET" id="salaryReportForm">
                    <input type="hidden" name="action" value="generate">
                    <input type="hidden" name="groupBy" id="groupBy" value="${groupBy}">

                    <div class="filter-form-grid">
                        <div class="form-group">
                            <label for="periodType">Reporting Period</label>
                            <select name="periodType" id="periodType">
                                <option value="month" ${periodType == 'month' ? 'selected' : ''}>Monthly</option>
                                <option value="quarter" ${periodType == 'quarter' ? 'selected' : ''}>Quarterly</option>
                                <option value="year" ${periodType == 'year' ? 'selected' : ''}>Yearly</option>
                            </select>
                        </div>

                        <div class="form-group" id="monthGroup">
                            <label for="month">Month</label>
                            <select name="month" id="month">
                                <c:forEach var="m" begin="1" end="12">
                                    <option value="${m}" ${selectedMonth == m ? 'selected' : ''}>Month ${m}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group" id="quarterGroup">
                            <label for="quarter">Quarter</label>
                            <select name="quarter" id="quarter">
                                <c:forEach var="q" begin="1" end="4">
                                    <option value="${q}" ${selectedQuarter == q ? 'selected' : ''}>Quarter ${q}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="year">Year</label>
                            <select name="year" id="year">
                                <c:forEach var="y" items="${years}">
                                    <option value="${y}" ${selectedYear == y ? 'selected' : ''}>${y}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="salarySort">Sort by Net Pay</label>
                            <select name="salarySort" id="salarySort">
                                <option value="default" ${salarySort == 'default' ? 'selected' : ''}>Default</option>
                                <option value="salaryAsc" ${salarySort == 'salaryAsc' ? 'selected' : ''}>Low to High</option>
                                <option value="salaryDesc" ${salarySort == 'salaryDesc' ? 'selected' : ''}>High to Low</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <button type="submit" class="btn-generate">Generate Report</button>
                        </div>
                    </div>
                </form>
            </div>

            <c:choose>
                <c:when test="${not isGenerated}">
                    <div class="empty-state">
                        <h3>Select filters and click "Generate Report" to display payroll data.</h3>
                        <p>The report can be grouped by employee, job position, or department.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="summary-grid">
                        <div class="summary-card">
                            <div class="summary-title">Total Employees</div>
                            <div class="summary-value">${totalEmployees}</div>
                        </div>
                        <div class="summary-card">
                            <div class="summary-title">Bonus / Allowance</div>
                            <div class="summary-value"><fmt:formatNumber value="${totalProductIncome}" type="number" maxFractionDigits="0"/></div>
                        </div>
                        <div class="summary-card">
                            <div class="summary-title">Overtime Pay</div>
                            <div class="summary-value"><fmt:formatNumber value="${totalOvertimeIncome}" type="number" maxFractionDigits="0"/></div>
                        </div>
                        <div class="summary-card">
                            <div class="summary-title">Sick Leave Pay</div>
                            <div class="summary-value"><fmt:formatNumber value="${totalSickLeaveIncome}" type="number" maxFractionDigits="0"/></div>
                        </div>
                        <div class="summary-card">
                            <div class="summary-title">Total Gross Pay</div>
                            <div class="summary-value"><fmt:formatNumber value="${totalGrossIncome}" type="number" maxFractionDigits="0"/></div>
                        </div>
                        <div class="summary-card">
                            <div class="summary-title">Total Net Pay</div>
                            <div class="summary-value"><fmt:formatNumber value="${totalIncome}" type="number" maxFractionDigits="0"/></div>
                        </div>
                    </div>

                    <div class="analytics-grid">
                        <c:if test="${periodType != 'month'}">
                            <div class="report-panel">
                                <div class="chart-wrap">
                                    <h2 class="chart-title">Company-wide Net Pay by Month</h2>
                                    <div class="chart-inner"><canvas id="companySalaryTrendChart"></canvas></div>
                                    <div id="monthlySalaryData" hidden>
                                        <c:forEach var="item" items="${monthlySalaryTotals}">
                                            <span data-label="Month ${item.month}/${item.year}" data-value="${item.totalNetPay}"></span>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <div class="report-panel">
                            <div class="chart-wrap">
                                <h2 class="chart-title">Net Pay by Department</h2>
                                <div class="chart-inner"><canvas id="departmentSalaryChart"></canvas></div>
                                <div id="departmentSalaryData" hidden>
                                    <c:forEach var="item" items="${departmentRows}">
                                        <span data-label="${item.groupName}"
                                              data-net-pay="${item.totalIncome}"></span>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="report-panel">
                        <div class="salary-tabs">
                            <button type="submit" form="salaryReportForm" class="salary-tab ${groupBy == 'employee' ? 'active' : ''}" onclick="document.getElementById('groupBy').value='employee'">Employees</button>
                            <button type="submit" form="salaryReportForm" class="salary-tab ${groupBy == 'position' ? 'active' : ''}" onclick="document.getElementById('groupBy').value='position'">Job Positions</button>
                            <button type="submit" form="salaryReportForm" class="salary-tab ${groupBy == 'department' ? 'active' : ''}" onclick="document.getElementById('groupBy').value='department'">Departments</button>
                        </div>

                        <div class="table-wrap">
                            <table class="salary-table" id="salaryTable">
                                <thead>
                                <tr>
                                    <th rowspan="2">No.</th>
                                    <th rowspan="2">
                                        <c:choose>
                                            <c:when test="${groupBy == 'employee'}">Employee</c:when>
                                            <c:when test="${groupBy == 'department'}">Department</c:when>
                                            <c:otherwise>Job Position</c:otherwise>
                                        </c:choose>
                                    </th>
                                    <th rowspan="2">Department</th>
                                    <c:if test="${groupBy != 'employee'}">
                                        <th rowspan="2" class="number">Employee Count</th>
                                    </c:if>
                                    <th colspan="5" class="number">Earnings</th>
                                </tr>
                                <tr>
                                    <th class="number">Bonus / Allowance</th>
                                    <th class="number">Overtime Pay</th>
                                    <th class="number">Sick Leave Pay</th>
                                    <th class="number">Gross Pay</th>
                                    <th class="number">Net Pay</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="row" items="${reportRows}" varStatus="status">
                                    <tr>
                                        <td>${status.index + 1}</td>
                                        <td><c:out value="${row.groupName}"/></td>
                                        <td><c:out value="${row.departmentName}"/></td>
                                        <c:if test="${groupBy != 'employee'}">
                                            <td class="number">${row.employeeCount}</td>
                                        </c:if>
                                        <td class="number"><fmt:formatNumber value="${row.productIncome}" type="number" maxFractionDigits="0"/></td>
                                        <td class="number"><fmt:formatNumber value="${row.overtimeIncome}" type="number" maxFractionDigits="0"/></td>
                                        <td class="number"><fmt:formatNumber value="${row.sickLeaveIncome}" type="number" maxFractionDigits="0"/></td>
                                        <td class="number"><fmt:formatNumber value="${row.grossIncome}" type="number" maxFractionDigits="0"/></td>
                                        <td class="number"><fmt:formatNumber value="${row.totalIncome}" type="number" maxFractionDigits="0"/></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty reportRows}">
                                    <tr>
                                        <td colspan="${groupBy == 'employee' ? 8 : 9}" style="text-align: center; color: var(--text-secondary);">No payroll data is available for the selected period.</td>
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

        const currency = value => new Intl.NumberFormat("en-US").format(value) + " VND";
        const commonMoneyAxis = {
            beginAtZero: true,
            grid: { color: "#eef2f7" },
            ticks: { callback: value => new Intl.NumberFormat("en-US").format(value) }
        };

        const monthlyData = Array.from(document.querySelectorAll("#monthlySalaryData span"));
        const trendCanvas = document.getElementById("companySalaryTrendChart");
        if (trendCanvas && monthlyData.length && window.Chart) {
            new Chart(trendCanvas.getContext("2d"), {
                type: "line",
                data: {
                    labels: monthlyData.map(item => item.dataset.label),
                    datasets: [{
                        label: "Total Net Pay",
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
                    datasets: [{
                        label: "Net Pay",
                        data: departmentData.map(item => Number(item.dataset.netPay || 0)),
                        backgroundColor: "#16a34a",
                        borderColor: "#15803d",
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    indexAxis: "y",
                    scales: {
                        x: commonMoneyAxis,
                        y: { grid: { display: false } }
                    },
                    plugins: {
                        tooltip: { callbacks: { label: context => context.dataset.label + ": " + currency(context.parsed.x) } }
                    }
                }
            });
        }

    });
</script>
</body>
</html>
