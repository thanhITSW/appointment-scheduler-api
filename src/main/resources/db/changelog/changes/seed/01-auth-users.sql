-- Staff users (password: Admin@123)
INSERT INTO user (id, email, employee_id, password, role, status, is_deleted, is_blocked, created_by, created_date, last_modified_by, last_modified_date, note, full_name, two_fa_enabled)
VALUES (1, 'admin@appointment.com', 'admin01', '$2a$10$RLZUF8nlxoNt7HLKw1MmZ.sNpx2Uz7h9FYON1PuqkTda4lHteXn1e', 'ADMIN', 'ACTIVATED', 0, 0, 'SYSTEM', NOW(), 'SYSTEM', NOW(), null, 'Admin User', 0);

INSERT INTO user (id, email, employee_id, password, role, status, is_deleted, is_blocked, created_by, created_date, last_modified_by, last_modified_date, note, full_name, two_fa_enabled)
VALUES (2, 'manager@appointment.com', 'mgr01', '$2a$10$RLZUF8nlxoNt7HLKw1MmZ.sNpx2Uz7h9FYON1PuqkTda4lHteXn1e', 'MANAGER', 'ACTIVATED', 0, 0, 'SYSTEM', NOW(), 'SYSTEM', NOW(), null, 'Manager User', 0);

INSERT INTO user (id, email, employee_id, password, role, status, is_deleted, is_blocked, created_by, created_date, last_modified_by, last_modified_date, note, full_name, two_fa_enabled)
VALUES (3, 'advisor@appointment.com', 'adv01', '$2a$10$RLZUF8nlxoNt7HLKw1MmZ.sNpx2Uz7h9FYON1PuqkTda4lHteXn1e', 'ADVISOR', 'ACTIVATED', 0, 0, 'SYSTEM', NOW(), 'SYSTEM', NOW(), null, 'Advisor User', 0);

INSERT INTO user (id, email, employee_id, password, role, status, is_deleted, is_blocked, created_by, created_date, last_modified_by, last_modified_date, note, full_name, two_fa_enabled)
VALUES (4, 'tech@appointment.com', 'tech01', '$2a$10$RLZUF8nlxoNt7HLKw1MmZ.sNpx2Uz7h9FYON1PuqkTda4lHteXn1e', 'TECHNICIAN', 'ACTIVATED', 0, 0, 'SYSTEM', NOW(), 'SYSTEM', NOW(), null, 'Technician User', 0);
