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
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            height: 42px;
        }
        .btn-generate:hover { background: var(--primary-dark); }

        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
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
        }
        .stat-header { display: flex; justify-content: space-between; align-items: center; }
        .stat-title { font-size: 11px; font-weight: 600; color: var(--text-secondary); text-transform: uppercase; }
        .stat-icon { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: var(--primary-light); color: var(--primary); }
        .stat-value { font-size: 22px; font-weight: 700; color: var(--text-primary); }

        .chart-full-wrapper {
            background: var(--white);
            border-radius: var(--radius);
            border: 1px solid var(--border-color);
            box-shadow: var(--shadow);
            padding: 24px;
            margin-bottom: 24px;
        }
        .chart-box-title { font-size: 15px; font-weight: 600; color: var(--text-primary); margin-bottom: 16px; border-bottom: 1px solid var(--border-color); padding-bottom: 10px; }
        .chart-container { position: relative; width: 100%; height: 280px; }
        .empty-state { text-align: center; padding: 48px 24px; background: var(--white); border-radius: var(--radius); border: 1px solid var(--border-color); color: var(--text-secondary); }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <h1 class="header-title">Tổng Quan Nhân Sự</h1>
        </div>

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
                        <button type="submit" class="btn-generate">📊 Tạo báo cáo</button>
                    </div>
                </div>
            </form>
        </div>

        <c:choose>
            <c:when test="${not isGenerated}">
                <div class="empty-state">
                    <h3>Chọn bộ lọc và click "Tạo báo cáo" để hiển thị dữ liệu nhân sự</h3>
                    <p>Hệ thống hỗ trợ tổng hợp biểu đồ cơ cấu và phân tách dữ liệu trạng thái nhân viên.</p>
                </div>
            </c:when>

            <c:otherwise>
                <div class="dashboard-grid">
                    <div class="stat-card">
                        <div class="stat-header"><span class="stat-title">Tổng nhân sự</span></div>
                        <div class="stat-value">${reportData.totalEmployees}</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-header"><span class="stat-title">Theo Giới Tính</span></div>
                        <div class="stat-value" style="font-size: 18px; margin-top: 5px;">${reportData.maleCount} Nam | ${reportData.femaleCount} Nữ</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-header"><span class="stat-title">Theo Chức Vụ</span></div>
                        <div class="stat-value" style="font-size: 18px; margin-top: 5px;">${reportData.managerCount} Quản lý | ${reportData.employeeCount} Nhân viên</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-header"><span class="stat-title">Trạng Thái Hợp Đồng</span></div>
                        <div class="stat-value" style="font-size: 18px; margin-top: 5px;"><span>${reportData.regularCount} Chính Thức</span> | <span>${reportData.probationCount} Thử Việc</span></div>
                    </div>
                </div>

                <div class="chart-full-wrapper">
                    <div class="chart-box-title">📊 Cơ Cấu Loại Hợp Đồng</div>
                    <div class="chart-container"><canvas id="contractChart"></canvas></div>
                </div>

                <div class="chart-full-wrapper">
                    <div class="chart-box-title">🏢 Số Lượng Nhân Sự Theo Phòng Ban</div>
                    <div class="chart-container"><canvas id="deptChart"></canvas></div>
                </div>
            </c:otherwise>
        </c:choose>
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

        // Đọc dữ liệu phòng ban vào Object bằng JSTL an toàn (tránh lỗi font, dấu nháy)
        const rawDeptData = {};
        <c:forEach var="entry" items="${reportData.departmentData}">
        rawDeptData["${entry.key}"] = ${entry.value};
        </c:forEach>

        // Tách mảng Labels và Values từ Object JavaScript
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
                ctx.fillStyle = '#1F2937';
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

        // Khởi tạo Biểu đồ Hợp đồng (Mỗi cột 1 màu riêng biệt, ẩn Legend)
        new Chart(document.getElementById('contractChart').getContext('2d'), {
            type: 'bar',
            data: {
                labels: finalContractLabels,
                datasets: [{
                    data: finalContractValues,
                    backgroundColor: [
                        '#3B82F6', // FIXED_TERM -> Xanh dương
                        '#10B981', // INDEFINITE_TERM -> Xanh lá
                        '#F59E0B', // Probation -> Vàng/Cam
                        '#EC4899'  // Part-time -> Hồng
                    ],
                    barPercentage: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        grace: '10%',
                        ticks: {
                            stepSize: 10
                        }
                    }
                },
                plugins: {
                    legend: { display: false } // Đã ẩn cục label "Nhân sự" dư thừa
                }
            },
            plugins: [topLabelsPlugin]
        });

        // Khởi tạo Biểu đồ Phòng ban (Mỗi cột 1 màu riêng biệt, ẩn Legend)
        new Chart(document.getElementById('deptChart').getContext('2d'), {
            type: 'bar',
            data: {
                labels: deptLabels,
                datasets: [{
                    data: deptValues,
                    backgroundColor: [
                        '#10B981', '#3B82F6', '#8B5CF6', '#F59E0B', '#EF4444', '#EC4899', '#06B6D4', '#64748B'
                    ],
                    barPercentage: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        grace: '10%',
                        ticks: {
                            stepSize: 10
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