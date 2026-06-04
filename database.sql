DROP DATABASE IF EXISTS hrm_db;
CREATE DATABASE hrm_db;
USE hrm_db;

-- 1. ROLES

CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME
);

-- 2. DEPARTMENTS

CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    manager_user_id INT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME
);

-- 3. POSITIONS

CREATE TABLE positions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME
);

-- 4. DEPARTMENT_POSITIONS
-- Một department có nhiều position
-- Một position có thể thuộc nhiều department

CREATE TABLE department_positions (
    department_id INT NOT NULL,
    position_id INT NOT NULL,

    PRIMARY KEY (department_id, position_id),

    CONSTRAINT fk_department_positions_department
        FOREIGN KEY (department_id) REFERENCES departments(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_department_positions_position
        FOREIGN KEY (position_id) REFERENCES positions(id)
        ON DELETE CASCADE
);

-- 5. USERS

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

    CONSTRAINT fk_users_roles
        FOREIGN KEY (role_id) REFERENCES roles(id),

    CONSTRAINT fk_users_departments
        FOREIGN KEY (department_id) REFERENCES departments(id),

    CONSTRAINT fk_users_positions
        FOREIGN KEY (position_id) REFERENCES positions(id),

    CONSTRAINT fk_users_department_positions
        FOREIGN KEY (department_id, position_id)
        REFERENCES department_positions(department_id, position_id)
);

ALTER TABLE departments
ADD CONSTRAINT fk_departments_manager
    FOREIGN KEY (manager_user_id) REFERENCES users(id);

-- 6. PERMISSIONS

CREATE TABLE permissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- 7. ROLE_PERMISSIONS

CREATE TABLE role_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,

    PRIMARY KEY (role_id, permission_id),

    CONSTRAINT fk_role_permissions_roles
        FOREIGN KEY (role_id) REFERENCES roles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_role_permissions_permissions
        FOREIGN KEY (permission_id) REFERENCES permissions(id)
        ON DELETE CASCADE
);

-- 8. PASSWORD RESET REQUESTS

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

-- 9. LABOR CONTRACTS

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

-- 10. INSERT ROLES

INSERT INTO roles (name, description, active)
VALUES
    ('ADMIN', 'System administrator: manages users, roles, and permissions', TRUE),
    ('HR_MANAGER', 'Manages HR business operations across the company', TRUE),
    ('HR_STAFF', 'Handles employee records, departments, and attendance operations', TRUE),
    ('DEPARTMENT_MANAGER', 'Manages employees within their own department', TRUE),
    ('PAYROLL_STAFF', 'Handles payroll processing', TRUE),
    ('EMPLOYEE', 'Normal employee with self-service functions', TRUE);

-- 11. INSERT DEPARTMENTS

INSERT INTO departments (name, description, active)
VALUES
    ('Human Resources', 'Human resource department', TRUE),
    ('Information Technology', 'Information technology department', TRUE),
    ('Finance', 'Finance department', TRUE),
    ('Sales', 'Sales department', TRUE);

-- 12. INSERT POSITIONS

INSERT INTO positions (name, description, active)
VALUES
    ('System Administrator', 'Responsible for system administration', TRUE),
    ('HR Manager', 'Responsible for HR management', TRUE),
    ('HR Staff', 'Responsible for HR daily operations', TRUE),
    ('Department Manager', 'Responsible for managing a department', TRUE),
    ('Payroll Staff', 'Responsible for payroll processing', TRUE),
    ('Software Developer', 'Responsible for software development', TRUE),
    ('Accountant', 'Responsible for finance and accounting tasks', TRUE),
    ('Sales Staff', 'Responsible for sales activities', TRUE),
    ('Employee', 'Normal employee position', TRUE);

-- 13. INSERT DEPARTMENT_POSITIONS

