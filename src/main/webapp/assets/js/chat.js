const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));
const API = {
    conversations: contextPath + '/chat/conversations',
    messages: contextPath + '/chat/messages',
    send: contextPath + '/chat/send',
    start: contextPath + '/chat/start',
    read: contextPath + '/chat/read',
    search: contextPath + '/chat/search'
};

let currentConversationId = null;
let currentUserId = (window.CURRENT_USER && window.CURRENT_USER.id) || null;
let currentUserName = (window.CURRENT_USER && window.CURRENT_USER.fullName) || null;
let pollingInterval = null;
let lastMessageId = 0;
let isLoadingMore = false;

document.addEventListener('DOMContentLoaded', () => {
    initChat();
});

async function initChat() {
    await loadConversations();
    setupEventListeners();
    startPolling();
}

async function loadConversations() {
    try {
        const response = await fetch(API.conversations);
        if (!response.ok) throw new Error('Failed to load conversations');
        const conversations = await response.json();
        renderConversations(conversations);
    } catch (error) {
        console.error('Error loading conversations:', error);
    }
}

function renderConversations(conversations) {
    const container = document.getElementById('conversationList');
    if (!container) return;

    container.innerHTML = conversations.map(conv => {
        const participant = conv.participants.find(p => p.id !== currentUserId) || conv.participants[0];
        const name = conv.isGroup ? (conv.name || 'Group') : (participant?.fullName || 'Unknown');
        const avatar = participant?.fullName?.charAt(0) || '?';
        const lastMsg = conv.lastMessage;
        const time = lastMsg?.sentAt ? formatTime(lastMsg.sentAt) : formatTime(conv.createdAt);
        const preview = lastMsg ? (lastMsg.senderName + ': ' + lastMsg.content) : 'Start chatting';
        const isActive = conv.id === currentConversationId;
        const isOnline = participant?.online === true;
        const unread = conv.unreadCount > 0;

        return `
            <div class="conversation-item ${isActive ? 'active' : ''}" onclick="openConversation(${conv.id}, '${escapeHtml(name)}', '${avatar}')">
                <div class="conversation-avatar${isOnline ? ' online' : ''}">${avatar}</div>
                <div class="conversation-info">
                    <div class="conversation-name">${escapeHtml(name)}</div>
                    <div class="conversation-preview">${escapeHtml(preview)}</div>
                </div>
                <div class="conversation-meta">
                    <div class="conversation-time">${time}</div>
                    ${unread ? `<span class="unread-badge">${conv.unreadCount}</span>` : ''}
                </div>
            </div>
        `;
    }).join('');
}

async function openConversation(conversationId, name, avatar) {
    currentConversationId = conversationId;
    document.getElementById('chatPlaceholder').classList.add('hidden');
    document.getElementById('chatArea').classList.remove('hidden');
    document.getElementById('chatName').textContent = name;
    document.getElementById('chatAvatar').textContent = avatar;

    // Scroll to top and load messages
    const messagesContainer = document.getElementById('chatMessages');
    messagesContainer.scrollTop = 0;

    await loadMessages(conversationId);
    await markAsRead(conversationId);
    loadConversations();
}

async function loadMessages(conversationId, offset = 0) {
    try {
        const response = await fetch(`${API.messages}?conversationId=${conversationId}&offset=${offset}&limit=50`);
        if (!response.ok) throw new Error('Failed to load messages');
        const messages = await response.json();
        if (offset === 0) {
            renderMessages(messages);
        } else {
            prependMessages(messages);
        }
        if (messages.length > 0) {
            lastMessageId = Math.max(lastMessageId, ...messages.map(m => m.id));
        }
    } catch (error) {
        console.error('Error loading messages:', error);
    }
}

function renderMessages(messages) {
    const container = document.getElementById('chatMessages');
    container.innerHTML = buildMessagesHTML(messages);
    scrollToBottom();
}

function prependMessages(messages) {
    const container = document.getElementById('chatMessages');
    const oldScrollHeight = container.scrollHeight;
    const oldScrollTop = container.scrollTop;
    const html = buildMessagesHTML(messages);
    container.insertAdjacentHTML('afterbegin', html);
    container.scrollTop = oldScrollTop + (container.scrollHeight - oldScrollHeight);
}

