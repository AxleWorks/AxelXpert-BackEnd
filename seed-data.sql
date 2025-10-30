-- seed-data.sql
-- Compact seed for AxelXpert backend
-- Inserts: 10 customers, 10 managers, 10 employees, 6 services, 5 branches, 10 vehicles, 20 bookings (October 2025)
-- Adjust columns if your schema requires additional NOT NULL fields (createdAt/updatedAt etc.)

-- 1) Users: 10 customers (1..10), 10 managers (11..20), 10 employees (21..30)
-- Include additional columns from the User entity: is_Blocked, is_Active, token, address, phoneNumber, createdAt, updatedAt
-- Password: BCrypt hash for 'pass' = $2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW
INSERT INTO user (id, username, password, role, email, is_Blocked, is_Active, token, address, phone_number, created_at, updated_at) VALUES
(1,'cust1','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','customer','cust1@example.com', false, true, NULL, '10 Customer St', '555-1001', '2025-09-01 09:00:00', '2025-09-01 09:00:00'),
(2,'cust2','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','customer','cust2@example.com', false, true, NULL, '11 Customer St', '555-1002', '2025-09-01 09:05:00', '2025-09-01 09:05:00'),
(3,'cust3','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','customer','cust3@example.com', false, true, NULL, '12 Customer St', '555-1003', '2025-09-01 09:10:00', '2025-09-01 09:10:00'),
(4,'cust4','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','customer','cust4@example.com', false, true, NULL, '13 Customer St', '555-1004', '2025-09-01 09:15:00', '2025-09-01 09:15:00'),
(5,'cust5','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','customer','cust5@example.com', false, true, NULL, '14 Customer St', '555-1005', '2025-09-01 09:20:00', '2025-09-01 09:20:00'),
(6,'cust6','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','customer','cust6@example.com', false, true, NULL, '15 Customer St', '555-1006', '2025-09-01 09:25:00', '2025-09-01 09:25:00'),
(7,'cust7','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','customer','cust7@example.com', false, true, NULL, '16 Customer St', '555-1007', '2025-09-01 09:30:00', '2025-09-01 09:30:00'),
(8,'cust8','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','customer','cust8@example.com', false, true, NULL, '17 Customer St', '555-1008', '2025-09-01 09:35:00', '2025-09-01 09:35:00'),
(9,'cust9','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','customer','cust9@example.com', false, true, NULL, '18 Customer St', '555-1009', '2025-09-01 09:40:00', '2025-09-01 09:40:00'),
(10,'cust10','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','customer','cust10@example.com', false, true, NULL, '19 Customer St', '555-1010', '2025-09-01 09:45:00', '2025-09-01 09:45:00'),
(11,'mgr1','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','manager','mgr1@example.com', false, true, NULL, '1 Manager Ave', '555-2001', '2025-09-01 10:00:00', '2025-09-01 10:00:00'),
(12,'mgr2','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','manager','mgr2@example.com', false, true, NULL, '2 Manager Ave', '555-2002', '2025-09-01 10:05:00', '2025-09-01 10:05:00'),
(13,'mgr3','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','manager','mgr3@example.com', false, true, NULL, '3 Manager Ave', '555-2003', '2025-09-01 10:10:00', '2025-09-01 10:10:00'),
(14,'mgr4','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','manager','mgr4@example.com', false, true, NULL, '4 Manager Ave', '555-2004', '2025-09-01 10:15:00', '2025-09-01 10:15:00'),
(15,'mgr5','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','manager','mgr5@example.com', false, true, NULL, '5 Manager Ave', '555-2005', '2025-09-01 10:20:00', '2025-09-01 10:20:00'),
(16,'mgr6','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','manager','mgr6@example.com', false, true, NULL, '6 Manager Ave', '555-2006', '2025-09-01 10:25:00', '2025-09-01 10:25:00'),
(17,'mgr7','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','manager','mgr7@example.com', false, true, NULL, '7 Manager Ave', '555-2007', '2025-09-01 10:30:00', '2025-09-01 10:30:00'),
(18,'mgr8','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','manager','mgr8@example.com', false, true, NULL, '8 Manager Ave', '555-2008', '2025-09-01 10:35:00', '2025-09-01 10:35:00'),
(19,'mgr9','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','manager','mgr9@example.com', false, true, NULL, '9 Manager Ave', '555-2009', '2025-09-01 10:40:00', '2025-09-01 10:40:00'),
(20,'mgr10','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','manager','mgr10@example.com', false, true, NULL, '10 Manager Ave', '555-2010', '2025-09-01 10:45:00', '2025-09-01 10:45:00'),
(21,'emp1','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','employee','emp1@example.com', false, true, NULL, '21 Employee Rd', '555-3001', '2025-09-01 11:00:00', '2025-09-01 11:00:00'),
(22,'emp2','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','employee','emp2@example.com', false, true, NULL, '22 Employee Rd', '555-3002', '2025-09-01 11:05:00', '2025-09-01 11:05:00'),
(23,'emp3','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','employee','emp3@example.com', false, true, NULL, '23 Employee Rd', '555-3003', '2025-09-01 11:10:00', '2025-09-01 11:10:00'),
(24,'emp4','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','employee','emp4@example.com', false, true, NULL, '24 Employee Rd', '555-3004', '2025-09-01 11:15:00', '2025-09-01 11:15:00'),
(25,'emp5','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','employee','emp5@example.com', false, true, NULL, '25 Employee Rd', '555-3005', '2025-09-01 11:20:00', '2025-09-01 11:20:00'),
(26,'emp6','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','employee','emp6@example.com', false, true, NULL, '26 Employee Rd', '555-3006', '2025-09-01 11:25:00', '2025-09-01 11:25:00'),
(27,'emp7','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','employee','emp7@example.com', false, true, NULL, '27 Employee Rd', '555-3007', '2025-09-01 11:30:00', '2025-09-01 11:30:00'),
(28,'emp8','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','employee','emp8@example.com', false, true, NULL, '28 Employee Rd', '555-3008', '2025-09-01 11:35:00', '2025-09-01 11:35:00'),
(29,'emp9','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','employee','emp9@example.com', false, true, NULL, '29 Employee Rd', '555-3009', '2025-09-01 11:40:00', '2025-09-01 11:40:00'),
(30,'emp10','$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW','employee','emp10@example.com', false, true, NULL, '30 Employee Rd', '555-3010', '2025-09-01 11:45:00', '2025-09-01 11:45:00');

