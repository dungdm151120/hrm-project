<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sơ đồ tổ chức | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        /* ══════════════════════════════════════════════════════
           ORG CHART — uses system CSS variables from style.css
        ══════════════════════════════════════════════════════ */

        /* Toolbar */
        .chart-toolbar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 0.75rem;
            margin-bottom: 1.25rem;
        }

        .chart-toolbar-label {
            font-size: 0.78rem;
            font-weight: 700;
            color: var(--text-muted);
            text-transform: uppercase;
            letter-spacing: 0.06em;
        }

        .chart-toolbar-actions {
            display: flex;
            gap: 0.5rem;
        }

        .chart-toolbar-actions button {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 0.35rem;
            height: 36px;
            padding: 0 1rem;
            background: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-pill);
            font-family: 'Inter', sans-serif;
            font-size: 0.8rem;
            font-weight: 600;
            color: var(--text-secondary);
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .chart-toolbar-actions button:hover {
            background: var(--primary-light);
            border-color: var(--primary);
            color: var(--primary-dark);
        }

        /* Viewport */
        #chart-viewport {
            background:
                radial-gradient(circle at 10% 10%, rgba(59,130,246,0.04), transparent 50%),
                radial-gradient(circle at 90% 90%, rgba(59,130,246,0.03), transparent 50%),
                var(--bg);
            border-radius: var(--radius);
            border: 1px solid var(--border-color);
            overflow: auto;
            min-height: 540px;
            padding: 40px 32px 56px;
            user-select: none;
            box-shadow: var(--shadow-sm);
        }

        /* ── Tree structure ── */
        .org-tree,
        .org-tree ul {
            list-style: none;
            margin: 0;
            padding: 0;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .org-tree li {
            display: flex;
            flex-direction: column;
            align-items: center;
            position: relative;
        }

        /* ── Connectors ── */
        .connector-down {
            width: 2px;
            height: 32px;
            background: linear-gradient(to bottom, var(--primary), rgba(59,130,246,0.35));
            margin: 0 auto;
        }

        .children-wrapper {
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .h-bar-row {
            display: flex;
            flex-direction: row;
            align-items: flex-start;
            position: relative;
        }

        .child-col {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 0 16px;
            position: relative;
        }

        /* Horizontal connector — drawn by JS */
        .h-bar {
            position: absolute;
            top: 0;
            height: 2px;
            background: linear-gradient(to right, rgba(59,130,246,0.35), var(--primary), rgba(59,130,246,0.35));
            border-radius: 1px;
        }

        .v-tick {
            width: 2px;
            height: 28px;
            background: linear-gradient(to bottom, rgba(59,130,246,0.35), var(--primary));
            margin: 0 auto;
        }

        /* ── Node base ── */
        .org-node {
            display: flex;
            flex-direction: column;
            align-items: center;
            background: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-sm);
            padding: 18px 14px 14px;
            width: 152px;
            box-shadow: var(--shadow);
            cursor: default;
            transition: box-shadow 0.2s ease, transform 0.2s ease, border-color 0.2s ease;
            position: relative;
            box-sizing: border-box;
        }

        .org-node:hover {
            box-shadow: var(--shadow-md);
            transform: translateY(-3px);
            border-color: var(--primary);
        }

        /* ── Company (root) node ── */
        .org-node.type-company {
            width: 164px;
            background: var(--sidebar-bg);
            border-color: rgba(59,130,246,0.4);
            padding-top: 20px;
        }

        .org-node.type-company .node-name {
            font-weight: 800;
            color: #ffffff;
            font-size: 0.85rem;
            text-align: center;
        }

        .org-node.type-company .node-title {
            color: rgba(255,255,255,0.55);
        }

        /* Top accent bar on root node */
        .org-node.type-company::before {
            content: '';
            position: absolute;
            top: 0;
            left: 20%;
            width: 60%;
            height: 3px;
            background: linear-gradient(90deg, var(--primary), #60a5fa);
            border-radius: 0 0 3px 3px;
        }

        /* ── Department node ── */
        .org-node.type-department {
            background: var(--primary-light);
            border-color: rgba(59,130,246,0.25);
        }

        .org-node.type-department::before {
            content: '';
            position: absolute;
            top: 0;
            left: 20%;
            width: 60%;
            height: 2px;
            background: linear-gradient(90deg, var(--primary), #93c5fd);
            border-radius: 0 0 2px 2px;
        }

        .org-node.type-department .node-name {
            font-weight: 700;
            color: var(--primary-dark);
            font-size: 0.76rem;
            text-align: center;
            text-transform: uppercase;
            letter-spacing: 0.03em;
            line-height: 1.4;
        }

        .org-node.type-department .node-title {
            color: #2563EB;
        }

        /* ── Employee node ── */
        .org-node.type-employee {
            width: 138px;
            padding: 14px 10px 12px;
            background: var(--card-bg);
            border-color: var(--border-color);
        }

        .org-node.type-employee .node-name {
            font-weight: 600;
            color: var(--text-primary);
            font-size: 0.74rem;
            text-align: center;
        }

        /* ── Avatar ── */
        .node-avatar {
            width: 52px;
            height: 52px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid rgba(59,130,246,0.3);
            margin-bottom: 10px;
            background: var(--bg);
            box-shadow: 0 2px 8px rgba(59,130,246,0.15);
        }

        .org-node.type-company .node-avatar {
            border-color: rgba(255,255,255,0.25);
            box-shadow: 0 0 0 3px rgba(59,130,246,0.2);
        }

        .org-node.type-employee .node-avatar {
            width: 40px;
            height: 40px;
            margin-bottom: 8px;
        }

        /* Fallback initials avatar */
        .node-avatar-fallback {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 52px;
            height: 52px;
            border-radius: 50%;
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: #ffffff;
            font-weight: 800;
            font-size: 1.1rem;
            margin-bottom: 10px;
            border: 2px solid rgba(59,130,246,0.3);
            box-shadow: 0 2px 8px rgba(59,130,246,0.2);
            flex-shrink: 0;
        }

        .org-node.type-company .node-avatar-fallback {
            background: linear-gradient(135deg, var(--primary), #818cf8);
            border-color: rgba(255,255,255,0.2);
        }

        .org-node.type-employee .node-avatar-fallback {
            width: 40px;
            height: 40px;
            font-size: 0.9rem;
            margin-bottom: 8px;
            background: linear-gradient(135deg, var(--primary-dark), var(--primary));
        }

        /* ── Node text ── */
        .node-name {
            margin: 0 0 3px;
            line-height: 1.35;
        }

        .node-title {
            font-size: 0.68rem;
            color: var(--text-muted);
            text-align: center;
            margin: 0;
            font-weight: 500;
            line-height: 1.3;
        }

        /* ── Expand/collapse toggle ── */
        .node-toggle {
            position: absolute;
            bottom: -11px;
            left: 50%;
            transform: translateX(-50%);
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: #fff;
            font-size: 0.72rem;
            font-weight: 700;
            line-height: 20px;
            text-align: center;
            cursor: pointer;
            z-index: 2;
            border: 2px solid var(--card-bg);
            box-shadow: 0 2px 6px rgba(59,130,246,0.4);
            display: none;
            transition: transform 0.15s ease, box-shadow 0.15s ease;
        }

        .node-toggle:hover {
            box-shadow: 0 4px 12px rgba(59,130,246,0.5);
            transform: translateX(-50%) scale(1.15);
        }

        /* ── Empty state ── */
        .org-empty {
            text-align: center;
            padding: 4rem 1rem;
            color: var(--text-muted);
        }

        .org-empty svg {
            width: 48px;
            height: 48px;
            opacity: 0.3;
            margin-bottom: 1rem;
        }

        .org-empty p {
            font-size: 0.95rem;
            font-weight: 500;
        }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Sơ đồ tổ chức</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty param.success}">
                <div class="alert alert-success">✓ ${param.success}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-error">⚠ ${param.error}</div>
            </c:if>

            <%-- Toolbar --%>
            <div class="chart-toolbar">
                <span class="chart-toolbar-label">Sơ đồ phân cấp tổ chức</span>
                <div class="chart-toolbar-actions">
                    <button id="btn-zoom-out" title="Thu nhỏ">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/></svg>
                        Thu nhỏ
                    </button>
                    <button id="btn-zoom-in" title="Phóng to">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                        Phóng to
                    </button>
                    <button id="btn-reset" title="Đặt lại">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 .49-3.13"/></svg>
                        Đặt lại
                    </button>
                </div>
            </div>

            <div id="chart-viewport">
                <div id="chart-inner" style="transform-origin: top center; transition: transform 0.2s ease;">

                    <c:choose>
                        <c:when test="${not empty orgRoot}">
                            <ul class="org-tree">
                                <li>
                                    <%-- ROOT NODE --%>
                                    <div class="org-node type-company" id="node-root">
                                        <c:choose>
                                            <c:when test="${not empty orgRoot.avatarUrl}">
                                                <img class="node-avatar"
                                                     src="${orgRoot.avatarUrl}"
                                                     alt="${orgRoot.name}"
                                                     onerror="this.style.display='none';this.nextElementSibling.style.display='flex';">
                                                <div class="node-avatar-fallback" style="display:none;">${orgRoot.name.substring(0,1)}</div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="node-avatar-fallback">${orgRoot.name.substring(0,1)}</div>
                                            </c:otherwise>
                                        </c:choose>
                                        <p class="node-name">${orgRoot.name}</p>
                                        <c:if test="${not empty orgRoot.title}">
                                            <p class="node-title">${orgRoot.title}</p>
                                        </c:if>
                                        <c:if test="${not empty orgRoot.children}">
                                            <span class="node-toggle">+</span>
                                        </c:if>
                                    </div>

                                    <%-- DEPARTMENTS LEVEL --%>
                                    <c:if test="${not empty orgRoot.children}">
                                        <div class="connector-down" style="display:none;"></div>
                                        <div class="children-wrapper" id="children-root" style="display:none;">
                                            <div class="h-bar-row" id="hbar-root">
                                                <c:forEach var="dept" items="${orgRoot.children}">
                                                    <div class="child-col">
                                                        <div class="v-tick"></div>

                                                        <div class="org-node type-department">
                                                            <c:choose>
                                                                <c:when test="${not empty dept.avatarUrl}">
                                                                    <img class="node-avatar"
                                                                         src="${dept.avatarUrl}"
                                                                         alt="${dept.name}"
                                                                         onerror="this.style.display='none';this.nextElementSibling.style.display='flex';">
                                                                    <div class="node-avatar-fallback" style="display:none;">${dept.name.substring(0,1)}</div>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <div class="node-avatar-fallback">${dept.name.substring(0,1)}</div>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <p class="node-name">${dept.name}</p>
                                                            <c:if test="${not empty dept.title}">
                                                                <p class="node-title">${dept.title}</p>
                                                            </c:if>
                                                            <c:if test="${not empty dept.children}">
                                                                <span class="node-toggle">+</span>
                                                            </c:if>
                                                        </div>

                                                        <%-- EMPLOYEES LEVEL --%>
                                                        <c:if test="${not empty dept.children}">
                                                            <div class="connector-down" style="height:28px; display:none;"></div>
                                                            <div class="children-wrapper" style="display:none;">
                                                                <div class="h-bar-row">
                                                                    <c:forEach var="member" items="${dept.children}">
                                                                        <div class="child-col">
                                                                            <div class="v-tick"></div>
                                                                            <div class="org-node type-employee">
                                                                                <c:choose>
                                                                                    <c:when test="${not empty member.avatarUrl}">
                                                                                        <img class="node-avatar"
                                                                                             src="${member.avatarUrl}"
                                                                                             alt="${member.name}"
                                                                                             onerror="this.style.display='none';this.nextElementSibling.style.display='flex';">
                                                                                        <div class="node-avatar-fallback" style="display:none;">${member.name.substring(0,1)}</div>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <div class="node-avatar-fallback">${member.name.substring(0,1)}</div>
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                                <p class="node-name">${member.name}</p>
                                                                                <c:if test="${not empty member.title}">
                                                                                    <p class="node-title">${member.title}</p>
                                                                                </c:if>
                                                                            </div>
                                                                        </div>
                                                                    </c:forEach>
                                                                </div>
                                                            </div>
                                                        </c:if>

                                                    </div><%-- END child-col (dept) --%>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </c:if>

                                </li>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <div class="org-empty">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                                    <rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/>
                                    <rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>
                                </svg>
                                <p>Chưa có dữ liệu tổ chức.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>

                </div><%-- #chart-inner --%>
            </div><%-- #chart-viewport --%>
        </div>
    </div>
</div>

<script>
(function () {
    /* ── Draw horizontal connector bars ── */
    function drawHBars() {
        document.querySelectorAll('.h-bar-row').forEach(function (row) {
            row.querySelectorAll('.h-bar').forEach(function (b) { b.remove(); });

            var cols = row.querySelectorAll(':scope > .child-col');
            if (cols.length < 2) return;

            var rowRect   = row.getBoundingClientRect();
            var firstRect = cols[0].getBoundingClientRect();
            var lastRect  = cols[cols.length - 1].getBoundingClientRect();

            var left  = firstRect.left  + firstRect.width  / 2 - rowRect.left;
            var right = lastRect.left   + lastRect.width   / 2 - rowRect.left;

            var bar = document.createElement('div');
            bar.className = 'h-bar';
            bar.style.left  = left  + 'px';
            bar.style.width = (right - left) + 'px';
            row.appendChild(bar);
        });
    }

    /* ── Zoom controls ── */
    var scale = 1;
    var inner = document.getElementById('chart-inner');

    document.getElementById('btn-zoom-in').addEventListener('click', function () {
        scale = Math.min(scale + 0.1, 2.5);
        inner.style.transform = 'scale(' + scale + ')';
    });
    document.getElementById('btn-zoom-out').addEventListener('click', function () {
        scale = Math.max(scale - 0.1, 0.3);
        inner.style.transform = 'scale(' + scale + ')';
    });
    document.getElementById('btn-reset').addEventListener('click', function () {
        scale = 1;
        inner.style.transform = 'scale(1)';
    });

    /* ── Toggle expand / collapse ── */
    document.querySelectorAll('.node-toggle').forEach(function (btn) {
        btn.style.display = 'block';
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            var col      = btn.closest('.child-col') || btn.closest('li');
            var willOpen = btn.textContent.trim() === '+';

            Array.from(col.children).forEach(function (el) {
                if (!el.classList.contains('org-node') && !el.classList.contains('v-tick')) {
                    el.style.display = willOpen ? '' : 'none';
                }
            });

            btn.textContent = willOpen ? '−' : '+';
            drawHBars();
        });
    });

    window.addEventListener('load', drawHBars);
    window.addEventListener('resize', drawHBars);
    setTimeout(drawHBars, 120);
})();
</script>
</body>
</html>