INSERT INTO department_positions (department_id, position_id)
VALUES
    -- Human Resources
    (
        (SELECT id FROM departments WHERE name = 'Human Resources'),
        (SELECT id FROM positions WHERE name = 'HR Manager')
    ),
    (
        (SELECT id FROM departments WHERE name = 'Human Resources'),
        (SELECT id FROM positions WHERE name = 'HR Staff')
    ),
    (
        (SELECT id FROM departments WHERE name = 'Human Resources'),
        (SELECT id FROM positions WHERE name = 'Employee')
    ),

    -- Information Technology
    (
        (SELECT id FROM departments WHERE name = 'Information Technology'),
        (SELECT id FROM positions WHERE name = 'System Administrator')
    ),
    (
        (SELECT id FROM departments WHERE name = 'Information Technology'),
        (SELECT id FROM positions WHERE name = 'Department Manager')
    ),
    (
        (SELECT id FROM departments WHERE name = 'Information Technology'),
        (SELECT id FROM positions WHERE name = 'Software Developer')
    ),
    (
        (SELECT id FROM departments WHERE name = 'Information Technology'),
        (SELECT id FROM positions WHERE name = 'Employee')
    ),

    -- Finance
    (
        (SELECT id FROM departments WHERE name = 'Finance'),
        (SELECT id FROM positions WHERE name = 'Payroll Staff')
    ),
    (
        (SELECT id FROM departments WHERE name = 'Finance'),
        (SELECT id FROM positions WHERE name = 'Accountant')
    ),
    (
        (SELECT id FROM departments WHERE name = 'Finance'),
        (SELECT id FROM positions WHERE name = 'Employee')
    ),

    -- Sales
    (
        (SELECT id FROM departments WHERE name = 'Sales'),
        (SELECT id FROM positions WHERE name = 'Sales Staff')
    ),
    (
        (SELECT id FROM departments WHERE name = 'Sales'),
        (SELECT id FROM positions WHERE name = 'Department Manager')
    ),
    (
        (SELECT id FROM departments WHERE name = 'Sales'),
        (SELECT id FROM positions WHERE name = 'Employee')
    );

-- 14. INSERT USERS
-- Default password: 123456

INSERT INTO users (
    employee_code,
    full_name,
    email,
    password,
    phone,
    gender,
    date_of_birth,
    address,
    avatar_url,
    role_id,
    department_id,
    position_id,
    hire_date,
    employment_status,
    active
)
VALUES
    (
        'EMP001',
        'Admin User',
        'admin@company.com',
        '123456',
        '0900000001',
        'Male',
        '2000-01-01 00:00:00',
        'Ho Chi Minh City',
        NULL,
        (SELECT id FROM roles WHERE name = 'ADMIN'),
        (SELECT id FROM departments WHERE name = 'Information Technology'),
        (SELECT id FROM positions WHERE name = 'System Administrator'),
        '2024-01-01',
        'WORKING',
        TRUE
    ),
    (
        'EMP002',
        'HR Manager',
        'hrmanager@company.com',
        '123456',
        '0900000002',
        'Female',
        '1998-02-02 00:00:00',
        'Ha Noi',
        NULL,
        (SELECT id FROM roles WHERE name = 'HR_MANAGER'),
        (SELECT id FROM departments WHERE name = 'Human Resources'),
        (SELECT id FROM positions WHERE name = 'HR Manager'),
        '2024-01-15',
        'WORKING',
        TRUE
    ),
    (
        'EMP003',
        'HR Staff',
        'hrstaff@company.com',
        '123456',
        '0900000003',
        'Female',
        '2001-03-03 00:00:00',
        'Ha Noi',
        NULL,
        (SELECT id FROM roles WHERE name = 'HR_STAFF'),
        (SELECT id FROM departments WHERE name = 'Human Resources'),
        (SELECT id FROM positions WHERE name = 'HR Staff'),
        '2024-02-01',
        'WORKING',
        TRUE
    ),
    (
        'EMP004',
        'IT Manager',
        'itmanager@company.com',
        '123456',
        '0900000004',
        'Male',
        '1997-04-04 00:00:00',
        'Da Nang',
        NULL,
        (SELECT id FROM roles WHERE name = 'DEPARTMENT_MANAGER'),
        (SELECT id FROM departments WHERE name = 'Information Technology'),
        (SELECT id FROM positions WHERE name = 'Department Manager'),
        '2024-02-15',
        'WORKING',
        TRUE
    ),
    (
        'EMP005',
        'Payroll Staff',
        'payroll@company.com',
        '123456',
        '0900000005',
        'Female',
        '1999-05-05 00:00:00',
        'Ho Chi Minh City',
        NULL,
        (SELECT id FROM roles WHERE name = 'PAYROLL_STAFF'),
        (SELECT id FROM departments WHERE name = 'Finance'),
        (SELECT id FROM positions WHERE name = 'Payroll Staff'),
        '2024-03-01',
        'WORKING',
        TRUE
    ),
    (
        'EMP006',
        'Employee User',
        'employee@company.com',
        '123456',
        '0900000006',
        'Male',
        '2002-06-06 00:00:00',
        'Da Nang',
        NULL,
        (SELECT id FROM roles WHERE name = 'EMPLOYEE'),
        (SELECT id FROM departments WHERE name = 'Information Technology'),
        (SELECT id FROM positions WHERE name = 'Software Developer'),
        '2024-03-15',
        'WORKING',
        TRUE
    );

