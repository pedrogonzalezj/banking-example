CREATE OR REPLACE TABLE users (
    id BINARY(16) NOT NULL PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    national_id VARCHAR(30) NOT NULL,
    birth_date DATE,
    email VARCHAR(100),
    phone_number VARCHAR(30),
    street VARCHAR(200),
    number VARCHAR(10),
    apartment_number VARCHAR(30),
    zip_code VARCHAR(10),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100)
) CHARACTER SET utf8;