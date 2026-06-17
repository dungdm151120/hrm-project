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
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/chat.css">
</head>
<body class="dashboard-body">
<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="dashboard-main">
        <div class="dashboard-content chat-dashboard-content">
            <div class="chat-layout">
                <!-- Sidebar danh sách hội thoại -->
                <aside class="chat-sidebar">
                    <div class="chat-sidebar-header">
                        <h2>Chat</h2>
                        <div class="chat-header-actions">
                            <button id="newChatBtn" class="chat-icon-btn" title="New chat">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 5v14M5 12h14"/></svg>
                            </button>
                            <button class="chat-icon-btn" title="Settings">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="1"/><circle cx="19" cy="12" r="1"/><circle cx="5" cy="12" r="1"/></svg>
                            </button>
                        </div>
                    </div>
                    <div class="chat-search">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                        <input type="text" id="searchUserInput" placeholder="Search Messenger..." autocomplete="off">
                        <div id="searchResults" class="search-results-dropdown hidden"></div>
                    </div>
                    <div id="conversationList" class="chat-conversation-list"></div>
                </aside>

                <!-- Khu vực chat chính -->
                <section class="chat-main" id="chatMain">
                    <div class="chat-placeholder" id="chatPlaceholder">
                        <div class="placeholder-icon">
                            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2"><path d="M21 11.5a8.38 8.38 0 0 1-1.76 5.18 8.5 8.5 0 0 1-9.3 3.76 8.38 8.38 0 0 1-5.18-1.76L2 21l3.3-3.3A8.5 8.5 0 0 1 12.5 3a8.38 8.38 0 0 1 5.18 1.76A8.5 8.5 0 0 1 21 11.5z"/></svg>
                        </div>
                        <h3>Select a chat to continue</h3>
                        <p>Choose a person or group to start messaging</p>
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
                            <div class="chat-header-actions">
                                <button class="chat-icon-btn" title="Call">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
                                </button>
                                <button class="chat-icon-btn" title="More">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="1"/><circle cx="19" cy="12" r="1"/><circle cx="5" cy="12" r="1"/></svg>
                                </button>
                            </div>
                        </header>
                        <div class="chat-messages" id="chatMessages"></div>
                        <form id="messageForm" class="chat-input-area" autocomplete="off">
                            <button type="button" class="chat-attach-btn" title="Attach">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21.5 2v6h-6M2.5 22v-6h6M2 11.5a10 10 0 0 0 19.8 4.3M22 12.5a10 10 0 0 0-19.8-4.2"/></svg>
                            </button>
                            <input type="text" id="messageInput" placeholder="Aa" autocomplete="off">
                            <button type="submit" class="chat-send-btn">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M16 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="10" cy="7" r="4"/></svg>
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
