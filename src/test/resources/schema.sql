-- 测试用表结构（H2 MySQL 兼容模式）

CREATE TABLE IF NOT EXISTS room (
  room_id INT AUTO_INCREMENT PRIMARY KEY,
  room_number VARCHAR(20),
  room_type VARCHAR(50),
  floor INT,
  price DECIMAL(10,2),
  status VARCHAR(20),
  description VARCHAR(255),
  image VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS guest (
  guest_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50),
  gender VARCHAR(10),
  native_place VARCHAR(100),
  company VARCHAR(100),
  occupation VARCHAR(50),
  reason VARCHAR(255),
  id_card VARCHAR(20),
  phone VARCHAR(20),
  check_in_date TIMESTAMP,
  pre_leave_date DATE,
  actual_leave_date TIMESTAMP,
  room_id INT,
  guest_type VARCHAR(20),
  group_id INT,
  status VARCHAR(20),
  create_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stock (
  stock_id INT AUTO_INCREMENT PRIMARY KEY,
  item_name VARCHAR(100),
  category VARCHAR(50),
  specification VARCHAR(100),
  unit VARCHAR(20),
  current_quantity INT,
  unit_price DECIMAL(10,2),
  warning_level INT,
  update_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS idempotent_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  idempotent_key VARCHAR(128) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'PROCESSING',
  result TEXT,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  UNIQUE (idempotent_key)
);

CREATE TABLE IF NOT EXISTS order_main (
  order_id INT AUTO_INCREMENT PRIMARY KEY,
  guest_id INT,
  room_id INT,
  status VARCHAR(20),
  total_amount DECIMAL(10,2),
  settle_time TIMESTAMP,
  create_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_detail (
  detail_id INT AUTO_INCREMENT PRIMARY KEY,
  guest_id INT,
  order_id INT,
  item_type VARCHAR(50),
  item_name VARCHAR(100),
  quantity INT,
  price DECIMAL(10,2),
  amount DECIMAL(10,2),
  is_settled INT DEFAULT 0,
  remark VARCHAR(500),
  operator VARCHAR(50),
  create_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee (
  emp_id INT AUTO_INCREMENT PRIMARY KEY,
  emp_name VARCHAR(50),
  password VARCHAR(255),
  role VARCHAR(20),
  status VARCHAR(20),
  phone VARCHAR(20),
  email VARCHAR(100),
  hire_date DATE,
  salary DECIMAL(10,2)
);

CREATE TABLE IF NOT EXISTS audit_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  operator VARCHAR(64) NOT NULL DEFAULT '',
  operation VARCHAR(256) NOT NULL DEFAULT '',
  detail TEXT,
  result VARCHAR(16) NOT NULL DEFAULT 'SUCCESS',
  ip VARCHAR(64) DEFAULT '',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
