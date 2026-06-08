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
    ('SYSTEM ADMIN', 'System administrator: manages users, roles, and permissions', TRUE),
    ('BUSINESS ADMIN', 'Business administrator: has all permissions in the system', TRUE),
    ('HR_MANAGER', 'Manages HR business operations across the company', TRUE),
    ('HR_STAFF', 'Handles employee records, departments, and attendance operations', TRUE),
    ('PAYROLL_MANAGER', 'Manages payroll operations and payroll staff', TRUE),
    ('PAYROLL_STAFF', 'Handles payroll processing', TRUE),
    ('DEPARTMENT_MANAGER', 'Manages employees within their own department', TRUE),
    ('EMPLOYEE', 'Normal employee with self-service functions', TRUE);

-- 11. INSERT DEPARTMENTS

INSERT INTO departments (name, description, active)
VALUES
    ('Human Resources', 'Human resource department', TRUE),
    ('Information Technology', 'Information technology department', TRUE),
    ('Finance', 'Finance department (Payroll)', TRUE),
    ('Sales', 'Sales department', TRUE);

-- 12. INSERT POSITIONS

INSERT INTO positions (name, description, active)
VALUES
    ('System Administrator', 'Responsible for system administration', TRUE),
    ('HR Manager', 'Responsible for HR management', TRUE),
    ('HR Staff', 'Responsible for HR daily operations', TRUE),
    ('Payroll Manager', 'Manages payroll operations', TRUE),
    ('Payroll Staff', 'Responsible for payroll processing', TRUE),
    ('Department Manager', 'Responsible for managing a department', TRUE),
    ('Employee', 'Normal employee position', TRUE);

-- 13. INSERT DEPARTMENT_POSITIONS

INSERT INTO department_positions (department_id, position_id)
VALUES
    ((SELECT id FROM departments WHERE name = 'Human Resources'),
     (SELECT id FROM positions WHERE name = 'HR Manager')),
    ((SELECT id FROM departments WHERE name = 'Human Resources'),
     (SELECT id FROM positions WHERE name = 'HR Staff')),

    ((SELECT id FROM departments WHERE name = 'Information Technology'),
     (SELECT id FROM positions WHERE name = 'System Administrator')),
    ((SELECT id FROM departments WHERE name = 'Information Technology'),
     (SELECT id FROM positions WHERE name = 'Employee')),

    ((SELECT id FROM departments WHERE name = 'Finance'),
     (SELECT id FROM positions WHERE name = 'Payroll Manager')),
    ((SELECT id FROM departments WHERE name = 'Finance'),
     (SELECT id FROM positions WHERE name = 'Payroll Staff')),
	((SELECT id FROM departments WHERE name = 'Finance'),
	 (SELECT id FROM positions WHERE name = 'Employee')),
    
    ((SELECT id FROM departments WHERE name = 'Sales'),
     (SELECT id FROM positions WHERE name = 'Department Manager')),
    ((SELECT id FROM departments WHERE name = 'Sales'),
     (SELECT id FROM positions WHERE name = 'Employee'));

-- 14. INSERT USERS