-- 15. ASSIGN DEPARTMENT MANAGERS

UPDATE departments
SET manager_user_id = (SELECT id FROM users WHERE email = 'hrmanager@company.com')
WHERE name = 'Human Resources';

UPDATE departments
SET manager_user_id = (SELECT id FROM users WHERE email = 'itmanager@company.com')
WHERE name = 'Information Technology';

-- 16. INSERT PERMISSIONS

INSERT INTO permissions (code, name, description)
VALUES
    -- Common / Auth / Profile
    ('HOMEPAGE_VIEW', 'View homepage', 'Can view homepage/dashboard'),
    ('AUTH_LOGIN', 'Login', 'Can login to the system'),
    ('AUTH_LOGOUT', 'Logout', 'Can logout from the system'),
    ('AUTH_FORGOT_PASSWORD', 'Forgot password', 'Can request password reset'),
    ('PROFILE_VIEW', 'View my profile', 'Can view own profile'),
    ('PROFILE_CHANGE_PASSWORD', 'Change password', 'Can change own password'),

    -- User Management
    ('USER_VIEW_LIST', 'View user list', 'Can view list of users'),
    ('USER_VIEW_DETAIL', 'View user information', 'Can view user detail'),
    ('USER_CREATE', 'Add new user', 'Can create new user'),
    ('USER_UPDATE', 'Update user information', 'Can update user information'),
    ('USER_TOGGLE_STATUS', 'Active/deactive user', 'Can activate or deactivate user'),

    -- Role / Permission Management
    ('ROLE_VIEW_LIST', 'View role list', 'Can view role list'),
    ('ROLE_VIEW_PERMISSION', 'View role permissions', 'Can view permissions of role'),
    ('ROLE_UPDATE', 'Update role information', 'Can update role information'),
    ('ROLE_TOGGLE_STATUS', 'Active/deactive role', 'Can activate or deactivate role'),
    ('ROLE_EDIT_PERMISSION', 'Edit role permissions', 'Can edit permissions of role'),
    ('ROLE_CREATE', 'Add new role', 'Can create new role'),

    -- Department Management
    ('DEPARTMENT_VIEW_LIST', 'View department list', 'Can view list of departments'),
    ('DEPARTMENT_VIEW_DETAIL', 'View department detail', 'Can view department detail'),
    ('DEPARTMENT_CREATE', 'Add department', 'Can create new department'),
    ('DEPARTMENT_UPDATE', 'Update department', 'Can update department information'),
    ('DEPARTMENT_TOGGLE_STATUS', 'Active/deactive department', 'Can activate or deactivate department'),
    ('DEPARTMENT_ASSIGN_MANAGER', 'Assign department manager', 'Can assign manager to department'),
    ('DEPARTMENT_VIEW_EMPLOYEES', 'View department employees', 'Can view employees in a department'),

    -- Position Management
    ('POSITION_VIEW_LIST', 'View position list', 'Can view list of positions'),
    ('POSITION_VIEW_DETAIL', 'View position detail', 'Can view position detail'),
    ('POSITION_CREATE', 'Add position', 'Can create new position'),
    ('POSITION_UPDATE', 'Update position', 'Can update position information'),
    ('POSITION_TOGGLE_STATUS', 'Active/deactive position', 'Can activate or deactivate position'),

    -- Labor Contract Management
    ('CONTRACT_VIEW_LIST', 'View contract list', 'Can view list of labor contracts'),
    ('CONTRACT_VIEW_DETAIL', 'View contract detail', 'Can view contract detail'),
    ('CONTRACT_VIEW_OWN', 'View own contract', 'Can view own labor contract'),
    ('CONTRACT_CREATE', 'Add contract', 'Can create new labor contract'),
    ('CONTRACT_UPDATE', 'Update labor contract', 'Can update labor contract'),
    ('CONTRACT_TERMINATE', 'Terminate contract', 'Can terminate labor contract'),
    ('CONTRACT_RENEW', 'Renew contract', 'Can renew labor contract'),

    -- Attendance Management
    ('ATTENDANCE_CHECK_IN', 'Check in', 'Can check in'),
    ('ATTENDANCE_CHECK_OUT', 'Check out', 'Can check out'),
    ('ATTENDANCE_VIEW_OWN', 'View own attendance', 'Can view own attendance'),
    ('ATTENDANCE_VIEW_DEPARTMENT', 'View department attendance', 'Can view attendance of own department'),
    ('ATTENDANCE_VIEW_ALL', 'View all attendance', 'Can view all attendance records'),
    ('ATTENDANCE_UPDATE', 'Update attendance', 'Can update attendance records'),
    ('ATTENDANCE_EXPORT_REPORT', 'Export attendance report', 'Can export attendance report'),

    -- Payroll Management
    ('PAYROLL_VIEW_OWN', 'View own salary', 'Can view own salary'),
    ('PAYROLL_VIEW_LIST', 'View payroll list', 'Can view payroll list'),
    ('PAYROLL_VIEW_DETAIL', 'View employee salary detail', 'Can view employee salary detail'),
    ('PAYROLL_GENERATE', 'Generate payroll', 'Can generate monthly payroll'),
    ('PAYROLL_UPDATE_COMPONENT', 'Update salary component', 'Can update salary components'),
    ('PAYROLL_CONFIRM', 'Confirm payroll', 'Can confirm payroll'),
    ('PAYROLL_EXPORT_REPORT', 'Export payroll report', 'Can export payroll report');

