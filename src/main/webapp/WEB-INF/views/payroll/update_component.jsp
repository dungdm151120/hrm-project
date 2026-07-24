<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.LocalDate" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    int currentYear = LocalDate.now().getYear();
    int currentMonth = LocalDate.now().getMonthValue();
    request.setAttribute("currentYear", currentYear);
    request.setAttribute("currentMonth", currentMonth);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payroll Configurations | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

    <style>
        .page-subtitle {
            font-size: 0.88rem;
            color: #64748b;
            margin-top: 2px;
        }

        .config-hub-container {
            max-width: 960px;
            margin: 20px auto;
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
            gap: 24px;
        }

        .hub-card {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            padding: 24px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: all 0.25s ease-in-out;
            box-shadow: 0 2px 4px rgba(0,0,0,0.02);
            position: relative;
            overflow: hidden;
        }

        .hub-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 24px rgba(0,0,0,0.08);
            border-color: #3b82f6;
        }

        .hub-card-header {
            display: flex;
            align-items: center;
            gap: 16px;
            margin-bottom: 12px;
        }

        .hub-icon-wrapper {
            width: 48px;
            height: 48px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            flex-shrink: 0;
        }

        .icon-pit {
            background: #eff6ff;
            color: #2563eb;
            border: 1px solid #bfdbfe;
        }

        .icon-insurance {
            background: #f0fdf4;
            color: #16a34a;
            border: 1px solid #bbf7d0;
        }

        .hub-card-title {
            font-size: 1.1rem;
            font-weight: 700;
            color: #0f172a;
            margin: 0;
        }

        .hub-card-desc {
            font-size: 0.85rem;
            color: #475569;
            line-height: 1.5;
            margin-bottom: 16px;
        }

        .hub-feature-list {
            list-style: none;
            padding: 0;
            margin: 0 0 20px 0;
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
        }

        .hub-feature-chip {
            font-size: 0.75rem;
            font-weight: 600;
            background: #f8fafc;
            color: #334155;
            border: 1px solid #e2e8f0;
            padding: 4px 10px;
            border-radius: 20px;
            display: inline-flex;
            align-items: center;
            gap: 4px;
        }

        .btn-hub-action {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            padding: 10px 18px;
            font-size: 0.88rem;
            font-weight: 600;
            border-radius: 6px;
            text-decoration: none;
            transition: background 0.2s;
            text-align: center;
        }

        .btn-pit-action {
            background-color: #2563eb;
            color: #ffffff !important;
        }
        .btn-pit-action:hover {
            background-color: #1d4ed8;
        }

        .btn-insurance-action {
            background-color: #059669;
            color: #ffffff !important;
        }
        .btn-insurance-action:hover {
            background-color: #047857;
        }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">

        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Payroll System Configurations</h1>
                <p class="page-subtitle">Manage tax brackets, insurance contribution rates, and personal tax relief parameters.</p>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="alert alert-success">${success}</div>
            </c:if>

            <div class="config-hub-container">

                <div class="hub-card">
                    <div>
                        <div class="hub-card-header">
                            <div>
                                <h2 class="hub-card-title">PIT Tax Brackets</h2>
                                <span style="font-size: 0.72rem; color: #2563eb; font-weight: 600;">Personal Income Tax</span>
                            </div>
                        </div>

                        <p class="hub-card-desc">
                            Configure progressive tax brackets and marginal tax rates applied to employee taxable income according to statutory PIT regulations.
                        </p>

                        <div class="hub-feature-list">
                            <span class="hub-feature-chip">✓ Progressive Tax Brackets</span>
                            <span class="hub-feature-chip">✓ Marginal Tax Rates (%)</span>
                            <span class="hub-feature-chip">✓ Taxable Income Thresholds</span>
                        </div>
                    </div>

                    <a href="${pageContext.request.contextPath}/payroll/pit/list" class="btn-hub-action btn-pit-action">
                        View & Edit PIT Brackets →
                    </a>
                </div>

                <div class="hub-card">
                    <div>
                        <div class="hub-card-header">
                            <div>
                                <h2 class="hub-card-title">Insurances & Reliefs</h2>
                                <span style="font-size: 0.72rem; color: #059669; font-weight: 600;">Contributions & Allowances</span>
                            </div>
                        </div>

                        <p class="hub-card-desc">
                            Set up social, health, and unemployment insurance rates for both employees and employers, along with trade union fees and tax reliefs.
                        </p>

                        <div class="hub-feature-list">
                            <span class="hub-feature-chip">✓ Social / Health / Unemp. Rates</span>
                            <span class="hub-feature-chip">✓ Trade Union Fee (%)</span>
                            <span class="hub-feature-chip">✓ Self & Dependent Reliefs</span>
                        </div>
                    </div>

                    <a href="${pageContext.request.contextPath}/payroll/setting/list" class="btn-hub-action btn-insurance-action">
                        Configure Insurance & Reliefs →
                    </a>
                </div>

            </div>
        </div>
    </div>
</div>

</body>
</html>