INSERT INTO users (
    employee_code, full_name, email, password, phone, gender,
    date_of_birth, address, avatar_url, role_id, department_id, position_id,
    hire_date, employment_status, active
)
VALUES
    -- SYSTEM ADMIN (IT Manager)
    ('EMP001', 'Nguyễn Minh Quân', 'admin@company.com', '123456',
     '0900000001', 'Male', '1998-01-10 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'SYSTEM ADMIN'),
     (SELECT id FROM departments WHERE name = 'Information Technology'),
     (SELECT id FROM positions WHERE name = 'System Administrator'),
     '2024-01-01', 'WORKING', TRUE),

    -- EMPLOYEES IT
    ('EMP002', 'Trần Đức Anh', 'ducanh.it@company.com', '123456',
     '0900000002', 'Male', '2000-03-15 00:00:00', 'Đà Nẵng', NULL,
     (SELECT id FROM roles WHERE name = 'EMPLOYEE'),
     (SELECT id FROM departments WHERE name = 'Information Technology'),
     (SELECT id FROM positions WHERE name = 'Employee'),
     '2024-01-10', 'WORKING', TRUE),

    ('EMP003', 'Phạm Gia Huy', 'giahuy.it@company.com', '123456',
     '0900000003', 'Male', '2001-07-20 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'EMPLOYEE'),
     (SELECT id FROM departments WHERE name = 'Information Technology'),
     (SELECT id FROM positions WHERE name = 'Employee'),
     '2024-02-01', 'WORKING', TRUE),

    ('EMP004', 'Lê Hoàng Nam', 'hoangnam.it@company.com', '123456',
     '0900000004', 'Male', '1999-11-05 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'EMPLOYEE'),
     (SELECT id FROM departments WHERE name = 'Information Technology'),
     (SELECT id FROM positions WHERE name = 'Employee'),
     '2024-02-15', 'WORKING', TRUE),

    -- HR MANAGER
    ('EMP005', 'Nguyễn Thu Hà', 'hrmanager@company.com', '123456',
     '0900000005', 'Female', '1995-02-12 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'HR_MANAGER'),
     (SELECT id FROM departments WHERE name = 'Human Resources'),
     (SELECT id FROM positions WHERE name = 'HR Manager'),
     '2024-01-15', 'WORKING', TRUE),

    -- HR STAFF
    ('EMP006', 'Trần Mai Anh', 'maianh.hr@company.com', '123456',
     '0900000006', 'Female', '1999-04-18 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'HR_STAFF'),
     (SELECT id FROM departments WHERE name = 'Human Resources'),
     (SELECT id FROM positions WHERE name = 'HR Staff'),
     '2024-02-01', 'WORKING', TRUE),

    ('EMP007', 'Phạm Ngọc Linh', 'ngoclinh.hr@company.com', '123456',
     '0900000007', 'Female', '2000-08-25 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'HR_STAFF'),
     (SELECT id FROM departments WHERE name = 'Human Resources'),
     (SELECT id FROM positions WHERE name = 'HR Staff'),
     '2024-02-20', 'WORKING', TRUE),

    ('EMP008', 'Vũ Hải Yến', 'haiyen.hr@company.com', '123456',
     '0900000008', 'Female', '2001-12-03 00:00:00', 'Đà Nẵng', NULL,
     (SELECT id FROM roles WHERE name = 'HR_STAFF'),
     (SELECT id FROM departments WHERE name = 'Human Resources'),
     (SELECT id FROM positions WHERE name = 'HR Staff'),
     '2024-03-01', 'WORKING', TRUE),

    -- PAYROLL MANAGER (Finance Manager)
    ('EMP009', 'Đỗ Quang Huy', 'payrollmanager@company.com', '123456',
     '0900000009', 'Male', '1994-06-09 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'PAYROLL_MANAGER'),
     (SELECT id FROM departments WHERE name = 'Finance'),
     (SELECT id FROM positions WHERE name = 'Payroll Manager'),
     '2024-01-20', 'WORKING', TRUE),

    -- PAYROLL STAFF (Finance)
    ('EMP010', 'Nguyễn Thảo Vy', 'thaovy.payroll@company.com', '123456',
     '0900000010', 'Female', '2000-09-14 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'PAYROLL_STAFF'),
     (SELECT id FROM departments WHERE name = 'Finance'),
     (SELECT id FROM positions WHERE name = 'Payroll Staff'),
     '2024-02-10', 'WORKING', TRUE),

    ('EMP011', 'Bùi Minh Khang', 'minhkhang.payroll@company.com', '123456',
     '0900000011', 'Male', '1998-10-22 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'PAYROLL_STAFF'),
     (SELECT id FROM departments WHERE name = 'Finance'),
     (SELECT id FROM positions WHERE name = 'Payroll Staff'),
     '2024-03-05', 'WORKING', TRUE),

    ('EMP012', 'Lê Phương Anh', 'phuonganh.payroll@company.com', '123456',
     '0900000012', 'Female', '2001-05-17 00:00:00', 'Đà Nẵng', NULL,
     (SELECT id FROM roles WHERE name = 'PAYROLL_STAFF'),
     (SELECT id FROM departments WHERE name = 'Finance'),
     (SELECT id FROM positions WHERE name = 'Payroll Staff'),
     '2024-03-18', 'WORKING', TRUE),

    -- DEPARTMENT_MANAGER Sales
    ('EMP013', 'Hoàng Minh Đức', 'salesmanager@company.com', '123456',
     '0900000013', 'Male', '1993-03-30 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'DEPARTMENT_MANAGER'),
     (SELECT id FROM departments WHERE name = 'Sales'),
     (SELECT id FROM positions WHERE name = 'Department Manager'),
     '2024-01-25', 'WORKING', TRUE),

    -- EMPLOYEES Sales
    ('EMP014', 'Nguyễn Khánh Ly', 'khanhly.sales@company.com', '123456',
     '0900000014', 'Female', '2000-07-11 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'EMPLOYEE'),
     (SELECT id FROM departments WHERE name = 'Sales'),
     (SELECT id FROM positions WHERE name = 'Employee'),
     '2024-02-12', 'WORKING', TRUE),

    ('EMP015', 'Trần Quốc Bảo', 'quocbao.sales@company.com', '123456',
     '0900000015', 'Male', '1999-01-28 00:00:00', 'Đà Nẵng', NULL,
     (SELECT id FROM roles WHERE name = 'EMPLOYEE'),
     (SELECT id FROM departments WHERE name = 'Sales'),
     (SELECT id FROM positions WHERE name = 'Employee'),
     '2024-03-08', 'WORKING', TRUE),

    -- BUSINESS ADMIN (không thuộc phòng ban, không có chức vụ)
    ('EMP016', 'Phan Bảo Long', 'businessadmin@company.com', '123456',
     '0900000016', 'Male', '1996-08-22 00:00:00', 'Hà Nội', NULL,
     (SELECT id FROM roles WHERE name = 'BUSINESS ADMIN'),
     NULL, NULL,
     '2024-04-01', 'WORKING', TRUE),

    -- PAYROLL_STAFF (additional)
    ('EMP017', 'Đặng Thanh Hương', 'payroll@company.com', '123456',
     '0900000017', 'Female', '1997-05-14 00:00:00', 'Hồ Chí Minh', NULL,
     (SELECT id FROM roles WHERE name = 'PAYROLL_STAFF'),
     (SELECT id FROM departments WHERE name = 'Finance'),
     (SELECT id FROM positions WHERE name = 'Payroll Staff'),
     '2024-04-10', 'WORKING', TRUE);

