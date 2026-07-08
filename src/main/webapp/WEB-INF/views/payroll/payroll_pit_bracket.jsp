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
            <a class="back-link" href="${pageContext.request.contextPath}/payroll/update_component">Back to settings</a>
            <h2 class="form-title">PIT Tax Brackets List</h2>

            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-danger">${sessionScope.error}</div>
                <% session.removeAttribute("error"); %>
            </c:if>
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success">${sessionScope.message}</div>
                <% session.removeAttribute("message"); %>
            </c:if>

            <form action="${pageContext.request.contextPath}/payroll/pit" method="POST">

                <div class="form-group">
                    <div class="form-group">
                        <label>Effective Date (Applied to all levels)</label>
                        <input type="date" name="commonEffectiveDate" class="form-control" value="${commonEffectiveDate}" required>
                    </div>
                </div>

                <table class="table">
                    <thead>
                        <tr>
                            <th>Level</th>
                            <th>Min Value (VND)</th>
                            <th>Max Value (VND)</th>
                            <th>Tax Rate (%)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="b" items="${bracketList}">
                            <tr>
                                <td>
                                    <strong>Level ${b.bracketLevel}</strong>
                                    <input type="hidden" name="bracketIds" value="${b.id}">
                                    <input type="hidden" name="bracketLevels" value="${b.bracketLevel}">
                                </td>
                                <td>
                                    <input type="number" name="minValues" class="form-control" value="${b.minValue}" required>
                                </td>
                                <td>
                                    <input type="number" name="maxValues" class="form-control" value="${b.maxValue}" placeholder="Leave blank for infinity">
                                </td>
                                <td>
                                    <input type="number" step="0.1" name="taxRates" class="form-control" value="${b.taxRate}" required>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <div class="form-actions">
                    <button type="submit" class="btn-save">Update All Brackets</button>
                    <a href="${pageContext.request.contextPath}/payroll/update_component" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>