/**
 * Dựng HTML cho 1 danh sách tin nhắn, có chèn date divider và
 * chỉ hiện avatar ở tin đầu mỗi cụm liên tiếp của người gửi (giống Messenger).
 */
function buildMessagesHTML(messages) {
    let html = '';
    let lastDateKey = null;
    let lastSenderId = null;

    messages.forEach((msg, index) => {
        const dateKey = new Date(msg.sentAt).toDateString();
        if (dateKey !== lastDateKey) {
            html += `<div class="chat-date-divider"><span>${formatDateDivider(msg.sentAt)}</span></div>`;
            lastDateKey = dateKey;
            lastSenderId = null; // reset cụm khi qua ngày mới
        }

        const isFirstInGroup = msg.senderId !== lastSenderId;
        html += createMessageHTML(msg, isFirstInGroup);
        lastSenderId = msg.senderId;
    });

    return html;
}

function createMessageHTML(msg, showAvatar = true) {
    const isSent = msg.senderId === currentUserId;
    const wrapperClass = isSent ? 'sent' : 'received';
    const time = formatTime(msg.sentAt);
    const avatarInitial = msg.senderName ? msg.senderName.charAt(0).toUpperCase() : '?';

    // Avatar chỉ áp dụng cho tin nhắn received; tin sent của mình không cần avatar bên cạnh
    const avatarHtml = !isSent
        ? `<div class="message-avatar${showAvatar ? '' : ' spacer'}">${avatarInitial}</div>`
        : '';

    return `
        <div class="message-wrapper ${wrapperClass}">
            ${avatarHtml}
            <div class="message-content">
                <div class="message-bubble">
                    ${(!isSent && showAvatar) ? `<div class="message-sender">${escapeHtml(msg.senderName)}</div>` : ''}
                    <div>${escapeHtml(msg.content)}</div>
                </div>
                <div class="message-time">${time}</div>
            </div>
        </div>
    `;
}

async function sendMessage(content) {
    if (!currentConversationId || !content.trim()) return;

    const input = document.getElementById('messageInput');
    const sendBtn = document.getElementById('messageForm').querySelector('.chat-send-btn');

    // Disable button while sending
    sendBtn.disabled = true;

    try {
        const response = await fetch(API.send, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ conversationId: currentConversationId, content: content.trim() })
        });
        if (!response.ok) throw new Error('Failed to send message');
        const msg = await response.json();
        appendMessage(msg);
        input.value = '';
        loadConversations();
    } catch (error) {
        console.error('Error sending message:', error);
        alert('Failed to send message. Please try again.');
    } finally {
        sendBtn.disabled = false;
        input.focus();
    }
}

function appendMessage(msg) {
    const container = document.getElementById('chatMessages');
    const lastWrapper = container.querySelector('.message-wrapper:last-of-type');
    // Nếu tin trước đó cùng người gửi và cùng ngày thì không cần thêm divider/avatar mới
    const showAvatar = true; // tin mới gửi luôn coi là đầu cụm hiển thị đơn giản
    container.insertAdjacentHTML('beforeend', createMessageHTML(msg, showAvatar));
    scrollToBottom();
    lastMessageId = msg.id;
}

async function markAsRead(conversationId) {
    try {
        await fetch(API.read, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ conversationId: conversationId })
        });
    } catch (error) {
        console.error('Error marking as read:', error);
    }
}

async function searchUsers(keyword) {
    const dropdown = document.getElementById('searchResults');
    if (!keyword.trim()) {
        dropdown.classList.add('hidden');
        return;
    }
    try {
        const response = await fetch(`${API.search}?q=${encodeURIComponent(keyword)}`);
        if (!response.ok) throw new Error('Search failed');
        const users = await response.json();
        renderSearchResults(users);
    } catch (error) {
        console.error('Error searching users:', error);
    }
}