-- 15. ASSIGN DEPARTMENT MANAGERS

UPDATE departments
SET manager_user_id = (SELECT id FROM users WHERE email = 'admin@company.com')
WHERE name = 'Information Technology';

UPDATE departments
SET manager_user_id = (SELECT id FROM users WHERE email = 'hrmanager@company.com')
WHERE name = 'Human Resources';

UPDATE departments
SET manager_user_id = (SELECT id FROM users WHERE email = 'payrollmanager@company.com')
WHERE name = 'Finance';

UPDATE departments
SET manager_user_id = (SELECT id FROM users WHERE email = 'salesmanager@company.com')
WHERE name = 'Sales';

-- 16. INSERT PERMISSIONS

INSERT INTO permissions (code, name, description)
VALUES
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
    ('PAYROLL_EXPORT_REPORT', 'Export payroll report', 'Can export payroll report');

-- 17. ROLE PERMISSIONS

-- SYSTEM ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
WHERE r.name = 'SYSTEM ADMIN'
  AND p.code IN (
                 'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD',
                 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                 'USER_VIEW_LIST', 'USER_VIEW_DETAIL', 'USER_CREATE', 'USER_UPDATE', 'USER_TOGGLE_STATUS',
                 'ROLE_VIEW_LIST', 'ROLE_VIEW_PERMISSION', 'ROLE_UPDATE', 'ROLE_TOGGLE_STATUS',
                 'ROLE_EDIT_PERMISSION', 'ROLE_CREATE'
    );

-- BUSINESS ADMIN (tất cả permissions)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'BUSINESS ADMIN';

-- HR_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
WHERE r.name = 'HR_MANAGER'
  AND p.code IN (
                 'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD',
                 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                 'USER_VIEW_LIST', 'USER_VIEW_DETAIL', 'USER_CREATE', 'USER_UPDATE', 'USER_TOGGLE_STATUS',
                 'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_CREATE', 'DEPARTMENT_UPDATE',
                 'DEPARTMENT_TOGGLE_STATUS', 'DEPARTMENT_ASSIGN_MANAGER', 'DEPARTMENT_VIEW_EMPLOYEES',
                 'POSITION_VIEW_LIST', 'POSITION_VIEW_DETAIL', 'POSITION_CREATE', 'POSITION_UPDATE',
                 'POSITION_TOGGLE_STATUS',
                 'CONTRACT_VIEW_LIST', 'CONTRACT_VIEW_DETAIL', 'CONTRACT_VIEW_OWN', 'CONTRACT_CREATE',
                 'CONTRACT_UPDATE', 'CONTRACT_TERMINATE', 'CONTRACT_RENEW',
                 'ATTENDANCE_VIEW_OWN', 'ATTENDANCE_VIEW_DEPARTMENT', 'ATTENDANCE_VIEW_ALL',
                 'ATTENDANCE_UPDATE', 'ATTENDANCE_EXPORT_REPORT',
                 'PAYROLL_VIEW_OWN', 'PAYROLL_VIEW_LIST', 'PAYROLL_CONFIRM', 'PAYROLL_EXPORT_REPORT'
    );

