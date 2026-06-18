package model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private long id;
    private String title;
    private String description;
    private String status;
    private Date deadline;
    private int progress;
    private boolean allowParticipantsCompleteChecklist;
    private long createdBy;
    private long assignedTo;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private String createdByName;
    private String assignedToName;
    private List<TaskParticipant> participants = new ArrayList<>();
    private List<TaskObserver> observers = new ArrayList<>();
    private List<TaskChecklistItem> checklistItems = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isAllowParticipantsCompleteChecklist() {
        return allowParticipantsCompleteChecklist;
    }

    public void setAllowParticipantsCompleteChecklist(boolean allowParticipantsCompleteChecklist) {
        this.allowParticipantsCompleteChecklist = allowParticipantsCompleteChecklist;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public List<TaskParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<TaskParticipant> participants) {
        this.participants = participants;
    }

    public List<TaskObserver> getObservers() {
        return observers;
    }

    public void setObservers(List<TaskObserver> observers) {
        this.observers = observers;
    }

    public List<TaskChecklistItem> getChecklistItems() {
        return checklistItems;
    }

    public void setChecklistItems(List<TaskChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }

    public String getDisplayStatus() {
        if (deadline != null && !"COMPLETED".equals(status) && deadline.toLocalDate().isBefore(LocalDate.now())) {
            return "OVERDUE";
        }
        return status;
    }

    public String getReadableStatus() {
        return switch (getDisplayStatus()) {
            case "TODO" -> "To do";
            case "IN_PROGRESS" -> "In progress";
            case "COMPLETED" -> "Completed";
            case "PAUSED" -> "Paused";
            case "OVERDUE" -> "Overdue";
            default -> getDisplayStatus();
        };
    }
}
