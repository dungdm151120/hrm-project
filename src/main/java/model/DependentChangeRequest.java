package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DependentChangeRequest {
    private int id;
    private int requestId;
    private String changeType;
    private Integer dependentId;
    private String dependentName;
    private LocalDate dependentDob;
    private String dependentIdNumber;
    private String relationship;
    private String documentPath;
    private LocalDateTime createdAt;

    public DependentChangeRequest() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public Integer getDependentId() {
        return dependentId;
    }

    public void setDependentId(Integer dependentId) {
        this.dependentId = dependentId;
    }

    public String getDependentName() {
        return dependentName;
    }

    public void setDependentName(String dependentName) {
        this.dependentName = dependentName;
    }

    public LocalDate getDependentDob() {
        return dependentDob;
    }

    public void setDependentDob(LocalDate dependentDob) {
        this.dependentDob = dependentDob;
    }

    public String getDependentIdNumber() {
        return dependentIdNumber;
    }

    public void setDependentIdNumber(String dependentIdNumber) {
        this.dependentIdNumber = dependentIdNumber;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