function renderSearchResults(users) {
    const dropdown = document.getElementById('searchResults');
    if (users.length === 0) {
        dropdown.innerHTML = '<div style="padding: 0.75rem 1rem; color: #90949c; font-size: 0.85rem;">No users found</div>';
        dropdown.classList.remove('hidden');
        return;
    }
    dropdown.innerHTML = users.map(user => `
        <div class="search-result-item" onclick="startConversation(${user.id}, '${escapeHtml(user.fullName)}')">
            <div class="search-result-avatar">${user.fullName.charAt(0)}</div>
            <div>
                <div class="search-result-name">${escapeHtml(user.fullName)}</div>
                <div class="search-result-email">${escapeHtml(user.email)}</div>
            </div>
        </div>
    `).join('');
    dropdown.classList.remove('hidden');
}

async function startConversation(recipientId, recipientName) {
    try {
        const response = await fetch(API.start, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ recipientId: recipientId })
        });
        if (!response.ok) throw new Error('Failed to start conversation');
        const data = await response.json();
        document.getElementById('searchResults').classList.add('hidden');
        document.getElementById('searchUserInput').value = '';
        await loadConversations();
        openConversation(data.conversationId, recipientName, recipientName.charAt(0));
    } catch (error) {
        console.error('Error starting conversation:', error);
    }
}

function startPolling() {
    if (pollingInterval) clearInterval(pollingInterval);
    pollingInterval = setInterval(pollNewMessages, 3000);
}

async function pollNewMessages() {
    if (!currentConversationId) return;
    try {
        const response = await fetch(`${API.messages}?conversationId=${currentConversationId}&offset=0&limit=1`);
        if (!response.ok) return;
        const messages = await response.json();
        if (messages.length > 0 && messages[0].id > lastMessageId) {
            await loadMessages(currentConversationId);
            await markAsRead(currentConversationId);
            loadConversations();
        }
    } catch (error) {
        console.error('Polling error:', error);
    }
}

function setupEventListeners() {
    // Message form submit
    document.getElementById('messageForm').addEventListener('submit', (e) => {
        e.preventDefault();
        const input = document.getElementById('messageInput');
        sendMessage(input.value);
    });

    // New chat button
    document.getElementById('newChatBtn').addEventListener('click', () => {
        const searchInput = document.getElementById('searchUserInput');
        searchInput.focus();
        searchInput.value = '';
        document.getElementById('searchResults').classList.add('hidden');
    });

    // Search with debounce
    let searchTimeout;
    document.getElementById('searchUserInput').addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => searchUsers(e.target.value), 300);
    });

    // Close search dropdown when clicking outside
    document.addEventListener('click', (e) => {
        const dropdown = document.getElementById('searchResults');
        const searchInput = document.getElementById('searchUserInput');
        if (!searchInput.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.classList.add('hidden');
        }
    });

    // Load more messages on scroll to top
    const messagesContainer = document.getElementById('chatMessages');
    messagesContainer.addEventListener('scroll', () => {
        if (messagesContainer.scrollTop === 0 && !isLoadingMore && currentConversationId) {
            isLoadingMore = true;
            const messageWrappers = messagesContainer.querySelectorAll('.message-wrapper');
            if (messageWrappers.length > 0) {
                const offset = messageWrappers.length;
                loadMessages(currentConversationId, offset).finally(() => {
                    isLoadingMore = false;
                });
            }
        }
    });
}

function scrollToBottom() {
    const container = document.getElementById('chatMessages');
    setTimeout(() => {
        container.scrollTop = container.scrollHeight;
    }, 0);
}

function formatTime(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    const now = new Date();
    const isToday = date.toDateString() === now.toDateString();
    if (isToday) {
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    return date.toLocaleDateString([], { month: 'short', day: 'numeric' }) + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

function formatDateDivider(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const yesterday = new Date(now);
    yesterday.setDate(now.getDate() - 1);

    if (date.toDateString() === now.toDateString()) return 'Today';
    if (date.toDateString() === yesterday.toDateString()) return 'Yesterday';
    return date.toLocaleDateString([], { weekday: 'short', month: 'short', day: 'numeric' });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