-- 17. ROLE PERMISSIONS

-- ADMIN

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.name = 'ADMIN'
  AND p.code IN (
    'HOMEPAGE_VIEW',
    'AUTH_LOGIN',
    'AUTH_LOGOUT',
    'AUTH_FORGOT_PASSWORD',
    'PROFILE_VIEW',
    'PROFILE_CHANGE_PASSWORD',

    'USER_VIEW_LIST',
    'USER_VIEW_DETAIL',
    'USER_CREATE',
    'USER_UPDATE',
    'USER_TOGGLE_STATUS',

    'ROLE_VIEW_LIST',
    'ROLE_VIEW_PERMISSION',
    'ROLE_UPDATE',
    'ROLE_TOGGLE_STATUS',
    'ROLE_EDIT_PERMISSION',
    'ROLE_CREATE'
);

-- HR_MANAGER

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.name = 'HR_MANAGER'
  AND p.code IN (
    'HOMEPAGE_VIEW',
    'AUTH_LOGIN',
    'AUTH_LOGOUT',
    'AUTH_FORGOT_PASSWORD',
    'PROFILE_VIEW',
    'PROFILE_CHANGE_PASSWORD',

    'USER_VIEW_LIST',
    'USER_VIEW_DETAIL',
    'USER_CREATE',
    'USER_UPDATE',
    'USER_TOGGLE_STATUS',

    'DEPARTMENT_VIEW_LIST',
    'DEPARTMENT_VIEW_DETAIL',
    'DEPARTMENT_CREATE',
    'DEPARTMENT_UPDATE',
    'DEPARTMENT_TOGGLE_STATUS',
    'DEPARTMENT_ASSIGN_MANAGER',
    'DEPARTMENT_VIEW_EMPLOYEES',

    'POSITION_VIEW_LIST',
    'POSITION_VIEW_DETAIL',
    'POSITION_CREATE',
    'POSITION_UPDATE',
    'POSITION_TOGGLE_STATUS',

    'CONTRACT_VIEW_LIST',
    'CONTRACT_VIEW_DETAIL',
    'CONTRACT_VIEW_OWN',
    'CONTRACT_CREATE',
    'CONTRACT_UPDATE',
    'CONTRACT_TERMINATE',
    'CONTRACT_RENEW',

    'ATTENDANCE_VIEW_OWN',
    'ATTENDANCE_VIEW_DEPARTMENT',
    'ATTENDANCE_VIEW_ALL',
    'ATTENDANCE_UPDATE',
    'ATTENDANCE_EXPORT_REPORT',

    'PAYROLL_VIEW_OWN',
    'PAYROLL_VIEW_LIST',
    'PAYROLL_CONFIRM',
    'PAYROLL_EXPORT_REPORT'
);

