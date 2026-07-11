DROP DATABASE IF EXISTS hrm_db;
CREATE DATABASE hrm_db;
USE hrm_db;

-- ============================================================
-- 1. CORE TABLES (ROLES, DEPARTMENTS, POSITIONS, USERS)
-- ============================================================

CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME
);

CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    manager_user_id INT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME
);

CREATE TABLE positions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME
);

CREATE TABLE department_positions (
    department_id INT NOT NULL,
    position_id INT NOT NULL,
    PRIMARY KEY (department_id, position_id),
    CONSTRAINT fk_department_positions_department
        FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    CONSTRAINT fk_department_positions_position
        FOREIGN KEY (position_id) REFERENCES positions(id) ON DELETE CASCADE
);

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_code VARCHAR(50) UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    gender VARCHAR(10),
    date_of_birth DATETIME,
    address VARCHAR(255),
    avatar_url VARCHAR(255),
    role_id INT NOT NULL,
    department_id INT,
    position_id INT,
    hire_date DATE,
    employment_status VARCHAR(30) NOT NULL DEFAULT 'WORKING',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    reset_token VARCHAR(255),
    reset_token_expired_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    CONSTRAINT fk_users_roles FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_users_departments FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT fk_users_positions FOREIGN KEY (position_id) REFERENCES positions(id),
    CONSTRAINT fk_users_department_positions
        FOREIGN KEY (department_id, position_id)
        REFERENCES department_positions(department_id, position_id)
);

ALTER TABLE departments
    ADD CONSTRAINT fk_departments_manager
        FOREIGN KEY (manager_user_id) REFERENCES users(id);

-- ============================================================
-- 2. PERMISSIONS & ROLE PERMISSIONS
-- ============================================================

CREATE TABLE permissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE role_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_roles
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permissions
        FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE TABLE password_reset_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    email VARCHAR(100) NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    generated_password VARCHAR(100),
    admin_note VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    handled_at DATETIME,
    handled_by INT,
    CONSTRAINT fk_password_reset_requests_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_password_reset_requests_admin
        FOREIGN KEY (handled_by) REFERENCES users(id)
);

-- ============================================================
-- 3. LABOR CONTRACTS
-- ============================================================

CREATE TABLE labor_contracts (
                                 id INT PRIMARY KEY AUTO_INCREMENT,
                                 user_id INT NOT NULL,
                                 contract_code VARCHAR(50) NOT NULL UNIQUE,
                                 contract_type VARCHAR(50) NOT NULL,
                                 start_date DATE NOT NULL,
                                 end_date DATE,
                                 base_salary DECIMAL(15,2),
                                 working_time VARCHAR(100),
                                 work_location VARCHAR(255),
                                 status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                                 note TEXT,
                                 terminated_at DATETIME,
                                 terminated_by INT,
                                 termination_reason TEXT,
                                 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at DATETIME,
                                 CONSTRAINT fk_labor_contracts_users
                                     FOREIGN KEY (user_id) REFERENCES users(id),
                                 CONSTRAINT fk_labor_contracts_terminated_by
                                     FOREIGN KEY (terminated_by) REFERENCES users(id)
);

-- ============================================================
-- 4. ATTENDANCE
-- ============================================================

CREATE TABLE attendance_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    work_date DATE NOT NULL,
    employee_id INT NOT NULL,
    check_in DATETIME NULL,
    check_out DATETIME NULL,
    CONSTRAINT unique_attendance_log UNIQUE (employee_id, work_date),
    CONSTRAINT fk_attendance_logs_employee
        FOREIGN KEY (employee_id) REFERENCES users(id)
);

CREATE TABLE attendance_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    work_date DATE NOT NULL,
    check_in DATETIME NULL,
    check_out DATETIME NULL,
    total_work_hours DECIMAL(5,2) NULL COMMENT 'Tổng giờ làm thực tế',
    overtime_hours DECIMAL(5,2) DEFAULT 0.00 COMMENT 'Số giờ OT được duyệt',
    late_hours DECIMAL(5,2) DEFAULT 0.00 COMMENT 'Số giờ đi muộn',
    early_leave_hours DECIMAL(5,2) DEFAULT 0.00 COMMENT 'Số giờ về sớm',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT 'ON_TIME, LATE, EARLY_LEAVE, LATE_AND_EARLY, ABSENT, ON_LEAVE, FORGOT_CHECKIN, FORGOT_CHECKOUT, SICK_LEAVE...',
    note TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_attendance_record UNIQUE (user_id, work_date)
);

-- Bảng snapshot lưu dữ liệu đã chốt
CREATE TABLE attendance_snapshot (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    work_date DATE NOT NULL,
    check_in DATETIME,
    check_out DATETIME,
    total_work_hours DECIMAL(5,2),
    overtime_hours DECIMAL(5,2),
    late_hours DECIMAL(5,2),
    early_leave_hours DECIMAL(5,2),
    status VARCHAR(30),
    note TEXT,
    snapshot_month INT NOT NULL,
    snapshot_year INT NOT NULL,
    confirmed_by_dept INT,
    confirmed_at_dept DATETIME,
    confirmed_by_hr INT,
    confirmed_at_hr DATETIME,
    confirmed_by_business INT,
    confirmed_at_business DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (confirmed_by_dept) REFERENCES users(id),
    FOREIGN KEY (confirmed_by_hr) REFERENCES users(id),
    FOREIGN KEY (confirmed_by_business) REFERENCES users(id),
    UNIQUE KEY unique_snapshot (user_id, work_date, snapshot_month, snapshot_year)
);

-- Bảng lịch sử chốt (kiểm toán)
CREATE TABLE attendance_lock_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    month INT NOT NULL,
    year INT NOT NULL,
    department_id INT,
    action VARCHAR(50) NOT NULL COMMENT 'DEPT_CONFIRM, HR_SEND, BUSINESS_APPROVE, SNAPSHOT_CREATED',
    user_id INT NOT NULL,
    note TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- ============================================================
-- 5. HOLIDAYS (NEW)
-- ============================================================

