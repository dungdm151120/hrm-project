<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Import Attendance | ${pageContext.servletContext.servletContextName}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <%-- SIDEBAR --%>
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <%-- MAIN CONTENT --%>
    <div class="dashboard-main">

        <%-- STICKY HEADER WITH BREADCRUMB --%>
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Import Attendance</h1>
            </div>
            <div class="header-right">
                <nav class="breadcrumb" aria-label="Breadcrumb">
                    <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                    <span class="separator">›</span>
                    <a href="${pageContext.request.contextPath}/admin/attendance">Attendance</a>
                    <span class="separator">›</span>
                    <span class="current">Import Excel</span>
                </nav>
            </div>
        </div>

        <%-- CENTERED PAGE CONTENT --%>
        <div class="dashboard-content">
            <div style="max-width: 720px; margin: 0 auto;">

                <%-- ERROR / SUCCESS MESSAGES --%>
                <c:if test="${not empty importError}">
                    <div class="alert alert-error" role="alert">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"/>
                            <line x1="15" y1="9" x2="9" y2="15"/>
                            <line x1="9" y1="9" x2="15" y2="15"/>
                        </svg>
                        <span>${importError}</span>
                    </div>
                </c:if>
                <c:if test="${not empty importSuccess}">
                    <div class="alert alert-success" role="alert">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                            <polyline points="22 4 12 14.01 9 11.01"/>
                        </svg>
                        <span>${importSuccess}</span>
                    </div>
                    <c:remove var="importSuccess" scope="session"/>
                </c:if>

                <%-- UPLOAD CARD --%>
                <div class="dashboard-card">
                    <div class="card-header">
                        <h3>
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right: 0.5rem; vertical-align: middle;">
                                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                                <polyline points="14 2 14 8 20 8"/>
                                <line x1="16" y1="13" x2="8" y2="13"/>
                                <line x1="16" y1="17" x2="8" y2="17"/>
                                <polyline points="10 9 9 9 8 9"/>
                            </svg>
                            Upload Attendance Excel File
                        </h3>
                    </div>
                    <div class="card-content">

                        <%-- INSTRUCTIONS --%>
                        <div class="info-note" style="margin-top: 0; margin-bottom: 1.5rem;">
                            <strong>📋 Instructions:</strong> The Excel file (.xlsx) must contain columns: <em>Employee Code, Date, Check-in Time, Check-out Time</em>.
                            The system will automatically validate and import valid records. Rows with errors will be skipped.
                        </div>

                        <%-- UPLOAD FORM --%>
                        <form action="${pageContext.request.contextPath}/admin/attendance/import"
                              method="post"
                              enctype="multipart/form-data"
                              id="import-form">

                            <div class="form-group">
                                <label for="excelFile">
                                    Select Excel file <span class="required-star">*</span>
                                </label>
                                <input type="file"
                                       name="excelFile"
                                       id="excelFile"
                                       accept=".xlsx"
                                       required
                                       style="padding: 0.85rem 1.1rem;">
                                <p class="text-muted" style="margin-top: 0.4rem; font-size: 0.8rem;">
                                    Only .xlsx files are accepted (Excel 2007 and later). Maximum file size: 10 MB.
                                </p>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn-save">
                                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="margin-right: 0.5rem;">
                                        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                        <polyline points="17 8 12 3 7 8"/>
                                        <line x1="12" y1="3" x2="12" y2="15"/>
                                    </svg>
                                    Upload and Import
                                </button>
                                <a href="${pageContext.request.contextPath}/admin/attendance" class="btn-cancel">
                                    ← Back
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                <%-- IMPORTANT NOTES --%>
                <div class="dashboard-card" style="margin-top: 1.5rem;">
                    <div class="card-header">
                        <h3>📌 Important Notes</h3>
                    </div>
                    <div class="card-content">
                        <ul style="padding-left: 1.2rem; color: var(--text-secondary); font-size: 0.9rem; line-height: 1.8;">
                            <li>Date format must be <strong>dd/MM/yyyy</strong> (e.g., 25/12/2024).</li>
                            <li>Time format must be <strong>HH:mm</strong> in 24-hour format (e.g., 08:00, 17:30).</li>
                            <li>Employee codes must exist in the system, otherwise those rows will be skipped.</li>
                            <li>After a successful import, data will appear in the attendance table.</li>
                            <li>It is recommended to <strong>backup data</strong> before performing bulk imports.</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div><%-- /dashboard-content --%>
    </div><%-- /dashboard-main --%>

    <%-- LIGHTWEIGHT JAVASCRIPT --%>
    <script>
        // Display selected file name
        document.getElementById('excelFile').addEventListener('change', function(e) {
            const fileName = e.target.files[0]?.name;
            if (fileName) {
                const label = document.querySelector('label[for="excelFile"]');
                label.innerHTML = 'Selected: <strong>' + fileName + '</strong> <span class="required-star">*</span>';
            }
        });

        // Validate before submit
        document.getElementById('import-form').addEventListener('submit', function(e) {
            const fileInput = document.getElementById('excelFile');
            if (!fileInput.files.length) {
                e.preventDefault();
                alert('Please select an Excel file.');
                return false;
            }
            const fileName = fileInput.files[0].name;
            if (!fileName.toLowerCase().endsWith('.xlsx')) {
                e.preventDefault();
                alert('Only .xlsx files are accepted. Please choose a valid file.');
                return false;
            }
            return confirm('Are you sure you want to import data from "' + fileName + '"?');
        });
    </script>
</body>
</html>
