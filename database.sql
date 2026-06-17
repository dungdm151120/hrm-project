-- ============================================================
-- DATABASE: hrm_db (Merged from DB1 + DB2, without leave_balances)
-- ============================================================
DROP DATABASE IF EXISTS hrm_db;
CREATE DATABASE hrm_db;
USE hrm_db;

-- ============================================================
-- 1. CORE TABLES
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
    file_url VARCHAR(255),
    note TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    CONSTRAINT fk_labor_contracts_users
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 2. ATTENDANCE TABLES (không có leave_balances)
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
    total_work_hours DECIMAL(5,2) NULL COMMENT 'Tổng giờ làm thực tế (bao gồm OT nếu tính chung)',
    overtime_hours DECIMAL(5,2) DEFAULT 0.00 COMMENT 'Số giờ OT được duyệt',
    late_hours DECIMAL(5,2) DEFAULT 0.00 COMMENT 'Số giờ đi muộn',
    early_leave_hours DECIMAL(5,2) DEFAULT 0.00 COMMENT 'Số giờ về sớm',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT 'Trạng thái: ON_TIME, LATE, EARLY_LEAVE, LATE_AND_EARLY, ABSENT, ON_LEAVE, FORGOT_CHECKIN, FORGOT_CHECKOUT...',
    note TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_attendance_record UNIQUE (user_id, work_date)
);

-- ============================================================
-- 3. REQUEST TABLES (từ DB2)
-- ============================================================

CREATE TABLE requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    department_id INT NULL,
    type ENUM('LEAVE_REQUEST', 'LATE_EARLY_REQUEST', 'EMP_MOVE_REMOVE', 'POSITION_HANDOVER', 'OVERTIME', 'ATTENDANCE_ADJUST') NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
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

-- ============================================================
-- 4. ANNOUNCEMENT TABLES
-- ============================================================

-- Task Management
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
        CHECK (
            (target_scope = 'ALL' AND department_id IS NULL)
            OR (target_scope = 'DEPARTMENT' AND department_id IS NOT NULL)
        )
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
-- 5. CHAT FEATURE TABLES (từ DB1)
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
    CONSTRAINT fk_cp_conversation
        FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_cp_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    conversation_id INT NOT NULL,
    sender_id INT NOT NULL,
    content TEXT NOT NULL,
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_messages_conversation
        FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_sender
        FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_messages_conversation_time (conversation_id, sent_at)
);

-- ============================================================
-- 6. PAYROLL TABLE (từ DB1)
-- ============================================================

CREATE TABLE payrolls (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    expected_hours DOUBLE NOT NULL,
    actual_hours DOUBLE NOT NULL,
    basic_salary DOUBLE NOT NULL,
    rate_multiplier DOUBLE NOT NULL,
    total_income DOUBLE NOT NULL,
    bonus DOUBLE,
    description NVARCHAR(200),
    social_insurance DOUBLE NOT NULL,
    health_insurance DOUBLE NOT NULL,
    unemployment_insurance DOUBLE NOT NULL,
    income_before_tax DOUBLE NOT NULL,
    taxable_income DOUBLE NOT NULL,
    income_tax DOUBLE NOT NULL,
    net_pay DOUBLE NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_month_year UNIQUE (user_id, month, year)
);

