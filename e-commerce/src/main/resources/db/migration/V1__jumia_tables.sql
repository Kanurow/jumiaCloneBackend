use jumia;
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);
CREATE TABLE user_roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(50),
    role_id VARCHAR(50)
);

CREATE TABLE favourite (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    user_id INT
);
CREATE TABLE cart_table (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    user_id INT
);


CREATE TABLE product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_name VARCHAR(40) NOT NULL,
  selling_price DECIMAL(10, 2) NOT NULL,
  amount_discounted DECIMAL(10, 2) NOT NULL,
  quantity INT NOT NULL,
  percentage_discount INT NOT NULL,
  description TEXT,
  image LONGBLOB,
  image_url VARCHAR(500) NOT NULL,
  category VARCHAR(40) NOT NULL
);





CREATE TABLE users_table (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    account_number VARCHAR(10) UNIQUE NOT NULL,
    `password` VARCHAR(10000) NOT NULL ,
    mobile VARCHAR(100) ,
    date_of_birth VARCHAR(100) ,
    authorities VARCHAR(50),
    account_balance INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE transaction_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT,
    receiver_id BIGINT,
    amount DOUBLE,
    transaction_type VARCHAR(20),
    transaction_time DATETIME

);


CREATE TABLE promo_table (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50),
    promo_amount INT
);


CREATE TABLE cart_checkout (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_address VARCHAR(255) NOT NULL,
  price DOUBLE NOT NULL,
  quantity INT NOT NULL,
  user_id INT NOT NULL
);

CREATE TABLE cart_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_name VARCHAR(255) NOT NULL,
  price DOUBLE NOT NULL,
  quantity INT NOT NULL,
  subtotal DOUBLE NOT NULL,
  cart_checkout_id BIGINT,
  FOREIGN KEY (cart_checkout_id) REFERENCES cart_checkout(id)
);

INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');

