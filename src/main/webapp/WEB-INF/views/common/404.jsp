<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Page Not Found</title>
    <style>
        :root {
            --bg-start: #eef6ff;
            --bg-end: #f8fafc;
            --ink: #172033;
            --muted: #64748b;
            --primary: #2563eb;
            --primary-dark: #1d4ed8;
            --card: rgba(255, 255, 255, 0.9);
            --border: rgba(148, 163, 184, 0.28);
        }

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            display: grid;
            place-items: center;
            padding: 24px;
            font-family: "Segoe UI", Tahoma, sans-serif;
            color: var(--ink);
            background:
                    radial-gradient(circle at top left, rgba(37, 99, 235, 0.16), transparent 32rem),
                    radial-gradient(circle at bottom right, rgba(14, 165, 233, 0.18), transparent 28rem),
                    linear-gradient(135deg, var(--bg-start), var(--bg-end));
        }

        .error-card {
            width: min(100%, 680px);
            padding: 48px;
            border: 1px solid var(--border);
            border-radius: 28px;
            background: var(--card);
            box-shadow: 0 24px 70px rgba(15, 23, 42, 0.12);
            text-align: center;
        }

        .error-code {
            margin: 0;
            font-size: clamp(84px, 20vw, 168px);
            line-height: 0.9;
            font-weight: 800;
            letter-spacing: -0.08em;
            color: var(--primary);
        }

        .error-title {
            margin: 24px 0 12px;
            font-size: clamp(28px, 5vw, 42px);
            line-height: 1.15;
        }

        .error-message {
            margin: 0 auto 32px;
            max-width: 520px;
            color: var(--muted);
            font-size: 17px;
            line-height: 1.7;
        }

        .actions {
            display: flex;
            justify-content: center;
            gap: 12px;
            flex-wrap: wrap;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-height: 46px;
            padding: 0 20px;
            border-radius: 999px;
            border: 1px solid transparent;
            font-weight: 700;
            text-decoration: none;
            transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
        }

        .btn:hover {
            transform: translateY(-2px);
        }

        .btn-primary {
            color: #ffffff;
            background: var(--primary);
            box-shadow: 0 12px 24px rgba(37, 99, 235, 0.26);
        }

        .btn-primary:hover {
            background: var(--primary-dark);
        }

        .btn-secondary {
            color: var(--ink);
            border-color: var(--border);
            background: #ffffff;
        }

        @media (max-width: 560px) {
            .error-card {
                padding: 36px 24px;
                border-radius: 22px;
            }

            .actions {
                flex-direction: column;
            }

            .btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
<main class="error-card">
    <p class="error-code">404</p>
    <h1 class="error-title">Page Not Found</h1>
    <p class="error-message">
        The page you are looking for does not exist or has been moved.
        Return to the home page to continue using the HRM system.
    </p>
    <div class="actions">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/home">Back to Home</a>
        <a class="btn btn-secondary" href="javascript:history.back()">Go Back</a>
    </div>
</main>
</body>
</html>