-- HR_STAFF
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
WHERE r.name = 'HR_STAFF'
  AND p.code IN (
                 'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD',
                 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                 'USER_VIEW_LIST', 'USER_VIEW_DETAIL',
                 'DEPARTMENT_VIEW_LIST', 'DEPARTMENT_VIEW_DETAIL', 'DEPARTMENT_VIEW_EMPLOYEES',
                 'POSITION_VIEW_LIST', 'POSITION_VIEW_DETAIL',
                 'CONTRACT_VIEW_LIST', 'CONTRACT_VIEW_DETAIL', 'CONTRACT_VIEW_OWN', 'CONTRACT_CREATE',
                 'CONTRACT_UPDATE', 'CONTRACT_TERMINATE',
                 'ATTENDANCE_VIEW_OWN', 'ATTENDANCE_VIEW_DEPARTMENT', 'ATTENDANCE_VIEW_ALL',
                 'ATTENDANCE_UPDATE', 'ATTENDANCE_EXPORT_REPORT',
                 'PAYROLL_VIEW_OWN'
    );

-- PAYROLL_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
WHERE r.name = 'PAYROLL_MANAGER'
  AND p.code IN (
                 'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD',
                 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                 'DEPARTMENT_VIEW_EMPLOYEES',
                 'ATTENDANCE_VIEW_OWN', 'ATTENDANCE_VIEW_DEPARTMENT',
                 'CONTRACT_VIEW_OWN',
                 'PAYROLL_VIEW_OWN', 'PAYROLL_VIEW_LIST', 'PAYROLL_VIEW_DETAIL',
                 'PAYROLL_GENERATE', 'PAYROLL_UPDATE_COMPONENT', 'PAYROLL_CONFIRM', 'PAYROLL_EXPORT_REPORT'
    );

-- PAYROLL_STAFF
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
WHERE r.name = 'PAYROLL_STAFF'
  AND p.code IN (
                 'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD',
                 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                 'ATTENDANCE_VIEW_ALL', 'ATTENDANCE_EXPORT_REPORT',
                 'CONTRACT_VIEW_OWN',
                 'PAYROLL_VIEW_OWN', 'PAYROLL_VIEW_LIST', 'PAYROLL_VIEW_DETAIL',
                 'PAYROLL_GENERATE', 'PAYROLL_UPDATE_COMPONENT', 'PAYROLL_EXPORT_REPORT'
    );

-- DEPARTMENT_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
WHERE r.name = 'DEPARTMENT_MANAGER'
  AND p.code IN (
                 'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD',
                 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                 'DEPARTMENT_VIEW_EMPLOYEES',
                 'ATTENDANCE_VIEW_OWN', 'ATTENDANCE_VIEW_DEPARTMENT',
                 'CONTRACT_VIEW_OWN', 'PAYROLL_VIEW_OWN'
    );

-- EMPLOYEE
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
WHERE r.name = 'EMPLOYEE'
  AND p.code IN (
                 'HOMEPAGE_VIEW', 'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_FORGOT_PASSWORD',
                 'PROFILE_VIEW', 'PROFILE_CHANGE_PASSWORD',
                 'ATTENDANCE_CHECK_IN', 'ATTENDANCE_CHECK_OUT', 'ATTENDANCE_VIEW_OWN',
                 'CONTRACT_VIEW_OWN', 'PAYROLL_VIEW_OWN'
    );

-- 18. SAMPLE LABOR CONTRACTS

INSERT INTO labor_contracts (user_id, contract_code, contract_type, start_date, end_date,
                             base_salary, working_time, work_location, status, file_url, note)
