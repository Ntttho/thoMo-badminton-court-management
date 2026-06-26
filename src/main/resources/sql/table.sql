-- Tạo các bảng chính
CREATE TABLE roles (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(20) NOT NULL
);

CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       full_name VARCHAR(100),
                       email VARCHAR(100) UNIQUE,
                       phone_number VARCHAR(20),
                       is_enabled BOOLEAN DEFAULT true
);

CREATE TABLE user_roles (
                            user_id BIGINT,
                            role_id BIGINT,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE badminton_clusters (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    name VARCHAR(100),
                                    address VARCHAR(255),
                                    hot_line VARCHAR(20),
                                    manager_id BIGINT,
                                    FOREIGN KEY (manager_id) REFERENCES users(id)
);

CREATE TABLE courts (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        court_name VARCHAR(50),
                        type VARCHAR(50),
                        image_url VARCHAR(255),
                        is_available BOOLEAN,
                        cluster_id BIGINT,
                        FOREIGN KEY (cluster_id) REFERENCES badminton_clusters(id)
);

CREATE TABLE bookings (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          booking_date DATE,
                          time_slot VARCHAR(50),
                          total_price DECIMAL(10,2),
                          status VARCHAR(20),
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          user_id BIGINT,
                          court_id BIGINT,
                          FOREIGN KEY (user_id) REFERENCES users(id),
                          FOREIGN KEY (court_id) REFERENCES courts(id)
);