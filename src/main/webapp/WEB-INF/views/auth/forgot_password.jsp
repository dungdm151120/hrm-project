<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password | HRSync</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        body.login-page {
            padding: 0;
            margin: 0;
            background: var(--bg-main);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .login-card {
            width: 100%;
            max-width: 420px;
            background: var(--bg-card);
            border-radius: var(--radius-md);
            box-shadow: var(--shadow-lg);
            padding: 2.5rem;
            border: 1px solid var(--border-color);
        }

        .login-logo {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 2rem;
            justify-content: center;
        }

        .login-logo svg {
            width: 48px;
            height: 48px;
        }

        .login-logo .logo-text {
            font-size: 1.75rem;
            font-weight: 700;
            color: var(--text-primary);
            letter-spacing: -0.02em;
        }

        .login-logo .logo-text span {
            color: var(--brand-primary);
        }

        .login-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .login-header h1 {
            font-size: 1.75rem;
            font-weight: 700;
            color: var(--text-primary);
            margin-bottom: 0.5rem;
        }

        .login-header p {
            font-size: 0.9rem;
            color: var(--text-secondary);
        }

        .form-group {
            margin-bottom: 1.25rem;
        }

        .form-group label {
            display: block;
            font-weight: 500;
            font-size: 0.85rem;
            color: var(--text-primary);
            margin-bottom: 0.4rem;
        }

        .form-group input[type="email"],
        .form-group textarea {
            width: 100%;
            padding: 0.75rem 0.9rem;
            font-family: inherit;
            font-size: 0.9rem;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-sm);
            background-color: var(--bg-input);
            color: var(--text-primary);
            transition: border-color 0.2s, box-shadow 0.2s;
            outline: none;
            resize: vertical;
        }

        .form-group textarea {
            min-height: 100px;
        }

        .form-group input:focus,
        .form-group textarea:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
        }

        .btn-login {
            width: 100%;
            padding: 0.75rem;
            background: var(--primary);
            color: white;
            font-weight: 600;
            font-size: 0.95rem;
            border: none;
            border-radius: var(--radius-sm);
            cursor: pointer;
            transition: background 0.2s, transform 0.2s;
            font-family: inherit;
            margin-top: 0.5rem;
        }

        .btn-login:hover {
            background: var(--primary-dark);
            transform: translateY(-1px);
        }

        .login-footer {
            margin-top: 1.5rem;
            text-align: center;
        }

        .login-footer a {
            color: var(--primary-light);
            text-decoration: none;
            font-weight: 500;
            font-size: 0.85rem;
            transition: color 0.2s;
        }

        .login-footer a:hover {
            color: var(--accent-blue);
        }

        .alert {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 0.75rem 1rem;
            border-radius: var(--radius-sm);
            font-size: 0.85rem;
            margin-bottom: 1.25rem;
        }

        .alert-error {
            background: var(--danger-bg);
            color: var(--accent-red);
            border: 1px solid rgba(239, 68, 68, 0.2);
        }

        .alert-success {
            background: var(--success-bg);
            color: var(--accent-green);
            border: 1px solid rgba(16, 185, 129, 0.2);
        }

        @media (max-width: 480px) {
            .login-card {
                margin: 1rem;
                padding: 1.5rem;
            }
        }
    </style>
</head>
<body class="login-page">

<div class="login-card">
    <div class="login-logo">
        <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M24 4C12.954 4 4 12.954 4 24s8.954 20 20 20 20-8.954 20-20S35.046 4 24 4z" fill="#FF9F43"/>
            <path d="M24 8c-8.837 0-16 7.163-16 16s7.163 16 16 16 16-7.163 16-16S32.837 8 24 8z" fill="#0F172A"/>
            <path d="M24 12c-6.627 0-12 5.373-12 12s5.373 12 12 12 12-5.373 12-12-5.373-12-12-12z" fill="#3B82F6"/>
            <circle cx="24" cy="24" r="6" fill="#FF9F43"/>
        </svg>
        <span class="logo-text">HR<span>Sync</span></span>
    </div>

    <div class="login-header">
        <h1>Forgot Password</h1>
        <p>Enter your email and we'll help you reset your password</p>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">
            <span>⚠</span> <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success">
            <span>✓</span> <%= request.getAttribute("success") %>
        </div>
    <% } %>

    <form action="<%= request.getContextPath() %>/forgot-password" method="post">
        <div class="form-group">
            <label for="email">Email Address <span style="color: var(--danger);">*</span></label>
            <input type="email" id="email" name="email" placeholder="name@company.com" required>
        </div>

        <div class="form-group">
            <label for="reason">Reason</label>
            <textarea id="reason" name="reason" rows="4" placeholder="Describe why you need to reset your password..."></textarea>
        </div>

        <button type="submit" class="btn-login">Send Request to Admin</button>
    </form>

    <div class="login-footer">
        <a href="<%= request.getContextPath() %>/login">← Back to Login</a>
    </div>
</div>

</body>
</html>