CREATE TABLE holidays (
    holiday_date DATE PRIMARY KEY,
    holiday_name VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 6. REQUESTS (ĐƠN TỪ)
-- ============================================================

CREATE TABLE requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    department_id INT NULL,
    type ENUM('LEAVE_REQUEST', 'LATE_EARLY_REQUEST', 'EMP_MOVE_REMOVE', 'POSITION_HANDOVER', 'OVERTIME', 'ATTENDANCE_ADJUST', 'SICK_LEAVE_REQUEST') NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'CONFIRMED') DEFAULT 'PENDING',
    reason TEXT,
    approver_id INT,
    approver_comment TEXT NULL,
    observer_id INT,
    handler_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    CONSTRAINT fk_request_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_request_dept FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT fk_request_approver FOREIGN KEY (approver_id) REFERENCES users(id),
    CONSTRAINT fk_request_observer FOREIGN KEY (observer_id) REFERENCES users(id),
    CONSTRAINT fk_request_handler FOREIGN KEY (handler_id) REFERENCES users(id)
);

CREATE TABLE request_observers (
    request_id INT NOT NULL,
    observer_id INT NOT NULL,
    PRIMARY KEY (request_id, observer_id),
    FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE,
    FOREIGN KEY (observer_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE request_notifications (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       request_id INT NOT NULL,
                                       user_id INT NOT NULL,
                                       actor_user_id INT NULL,
                                       event_type VARCHAR(50) NOT NULL,
                                       message VARCHAR(255) NOT NULL,
                                       read_at DATETIME NULL,
                                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       CONSTRAINT fk_request_notifications_request
                                           FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE,
                                       CONSTRAINT fk_request_notifications_user
                                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                       CONSTRAINT fk_request_notifications_actor
                                           FOREIGN KEY (actor_user_id) REFERENCES users(id),
                                       INDEX idx_request_notifications_user_read (user_id, read_at, created_at)
);

-- ============================================================
-- 7. ANNOUNCEMENTS
-- ============================================================

CREATE TABLE announcements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    target_scope ENUM('ALL', 'DEPARTMENT') NOT NULL DEFAULT 'ALL',
    department_id INT,
    publish_date DATETIME NOT NULL,
    created_by INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    CONSTRAINT fk_announcements_departments
        FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT fk_announcements_created_by
        FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT ck_announcements_department_scope
        CHECK ( (target_scope = 'ALL' AND department_id IS NULL) OR (target_scope = 'DEPARTMENT' AND department_id IS NOT NULL) )
);

CREATE TABLE announcement_recipients (
    announcement_id INT NOT NULL,
    user_id INT NOT NULL,
    read_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (announcement_id, user_id),
    CONSTRAINT fk_announcement_recipients_announcements
        FOREIGN KEY (announcement_id) REFERENCES announcements(id) ON DELETE CASCADE,
    CONSTRAINT fk_announcement_recipients_users
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_announcement_recipients_user (user_id, read_at)
);

-- ============================================================
-- 8. CHAT (CONVERSATIONS & MESSAGES)
-- ============================================================

CREATE TABLE conversations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    is_group BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(100) NULL COMMENT 'Tên nhóm (chỉ dùng khi is_group = TRUE)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE conversation_participants (
    conversation_id INT NOT NULL,
    user_id INT NOT NULL,
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (conversation_id, user_id),
    CONSTRAINT fk_cp_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_cp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    conversation_id INT NOT NULL,
    sender_id INT NOT NULL,
    content TEXT NOT NULL,
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_messages_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_messages_conversation_time (conversation_id, sent_at)
);

-- ============================================================
-- 9. PAYROLL
-- ============================================================

CREATE TABLE payrolls (
	id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    expected_hours DOUBLE NOT NULL,
    actual_hours DOUBLE NOT NULL,
    basic_salary BIGINT NOT NULL,
    rate_multiplier DOUBLE NOT NULL,
    total_income BIGINT NOT NULL,
    bonus BIGINT DEFAULT 0,
    description NVARCHAR(200),
    social_insurance BIGINT NOT NULL,
    health_insurance BIGINT NOT NULL,
    unemployment_insurance BIGINT NOT NULL,
    union_fee BIGINT NOT NULL,
    income_before_tax BIGINT NOT NULL,
    taxable_income BIGINT NOT NULL,
    income_tax BIGINT NOT NULL,
    net_pay BIGINT NOT NULL,
    company_social_insurance BIGINT NOT NULL DEFAULT 0,
    company_health_insurance BIGINT NOT NULL DEFAULT 0,
    company_unemployment_insurance BIGINT NOT NULL DEFAULT 0,
    company_union_fee BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(20) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_month_year UNIQUE (user_id, month, year)
);

CREATE TABLE payroll_settings (
	id INT AUTO_INCREMENT PRIMARY KEY,
    employee_social_insurance DOUBLE DEFAULT 8.0,
    employee_health_insurance DOUBLE DEFAULT 1.5,
    employee_unemployment_insurance DOUBLE DEFAULT 1.0,
    employee_union DOUBLE DEFAULT 1.0,
    company_social_insurance DOUBLE DEFAULT 17.5,
    company_health_insurance DOUBLE DEFAULT 3.0,
    company_unemployment_insurance DOUBLE DEFAULT 1.0,
    company_union DOUBLE DEFAULT 2.0,
    self_deduction BIGINT DEFAULT 15500000,
    dependent_deduction BIGINT DEFAULT 6200000,
    effective_date DATE DEFAULT '2025-07-01'
);

CREATE TABLE pit_brackets (
	id INT AUTO_INCREMENT PRIMARY KEY,
    bracket_level INT NOT NULL,
    min_value BIGINT NOT NULL,
    max_value BIGINT,
    tax_rate DOUBLE NOT NULL,
    effective_date DATE DEFAULT '2025-07-01'
);

CREATE TABLE dependent_number (
	id INT AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL,
    dependent INT NOT NULL DEFAULT 0,
    effective_date DATE NOT NULL DEFAULT '2026-01-01',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE user_union_membership (
    user_id INT PRIMARY KEY,
    is_member TINYINT(1) DEFAULT 0, 
    joined_date DATE NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- 10. TASK MANAGEMENT
-- ============================================================

CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'TODO',
    deadline DATE NOT NULL,
    progress INT DEFAULT 0,
    allow_participants_complete_checklist BOOLEAN DEFAULT FALSE,
    created_by INT NOT NULL,
    assigned_to INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasks_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_tasks_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id)
);

