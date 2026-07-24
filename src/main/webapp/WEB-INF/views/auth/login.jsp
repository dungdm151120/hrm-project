<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập | HRSync</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Playfair+Display:ital,wght@1,700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="login-body">

<div class="login-container">

    <div class="login-right" style="width: 66.666%;">
        <div class="decoration decoration-1"></div>
        <div class="decoration decoration-2"></div>
        <div class="decoration decoration-3"></div>

        <div class="quote-container" style="text-align: center;">
            <blockquote class="quote-text" style="font-size: 3.2rem; line-height: 1.4; font-weight: 700; margin: 0 0 2.5rem 0; font-style: italic; font-family: 'Playfair Display', Georgia, serif;">
                Your most reliable Human Resource System.
            </blockquote>

            <div class="stats-bar" style="display: flex; justify-content: center; align-items: center; gap: 2rem; color: rgba(255,255,255,0.75); font-weight: 500; font-size: 1.1rem; letter-spacing: 0.1em; font-family: 'Inter', sans-serif; text-transform: uppercase;">
                <span>Easy</span>
                <span style="opacity: 0.4;">|</span>
                <span>Fast</span>
                <span style="opacity: 0.4;">|</span>
                <span>Reliable</span>
            </div>
        </div>
    </div>

    <div class="login-left" style="width: 33.333%; border-right: none; border-left: 1px solid rgba(255,255,255,0.08);">
        <div class="login-content">

            <h2 class="login-title" style="text-align: center;">Welcome Back</h2>
            <p class="login-subtitle" style="text-align: center;">Sign in to your account to continue</p>

            <% if (request.getAttribute("error") != null) { %>
                <div class="login-alert alert-error">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="12" y1="8" x2="12" y2="12"></line>
                        <line x1="12" y1="16" x2="12.01" y2="16"></line>
                    </svg>
                    <span><%= request.getAttribute("error") %></span>
                </div>
            <% } %>

            <form class="login-form" action="<%= request.getContextPath() %>/login" method="post">
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" placeholder="your@email.com"
                           maxlength="100" autocomplete="username"
                           value="<c:out value='${email}'/>" required>
                </div>

                <div class="form-group">
                    <div class="password-header">
                        <label for="password">Password</label>
                        <a href="<%= request.getContextPath() %>/forgot-password" class="forgot-link">Forgot?</a>
                    </div>
                    <input type="password" id="password" name="password" placeholder="••••••••"
                           maxlength="72" autocomplete="current-password" required>
                </div>

                <button type="submit" class="btn-login">
                    <span>Sign In</span>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                        <polyline points="12 5 19 12 12 19"></polyline>
                    </svg>
                </button>
            </form>

            <div class="security-badges" style="justify-content: center;">
                <div class="badge">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                    </svg>
                </div>
                <div class="badge">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
                        <polyline points="2 12 12 17 22 12"></polyline>
                        <polyline points="2 17 12 22 22 17"></polyline>
                    </svg>
                </div>
            </div>
        </div>
    </div>

</div>

</body>
</html>
