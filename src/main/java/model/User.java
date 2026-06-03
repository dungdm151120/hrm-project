package model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private LocalDateTime dateOfBirth;
    private String address;
    private String avatarUrl;
    private int roleId;
    private String roleName;
    private Integer departmentId;   // mới thêm
    private String departmentName; // moi them
    private Integer positionId;     // mới thêm (phòng hờ sau này)
    private String positionName; // moi them
    private boolean isManager;
    private boolean active;
    private String resetToken;
    private LocalDateTime resetTokenExpiredAt;

    public User() {}

    public User(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }
    // Getter/Setter cho departmentId
    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    // Getter/Setter cho positionId
    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    //getter/setter cho position/department name
    public String getPositionName() {
        return positionName;
    }
    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    // --- Các getter/setter còn lại giữ nguyên ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isManager() { return isManager; }

    public void setManager(boolean manager) { isManager = manager; }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiredAt() {
        return resetTokenExpiredAt;
    }

    public void setResetTokenExpiredAt(LocalDateTime resetTokenExpiredAt) {
        this.resetTokenExpiredAt = resetTokenExpiredAt;
    }
}
