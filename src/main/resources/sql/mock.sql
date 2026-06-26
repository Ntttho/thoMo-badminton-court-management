INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_MANAGER'),
('ROLE_CUSTOMER');

INSERT INTO users (username, password, full_name, email, phone_number, is_enabled) VALUES
('admin_thanh', '$2a$10$eACCYoNOHEqXue8a.WkKge3.S4y61G2pW9.zF.z.3lH/0PqQ2zY1S', 'Nguyễn Admin', 'admin@sys.com', '0901000001', true),
('manager_hung', '$2a$10$eACCYoNOHEqXue8a.WkKge3.S4y61G2pW9.zF.z.3lH/0PqQ2zY1S', 'Trần Chủ Sân', 'manager@sys.com', '0902000002', true),
('customer_lan', '$2a$10$eACCYoNOHEqXue8a.WkKge3.S4y61G2pW9.zF.z.3lH/0PqQ2zY1S', 'Lê Khách Hàng', 'customer@sys.com', '0903000003', true);

-- Gán quyền (Dựa trên Use Case UC02)
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1), (2, 2), (3, 3);

INSERT INTO badminton_clusters (name, address, hot_line, manager_id) VALUES
('Sân Cầu Lông Thủ Đức', '1 Võ Văn Ngân, Thủ Đức', '0289999888', 2);

INSERT INTO courts (court_name, type, image_url, is_available, cluster_id) VALUES
('Sân số 1', 'Sân thảm', 'https://cdn.example.com/court1.jpg', true, 1),
('Sân số 2', 'Sân thảm', 'https://cdn.example.com/court2.jpg', true, 1);

INSERT INTO bookings (booking_date, time_slot, total_price, status, user_id, court_id) VALUES
('2026-06-27', '09:00-11:00', 120000, 'CONFIRMED', 3, 1),
('2026-06-28', '19:00-21:00', 150000, 'PENDING', 3, 2);

INSERT INTO token_blacklist (token, expiry_time, user_id) VALUES
('eyJhbGciOiJIUzI1NiJ9...', '2026-06-26 23:59:59', 3);