CREATE TABLE task_participants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT fk_task_participants_task FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT fk_task_participants_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uq_task_participant UNIQUE (task_id, user_id)
);

CREATE TABLE task_observers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT fk_task_observers_task FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT fk_task_observers_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uq_task_observer UNIQUE (task_id, user_id)
);

CREATE TABLE task_checklist_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    content VARCHAR(255) NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    assigned_to INT NULL,
    completed_at DATETIME NULL,
    CONSTRAINT fk_task_checklist_task FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT fk_task_checklist_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id)
);

CREATE TABLE task_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_comments_task FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT fk_task_comments_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE task_histories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    action_type VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_histories_task FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT fk_task_histories_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 11. LEAVE REQUESTS
-- ============================================================

CREATE TABLE leave_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT NOT NULL,
    leave_date DATE NOT NULL,
    leave_type ENUM('ON_LEAVE', 'LEAVE') NOT NULL DEFAULT 'ON_LEAVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_leave_request FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE,
    UNIQUE KEY unique_leave_request (request_id)
);

CREATE TABLE attendance_change_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT NOT NULL UNIQUE,
    work_date DATE NOT NULL,
    desired_check_in TIME NULL,
    desired_check_out TIME NULL,
    reason TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_change_request FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE
);

-- ============================================================
-- 12. OVERTIME MODULE
-- ============================================================

CREATE TABLE overtime_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NOT NULL UNIQUE,
    department_id INT NOT NULL,
    overtime_date DATE NOT NULL,
    shift_start TIME NOT NULL DEFAULT '17:00:00',
    shift_end TIME NOT NULL DEFAULT '19:00:00',
    total_hours DECIMAL(5,2) NOT NULL DEFAULT 0,
    reason TEXT,
    created_by INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    CONSTRAINT fk_overtime_request FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_overtime_department FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT fk_overtime_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE overtime_participants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    overtime_request_id INT NOT NULL,
    user_id INT NOT NULL,
    status ENUM('PENDING', 'REGISTERED', 'COMPLETED', 'PARTIAL', 'ABSENT', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
    hours_actual DECIMAL(4,1) DEFAULT 0.0,
    confirmed_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_overtime_participants_request FOREIGN KEY (overtime_request_id) REFERENCES overtime_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_overtime_participants_user FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_ot_participant (overtime_request_id, user_id)
);

CREATE INDEX idx_overtime_participants_user ON overtime_participants(user_id);
CREATE INDEX idx_overtime_requests_date ON overtime_requests(overtime_date);

-- ============================================================
-- 13. SICK LEAVE MODULE
-- ============================================================

CREATE TABLE sick_leave_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT NOT NULL UNIQUE,
    file_path VARCHAR(500) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sick_leave_request FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE
);

CREATE TABLE sick_leave_dates (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sick_leave_request_id INT NOT NULL,
    leave_date DATE NOT NULL,
    CONSTRAINT fk_sick_leave_dates FOREIGN KEY (sick_leave_request_id) REFERENCES sick_leave_requests(id) ON DELETE CASCADE
);

-- ============================================================
-- 14. INSERT DỮ LIỆU MẪU
-- ============================================================

-- 14.1. VAI TRÒ
INSERT INTO roles (name, description, active) VALUES
    ('SYSTEM ADMIN', 'System administrator: manages users, roles, and permissions', TRUE),
    ('BUSINESS ADMIN', 'Business administrator: has all permissions in the system', TRUE),
    ('HR_MANAGER', 'Manages HR business operations across the company', TRUE),
    ('HR_STAFF', 'Handles employee records, departments, and attendance operations', TRUE),
    ('PAYROLL_MANAGER', 'Manages payroll operations and payroll staff', TRUE),
    ('PAYROLL_STAFF', 'Handles payroll processing', TRUE),
    ('DEPARTMENT_MANAGER', 'Manages employees within their own department', TRUE),
    ('EMPLOYEE', 'Normal employee with self-service functions', TRUE);

-- 14.2. PHÒNG BAN
INSERT INTO departments (name, description, active) VALUES
    ('Human Resources', 'Human resource department', TRUE),
    ('Information Technology', 'Information technology department', TRUE),
    ('Finance', 'Finance department (Payroll)', TRUE),
    ('Sales', 'Sales department', TRUE);

-- 14.3. CHỨC VỤ
INSERT INTO positions (name, description, active) VALUES
    ('System Administrator', 'Responsible for system administration', TRUE),
    ('HR Manager', 'Responsible for HR management', TRUE),
    ('HR Staff', 'Responsible for HR daily operations', TRUE),
    ('Payroll Manager', 'Manages payroll operations', TRUE),
    ('Payroll Staff', 'Responsible for payroll processing', TRUE),
    ('Department Manager', 'Responsible for managing a department', TRUE),
    ('Employee', 'Normal employee position', TRUE);

-- 14.4. PHÒNG BAN - CHỨC VỤ
INSERT INTO department_positions (department_id, position_id) VALUES
    ((SELECT id FROM departments WHERE name = 'Human Resources'), (SELECT id FROM positions WHERE name = 'HR Manager')),
    ((SELECT id FROM departments WHERE name = 'Human Resources'), (SELECT id FROM positions WHERE name = 'HR Staff')),
    ((SELECT id FROM departments WHERE name = 'Information Technology'), (SELECT id FROM positions WHERE name = 'System Administrator')),
    ((SELECT id FROM departments WHERE name = 'Information Technology'), (SELECT id FROM positions WHERE name = 'Employee')),
    ((SELECT id FROM departments WHERE name = 'Finance'), (SELECT id FROM positions WHERE name = 'Payroll Manager')),
    ((SELECT id FROM departments WHERE name = 'Finance'), (SELECT id FROM positions WHERE name = 'Payroll Staff')),
    ((SELECT id FROM departments WHERE name = 'Sales'), (SELECT id FROM positions WHERE name = 'Department Manager')),
    ((SELECT id FROM departments WHERE name = 'Sales'), (SELECT id FROM positions WHERE name = 'Employee')),
    ((SELECT id FROM departments WHERE name = 'Information Technology'), (SELECT id FROM positions WHERE name = 'Department Manager'));

