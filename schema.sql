-- Run this once on your MySQL database (Clever Cloud ya jo bhi use kar rahe ho)
-- before you start using the app.

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    mobile VARCHAR(20),
    service VARCHAR(100),
    other_service VARCHAR(150),
    preferred_date VARCHAR(50),
    preferred_time VARCHAR(50),
    message TEXT
);
