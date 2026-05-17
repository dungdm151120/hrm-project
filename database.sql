CREATE DATABASE hrm_db;
USE hrm_db;

CREATE TABLE roles (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description VARCHAR(255),
                       active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       gender VARCHAR(10),
                       date_of_birth DATE,
                       address VARCHAR(255),
                       avatar_url VARCHAR(255),
                       role_id INT NOT NULL,
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       reset_token VARCHAR(255),
                       reset_token_expired_at DATETIME,
                       CONSTRAINT fk_users_roles
                           FOREIGN KEY (role_id) REFERENCES roles(id)
);

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
                                      FOREIGN KEY (role_id) REFERENCES roles(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_role_permissions_permissions
                                      FOREIGN KEY (permission_id) REFERENCES permissions(id)
                                          ON DELETE CASCADE
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

USE hrm_db;

INSERT INTO roles (name, description, active)
VALUES
    ('ADMIN', 'System administrator', TRUE),
    ('HR', 'Human resource staff', TRUE),
    ('EMPLOYEE', 'Normal employee', TRUE);

INSERT INTO permissions (code, name, description)
VALUES
    ('HOMEPAGE_VIEW', 'View homepage', 'Can view homepage'),
    ('AUTH_LOGIN', 'Login', 'Can login'),
    ('AUTH_LOGOUT', 'Logout', 'Can logout'),
    ('AUTH_FORGOT_PASSWORD', 'Forgot password', 'Can request password reset'),
    ('PROFILE_VIEW', 'View my profile', 'Can view own profile'),
    ('PROFILE_CHANGE_PASSWORD', 'Change password', 'Can change own password'),

    ('USER_VIEW_LIST', 'View user list', 'Can view list of users'),
    ('USER_VIEW_DETAIL', 'View user information', 'Can view user detail'),
    ('USER_CREATE', 'Add new user', 'Can create new user'),
    ('USER_TOGGLE_STATUS', 'Active/deactive user', 'Can activate or deactivate user'),
    ('USER_UPDATE', 'Update user information', 'Can update user information'),

    ('ROLE_VIEW_LIST', 'View role list', 'Can view role list'),
    ('ROLE_VIEW_PERMISSION', 'View role permissions', 'Can view permissions of role'),
    ('ROLE_UPDATE', 'Update role information', 'Can update role information'),
    ('ROLE_TOGGLE_STATUS', 'Active/deactive role', 'Can activate or deactivate role'),
    ('ROLE_EDIT_PERMISSION', 'Edit role permissions', 'Can edit permissions of role');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
WHERE r.name = 'ADMIN';

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
                 'PROFILE_CHANGE_PASSWORD'
    );

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
WHERE r.name = 'HR'
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
                 'USER_TOGGLE_STATUS'
    );

INSERT INTO users (
    full_name, email, password, phone, gender, date_of_birth, address, avatar_url, role_id, active
)
VALUES
    (
        'Admin User',
        'admin@company.com',
        '123456',
        '0900000001',
        'Male',
        '2000-01-01',
        'Ho Chi Minh City',
        NULL,
        (SELECT id FROM roles WHERE name = 'ADMIN'),
        TRUE
    ),
    (
        'HR Staff',
        'hr@company.com',
        '123456',
        '0900000002',
        'Female',
        '2001-02-02',
        'Ha Noi',
        NULL,
        (SELECT id FROM roles WHERE name = 'HR'),
        TRUE
    ),
    (
        'Employee User',
        'employee@company.com',
        '123456',
        '0900000003',
        'Male',
        '2002-03-03',
        'Da Nang',
        NULL,
        (SELECT id FROM roles WHERE name = 'EMPLOYEE'),
        TRUE
    );


