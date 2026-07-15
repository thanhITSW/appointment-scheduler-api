-- Customers, vehicles, appointments (demo)
INSERT INTO customers (id, first_name, last_name, phone, email, created_by, created_date, last_modified_by, last_modified_date)
VALUES (1, 'An', 'Nguyen', '0901000001', 'an.nguyen@example.com', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO customers (id, first_name, last_name, phone, email, created_by, created_date, last_modified_by, last_modified_date)
VALUES (2, 'Binh', 'Tran', '0901000002', 'binh.tran@example.com', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO customers (id, first_name, last_name, phone, email, created_by, created_date, last_modified_by, last_modified_date)
VALUES (3, 'Chi', 'Le', '0901000003', 'chi.le@example.com', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO customers (id, first_name, last_name, phone, email, created_by, created_date, last_modified_by, last_modified_date)
VALUES (4, 'Dung', 'Pham', '0901000004', 'dung.pham@example.com', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO customers (id, first_name, last_name, phone, email, created_by, created_date, last_modified_by, last_modified_date)
VALUES (5, 'Em', 'Hoang', '0901000005', 'em.hoang@example.com', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO vehicles (id, customer_id, vin, license_plate, make, model, year, created_by, created_date, last_modified_by, last_modified_date)
VALUES (1, 1, 'HNACCN20220001', '51A-11111', 'Hyundai', 'Accent', 2022, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO vehicles (id, customer_id, vin, license_plate, make, model, year, created_by, created_date, last_modified_by, last_modified_date)
VALUES (2, 1, 'HNTUSN20210002', '51A-11112', 'Hyundai', 'Tucson', 2021, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO vehicles (id, customer_id, vin, license_plate, make, model, year, created_by, created_date, last_modified_by, last_modified_date)
VALUES (3, 2, 'KIACER20200003', '51B-22221', 'Kia', 'Cerato', 2020, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO vehicles (id, customer_id, vin, license_plate, make, model, year, created_by, created_date, last_modified_by, last_modified_date)
VALUES (4, 3, 'TYTCOR20230004', '51C-33331', 'Toyota', 'Corolla', 2023, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO vehicles (id, customer_id, vin, license_plate, make, model, year, created_by, created_date, last_modified_by, last_modified_date)
VALUES (5, 4, 'FDFOCS20190005', '51D-44441', 'Ford', 'Focus', 2019, 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO vehicles (id, customer_id, vin, license_plate, make, model, year, created_by, created_date, last_modified_by, last_modified_date)
VALUES (6, 5, 'MZCX5X20220006', '51E-55551', 'Mazda', 'CX-5', 2022, 'SYSTEM', NOW(), 'SYSTEM', NOW());

-- Resources: D1 = tech 1–2 / bay 1–2; D2 = tech 3–4 / bay 3–4
INSERT INTO appointments (id, customer_id, vehicle_id, technician_id, service_bay_id, dealership_id, service_type_id, appointment_date, start_time, end_time, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (1, 1, 1, 1, 1, 1, 1, DATE_SUB(CURDATE(), INTERVAL 7 DAY), '09:00:00', '10:00:00', 'COMPLETED', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO appointments (id, customer_id, vehicle_id, technician_id, service_bay_id, dealership_id, service_type_id, appointment_date, start_time, end_time, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (2, 2, 3, 2, 2, 1, 2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '10:00:00', '10:45:00', 'COMPLETED', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO appointments (id, customer_id, vehicle_id, technician_id, service_bay_id, dealership_id, service_type_id, appointment_date, start_time, end_time, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (3, 3, 4, 3, 3, 2, 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '14:00:00', '14:40:00', 'CANCELLED', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO appointments (id, customer_id, vehicle_id, technician_id, service_bay_id, dealership_id, service_type_id, appointment_date, start_time, end_time, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (4, 1, 2, 2, 1, 1, 4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00:00', '10:00:00', 'CONFIRMED', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO appointments (id, customer_id, vehicle_id, technician_id, service_bay_id, dealership_id, service_type_id, appointment_date, start_time, end_time, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (5, 4, 5, 1, 2, 1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '11:00:00', '12:00:00', 'CONFIRMED', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO appointments (id, customer_id, vehicle_id, technician_id, service_bay_id, dealership_id, service_type_id, appointment_date, start_time, end_time, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (6, 5, 6, 4, 3, 2, 3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '08:30:00', '09:10:00', 'CONFIRMED', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO appointments (id, customer_id, vehicle_id, technician_id, service_bay_id, dealership_id, service_type_id, appointment_date, start_time, end_time, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (7, 2, 3, 3, 4, 2, 2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '13:00:00', '13:45:00', 'CONFIRMED', 'SYSTEM', NOW(), 'SYSTEM', NOW());

INSERT INTO appointments (id, customer_id, vehicle_id, technician_id, service_bay_id, dealership_id, service_type_id, appointment_date, start_time, end_time, status, created_by, created_date, last_modified_by, last_modified_date)
VALUES (8, 3, 4, 1, 1, 1, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '15:00:00', '16:00:00', 'CONFIRMED', 'SYSTEM', NOW(), 'SYSTEM', NOW());