VALUES
    ((SELECT id FROM users WHERE email = 'admin@company.com'),
     'HDLD-2024-001', 'FIXED_TERM', '2024-01-01', '2025-01-01',
     30000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL,
     'Contract for System Admin'),
    ((SELECT id FROM users WHERE email = 'hrmanager@company.com'),
     'HDLD-2024-002', 'FIXED_TERM', '2024-01-15', '2025-01-15',
     25000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL,
     'Contract for HR Manager'),
    ((SELECT id FROM users WHERE email = 'maianh.hr@company.com'),
     'HDLD-2024-003', 'FIXED_TERM', '2024-02-01', '2025-02-01',
     15000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL,
     'Contract for HR Staff'),
    ((SELECT id FROM users WHERE email = 'ngoclinh.hr@company.com'),
     'HDLD-2024-004', 'FIXED_TERM', '2024-02-20', '2025-02-20',
     15000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL,
     'Contract for HR Staff'),
    ((SELECT id FROM users WHERE email = 'haiyen.hr@company.com'),
     'HDLD-2024-005', 'FIXED_TERM', '2024-03-01', '2025-03-01',
     15000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', NULL,
     'Contract for HR Staff'),
    ((SELECT id FROM users WHERE email = 'ducanh.it@company.com'),
     'HDLD-2024-006', 'FIXED_TERM', '2024-01-10', '2025-01-10',
     12000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', NULL,
     'Contract for IT Employee'),
    ((SELECT id FROM users WHERE email = 'giahuy.it@company.com'),
     'HDLD-2024-007', 'FIXED_TERM', '2024-02-01', '2025-02-01',
     12000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL,
     'Contract for IT Employee'),
    ((SELECT id FROM users WHERE email = 'hoangnam.it@company.com'),
     'HDLD-2024-008', 'FIXED_TERM', '2024-02-15', '2025-02-15',
     12000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL,
     'Contract for IT Employee'),
    ((SELECT id FROM users WHERE email = 'payrollmanager@company.com'),
     'HDLD-2024-009', 'FIXED_TERM', '2024-01-20', '2025-01-20',
     22000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL,
     'Contract for Payroll Manager'),
    ((SELECT id FROM users WHERE email = 'thaovy.payroll@company.com'),
     'HDLD-2024-010', 'FIXED_TERM', '2024-02-10', '2025-02-10',
     11000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL,
     'Contract for Payroll Staff'),
    ((SELECT id FROM users WHERE email = 'minhkhang.payroll@company.com'),
     'HDLD-2024-011', 'FIXED_TERM', '2024-03-05', '2025-03-05',
     11000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL,
     'Contract for Payroll Staff'),
    ((SELECT id FROM users WHERE email = 'phuonganh.payroll@company.com'),
     'HDLD-2024-012', 'FIXED_TERM', '2024-03-18', '2025-03-18',
     11000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', NULL,
     'Contract for Payroll Staff'),
    ((SELECT id FROM users WHERE email = 'payroll@company.com'),
     'HDLD-2024-013', 'FIXED_TERM', '2024-04-10', '2025-04-10',
     16000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL,
     'Contract for Payroll Staff'),
    ((SELECT id FROM users WHERE email = 'salesmanager@company.com'),
     'HDLD-2024-014', 'FIXED_TERM', '2024-01-25', '2025-01-25',
     20000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL,
     'Contract for Sales Manager'),
    ((SELECT id FROM users WHERE email = 'khanhly.sales@company.com'),
     'HDLD-2024-015', 'FIXED_TERM', '2024-02-12', '2025-02-12',
     11000000, 'Monday to Friday, 8:00 - 17:00', 'Ho Chi Minh Office', 'ACTIVE', NULL,
     'Contract for Sales Employee'),
    ((SELECT id FROM users WHERE email = 'quocbao.sales@company.com'),
     'HDLD-2024-016', 'FIXED_TERM', '2024-03-08', '2025-03-08',
     11000000, 'Monday to Friday, 8:00 - 17:00', 'Da Nang Office', 'ACTIVE', NULL,
     'Contract for Sales Employee'),
    ((SELECT id FROM users WHERE email = 'businessadmin@company.com'),
     'HDLD-2024-017', 'FIXED_TERM', '2024-04-01', '2025-04-01',
     28000000, 'Monday to Friday, 8:00 - 17:00', 'Ha Noi Office', 'ACTIVE', NULL,
     'Contract for Business Admin');

-- 19. TEST ACCOUNTS

-- SYSTEM ADMIN (IT Manager):
-- Email: admin@company.com / Password: 123456

-- BUSINESS ADMIN (không phòng ban, không chức vụ):
-- Email: businessadmin@company.com / Password: 123456

-- HR_MANAGER (HR Manager):
-- Email: hrmanager@company.com / Password: 123456

-- HR_STAFF:
-- Email: maianh.hr@company.com / ngoclinh.hr@company.com / haiyen.hr@company.com
-- Password: 123456

-- PAYROLL_MANAGER (Finance Manager):
-- Email: payrollmanager@company.com / Password: 123456

-- PAYROLL_STAFF:
-- Email: thaovy.payroll@company.com / minhkhang.payroll@company.com / phuonganh.payroll@company.com / payroll@company.com
-- Password: 123456

-- DEPARTMENT_MANAGER (Sales Manager):
-- Email: salesmanager@company.com / Password: 123456

-- EMPLOYEE:
-- IT: ducanh.it@company.com / giahuy.it@company.com / hoangnam.it@company.com
-- Sales: khanhly.sales@company.com / quocbao.sales@company.com
-- Password: 123456