-- ============================================================
-- 7. PERMISSIONS (gộp từ DB1 và DB2, chuẩn hóa tên)
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
                                                      ('PAYROLL_VIEW_LIST', 'View payroll list', 'Can view payroll list'),
                                                      ('PAYROLL_VIEW_DETAIL', 'View employee salary detail', 'Can view employee salary detail'),
                                                      ('PAYROLL_GENERATE', 'Generate payroll', 'Can generate monthly payroll'),
                                                      ('PAYROLL_UPDATE_COMPONENT', 'Update salary component', 'Can update salary components'),
                                                      ('PAYROLL_CONFIRM', 'Confirm payroll', 'Can confirm payroll'),
                                                      ('PAYROLL_EXPORT_REPORT', 'Export payroll report', 'Can export payroll report'),
                                                      -- Yêu cầu bổ sung từ DB2
                                                      ('VIEW_MY_REQUEST', 'View own request', 'Can view own request'),
                                                      ('VIEW_ALL_REQUESTS', 'View all request', 'Can view all request'),
                                                      ('VIEW_REQUEST_DETAIL', 'View request detail', 'Can view request detail info'),
                                                      ('PROCESS_REQUEST', 'Process request', 'Can process request'),
                                                      ('CREATE_REQUEST', 'Create request', 'Can create new request'),
                                                      ('TASK_VIEW', 'View tasks', 'Can view task management module'),
                                                      ('TASK_CREATE', 'Create task', 'Can create new tasks'),
                                                      ('TASK_UPDATE', 'Update task', 'Can update task information'),
                                                      ('TASK_DELETE', 'Delete task', 'Can delete tasks'),
                                                      ('TASK_MANAGE_CHECKLIST', 'Manage task work items', 'Can add, update, and delete task work items'),
                                                      ('TASK_UPDATE_STATUS', 'Update task status', 'Can update task progress status'),
                                                      ('ANNOUNCEMENT_VIEW_LIST', 'View announcements', 'Can view announcements available to the user'),
                                                      ('ANNOUNCEMENT_VIEW_DETAIL', 'View announcement detail', 'Can view announcement detail'),
                                                      ('ANNOUNCEMENT_CREATE', 'Create announcement', 'Can create and send announcements');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'SYSTEM ADMIN' AND p.code IN (
                                                                                               'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                                                                                               'USER_VIEW_LIST', 'USER_VIEW_DETAIL', 'USER_CREATE', 'USER_UPDATE', 'USER_TOGGLE_STATUS', 'ROLE_VIEW_LIST',
                                                                                               'ROLE_VIEW_PERMISSION', 'ROLE_UPDATE', 'ROLE_TOGGLE_STATUS', 'ROLE_EDIT_PERMISSION', 'ROLE_CREATE',
                                                                                               'DEPARTMENT_MOVE_MEMBER', 'DEPARTMENT_ASSIGN_POSITION',
                                                                                              'TASK_VIEW',
                                                                                               'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL'
    );

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'BUSINESS ADMIN';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'HR_MANAGER' AND p.code IN (
                                                                                             'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                                                                                             'USER_VIEW_LIST', 'USER_VIEW_DETAIL', 'USER_CREATE', 'USER_UPDATE', 'USER_TOGGLE_STATUS', 'DEPARTMENT_VIEW_LIST',
                                                                                             'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_CREATE', 'DEPARTMENT_UPDATE', 'DEPARTMENT_TOGGLE_STATUS',
                                                                                             'DEPARTMENT_ASSIGN_MANAGER', 'DEPARTMENT_VIEW_EMPLOYEES', 'DEPARTMENT_MOVE_MEMBER', 'DEPARTMENT_ASSIGN_POSITION',
                                                                                             'POSITION_VIEW_LIST', 'POSITION_VIEW_DETAIL', 'POSITION_CREATE', 'POSITION_UPDATE', 'POSITION_TOGGLE_STATUS',
                                                                                             'CONTRACT_VIEW_LIST', 'CONTRACT_VIEW_DETAIL', 'CONTRACT_VIEW_OWN', 'CONTRACT_CREATE', 'CONTRACT_UPDATE',
                                                                                             'CONTRACT_TERMINATE', 'CONTRACT_RENEW', 'ATTENDANCE_VIEW_OWN', 'ATTENDANCE_VIEW_DEPARTMENT', 'ATTENDANCE_VIEW_ALL',
                                                                                             'ATTENDANCE_UPDATE', 'ATTENDANCE_EXPORT_REPORT', 'PAYROLL_VIEW_OWN', 'PAYROLL_VIEW_LIST', 'PAYROLL_CONFIRM', 'PAYROLL_EXPORT_REPORT',
                                                                                              'TASK_VIEW', 'TASK_CREATE', 'TASK_UPDATE', 'TASK_DELETE', 'TASK_MANAGE_CHECKLIST', 'TASK_UPDATE_STATUS',
                                                                                             'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL', 'ANNOUNCEMENT_CREATE'
    );

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'HR_STAFF' AND p.code IN (
                                                                                           'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                                                                                           'USER_VIEW_LIST', 'USER_VIEW_DETAIL', 'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_VIEW_EMPLOYEES',
                                                                                           'POSITION_VIEW_LIST', 'POSITION_VIEW_DETAIL', 'CONTRACT_VIEW_LIST', 'CONTRACT_VIEW_DETAIL', 'CONTRACT_VIEW_OWN',
                                                                                           'CONTRACT_CREATE', 'CONTRACT_UPDATE', 'CONTRACT_TERMINATE', 'ATTENDANCE_VIEW_OWN', 'ATTENDANCE_VIEW_DEPARTMENT',
                                                                                           'ATTENDANCE_VIEW_ALL', 'ATTENDANCE_UPDATE', 'ATTENDANCE_EXPORT_REPORT', 'PAYROLL_VIEW_OWN',
                                                                                              'TASK_VIEW',
                                                                                           'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL'
    );

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'PAYROLL_MANAGER' AND p.code IN (
                                                                                                  'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                                                                                                  'DEPARTMENT_VIEW_EMPLOYEES', 'ATTENDANCE_VIEW_OWN', 'ATTENDANCE_VIEW_DEPARTMENT', 'CONTRACT_VIEW_OWN',
                                                                                                  'PAYROLL_VIEW_OWN', 'PAYROLL_VIEW_LIST', 'PAYROLL_VIEW_DETAIL', 'PAYROLL_GENERATE', 'PAYROLL_UPDATE_COMPONENT',
                                                                                                  'PAYROLL_CONFIRM', 'PAYROLL_EXPORT_REPORT',
                                                                                              'TASK_VIEW', 'TASK_CREATE', 'TASK_UPDATE', 'TASK_DELETE', 'TASK_MANAGE_CHECKLIST', 'TASK_UPDATE_STATUS',
                                                                                                  'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL'
    );

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'PAYROLL_STAFF' AND p.code IN (
                                                                                                'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                                                                                                'ATTENDANCE_VIEW_ALL', 'ATTENDANCE_EXPORT_REPORT', 'CONTRACT_VIEW_OWN', 'PAYROLL_VIEW_OWN', 'PAYROLL_VIEW_LIST',
                                                                                                'PAYROLL_VIEW_DETAIL', 'PAYROLL_GENERATE', 'PAYROLL_UPDATE_COMPONENT', 'PAYROLL_EXPORT_REPORT',
                                                                                              'TASK_VIEW',
                                                                                                'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL'
    );

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'DEPARTMENT_MANAGER' AND p.code IN (
                                                                                                     'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                                                                                                     'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_VIEW_EMPLOYEES', 'ATTENDANCE_VIEW_OWN',
                                                                                                     'ATTENDANCE_VIEW_DEPARTMENT', 'CONTRACT_VIEW_OWN', 'PAYROLL_VIEW_OWN',
                                                                                              'TASK_VIEW', 'TASK_CREATE', 'TASK_UPDATE', 'TASK_DELETE', 'TASK_MANAGE_CHECKLIST', 'TASK_UPDATE_STATUS',
                                                                                                     'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL'
    );

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p WHERE r.name = 'EMPLOYEE' AND p.code IN (
                                                                                           'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD', 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                                                                                           'ATTENDANCE_CHECK_IN', 'ATTENDANCE_CHECK_OUT', 'ATTENDANCE_VIEW_OWN', 'CONTRACT_VIEW_OWN', 'PAYROLL_VIEW_OWN',
                                                                                           'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_VIEW_EMPLOYEES',
                                                                                           'VIEW_MY_REQUEST', 'VIEW_REQUEST_DETAIL', 'CREATE_REQUEST', 'PROCESS_REQUEST',
                                                                                              'TASK_VIEW',
                                                                                           'ANNOUNCEMENT_VIEW_LIST', 'ANNOUNCEMENT_VIEW_DETAIL'
    );