-- HR_STAFF

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.name = 'HR_STAFF'
  AND p.code IN (
    'HOMEPAGE_VIEW',
    'AUTH_LOGIN',
    'AUTH_LOGOUT',
    'AUTH_FORGOT_PASSWORD',
    'PROFILE_VIEW',
    'PROFILE_CHANGE_PASSWORD',

    'USER_VIEW_LIST',
    'USER_VIEW_DETAIL',

    'DEPARTMENT_VIEW_LIST',
    'DEPARTMENT_VIEW_DETAIL',
    'DEPARTMENT_VIEW_EMPLOYEES',

    'POSITION_VIEW_LIST',
    'POSITION_VIEW_DETAIL',

    'CONTRACT_VIEW_LIST',
    'CONTRACT_VIEW_DETAIL',
    'CONTRACT_VIEW_OWN',
    'CONTRACT_CREATE',
    'CONTRACT_UPDATE',
    'CONTRACT_TERMINATE',

    'ATTENDANCE_VIEW_OWN',
    'ATTENDANCE_VIEW_DEPARTMENT',
    'ATTENDANCE_VIEW_ALL',
    'ATTENDANCE_UPDATE',
    'ATTENDANCE_EXPORT_REPORT',

    'PAYROLL_VIEW_OWN'
);

-- DEPARTMENT_MANAGER

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.name = 'DEPARTMENT_MANAGER'
  AND p.code IN (
    'HOMEPAGE_VIEW',
    'AUTH_LOGIN',
    'AUTH_LOGOUT',
    'AUTH_FORGOT_PASSWORD',
    'PROFILE_VIEW',
    'PROFILE_CHANGE_PASSWORD',

    'DEPARTMENT_VIEW_EMPLOYEES',

    'ATTENDANCE_VIEW_OWN',
    'ATTENDANCE_VIEW_DEPARTMENT',

    'CONTRACT_VIEW_OWN',
    'PAYROLL_VIEW_OWN'
);