-- 2) Services (compact set, ids 1..6)
INSERT INTO services (id, name, price, duration_minutes) VALUES
(1,'Oil Change',29.99,30),
(2,'Tire Rotation',49.99,45),
(3,'Brake Inspection',79.99,60),
(4,'Battery Replacement',119.99,30),
(5,'Full Service',249.99,180),
(6,'AC Service',99.99,60);

-- 3) Branches (5 branches). manager_id left for UPDATE below.
INSERT INTO branches (id, name, address, phone, manager_id) VALUES
(1,'Central','100 Central Ave','555-4001', NULL),
(2,'North','200 North Rd','555-4002', NULL),
(3,'East','300 East Blvd','555-4003', NULL),
(4,'South','400 South St','555-4004', NULL),
(5,'West','500 West Ln','555-4005', NULL);

-- 4) Assign managers to branches (primary managers 11..15)
UPDATE branches SET manager_id = 11 WHERE id = 1;
UPDATE branches SET manager_id = 12 WHERE id = 2;
UPDATE branches SET manager_id = 13 WHERE id = 3;
UPDATE branches SET manager_id = 14 WHERE id = 4;
UPDATE branches SET manager_id = 15 WHERE id = 5;

-- 5) Assign managers' branch_id (if users.branch_id exists)
UPDATE users SET branch_id = 1 WHERE id IN (11,16);
UPDATE users SET branch_id = 2 WHERE id IN (12,17);
UPDATE users SET branch_id = 3 WHERE id IN (13,18);
UPDATE users SET branch_id = 4 WHERE id IN (14,19);
UPDATE users SET branch_id = 5 WHERE id IN (15,20);

-- 6) Assign employees to branches (two per branch)
UPDATE users SET branch_id = 1 WHERE id IN (21,22);
UPDATE users SET branch_id = 2 WHERE id IN (23,24);
UPDATE users SET branch_id = 3 WHERE id IN (25,26);
UPDATE users SET branch_id = 4 WHERE id IN (27,28);
UPDATE users SET branch_id = 5 WHERE id IN (29,30);

