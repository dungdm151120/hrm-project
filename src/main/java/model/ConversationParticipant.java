package model;

import java.time.LocalDateTime;

public class ConversationParticipant {
    private int conversationId;
    private int userId;
    private LocalDateTime joinedAt;

    // Để hiển thị tên user trong nhóm (nếu cần)
    private String fullName;
    private String avatarUrl;

    public ConversationParticipant() {}

    public int getConversationId() { return conversationId; }
    public void setConversationId(int conversationId) { this.conversationId = conversationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}