INSERT INTO labor_contracts (user_id, contract_code, contract_type, start_date, end_date, base_salary, working_time, work_location, status, file_url, note) VALUES
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'admin@company.com'), 'HDLD-2024-001', 'FIXED_TERM', '2024-01-01', '2025-01-01', 30000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL, 'Contract for System Admin'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'hrmanager@company.com'), 'HDLD-2024-002', 'FIXED_TERM', '2024-01-15', '2025-01-15', 25000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL, 'Contract for HR Manager'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'maianh.hr@company.com'), 'HDLD-2024-003', 'FIXED_TERM', '2024-02-01', '2025-02-01', 15000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL, 'Contract for HR Staff'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'ngoclinh.hr@company.com'), 'HDLD-2024-004', 'FIXED_TERM', '2024-02-20', '2025-02-20', 15000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL, 'Contract for HR Staff'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'haiyen.hr@company.com'), 'HDLD-2024-005', 'FIXED_TERM', '2024-03-01', '2025-03-01', 15000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', NULL, 'Contract for HR Staff'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'ducanh.it@company.com'), 'HDLD-2024-006', 'FIXED_TERM', '2024-01-10', '2025-01-10', 12000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', NULL, 'Contract for IT Employee'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'giahuy.it@company.com'), 'HDLD-2024-007', 'FIXED_TERM', '2024-02-01', '2025-02-01', 12000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL, 'Contract for IT Employee'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'hoangnam.it@company.com'), 'HDLD-2024-008', 'FIXED_TERM', '2024-02-15', '2025-02-15', 12000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL, 'Contract for IT Employee'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'payrollmanager@company.com'), 'HDLD-2024-009', 'FIXED_TERM', '2024-01-20', '2025-01-20', 22000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL, 'Contract for Payroll Manager'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'thaovy.payroll@company.com'), 'HDLD-2024-010', 'FIXED_TERM', '2024-02-10', '2025-02-10', 11000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL, 'Contract for Payroll Staff'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'minhkhang.payroll@company.com'), 'HDLD-2024-011', 'FIXED_TERM', '2024-03-05', '2025-03-05', 11000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL, 'Contract for Payroll Staff'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'phuonganh.payroll@company.com'), 'HDLD-2024-012', 'FIXED_TERM', '2024-03-18', '2025-03-18', 11000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', NULL, 'Contract for Payroll Staff'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'payroll@company.com'), 'HDLD-2024-013', 'FIXED_TERM', '2024-04-10', '2025-04-10', 16000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL, 'Contract for Payroll Staff'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'salesmanager@company.com'), 'HDLD-2024-014', 'FIXED_TERM', '2024-01-25', '2025-01-25', 20000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL, 'Contract for Sales Manager'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'khanhly.sales@company.com'), 'HDLD-2024-015', 'FIXED_TERM', '2024-02-12', '2025-02-12', 11000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL, 'Contract for Sales Employee'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'quocbao.sales@company.com'), 'HDLD-2024-016', 'FIXED_TERM', '2024-03-08', '2025-03-08', 11000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', NULL, 'Contract for Sales Employee'),
                                                                                                                                                                ((SELECT id FROM users WHERE email = 'businessadmin@company.com'), 'HDLD-2024-017', 'FIXED_TERM', '2024-04-01', '2025-04-01', 28000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL, 'Contract for Business Admin');

