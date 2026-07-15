-- Skills and mappings
INSERT INTO skills (id, code, name, created_by, created_date, last_modified_by, last_modified_date)
VALUES (1, 'OIL', 'Oil Change', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO skills (id, code, name, created_by, created_date, last_modified_by, last_modified_date)
VALUES (2, 'BRAKE', 'Brake Service', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO skills (id, code, name, created_by, created_date, last_modified_by, last_modified_date)
VALUES (3, 'TIRE', 'Tire Service', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO skills (id, code, name, created_by, created_date, last_modified_by, last_modified_date)
VALUES (4, 'DIAGNOSTIC', 'Diagnostics', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO service_type_skills (service_type_id, skill_id) VALUES (1, 1);
INSERT INTO service_type_skills (service_type_id, skill_id) VALUES (2, 2);
INSERT INTO service_type_skills (service_type_id, skill_id) VALUES (3, 3);
INSERT INTO service_type_skills (service_type_id, skill_id) VALUES (4, 4);

INSERT INTO technician_skills (technician_id, skill_id) VALUES (1, 1);
INSERT INTO technician_skills (technician_id, skill_id) VALUES (1, 2);
INSERT INTO technician_skills (technician_id, skill_id) VALUES (2, 3);
INSERT INTO technician_skills (technician_id, skill_id) VALUES (2, 1);
INSERT INTO technician_skills (technician_id, skill_id) VALUES (3, 4);
INSERT INTO technician_skills (technician_id, skill_id) VALUES (3, 2);
INSERT INTO technician_skills (technician_id, skill_id) VALUES (4, 1);
INSERT INTO technician_skills (technician_id, skill_id) VALUES (4, 2);
INSERT INTO technician_skills (technician_id, skill_id) VALUES (4, 3);
INSERT INTO technician_skills (technician_id, skill_id) VALUES (4, 4);
