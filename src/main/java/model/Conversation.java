package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Conversation {
    private int id;
    private boolean group;
    private String name;          // chỉ dùng cho nhóm
    private LocalDateTime createdAt;

    // Danh sách thành viên (tham gia)
    private List<User> participants;
    // Tin nhắn cuối cùng (dùng để hiển thị preview)
    private Message lastMessage;

    public Conversation() {
        this.participants = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isGroup() { return group; }
    public void setGroup(boolean group) { this.group = group; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<User> getParticipants() { return participants; }
    public void setParticipants(List<User> participants) { this.participants = participants; }

    public Message getLastMessage() { return lastMessage; }
    public void setLastMessage(Message lastMessage) { this.lastMessage = lastMessage; }
}