-- Dữ liệu ngày phép (Giữ nguyên từ DB1)
INSERT INTO leave_balances (user_id, year, entitled_days, advanced_days, used_days, remaining_days)
SELECT id, 2026, 12.00,
       CASE WHEN employee_code IN ('EMP002', 'EMP006', 'EMP014') THEN 1.00 WHEN employee_code IN ('EMP003', 'EMP010') THEN 2.00 ELSE 0.00 END AS advanced_days,
       CASE WHEN employee_code IN ('EMP002', 'EMP006') THEN 3.00 WHEN employee_code IN ('EMP003', 'EMP010') THEN 5.00 WHEN employee_code IN ('EMP014', 'EMP015') THEN 2.00 ELSE 0.00 END AS used_days,
       12.00 + CASE WHEN employee_code IN ('EMP002', 'EMP006', 'EMP014') THEN 1.00 WHEN employee_code IN ('EMP003', 'EMP010') THEN 2.00 ELSE 0.00 END - CASE WHEN employee_code IN ('EMP002', 'EMP006') THEN 3.00 WHEN employee_code IN ('EMP003', 'EMP010') THEN 5.00 WHEN employee_code IN ('EMP014', 'EMP015') THEN 2.00 ELSE 0.00 END AS remaining_days
FROM users
WHERE active = TRUE
ON DUPLICATE KEY UPDATE entitled_days = VALUES(entitled_days), advanced_days = VALUES(advanced_days), used_days = VALUES(used_days), remaining_days = VALUES(remaining_days);
