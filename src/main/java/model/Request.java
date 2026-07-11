package model;

import java.sql.Timestamp;
import java.util.*;

public class Request {
    private int id;
    private int userId;
    private String proposerName;
    private Integer departmentId;
    private String departmentName;
    private String type;
    private String reason;
    private String status;
    private int approverId;
    private String approverName;
    private String approverComment;
    private List<User> observer;
    private int handlerId;
    private String handlerName;
    private Timestamp createdAt;
    private Timestamp processedAt;

    public Request() {
        this.observer = new ArrayList<>();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProposerName() {
        return proposerName;
    }

    public void setProposerName(String proposerName) {
        this.proposerName = proposerName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getApproverId() {
        return approverId;
    }

    public void setApproverId(int approverId) {
        this.approverId = approverId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getApproverComment() {
        return approverComment;
    }

    public void setApproverComment(String approverComment) {
        this.approverComment = approverComment;
    }

    public List<User> getObserver() {
        return observer;
    }

    public void setObserver(List<User> observer) {
        this.observer = observer;
    }

    public int getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(int handlerId) {
        this.handlerId = handlerId;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Timestamp processedAt) {
        this.processedAt = processedAt;
    }

    // Dung cho list va detail
    public String getReadableType() {
        if (this.type == null) return "N/A";
        switch (this.type) {
            case "LEAVE_REQUEST": return "Leave request";
            case "LATE_EARLY_REQUEST": return "Late arrival/Early departure";
            case "EMP_MOVE_REMOVE": return "Move/Remove employee";
            case "POSITION_HANDOVER": return "Position handover";
            case "OVERTIME": return "Overtime";
            case "ATTENDANCE_ADJUST": return "Attendance adjust";
            case "SICK_LEAVE_REQUEST": return "Sick leave request";
            case "DEPENDENT_CHANGE_REQUEST": return "Dependent change request";
            default: return this.type;
        }
    }

    // Dung cho create, filter
    public static Map<String, String> getAllType() {
        Map<String, String> type = new LinkedHashMap<>();
        type.put("LEAVE_REQUEST", "Leave request");
        type.put("LATE_EARLY_REQUEST", "Late arrival/Early departure");
        type.put("EMP_MOVE_REMOVE", "Move/Remove employee");
        type.put("POSITION_HANDOVER", "Position handover");
        type.put("OVERTIME", "Overtime");
        type.put("ATTENDANCE_ADJUST", "Attendance adjust");
        type.put("SICK_LEAVE_REQUEST", "Sick leave request");
        type.put("DEPENDENT_CHANGE_REQUEST", "Dependent change request");
        return type;
    }
}