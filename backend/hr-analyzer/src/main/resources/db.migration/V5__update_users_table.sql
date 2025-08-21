-- V2__update_users_table.sql

ALTER TABLE users
    ADD COLUMN first_name VARCHAR(255),
    ADD COLUMN last_name VARCHAR(255),
    ADD COLUMN email VARCHAR(255) UNIQUE,
    ADD COLUMN phone VARCHAR(50) UNIQUE,
    DROP COLUMN username;