-- 14.5. NGƯỜI DÙNG
INSERT INTO users (
    employee_code, full_name, email, password, phone, gender,
    date_of_birth, address, avatar_url, role_id, department_id, position_id,
    hire_date, employment_status, active
) VALUES
    ('EMP001', 'Nguyễn Minh Quân', 'admin@company.com', '123456', '0900000001', 'Male', '1998-01-10 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'SYSTEM ADMIN'), NULL, NULL, '2024-01-01', 'WORKING', TRUE),
    ('EMP018', 'Nguyễn Minh Quân', 'minhquan.it@company.com', '123456', '0900000018', 'Male', '1998-01-10 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'DEPARTMENT_MANAGER'),
     (SELECT id FROM departments WHERE name = 'Information Technology'),
     (SELECT id FROM positions WHERE name = 'Department Manager'),
     '2024-05-01', 'WORKING', TRUE),
    ('EMP002', 'Trần Đức Anh', 'ducanh.it@company.com', '123456', '0900000002', 'Male', '2000-03-15 00:00:00', 'Đà Nẵng', NULL,
     (SELECT id FROM roles WHERE name = 'EMPLOYEE'), (SELECT id FROM departments WHERE name = 'Information Technology'), (SELECT id FROM positions WHERE name = 'Employee'), '2024-01-10', 'WORKING', TRUE),
    ('EMP003', 'Phạm Gia Huy', 'giahuy.it@company.com', '123456', '0900000003', 'Male', '2001-07-20 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'EMPLOYEE'), (SELECT id FROM departments WHERE name = 'Information Technology'), (SELECT id FROM positions WHERE name = 'Employee'), '2024-02-01', 'WORKING', TRUE),
    ('EMP004', 'Lê Hoàng Nam', 'hoangnam.it@company.com', '123456', '0900000004', 'Male', '1999-11-05 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'EMPLOYEE'), (SELECT id FROM departments WHERE name = 'Information Technology'), (SELECT id FROM positions WHERE name = 'Employee'), '2024-02-15', 'WORKING', TRUE),
    ('EMP005', 'Nguyễn Thu Hà', 'hrmanager@company.com', '123456', '0900000005', 'Female', '1995-02-12 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'HR_MANAGER'), (SELECT id FROM departments WHERE name = 'Human Resources'), (SELECT id FROM positions WHERE name = 'HR Manager'), '2024-01-15', 'WORKING', TRUE),
    ('EMP006', 'Trần Mai Anh', 'maianh.hr@company.com', '123456', '0900000006', 'Female', '1999-04-18 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'HR_STAFF'), (SELECT id FROM departments WHERE name = 'Human Resources'), (SELECT id FROM positions WHERE name = 'HR Staff'), '2024-02-01', 'WORKING', TRUE),
    ('EMP007', 'Phạm Ngọc Linh', 'ngoclinh.hr@company.com', '123456', '0900000007', 'Female', '2000-08-25 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'HR_STAFF'), (SELECT id FROM departments WHERE name = 'Human Resources'), (SELECT id FROM positions WHERE name = 'HR Staff'), '2024-02-20', 'WORKING', TRUE),
    ('EMP008', 'Vũ Hải Yến', 'haiyen.hr@company.com', '123456', '0900000008', 'Female', '2001-12-03 00:00:00', 'Đà Nẵng', NULL,
     (SELECT id FROM roles WHERE name = 'HR_STAFF'), (SELECT id FROM departments WHERE name = 'Human Resources'), (SELECT id FROM positions WHERE name = 'HR Staff'), '2024-03-01', 'WORKING', TRUE),
    ('EMP009', 'Đỗ Quang Huy', 'payrollmanager@company.com', '123456', '0900000009', 'Male', '1994-06-09 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'PAYROLL_MANAGER'), (SELECT id FROM departments WHERE name = 'Finance'), (SELECT id FROM positions WHERE name = 'Payroll Manager'), '2024-01-20', 'WORKING', TRUE),
    ('EMP010', 'Nguyễn Thảo Vy', 'thaovy.payroll@company.com', '123456', '0900000010', 'Female', '2000-09-14 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'PAYROLL_STAFF'), (SELECT id FROM departments WHERE name = 'Finance'), (SELECT id FROM positions WHERE name = 'Payroll Staff'), '2024-02-10', 'WORKING', TRUE),
    ('EMP011', 'Bùi Minh Khang', 'minhkhang.payroll@company.com', '123456', '0900000011', 'Male', '1998-10-22 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'PAYROLL_STAFF'), (SELECT id FROM departments WHERE name = 'Finance'), (SELECT id FROM positions WHERE name = 'Payroll Staff'), '2024-03-05', 'WORKING', TRUE),
    ('EMP012', 'Lê Phương Anh', 'phuonganh.payroll@company.com', '123456', '0900000012', 'Female', '2001-05-17 00:00:00', 'Đà Nẵng', NULL,
     (SELECT id FROM roles WHERE name = 'PAYROLL_STAFF'), (SELECT id FROM departments WHERE name = 'Finance'), (SELECT id FROM positions WHERE name = 'Payroll Staff'), '2024-03-18', 'WORKING', TRUE),
    ('EMP013', 'Hoàng Minh Đức', 'salesmanager@company.com', '123456', '0900000013', 'Male', '1993-03-30 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'DEPARTMENT_MANAGER'), (SELECT id FROM departments WHERE name = 'Sales'), (SELECT id FROM positions WHERE name = 'Department Manager'), '2024-01-25', 'WORKING', TRUE),
    ('EMP014', 'Nguyễn Khánh Ly', 'khanhly.sales@company.com', '123456', '0900000014', 'Female', '2000-07-11 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'EMPLOYEE'), (SELECT id FROM departments WHERE name = 'Sales'), (SELECT id FROM positions WHERE name = 'Employee'), '2024-02-12', 'WORKING', TRUE),
    ('EMP015', 'Trần Quốc Bảo', 'quocbao.sales@company.com', '123456', '0900000015', 'Male', '1999-01-28 00:00:00', 'Đà Nẵng', NULL,
     (SELECT id FROM roles WHERE name = 'EMPLOYEE'), (SELECT id FROM departments WHERE name = 'Sales'), (SELECT id FROM positions WHERE name = 'Employee'), '2024-03-08', 'WORKING', TRUE),
    ('EMP016', 'Phan Bảo Long', 'businessadmin@company.com', '123456', '0900000016', 'Male', '1996-08-22 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'BUSINESS ADMIN'), NULL, NULL, '2024-04-01', 'WORKING', TRUE),
    ('EMP017', 'Đặng Thanh Hương', 'payroll@company.com', '123456', '0900000017', 'Female', '1997-05-14 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'PAYROLL_STAFF'), (SELECT id FROM departments WHERE name = 'Finance'), (SELECT id FROM positions WHERE name = 'Payroll Staff'), '2024-04-10', 'WORKING', TRUE);

-- 14.6. CẬP NHẬT AVATAR & TRƯỞNG PHÒNG
SET SQL_SAFE_UPDATES = 0;
UPDATE users
SET avatar_url = CONCAT('https://api.dicebear.com/9.x/avataaars/svg?seed=', employee_code, '&size=200')
WHERE active = TRUE AND id > 0;
SET SQL_SAFE_UPDATES = 1;

UPDATE departments SET manager_user_id = (SELECT id FROM users WHERE email = 'minhquan.it@company.com') WHERE name = 'Information Technology';
UPDATE departments SET manager_user_id = (SELECT id FROM users WHERE email = 'hrmanager@company.com') WHERE name = 'Human Resources';
UPDATE departments SET manager_user_id = (SELECT id FROM users WHERE email = 'payrollmanager@company.com') WHERE name = 'Finance';
UPDATE departments SET manager_user_id = (SELECT id FROM users WHERE email = 'salesmanager@company.com') WHERE name = 'Sales';

-- 13.7. HỢP ĐỒNG LAO ĐỘNG
INSERT INTO labor_contracts (user_id, contract_code, contract_type, start_date, end_date, base_salary, working_time, work_location, status, note) VALUES
	((SELECT id FROM users WHERE email = 'admin@company.com'), 'HDLD-2024-001', 'FIXED_TERM', '2024-01-01', '2027-01-01', 30000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', 'Contract for System Admin'),
	((SELECT id FROM users WHERE email = 'minhquan.it@company.com'), 'HDLD-2024-018', 'FIXED_TERM', '2024-05-01', '2027-05-01', 28000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', 'Contract for IT Dept Manager'),
	((SELECT id FROM users WHERE email = 'hrmanager@company.com'), 'HDLD-2024-002', 'FIXED_TERM', '2024-01-15', '2027-01-15', 25000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', 'Contract for HR Manager'),
	((SELECT id FROM users WHERE email = 'maianh.hr@company.com'), 'HDLD-2024-003', 'FIXED_TERM', '2024-02-01', '2027-02-01', 15000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', 'Contract for HR Staff'),
	((SELECT id FROM users WHERE email = 'ngoclinh.hr@company.com'), 'HDLD-2024-004', 'FIXED_TERM', '2024-02-20', '2027-02-20', 15000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', 'Contract for HR Staff'),
	((SELECT id FROM users WHERE email = 'haiyen.hr@company.com'), 'HDLD-2024-005', 'FIXED_TERM', '2024-03-01', '2027-03-01', 15000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', 'Contract for HR Staff'),
	((SELECT id FROM users WHERE email = 'ducanh.it@company.com'), 'HDLD-2024-006', 'FIXED_TERM', '2024-01-10', '2027-01-10', 12000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', 'Contract for IT Employee'),
	((SELECT id FROM users WHERE email = 'giahuy.it@company.com'), 'HDLD-2024-007', 'FIXED_TERM', '2024-02-01', '2027-02-01', 12000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', 'Contract for IT Employee'),
	((SELECT id FROM users WHERE email = 'hoangnam.it@company.com'), 'HDLD-2024-008', 'FIXED_TERM', '2024-02-15', '2027-02-15', 12000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', 'Contract for IT Employee'),
	((SELECT id FROM users WHERE email = 'payrollmanager@company.com'), 'HDLD-2024-009', 'FIXED_TERM', '2024-01-20', '2027-01-20', 22000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', 'Contract for Payroll Manager'),
	((SELECT id FROM users WHERE email = 'thaovy.payroll@company.com'), 'HDLD-2024-010', 'FIXED_TERM', '2024-02-10', '2027-02-10', 11000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', 'Contract for Payroll Staff'),
	((SELECT id FROM users WHERE email = 'minhkhang.payroll@company.com'), 'HDLD-2024-011', 'FIXED_TERM', '2024-03-05', '2027-03-05', 11000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', 'Contract for Payroll Staff'),
	((SELECT id FROM users WHERE email = 'phuonganh.payroll@company.com'), 'HDLD-2024-012', 'FIXED_TERM', '2024-03-18', '2027-03-18', 11000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', 'Contract for Payroll Staff'),
	((SELECT id FROM users WHERE email = 'payroll@company.com'), 'HDLD-2024-013', 'FIXED_TERM', '2024-04-10', '2027-04-10', 16000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', 'Contract for Payroll Staff'),
	((SELECT id FROM users WHERE email = 'salesmanager@company.com'), 'HDLD-2024-014', 'FIXED_TERM', '2024-01-25', '2027-01-25', 20000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', 'Contract for Sales Manager'),
	((SELECT id FROM users WHERE email = 'khanhly.sales@company.com'), 'HDLD-2024-015', 'FIXED_TERM', '2024-02-12', '2027-02-12', 11000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', 'Contract for Sales Employee'),
	((SELECT id FROM users WHERE email = 'quocbao.sales@company.com'), 'HDLD-2024-016', 'FIXED_TERM', '2024-03-08', '2027-03-08', 11000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', 'Contract for Sales Employee'),
	((SELECT id FROM users WHERE email = 'businessadmin@company.com'), 'HDLD-2024-017', 'FIXED_TERM', '2024-04-01', '2027-04-01', 28000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', 'Contract for Business Admin');


INSERT INTO holidays (holiday_date, holiday_name) VALUES
	('2026-01-01', 'Tết Dương lịch'),
    ('2026-02-16', 'Tết Nguyên Đán Bính Ngọ'),
    ('2026-02-17', 'Tết Nguyên Đán'),
    ('2026-02-18', 'Tết Nguyên Đán'),
    ('2026-02-19', 'Tết Nguyên Đán'),
    ('2026-02-20', 'Tết Nguyên Đán'),
    ('2026-04-27', 'Giỗ Tổ Hùng Vương (nghỉ bù)'),
    ('2026-04-30', 'Ngày Chiến thắng'),
    ('2026-05-01', 'Ngày Quốc tế Lao động'),
    ('2026-08-31', 'Quốc khánh (nghỉ hoán đổi)'),
    ('2026-09-01', 'Quốc khánh (nghỉ)'),
    ('2026-09-02', 'Quốc khánh'),
    ('2027-01-01', 'Tết Dương lịch'),
    ('2027-02-06', 'Tết Nguyên Đán Đinh Mùi'),
    ('2027-02-07', 'Tết Nguyên Đán'),
    ('2027-02-08', 'Tết Nguyên Đán'),
    ('2027-02-09', 'Tết Nguyên Đán'),
    ('2027-02-10', 'Tết Nguyên Đán'),
    ('2027-04-17', 'Giỗ Tổ Hùng Vương'),
    ('2027-04-30', 'Ngày Chiến thắng'),
    ('2027-05-01', 'Ngày Quốc tế Lao động');
    
    -- Payroll
INSERT INTO payroll_settings (effective_date) VALUES ('2025-07-01');

INSERT INTO pit_brackets (bracket_level, min_value, max_value, tax_rate, effective_date) VALUES
	(1, 0, 10000000, 5.0, '2025-07-01'),
	(2, 10000001, 30000000, 10.0, '2025-07-01'),
	(3, 30000001, 60000000, 20.0, '2025-07-01'),
	(4, 60000001, 100000000, 30.0, '2025-07-01'),
	(5, 100000000, NULL, 35.0, '2025-07-01');

INSERT INTO user_union_membership (user_id, is_member, joined_date) VALUES
	(2, 1, '2024-03-15'),
	(3, 1, '2023-05-20'),
	(4, 0, NULL),
	(5, 1, '2025-01-10'),
	(6, 1, '2022-11-01'),
	(7, 1, '2023-02-15'),
	(8, 0, NULL),
	(9, 1, '2024-06-18'),
	(10, 1, '2021-08-25'),
	(11, 1, '2024-09-10'),
	(12, 0, NULL),
	(13, 1, '2023-11-12'),
	(14, 0, NULL),
	(15, 1, '2024-01-05'),
	(16, 1, '2024-07-19'),
	(17, 1, '2025-02-11'),
	(18, 0, NULL);
    
INSERT INTO dependent_number (user_id, depedent, effective_date) VALUES
	(2, 1, '2026-01-01'),
	(3, 2, '2026-01-01'),
	(6, 2, '2026-01-01'),
	(10, 1, '2026-01-01'),
	(11, 0, '2026-01-01'),
	(14, 3, '2026-01-01'),
	(15, 1, '2026-02-15');

-- ============================================================
-- 14. PERMISSIONS
-- ============================================================

INSERT INTO permissions (code, name, description) VALUES
    ('HOMEPAGE_VIEW', 'View homepage', 'Can view homepage/dashboard'),
    ('AUTH_LOGIN', 'Login', 'Can login to the system'),
    ('AUTH_LOGOUT', 'Logout', 'Can logout from the system'),
    ('AUTH_FORGOT_PASSWORD', 'Forgot password', 'Can request password reset'),
    ('PROFILE_VIEW', 'View my profile', 'Can view own profile'),
    ('PROFILE_CHANGE_PASSWORD', 'Change password', 'Can change own password'),
    ('USER_VIEW_LIST', 'View user list', 'Can view list of users'),
    ('USER_VIEW_DETAIL', 'View user information', 'Can view user detail'),
    ('USER_CREATE', 'Add new user', 'Can create new user'),
    ('USER_UPDATE', 'Update user information', 'Can update user information'),
    ('USER_TOGGLE_STATUS', 'Active/deactive user', 'Can activate or deactivate user'),
    ('PASSWORD_RESET_REQUEST_VIEW', 'View password reset requests', 'Can view password reset request list'),
    ('PASSWORD_RESET_REQUEST_PROCESS', 'Process password reset requests', 'Can approve or reject password reset requests'),
    ('ROLE_VIEW_LIST', 'View role list', 'Can view role list'),
    ('ROLE_VIEW_PERMISSION', 'View role permissions', 'Can view permissions of role'),
    ('ROLE_UPDATE', 'Update role information', 'Can update role information'),
    ('ROLE_TOGGLE_STATUS', 'Active/deactive role', 'Can activate or deactivate role'),
    ('ROLE_EDIT_PERMISSION', 'Edit role permissions', 'Can edit permissions of role'),
    ('ROLE_CREATE', 'Add new role', 'Can create new role'),
    ('DEPARTMENT_VIEW_LIST', 'View department list', 'Can view list of departments'),
    ('DEPARTMENT_VIEW_DETAIL', 'View department detail', 'Can view department detail'),
    ('DEPARTMENT_CREATE', 'Add department', 'Can create new department'),
    ('DEPARTMENT_UPDATE', 'Update department', 'Can update department information'),
    ('DEPARTMENT_TOGGLE_STATUS', 'Active/deactive department', 'Can activate or deactivate department'),
    ('DEPARTMENT_ASSIGN_MANAGER', 'Assign department manager', 'Can assign manager to department'),
    ('DEPARTMENT_VIEW_EMPLOYEES', 'View department employees', 'Can view employees in a department'),
    ('DEPARTMENT_MOVE_MEMBER', 'Move department member', 'Can move members between departments'),
    ('DEPARTMENT_ASSIGN_POSITION', 'Assign position to department member', 'Can assign position to members in department'),
    ('POSITION_VIEW_LIST', 'View position list', 'Can view list of positions'),
    ('POSITION_VIEW_DETAIL', 'View position detail', 'Can view position detail'),
    ('POSITION_CREATE', 'Add position', 'Can create new position'),
    ('POSITION_UPDATE', 'Update position', 'Can update position information'),
    ('POSITION_TOGGLE_STATUS', 'Active/deactive position', 'Can activate or deactivate position'),
    ('POSITION_ASSIGN', 'Assign position to user', 'Can assign a position to a user'),
    ('POSITION_ASSIGN_MANAGER', 'Assign manager position', 'Can assign manager-level positions'),
    ('CONTRACT_VIEW_LIST', 'View contract list', 'Can view list of labor contracts'),
    ('CONTRACT_VIEW_DETAIL', 'View contract detail', 'Can view contract detail'),
    ('CONTRACT_VIEW_OWN', 'View own contract', 'Can view own labor contract'),
    ('CONTRACT_CREATE', 'Add contract', 'Can create new labor contract'),
    ('CONTRACT_UPDATE', 'Update labor contract', 'Can update labor contract'),
    ('CONTRACT_TERMINATE', 'Terminate contract', 'Can terminate labor contract'),
    ('CONTRACT_RENEW', 'Renew contract', 'Can renew labor contract'),
    ('ATTENDANCE_CHECK_IN', 'Check in', 'Can check in'),
    ('ATTENDANCE_CHECK_OUT', 'Check out', 'Can check out'),
    ('ATTENDANCE_VIEW_OWN', 'View own attendance', 'Can view own attendance'),
    ('ATTENDANCE_VIEW_DEPARTMENT', 'View department attendance', 'Can view attendance of own department'),
    ('ATTENDANCE_VIEW_ALL', 'View all attendance', 'Can view all attendance records'),
    ('ATTENDANCE_UPDATE', 'Update attendance', 'Can update attendance records'),
    ('ATTENDANCE_EXPORT_REPORT', 'Export attendance report', 'Can export attendance report'),
    ('PAYROLL_VIEW_OWN', 'View own salary', 'Can view own salary'),
    ('PAYROLL_VIEW_DEPARTMENT', 'View payroll department', 'Can view payroll summary of all departments'), #new
    ('PAYROLL_VIEW_LIST', 'View payroll list', 'Can view payroll list'),
    ('PAYROLL_OVERVIEW', 'View overview money flow', 'Can view money flow and summary'), #new
    ('PAYROLL_VIEW_DETAIL', 'View employee salary detail', 'Can view employee salary detail'),
    ('PAYROLL_GENERATE', 'Generate payroll', 'Can generate monthly payroll'),
    ('PAYROLL_UPDATE_COMPONENT', 'Update salary component', 'Can update salary components'),
    ('PAYROLL_CONFIRM', 'Confirm payroll', 'Can confirm payroll'),
    ('PAYROLL_EXPORT_REPORT', 'Export payroll report', 'Can export payroll report'),
    ('VIEW_MY_REQUEST', 'View own request', 'Can view own request'),
    ('VIEW_ALL_REQUEST', 'View all request', 'Can view all request'),
    ('VIEW_REQUEST_DETAIL', 'View request detail', 'Can view request detail info'),
    ('PROCESS_REQUEST', 'Process request', 'Can process request (approve/reject)'),
    ('CREATE_REQUEST', 'Create request', 'Can create new request'),
    ('VIEW_DEPARTMENT_REQUESTS', 'View department requests', 'Can view requests of own department'),
    ('ANNOUNCEMENT_VIEW_LIST', 'View announcements', 'Can view announcements available to the user'),
    ('ANNOUNCEMENT_VIEW_DETAIL', 'View announcement detail', 'Can view announcement detail'),
    ('ANNOUNCEMENT_CREATE', 'Create announcement', 'Can create and send announcements'),
    ('TASK_VIEW', 'View tasks', 'Can view task management module'),
    ('TASK_CREATE', 'Create task', 'Can create new tasks'),
    ('TASK_UPDATE', 'Update task', 'Can update task information'),
    ('TASK_DELETE', 'Delete task', 'Can delete tasks'),
    ('TASK_MANAGE_CHECKLIST', 'Manage task work items', 'Can add, update, and delete task work items'),
    ('TASK_UPDATE_STATUS', 'Update task status', 'Can update task progress status'),
    -- them quyen cho confirm attendance
	('ATTENDANCE_CONFIRM_DEPT', 'Confirm department attendance', 'Department Manager can confirm attendance of their department'),
    ('ATTENDANCE_SEND_TO_BUSINESS', 'Send attendance to Business Admin', 'HR can send locked attendance request to Business Admin'),
    ('ATTENDANCE_APPROVE_BUSINESS', 'Approve attendance and create snapshot', 'Business Admin can approve and create final snapshot');

-- ============================================================
-- 17. PHÂN QUYỀN CHO TỪNG VAI TRÒ
-- ============================================================

-- SYSTEM ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'SYSTEM ADMIN' AND p.code IN (
    'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
    'USER_VIEW_LIST', 'USER_VIEW_DETAIL', 'USER_CREATE', 'USER_UPDATE', 'USER_TOGGLE_STATUS',
    'PASSWORD_RESET_REQUEST_VIEW', 'PASSWORD_RESET_REQUEST_PROCESS',
    'ROLE_VIEW_LIST', 'ROLE_VIEW_PERMISSION', 'ROLE_UPDATE', 'ROLE_TOGGLE_STATUS', 'ROLE_EDIT_PERMISSION', 'ROLE_CREATE',
    'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_CREATE', 'DEPARTMENT_UPDATE',
    'POSITION_VIEW_LIST', 'POSITION_VIEW_DETAIL', 'POSITION_CREATE', 'POSITION_UPDATE', 'POSITION_TOGGLE_STATUS'
);

-- BUSINESS ADMIN (tất cả quyền)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p WHERE r.name = 'BUSINESS ADMIN';

-- HR_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'HR_MANAGER' AND p.code IN (
    'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
    'USER_VIEW_LIST', 'USER_VIEW_DETAIL', 'USER_CREATE', 'USER_UPDATE', 'USER_TOGGLE_STATUS',
    'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_CREATE', 'DEPARTMENT_UPDATE', 'DEPARTMENT_TOGGLE_STATUS',
    'DEPARTMENT_ASSIGN_MANAGER', 'DEPARTMENT_VIEW_EMPLOYEES', 'DEPARTMENT_MOVE_MEMBER', 'DEPARTMENT_ASSIGN_POSITION',
    'POSITION_VIEW_LIST',
    'CONTRACT_VIEW_LIST', 'CONTRACT_VIEW_DETAIL', 'CONTRACT_VIEW_OWN', 'CONTRACT_CREATE', 'CONTRACT_UPDATE', 'CONTRACT_TERMINATE',
    'ATTENDANCE_VIEW_OWN', 'ATTENDANCE_VIEW_DEPARTMENT', 'ATTENDANCE_VIEW_ALL', 'ATTENDANCE_UPDATE', 'ATTENDANCE_EXPORT_REPORT',
    'PAYROLL_VIEW_OWN', 'PAYROLL_VIEW_DEPARTMENT', 'PAYROLL_UPDATE_COMPONENT', 'PAYROLL_VIEW_LIST', 'PAYROLL_VIEW_DETAIL',
    'PAYROLL_CONFIRM', 'PAYROLL_EXPORT_REPORT', 
    'VIEW_MY_REQUEST', 'VIEW_REQUEST_DETAIL', 'PROCESS_REQUEST', 'CREATE_REQUEST',
    'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL', 'ANNOUNCEMENT_CREATE',
    'TASK_VIEW', 'TASK_CREATE', 'TASK_UPDATE', 'TASK_DELETE', 'TASK_MANAGE_CHECKLIST', 'TASK_UPDATE_STATUS',
    'ATTENDANCE_CONFIRM_DEPT', 'ATTENDANCE_SEND_TO_BUSINESS'
);

-- HR_STAFF
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'HR_STAFF' AND p.code IN (
    'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
    'USER_VIEW_LIST', 'USER_VIEW_DETAIL',
    'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_VIEW_EMPLOYEES',
    'POSITION_VIEW_LIST',
    'CONTRACT_VIEW_LIST', 'CONTRACT_VIEW_DETAIL', 'CONTRACT_VIEW_OWN', 'CONTRACT_CREATE', 'CONTRACT_UPDATE',
    'ATTENDANCE_VIEW_OWN', 'ATTENDANCE_VIEW_DEPARTMENT', 'ATTENDANCE_VIEW_ALL', 'ATTENDANCE_UPDATE', 'ATTENDANCE_EXPORT_REPORT',
    'PAYROLL_VIEW_OWN', 'PAYROLL_VIEW_DETAIL',
    'VIEW_MY_REQUEST', 'VIEW_REQUEST_DETAIL', 'PROCESS_REQUEST', 'CREATE_REQUEST',
    'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL',
    'TASK_VIEW'
);

-- PAYROLL_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'PAYROLL_MANAGER' AND p.code IN (
    'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
    'USER_VIEW_LIST', 'USER_VIEW_DETAIL',
    'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_VIEW_EMPLOYEES',
    'POSITION_VIEW_LIST',
    'CONTRACT_VIEW_LIST', 'CONTRACT_VIEW_DETAIL', 'CONTRACT_VIEW_OWN',
    'ATTENDANCE_VIEW_OWN', 'ATTENDANCE_VIEW_DEPARTMENT',
    'PAYROLL_VIEW_OWN', 'PAYROLL_VIEW_DEPARTMENT', 'PAYROLL_VIEW_LIST', 'PAYROLL_VIEW_DETAIL', 
    'PAYROLL_GENERATE', 'PAYROLL_UPDATE_COMPONENT', 'PAYROLL_CONFIRM', 'PAYROLL_EXPORT_REPORT',
    'VIEW_MY_REQUEST', 'VIEW_REQUEST_DETAIL', 'PROCESS_REQUEST', 'CREATE_REQUEST',
    'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL', 'ANNOUNCEMENT_CREATE',
    'TASK_VIEW', 'TASK_CREATE', 'TASK_UPDATE', 'TASK_DELETE', 'TASK_MANAGE_CHECKLIST', 'TASK_UPDATE_STATUS',
    'ATTENDANCE_CONFIRM_DEPT'
);

-- PAYROLL_STAFF
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'PAYROLL_STAFF' AND p.code IN (
    'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
    'USER_VIEW_LIST',
    'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_VIEW_EMPLOYEES',
    'POSITION_VIEW_LIST',
    'CONTRACT_VIEW_OWN',
    'ATTENDANCE_VIEW_DEPARTMENT', 'ATTENDANCE_VIEW_ALL', 'ATTENDANCE_EXPORT_REPORT', 'ATTENDANCE_VIEW_OWN',
    'PAYROLL_VIEW_OWN', 'PAYROLL_VIEW_DEPARTMENT', 'PAYROLL_VIEW_LIST', 'PAYROLL_VIEW_DETAIL', 
    'PAYROLL_GENERATE', 'PAYROLL_UPDATE_COMPONENT', 'PAYROLL_EXPORT_REPORT',
    'VIEW_MY_REQUEST', 'VIEW_REQUEST_DETAIL', 'PROCESS_REQUEST', 'CREATE_REQUEST',
    'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL',
    'TASK_VIEW'
);

-- DEPARTMENT_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'DEPARTMENT_MANAGER' AND p.code IN (
    'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
    'USER_VIEW_LIST', 'USER_VIEW_DETAIL',
    'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_VIEW_EMPLOYEES',
    'POSITION_VIEW_LIST',
    'CONTRACT_VIEW_OWN',
    'ATTENDANCE_VIEW_OWN', 'ATTENDANCE_VIEW_DEPARTMENT',
    'PAYROLL_VIEW_OWN', 'PAYROLL_VIEW_DETAIL',
    'VIEW_MY_REQUEST', 'VIEW_REQUEST_DETAIL', 'PROCESS_REQUEST', 'CREATE_REQUEST',
    'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL', 'ANNOUNCEMENT_CREATE',
    'TASK_VIEW', 'TASK_CREATE', 'TASK_UPDATE', 'TASK_DELETE', 'TASK_MANAGE_CHECKLIST', 'TASK_UPDATE_STATUS',
    'ATTENDANCE_CONFIRM_DEPT'
);

-- EMPLOYEE
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'EMPLOYEE' AND p.code IN (
    'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
    'USER_VIEW_LIST',
    'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_VIEW_EMPLOYEES',
    'POSITION_VIEW_LIST',
    'CONTRACT_VIEW_OWN',
    'ATTENDANCE_VIEW_OWN',
    'PAYROLL_VIEW_OWN', -- 'PAYROLL_VIEW_DETAIL',
    'VIEW_MY_REQUEST', 'VIEW_REQUEST_DETAIL', 'CREATE_REQUEST',
    'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL','PROCESS_REQUEST',
    'TASK_VIEW'
);

-- ============================================================
-- 18. KẾT THÚC
-- ============================================================
