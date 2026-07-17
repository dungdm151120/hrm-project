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
    <title>HR Overview Report | HRM</title>
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

        /* Chart Grid Layout */
        .charts-section-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
            gap: 24px;
            margin-bottom: 24px;
        }

        @media (max-width: 768px) {
            .charts-section-grid {
                grid-template-columns: 1fr;
            }
        }

        .chart-full-wrapper {
            background: var(--white);
            border-radius: var(--radius);
            border: 1px solid var(--border-color);
            box-shadow: var(--shadow);
            padding: 24px;
            display: flex;
            flex-direction: column;
        }

        .chart-box-title {
            font-size: 15px;
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 16px;
            border-bottom: 1px solid var(--border-color);
            padding-bottom: 10px;
        }

        .chart-container {
            position: relative;
            width: 100%;
            height: 300px;
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

        .bold {
            font-weight: 600;
            color: var(--text-primary);
        }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Tổng Quan Nhân Sự</h1>
            </div>
        </div>

        <div class="dashboard-content">

            <!-- Filters Card -->
            <div class="report-filter-card">
                <form action="${ctx}/reports/hr" method="GET">
                    <input type="hidden" name="action" value="generate">

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

                        <div class="form-group" id="yearGroup">
                            <label for="year">Năm</label>
                            <select name="year" id="year">
                                <c:forEach var="y" items="${years}">
                                    <option value="${y}" ${selectedYear == y ? 'selected' : ''}>${y}</option>
                                </c:forEach>
                            </select>
                        </div>

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
                        <h3>Chọn bộ lọc và click "Tạo báo cáo" để hiển thị dữ liệu nhân sự</h3>
                        <p>Hệ thống hỗ trợ tổng hợp biểu đồ cơ cấu và phân tách dữ liệu trạng thái nhân viên.</p>
                    </div>
                </c:when>

                <c:otherwise>
                    <!-- Statistics Grid -->
                    <div class="dashboard-grid">

                        <!-- Card 1: Total Employees -->
                        <div class="stat-card">
                            <div class="stat-header">
                                <span class="stat-title">Tổng nhân sự</span>
                                <div class="stat-icon" style="background-color: #EFF6FF; color: #2563EB;">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                        <circle cx="9" cy="7" r="4"></circle>
                                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                                        <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                                    </svg>
                                </div>
                            </div>
                            <div class="stat-value" style="color: #1E3A8A;">${reportData.totalEmployees}</div>
                            <div class="stat-ratio">Nhân sự có hợp đồng hợp lệ</div>
                        </div>

                        <!-- Card 2: Gender Demographics -->
                        <div class="stat-card">
                            <div class="stat-header">
                                <span class="stat-title">Theo Giới Tính</span>
                                <div class="stat-icon" style="background-color: #F472B622; color: #EC4899;">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <circle cx="12" cy="12" r="10"></circle>
                                        <path d="M12 2a7 7 0 1 0 10 10"></path>
                                    </svg>
                                </div>
                            </div>
                            <div class="stat-value" style="font-size: 18px; margin-top: 5px;">
                                <span style="color: #2563EB;">${reportData.maleCount} Nam</span> | <span style="color: #EC4899;">${reportData.femaleCount} Nữ</span>
                            </div>
                            <div class="stat-ratio">Cơ cấu giới tính toàn công ty</div>
                        </div>

                        <!-- Card 3: Positions -->
                        <div class="stat-card">
                            <div class="stat-header">
                                <span class="stat-title">Theo Chức Vụ</span>
                                <div class="stat-icon" style="background-color: #FEF3C7; color: #D97706;">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                        <circle cx="9" cy="7" r="4"></circle>
                                        <polyline points="16 11 18 13 22 9"></polyline>
                                    </svg>
                                </div>
                            </div>
                            <div class="stat-value" style="font-size: 18px; margin-top: 5px;">
                                <span class="bold">${reportData.managerCount}</span> Quản lý | <span class="bold">${reportData.employeeCount}</span> NV
                            </div>
                            <div class="stat-ratio">Phân bổ cấp bậc nhân sự</div>
                        </div>

                        <!-- Card 4: Contract Status -->
                        <div class="stat-card">
                            <div class="stat-header">
                                <span class="stat-title">Trạng Thái Hợp Đồng</span>
                                <div class="stat-icon" style="background-color: #ECFDF5; color: #10B981;">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                        <polyline points="14 2 14 8 20 8"></polyline>
                                        <line x1="16" y1="13" x2="8" y2="13"></line>
                                        <line x1="16" y1="17" x2="8" y2="17"></line>
                                    </svg>
                                </div>
                            </div>
                            <div class="stat-value" style="font-size: 18px; margin-top: 5px;">
                                <span style="color: #10B981;">${reportData.regularCount} CT</span> | <span style="color: #F59E0B;">${reportData.probationCount} TV</span>
                            </div>
                            <div class="stat-ratio">Chính thức (CT) vs Thử việc (TV)</div>
                        </div>
                    </div>

                    <!-- Charts Display Section -->
                    <div class="charts-section-grid">

                        <!-- Chart 1: Contracts -->
                        <div class="chart-full-wrapper">
                            <div class="chart-box-title">📊 Cơ Cấu Loại Hợp Đồng</div>
                            <div class="chart-container">
                                <canvas id="contractChart"></canvas>
                            </div>
                        </div>

                        <!-- Chart 2: Departments -->
                        <div class="chart-full-wrapper">
                            <div class="chart-box-title">🏢 Số Lượng Nhân Sự Theo Phòng Ban</div>
                            <div class="chart-container">
                                <canvas id="deptChart"></canvas>
                            </div>
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
        updateFieldsVisibility();

        <c:if test="${isGenerated and not empty reportData}">
        const rawContractData = {};
        <c:forEach var="entry" items="${reportData.contractTypeData}">
        rawContractData["${entry.key}"] = ${entry.value};
        </c:forEach>

        const finalContractLabels = ["FIXED_TERM", "INDEFINITE_TERM", "PROBATION", "PART_TIME"];
        const finalContractValues = finalContractLabels.map(label => rawContractData[label] || 0);

        const rawDeptData = {};
        <c:forEach var="entry" items="${reportData.departmentData}">
        rawDeptData["${entry.key}"] = ${entry.value};
        </c:forEach>

        let deptLabels = Object.keys(rawDeptData);
        let deptValues = Object.values(rawDeptData);

        if (deptLabels.length === 0) {
            deptLabels = ["Không có dữ liệu"];
            deptValues = [0];
        }

        const topLabelsPlugin = {
            id: 'topLabels',
            afterDraw(chart) {
                const { ctx } = chart;
                ctx.save();
                ctx.font = 'bold 12px sans-serif';
                ctx.fillStyle = '#4B5563';
                ctx.textAlign = 'center';
                ctx.textBaseline = 'bottom';

                chart.data.datasets[0].data.forEach((value, index) => {
                    const meta = chart.getDatasetMeta(0);
                    if(meta.data[index]) {
                        const bar = meta.data[index];
                        ctx.fillText(value, bar.x, bar.y - 6);
                    }
                });
                ctx.restore();
            }
        };

        // Hàm ngắt dòng nhãn trục X: Cắt mỗi 10 ký tự hoặc theo khoảng trắng
        const wrapLabelCallback = function(value, index, values) {
            let label = this.getLabelForValue(value);
            if (label.length > 10) {
                // Tách chữ theo từ để ngắt dòng đẹp hơn thay vì cắt thô
                const words = label.split(' ');
                const lines = [];
                let currentLine = '';

                words.forEach(word => {
                    if ((currentLine + word).length > 10) {
                        if (currentLine) lines.push(currentLine.trim());
                        currentLine = word + ' ';
                    } else {
                        currentLine += word + ' ';
                    }
                });
                if (currentLine) lines.push(currentLine.trim());
                return lines;
            }
            return label;
        };

        // Chart 1: Contract Type Chart
        new Chart(document.getElementById('contractChart').getContext('2d'), {
            type: 'bar',
            data: {
                labels: finalContractLabels,
                datasets: [{
                    data: finalContractValues,
                    backgroundColor: [
                        '#3B82F6', // FIXED_TERM
                        '#10B981', // INDEFINITE_TERM
                        '#F59E0B', // Probation
                        '#EC4899'  // Part-time
                    ],
                    borderRadius: 4,
                    barPercentage: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                layout: {
                    padding: {
                        bottom: 10
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        afterFit: function(scaleInstance) {
                            scaleInstance.height = 55;
                        },
                        ticks: {
                            color: '#4B5563',
                            font: { weight: '600' },
                            minRotation: 0,
                            maxRotation: 0,
                            callback: wrapLabelCallback
                        }
                    },
                    y: {
                        beginAtZero: true,
                        grace: '15%',
                        grid: { color: '#E5E7EB' },
                        ticks: {
                            stepSize: 5,
                            precision: 0,
                            color: '#9CA3AF'
                        }
                    }
                },
                plugins: {
                    legend: { display: false }
                }
            },
            plugins: [topLabelsPlugin]
        });

        // Chart 2: Department Chart
        new Chart(document.getElementById('deptChart').getContext('2d'), {
            type: 'bar',
            data: {
                labels: deptLabels,
                datasets: [{
                    data: deptValues,
                    backgroundColor: [
                        '#10B981', '#3B82F6', '#8B5CF6', '#F59E0B', '#EF4444', '#EC4899', '#06B6D4', '#64748B'
                    ],
                    borderRadius: 4,
                    barPercentage: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                layout: {
                    padding: {
                        bottom: 10
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        afterFit: function(scaleInstance) {
                            scaleInstance.height = 55;
                        },
                        ticks: {
                            color: '#4B5563',
                            font: { weight: '600' },
                            minRotation: 0,
                            maxRotation: 0,
                            callback: wrapLabelCallback
                        }
                    },
                    y: {
                        beginAtZero: true,
                        grace: '15%',
                        grid: { color: '#E5E7EB' },
                        ticks: {
                            stepSize: 5,
                            precision: 0,
                            color: '#9CA3AF'
                        }
                    }
                },
                plugins: {
                    legend: { display: false }
                }
            },
            plugins: [topLabelsPlugin]
        });
        </c:if>
    });
</script>
</body>
</html>