-- PAYROLL_STAFF

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.name = 'PAYROLL_STAFF'
  AND p.code IN (
    'HOMEPAGE_VIEW',
    'AUTH_LOGIN',
    'AUTH_LOGOUT',
    'AUTH_FORGOT_PASSWORD',
    'PROFILE_VIEW',
    'PROFILE_CHANGE_PASSWORD',

    'ATTENDANCE_VIEW_ALL',
    'ATTENDANCE_EXPORT_REPORT',

    'CONTRACT_VIEW_OWN',

    'PAYROLL_VIEW_OWN',
    'PAYROLL_VIEW_LIST',
    'PAYROLL_VIEW_DETAIL',
    'PAYROLL_GENERATE',
    'PAYROLL_UPDATE_COMPONENT',
    'PAYROLL_EXPORT_REPORT'
);

-- EMPLOYEE

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.name = 'EMPLOYEE'
  AND p.code IN (
    'HOMEPAGE_VIEW',
    'AUTH_LOGIN',
    'AUTH_LOGOUT',
    'AUTH_FORGOT_PASSWORD',
    'PROFILE_VIEW',
    'PROFILE_CHANGE_PASSWORD',

    'ATTENDANCE_CHECK_IN',
    'ATTENDANCE_CHECK_OUT',
    'ATTENDANCE_VIEW_OWN',

    'CONTRACT_VIEW_OWN',
    'PAYROLL_VIEW_OWN'
);

-- 18. SAMPLE LABOR CONTRACTS

INSERT INTO labor_contracts (
    user_id,
    contract_code,
    contract_type,
    start_date,
    end_date,
    base_salary,
    working_time,
    work_location,
    status,
    file_url,
    note
)
VALUES
    (
        (SELECT id FROM users WHERE email = 'hrmanager@company.com'),
        'HDLD-2024-001',
        'FIXED_TERM',
        '2024-01-15',
        '2025-01-15',
        25000000,
        'Monday to Friday, 8:00 - 17:00',
        'Ha Noi Office',
        'ACTIVE',
        NULL,
        'Sample contract for HR Manager'
    ),
    (
        (SELECT id FROM users WHERE email = 'hrstaff@company.com'),
        'HDLD-2024-002',
        'FIXED_TERM',
        '2024-02-01',
        '2025-02-01',
        15000000,
        'Monday to Friday, 8:00 - 17:00',
        'Ha Noi Office',
        'ACTIVE',
        NULL,
        'Sample contract for HR Staff'
    ),
    (
        (SELECT id FROM users WHERE email = 'itmanager@company.com'),
        'HDLD-2024-003',
        'FIXED_TERM',
        '2024-02-15',
        '2025-02-15',
        22000000,
        'Monday to Friday, 8:00 - 17:00',
        'Da Nang Office',
        'ACTIVE',
        NULL,
        'Sample contract for IT Manager'
    ),
    (
        (SELECT id FROM users WHERE email = 'payroll@company.com'),
        'HDLD-2024-004',
        'FIXED_TERM',
        '2024-03-01',
        '2025-03-01',
        16000000,
        'Monday to Friday, 8:00 - 17:00',
        'Ho Chi Minh Office',
        'ACTIVE',
        NULL,
        'Sample contract for Payroll Staff'
    ),
    (
        (SELECT id FROM users WHERE email = 'employee@company.com'),
        'HDLD-2024-005',
        'FIXED_TERM',
        '2024-03-15',
        '2025-03-15',
        12000000,
        'Monday to Friday, 8:00 - 17:00',
        'Da Nang Office',
        'ACTIVE',
        NULL,
        'Sample contract for Employee'
    );

-- 19. TEST ACCOUNTS

-- ADMIN:
-- Email: admin@company.com
-- Password: 123456

-- HR_MANAGER:
-- Email: hrmanager@company.com
-- Password: 123456

-- HR_STAFF:
-- Email: hrstaff@company.com
-- Password: 123456

-- DEPARTMENT_MANAGER:
-- Email: itmanager@company.com
-- Password: 123456

-- PAYROLL_STAFF:
-- Email: payroll@company.com
-- Password: 123456

-- EMPLOYEE:
-- Email: employee@company.com
-- Password: 123456
