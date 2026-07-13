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
            <a class="back-link" href="${pageContext.request.contextPath}/payroll/pit/list" style="text-decoration: none; color: #2563EB; font-weight: bold; display: inline-block; margin-bottom: 15px;">
                ← Back to PIT versions
            </a>
            <h2 class="form-title" style="margin-bottom: 20px;">Configure Dynamic Tax Brackets</h2>

            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-danger" style="margin-bottom: 15px; padding: 12px; background: #FDE8E8; color: #9B1C1C; border-radius: 4px;">
                    ${sessionScope.error}
                </div>
                <% session.removeAttribute("error"); %>
            </c:if>

            <form action="${pageContext.request.contextPath}/payroll/pit/update" method="POST">

                <div style="background: #fff; padding: 20px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px;">
                    <h3 style="margin-top: 0; margin-bottom: 15px; color: #1e293b; border-bottom: 2px solid #e2e8f0; padding-bottom: 6px;">Effective Period Selection</h3>
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                        <div>
                            <label style="display: block; font-weight: 500; color: #344054; margin-bottom: 6px;">Effective Month *</label>
                            <select name="month" required class="form-control" style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #d0d5dd;">
                                <option value="">-- Select Month --</option>
                                <c:forEach var="m" begin="1" end="12">
                                    <option value="${m}">Month ${m}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div>
                            <label style="display: block; font-weight: 500; color: #344054; margin-bottom: 6px;">Effective Year *</label>
                            <select name="year" required class="form-control" style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #d0d5dd;">
                                <option value="">-- Select Year --</option>
                                <c:set var="currentYear" value="<%= java.time.Year.now().getValue() %>"/>
                                <c:forEach var="y" begin="${currentYear - 3}" end="${currentYear + 3}">
                                    <option value="${y}">Year ${y}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <small style="color: #64748b; display: block; margin-top: 8px;">
                        * Note: This bracket structure will override existing data or create a new version starting exactly on the 1st of the selected month.
                    </small>
                </div>

                <div class="table-container" style="background: #fff; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); overflow: hidden; padding: 15px;">
                    <table class="table" id="bracketTable" style="width: 100%; border-collapse: collapse; text-align: left; margin-bottom: 15px;">
                        <thead>
                            <tr style="background-color: #F9FAFB; border-bottom: 2px solid #E5E7EB;">
                                <th style="padding: 12px; font-weight: 600; color: #4B5563; width: 15%;">Level</th>
                                <th style="padding: 12px; font-weight: 600; color: #4B5563;">Min Value (VND) *</th>
                                <th style="padding: 12px; font-weight: 600; color: #4B5563;">Max Value (VND)</th>
                                <th style="padding: 12px; font-weight: 600; color: #4B5563; width: 20%;">Tax Rate (%) *</th>
                                <th style="padding: 12px; font-weight: 600; color: #4B5563; width: 10%; text-align: center;">Action</th>
                            </tr>
                        </thead>
                        <tbody id="bracketTableBody">
                            <c:forEach var="b" items="${bracketList}" varStatus="status">
                                <tr style="border-bottom: 1px solid #E5E7EB;">
                                    <td style="padding: 12px; vertical-align: middle;" class="level-label">
                                        <strong>Level ${status.index + 1}</strong>
                                    </td>
                                    <td style="padding: 12px;">
                                        <input type="number" name="minValues" class="form-control" value="${b.minValue}" required style="padding: 8px; border-radius: 4px; border: 1px solid #D1D5DB; width: 90%;">
                                    </td>
                                    <td style="padding: 12px;">
                                        <input type="number" name="maxValues" class="form-control" value="${b.maxValue}" placeholder="Blank for Infinity" style="padding: 8px; border-radius: 4px; border: 1px solid #D1D5DB; width: 90%;">
                                    </td>
                                    <td style="padding: 12px;">
                                        <input type="number" step="0.1" name="taxRates" class="form-control" value="${b.taxRate}" required style="padding: 8px; border-radius: 4px; border: 1px solid #D1D5DB; width: 80px;"> %
                                    </td>
                                    <td style="padding: 12px; text-align: center; vertical-align: middle;">
                                        <button type="button" onclick="deleteRow(this)" style="background: #EF4444; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer;">Delete</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <button type="button" onclick="addNewRow()" style="background: #10B981; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; font-weight: 500; margin-bottom: 10px;">
                        + Add New Bracket Row
                    </button>
                </div>

                <div class="form-actions" style="margin-top: 25px; display: flex; gap: 12px;">
                    <button type="submit" class="btn-save" style="padding: 10px 20px; background: #2563EB; color: white; border: none; border-radius: 4px; font-weight: bold; cursor: pointer;">Update All Brackets</button>
                    <a href="${pageContext.request.contextPath}/payroll/pit/list" class="btn-cancel" style="padding: 10px 20px; background: #6B7280; color: white; text-decoration: none; border-radius: 4px; font-weight: bold; display: inline-block;">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function addNewRow() {
        const tbody = document.getElementById("bracketTableBody");
        const nextLevel = tbody.children.length + 1;

        const newRow = document.createElement("tr");
        newRow.style.borderBottom = "1px solid #E5E7EB";
        newRow.innerHTML = `
            <td style="padding: 12px; vertical-align: middle;" class="level-label">
                <strong>Level ${'${nextLevel}'}</strong>
            </td>
            <td style="padding: 12px;">
                <input type="number" name="minValues" class="form-control" required style="padding: 8px; border-radius: 4px; border: 1px solid #D1D5DB; width: 90%;">
            </td>
            <td style="padding: 12px;">
                <input type="number" name="maxValues" class="form-control" placeholder="Blank for Infinity" style="padding: 8px; border-radius: 4px; border: 1px solid #D1D5DB; width: 90%;">
            </td>
            <td style="padding: 12px;">
                <input type="number" step="0.1" name="taxRates" class="form-control" required style="padding: 8px; border-radius: 4px; border: 1px solid #D1D5DB; width: 80px;"> %
            </td>
            <td style="padding: 12px; text-align: center; vertical-align: middle;">
                <button type="button" onclick="deleteRow(this)" style="background: #EF4444; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer;">Delete</button>
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
                levelTd.innerHTML = `<strong>Level ${'${index + 1}'}</strong>`;
            }
        });
    }
</script>
</body>
</html>