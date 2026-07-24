<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PIT Version Details | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

    <style>
        .dashboard-content {
            padding: 12px 20px !important;
        }

        /* Container căn giữa chuẩn layout dashboard (Sidebar + Header) */
        .pit-container {
            max-width: 880px;
            margin: 0 auto;
        }

        /* Styling Card chuẩn Payroll Setting */
        .card-box {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 18px 20px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            margin-top: 10px;
        }

        .card-title {
            font-size: 0.92rem;
            font-weight: 700;
            color: #1e293b;
            padding-bottom: 8px;
            margin-bottom: 16px;
            border-bottom: 2px solid #2563eb;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .effective-badge {
            font-size: 0.8rem;
            font-weight: 600;
            color: #2563eb;
            background-color: #eff6ff;
            border: 1px solid #bfdbfe;
            padding: 4px 10px;
            border-radius: 6px;
        }

        /* Bảng hiển thị thông tin tối giản & sạch sẽ */
        .pit-table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            font-size: 0.86rem;
        }

        .pit-table th {
            background: #f8fafc;
            color: #475569;
            font-weight: 600;
            padding: 10px 14px;
            border-top: 1px solid #e2e8f0;
            border-bottom: 1px solid #cbd5e1;
            text-align: left;
        }

        .pit-table th:first-child { border-top-left-radius: 6px; border-left: 1px solid #e2e8f0; }
        .pit-table th:last-child { border-top-right-radius: 6px; border-right: 1px solid #e2e8f0; }

        .pit-table td {
            padding: 11px 14px;
            border-bottom: 1px solid #e2e8f0;
            color: #0f172a;
        }

        .pit-table td:first-child { border-left: 1px solid #e2e8f0; }
        .pit-table td:last-child { border-right: 1px solid #e2e8f0; }

        .pit-table tbody tr:hover {
            background-color: #f8fafc;
        }

        /* Alignment helpers */
        .text-center { text-align: center; }
        .text-right { text-align: right; }

        .level-badge {
            display: inline-block;
            width: 24px;
            height: 24px;
            line-height: 24px;
            background: #f1f5f9;
            color: #334155;
            font-weight: 700;
            font-size: 0.78rem;
            border-radius: 50%;
            border: 1px solid #cbd5e1;
            text-align: center;
        }

        .infinite-text {
            color: #94a3b8;
            font-style: italic;
        }

        /* Alert Notifications */
        .alert {
            padding: 8px 12px;
            font-size: 0.85rem;
            border-radius: 6px;
            margin-bottom: 12px;
        }
        .alert-danger { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
        .alert-success { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">PIT Version Details</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <div class="pit-container">

                <div style="margin-bottom: 12px;">
                    <a class="back-link" href="${pageContext.request.contextPath}/payroll/pit/list" style="font-size: 0.82rem;">
                        Back to PIT versions
                    </a>
                </div>

                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger">${sessionScope.error}</div>
                    <% session.removeAttribute("error"); %>
                </c:if>
                <c:if test="${not empty sessionScope.message}">
                    <div class="alert alert-success">${sessionScope.message}</div>
                    <% session.removeAttribute("message"); %>
                </c:if>

                <div class="card-box">
                    <div class="card-title">
                        <span>PIT Tax Brackets List</span>
                        <span class="effective-badge">Effective Date: ${effectiveDate}</span>
                    </div>

                    <table class="pit-table">
                        <thead>
                            <tr>
                                <th class="text-center" style="width: 10%;">Level</th>
                                <th class="text-right" style="width: 35%;">Min Value (VND)</th>
                                <th class="text-right" style="width: 35%;">Max Value (VND)</th>
                                <th class="text-center" style="width: 20%;">Tax Rate (%)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${bracketList}">
                                <tr>
                                    <td class="text-center">
                                        <span class="level-badge">${b.bracketLevel}</span>
                                    </td>
                                    <td style="font-weight: 600;">
                                        <fmt:formatNumber value="${b.minValue}" type="number" maxFractionDigits="0"/>
                                    </td>
                                    <td style="font-weight: 600;">
                                        <c:choose>
                                            <c:when test="${b.maxValue != null}">
                                                <fmt:formatNumber value="${b.maxValue}" type="number" maxFractionDigits="0"/>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="infinite-text">No Limit</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center" style="font-weight: 700; color: #2563eb;">
                                        ${b.taxRate}%
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty bracketList}">
                                <tr>
                                    <td colspan="4" class="text-center" style="padding: 24px; color: #64748b;">
                                        No tax brackets found for this version.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

            </div>
        </div>
    </div>
</div>

</body>
</html>