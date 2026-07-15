-- Dealerships, service types, technicians, service bays
INSERT INTO dealerships (id, name, address, created_by, created_date, last_modified_by, last_modified_date)
VALUES (1, 'Downtown Service Center', '100 Main Street, Metro City', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO dealerships (id, name, address, created_by, created_date, last_modified_by, last_modified_date)
VALUES (2, 'Westside Auto Hub', '450 West Boulevard, Metro City', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO service_types (id, name, duration_minutes, created_by, created_date, last_modified_by, last_modified_date)
VALUES (1, 'Oil Change', 60, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO service_types (id, name, duration_minutes, created_by, created_date, last_modified_by, last_modified_date)
VALUES (2, 'Brake Inspection', 45, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO service_types (id, name, duration_minutes, created_by, created_date, last_modified_by, last_modified_date)
VALUES (3, 'Tire Rotation', 40, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO service_types (id, name, duration_minutes, created_by, created_date, last_modified_by, last_modified_date)
VALUES (4, 'Full Diagnostic', 60, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO technicians (id, name, employee_code, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (1, 'David', 'TECH-001', 'AVAILABLE', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO technicians (id, name, employee_code, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (2, 'Jordan Lee', 'TECH-002', 'AVAILABLE', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO technicians (id, name, employee_code, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (3, 'Sam Patel', 'TECH-003', 'AVAILABLE', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO technicians (id, name, employee_code, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (4, 'Casey Morgan', 'TECH-004', 'AVAILABLE', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO service_bays (id, name, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (1, 'Bay 1', 'AVAILABLE', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO service_bays (id, name, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (2, 'Bay 2', 'AVAILABLE', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO service_bays (id, name, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (3, 'Bay 3', 'AVAILABLE', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO service_bays (id, name, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (4, 'Bay 4', 'AVAILABLE', 'SYSTEM', NOW(), 'SYSTEM', NOW());