-- 7) Vehicles: one per customer (1..10)
INSERT INTO vehicles (id, type, year, make, model, fuel_type, plate_number, chassis_number, last_service_date, created_at, updated_at, user_id) VALUES
(1,'Car',2018,'Toyota','Corolla','petrol','PLT-1001','CHASSIS1001','2024-08-01','2025-09-01 12:00:00','2025-09-01 12:00:00',1),
(2,'Car',2020,'Honda','Civic','petrol','PLT-1002','CHASSIS1002','2025-02-05','2025-09-01 12:05:00','2025-09-01 12:05:00',2),
(3,'Truck',2015,'Ford','F-150','diesel','PLT-1003','CHASSIS1003','2024-12-10','2025-09-01 12:10:00','2025-09-01 12:10:00',3),
(4,'Car',2019,'Nissan','Altima','petrol','PLT-1004','CHASSIS1004','2025-01-15','2025-09-01 12:15:00','2025-09-01 12:15:00',4),
(5,'SUV',2021,'Mazda','CX-5','petrol','PLT-1005','CHASSIS1005','2025-04-20','2025-09-01 12:20:00','2025-09-01 12:20:00',5),
(6,'Car',2017,'Kia','Rio','petrol','PLT-1006','CHASSIS1006','2024-06-11','2025-09-01 12:25:00','2025-09-01 12:25:00',6),
(7,'Car',2016,'Hyundai','Elantra','petrol','PLT-1007','CHASSIS1007','2024-03-30','2025-09-01 12:30:00','2025-09-01 12:30:00',7),
(8,'SUV',2022,'Subaru','Forester','petrol','PLT-1008','CHASSIS1008','2025-06-02','2025-09-01 12:35:00','2025-09-01 12:35:00',8),
(9,'Van',2014,'Mercedes','Vito','diesel','PLT-1009','CHASSIS1009','2024-11-20','2025-09-01 12:40:00','2025-09-01 12:40:00',9),
(10,'Car',2023,'Tesla','Model 3','electric','PLT-1010','CHASSIS1010','2025-08-01','2025-09-01 12:45:00','2025-09-01 12:45:00',10);

-- 8) Bookings: 20 bookings in October 2025 (ids 1..20)
INSERT INTO bookings (id, customer_id, customer_name, customer_phone, vehicle, branch_id, service_id, start_at, end_at, status, assigned_employee_id, total_price) VALUES
(1,1,'cust1','555-1001','PLT-1001',1,1,'2025-10-01 09:00:00','2025-10-01 09:30:00','APPROVED',NULL,29.99),
(2,2,'cust2','555-1002','PLT-1002',2,2,'2025-10-01 10:00:00','2025-10-01 10:45:00','PENDING',NULL,49.99),
(3,3,'cust3','555-1003','PLT-1003',3,3,'2025-10-02 09:30:00','2025-10-02 10:30:00','APPROVED',NULL,79.99),
(4,4,'cust4','555-1004','PLT-1004',1,4,'2025-10-02 11:00:00','2025-10-02 11:30:00','COMPLETED',NULL,119.99),
(5,5,'cust5','555-1005','PLT-1005',4,5,'2025-10-03 08:30:00','2025-10-03 11:30:00','APPROVED',NULL,249.99),
(6,6,'cust6','555-1006','PLT-1006',2,6,'2025-10-03 13:00:00','2025-10-03 14:00:00','PENDING',NULL,99.99),
(7,7,'cust7','555-1007','PLT-1007',3,1,'2025-10-04 09:00:00','2025-10-04 09:30:00','APPROVED',NULL,29.99),
(8,8,'cust8','555-1008','PLT-1008',5,2,'2025-10-04 11:00:00','2025-10-04 11:45:00','COMPLETED',NULL,49.99),
(9,9,'cust9','555-1009','PLT-1009',4,3,'2025-10-05 14:00:00','2025-10-05 15:00:00','APPROVED',NULL,79.99),
(10,10,'cust10','555-1010','PLT-1010',5,4,'2025-10-05 09:00:00','2025-10-05 09:30:00','PENDING',NULL,119.99),
(11,1,'cust1','555-1001','PLT-1001',1,2,'2025-10-06 09:00:00','2025-10-06 09:45:00','APPROVED',NULL,49.99),
(12,2,'cust2','555-1002','PLT-1002',2,1,'2025-10-06 10:00:00','2025-10-06 10:30:00','COMPLETED',NULL,29.99),
(13,3,'cust3','555-1003','PLT-1003',3,5,'2025-10-07 14:00:00','2025-10-07 17:00:00','PENDING',NULL,249.99),
(14,4,'cust4','555-1004','PLT-1004',1,6,'2025-10-08 09:00:00','2025-10-08 10:00:00','APPROVED',NULL,99.99),
(15,5,'cust5','555-1005','PLT-1005',4,4,'2025-10-09 11:00:00','2025-10-09 11:30:00','CANCELLED',NULL,119.99),
(16,6,'cust6','555-1006','PLT-1006',2,3,'2025-10-10 08:00:00','2025-10-10 09:00:00','APPROVED',NULL,79.99),
(17,7,'cust7','555-1007','PLT-1007',3,6,'2025-10-11 15:00:00','2025-10-11 16:00:00','APPROVED',NULL,99.99),
(18,8,'cust8','555-1008','PLT-1008',5,2,'2025-10-12 09:00:00','2025-10-12 09:45:00','PENDING',NULL,49.99),
(19,9,'cust9','555-1009','PLT-1009',4,5,'2025-10-13 13:00:00','2025-10-13 16:00:00','APPROVED',NULL,249.99),
(20,10,'cust10','555-1010','PLT-1010',5,3,'2025-10-14 09:30:00','2025-10-14 10:30:00','APPROVED',NULL,79.99);
