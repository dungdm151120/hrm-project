package model;

import java.sql.Timestamp;

public class Department {
    private int id;
    private String name;
    private String description;
    private String managerName;
    private Integer managerUserId;  // có thể null
    private boolean active;
    private Timestamp createdAt;
    private Timestamp updatedAt;


    public Department() {
    }


    public Department(int id, String name, String description, Integer managerUserId,
                      String managerName, boolean active, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.managerUserId = managerUserId;
        this.managerName = managerName;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public Department(String name, String description, Integer managerUserId, boolean active) {
        this.name = name;
        this.description = description;
        this.managerUserId = managerUserId;
        this.active = active;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getManagerUserId() {
        return managerUserId;
    }

    public void setManagerUserId(Integer managerUserId) {
        this.managerUserId = managerUserId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", managerUserId=" + managerUserId +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}