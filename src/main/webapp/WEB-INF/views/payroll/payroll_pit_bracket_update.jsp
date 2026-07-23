<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update PIT Brackets | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

    <style>
        .dashboard-content {
            padding: 12px 20px !important;
        }

        .pit-container {
            max-width: 880px;
            margin: 0 auto;
        }

        .card-box {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 18px 20px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            margin-top: 12px;
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

        .grid-2 {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
        }

        .form-group {
            margin-bottom: 8px;
        }

        .form-label {
            display: block;
            font-size: 0.78rem;
            font-weight: 600;
            color: #475569;
            margin-bottom: 5px;
        }

        .form-control {
            width: 100%;
            padding: 8px 10px;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            font-size: 0.85rem;
            color: #0f172a;
            background-color: #ffffff;
            box-sizing: border-box;
            transition: border-color 0.15s ease-in-out;
        }

        .form-control:focus {
            outline: none;
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
        }

        .pit-table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            font-size: 0.85rem;
        }

        .pit-table th {
            background: #f8fafc;
            color: #475569;
            font-weight: 600;
            padding: 10px 12px;
            border-top: 1px solid #e2e8f0;
            border-bottom: 1px solid #cbd5e1;
            text-align: left;
        }

        .pit-table th:first-child { border-top-left-radius: 6px; border-left: 1px solid #e2e8f0; }
        .pit-table th:last-child { border-top-right-radius: 6px; border-right: 1px solid #e2e8f0; }

        .pit-table td {
            padding: 8px 10px;
            border-bottom: 1px solid #e2e8f0;
            vertical-align: middle;
        }

        .pit-table td:first-child { border-left: 1px solid #e2e8f0; }
        .pit-table td:last-child { border-right: 1px solid #e2e8f0; }

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

        .rate-input-wrapper {
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .btn-add-row {
            background-color: #eff6ff;
            color: #2563eb;
            border: 1px dashed #3b82f6;
            padding: 8px 14px;
            border-radius: 6px;
            font-size: 0.82rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            margin-top: 12px;
        }

        .btn-add-row:hover {
            background-color: #dbeafe;
        }

        .btn-delete-row {
            background-color: #fef2f2;
            color: #dc2626;
            border: 1px solid #fecaca;
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 0.78rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
        }

        .btn-delete-row:hover {
            background-color: #fee2e2;
        }

        .form-actions {
            margin-top: 20px;
            display: flex;
            gap: 12px;
        }

        .btn-save {
            background-color: #2563eb;
            color: #ffffff;
            border: none;
            padding: 9px 20px;
            border-radius: 6px;
            font-weight: 600;
            font-size: 0.85rem;
            cursor: pointer;
        }

        .btn-save:hover { background-color: #1d4ed8; }

        .btn-cancel {
            background-color: #f1f5f9;
            color: #475569;
            border: 1px solid #cbd5e1;
            padding: 9px 20px;
            border-radius: 6px;
            font-weight: 600;
            font-size: 0.85rem;
            text-decoration: none;
            display: inline-block;
        }

        .btn-cancel:hover { background-color: #e2e8f0; }

        .alert {
            padding: 8px 12px;
            font-size: 0.85rem;
            border-radius: 6px;
            margin-bottom: 12px;
        }
        .alert-danger { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Update PIT Brackets</h1>
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

                <form action="${pageContext.request.contextPath}/payroll/pit/update" method="POST">

                    <div class="card-box">
                        <div class="card-title">
                            <span>Effective Period Selection</span>
                        </div>

                        <div class="grid-2">
                            <div class="form-group">
                                <label class="form-label">Effective Month *</label>
                                <select name="month" required class="form-control">
                                    <option value="">-- Select Month --</option>
                                    <c:forEach var="m" begin="1" end="12">
                                        <option value="${m}">Month ${m}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Effective Year *</label>
                                <select name="year" required class="form-control">
                                    <option value="">-- Select Year --</option>
                                    <c:set var="currentYear" value="<%= java.time.Year.now().getValue() %>"/>
                                    <c:forEach var="y" begin="${currentYear - 3}" end="${currentYear + 3}">
                                        <option value="${y}">Year ${y}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <small style="color: #64748b; font-size: 0.75rem; display: block; margin-top: 8px;">
                            * Note: This bracket structure will override existing data or create a new version starting exactly on the 1st day of the selected month.
                        </small>
                    </div>

                    <div class="card-box">
                        <div class="card-title">
                            <span>Configure Dynamic Tax Brackets</span>
                        </div>

                        <table class="pit-table" id="bracketTable">
                            <thead>
                                <tr>
                                    <th class="text-center" style="width: 10%;">Level</th>
                                    <th style="width: 33%;">Min Value (VND) *</th>
                                    <th style="width: 33%;">Max Value (VND)</th>
                                    <th style="width: 14%;">Tax Rate (%) *</th>
                                    <th class="text-center" style="width: 10%;">Action</th>
                                </tr>
                            </thead>
                            <tbody id="bracketTableBody">
                                <c:forEach var="b" items="${bracketList}" varStatus="status">
                                    <tr>
                                        <td class="text-center level-label">
                                            <span class="level-badge">${status.index + 1}</span>
                                        </td>
                                        <td>
                                            <input type="number" name="minValues" class="form-control" value="${b.minValue}" required>
                                        </td>
                                        <td>
                                            <input type="number" name="maxValues" class="form-control" value="${b.maxValue}" placeholder="Blank for Infinity (∞)">
                                        </td>
                                        <td>
                                            <div class="rate-input-wrapper">
                                                <input type="number" step="0.1" name="taxRates" class="form-control" value="${b.taxRate}" required>
                                                <span style="font-weight: 600; color: #475569;">%</span>
                                            </div>
                                        </td>
                                        <td class="text-center">
                                            <button type="button" class="btn-delete-row" onclick="deleteRow(this)">Delete</button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <button type="button" class="btn-add-row" onclick="addNewRow()">
                            + Add New Bracket Row
                        </button>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-save">Update All Brackets</button>
                        <a href="${pageContext.request.contextPath}/payroll/pit/list" class="btn-cancel">Cancel</a>
                    </div>

                </form>

            </div>
        </div>
    </div>
</div>

<script>
    function addNewRow() {
        const tbody = document.getElementById("bracketTableBody");
        const nextLevel = tbody.children.length + 1;

        const newRow = document.createElement("tr");
        newRow.innerHTML = `
            <td class="text-center level-label">
                <span class="level-badge">${'${nextLevel}'}</span>
            </td>
            <td>
                <input type="number" name="minValues" class="form-control" required>
            </td>
            <td>
                <input type="number" name="maxValues" class="form-control" placeholder="Blank for Infinity (∞)">
            </td>
            <td>
                <div class="rate-input-wrapper">
                    <input type="number" step="0.1" name="taxRates" class="form-control" required>
                    <span style="font-weight: 600; color: #475569;">%</span>
                </div>
            </td>
            <td class="text-center">
                <button type="button" class="btn-delete-row" onclick="deleteRow(this)">Delete</button>
            </td>
        `;
        tbody.appendChild(newRow);
        recalculateLevels();
    }

    function deleteRow(button) {
        const row = button.closest("tr");
        row.remove();
        recalculateLevels();
    }

    function recalculateLevels() {
        const rows = document.querySelectorAll("#bracketTableBody tr");
        rows.forEach((row, index) => {
            const levelTd = row.querySelector(".level-label");
            if (levelTd) {
                levelTd.innerHTML = `<span class="level-badge">${'${index + 1}'}</span>`;
            }
        });
    }
</script>

</body>
</html>