<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chat | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/chat.css">
</head>
<body class="dashboard-body">
<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="dashboard-main">
        <header class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Messages</h1>
            </div>
        </header>

        <div class="dashboard-content chat-dashboard-content">
            <div class="chat-layout">
                <!-- Sidebar danh sách hội thoại -->
                <aside class="chat-sidebar">
                    <div class="chat-sidebar-header">
                        <h2>Chats</h2>
                        <button id="newChatBtn" class="chat-icon-btn" title="New chat">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
                        </button>
                    </div>
                    <div class="chat-search">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                        <input type="text" id="searchUserInput" placeholder="Search people..." autocomplete="off">
                        <div id="searchResults" class="search-results-dropdown hidden"></div>
                    </div>
                    <div id="conversationList" class="chat-conversation-list"></div>
                </aside>

                <!-- Khu vực chat chính -->
                <section class="chat-main" id="chatMain">
                    <div class="chat-placeholder" id="chatPlaceholder">
                        <div class="placeholder-icon">
                            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 11.5a8.38 8.38 0 0 1-1.76 5.18 8.5 8.5 0 0 1-9.3 3.76 8.38 8.38 0 0 1-5.18-1.76L2 21l3.3-3.3A8.5 8.5 0 0 1 12.5 3a8.38 8.38 0 0 1 5.18 1.76A8.5 8.5 0 0 1 21 11.5z"/></svg>
                        </div>
                        <h3>Your messages</h3>
                        <p>Select a conversation or start a new one.</p>
                    </div>

                    <div id="chatArea" class="chat-area hidden">
                        <header class="chat-header">
                            <div class="chat-header-info">
                                <div class="chat-avatar" id="chatAvatar"></div>
                                <div>
                                    <h3 id="chatName"></h3>
                                    <span id="chatStatus"></span>
                                </div>
                            </div>
                        </header>
                        <div class="chat-messages" id="chatMessages"></div>
                        <form id="messageForm" class="chat-input-area" autocomplete="off">
                            <input type="text" id="messageInput" placeholder="Type a message..." autocomplete="off">
                            <button type="submit" class="chat-send-btn">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
                            </button>
                        </form>
                    </div>
                </section>
            </div>
        </div>
    </main>
</div>

<!-- Truyền thông tin user hiện tại từ session sang JS để phân biệt tin nhắn sent/received -->
<script>
    window.CURRENT_USER = {
        id: <c:out value="${sessionScope.userId}" default="null"/>,
        fullName: "<c:out value="${sessionScope.fullName}" default=""/>"
    };
</script>
<script src="${pageContext.request.contextPath}/assets/js/chat.js"></script>
</body>
</html>
