CREATE DATABASE IF NOT EXISTS remotelauncher;

DROP TABLE IF EXISTS remotelauncher.users;

CREATE TABLE remotelauncher.users
(
    user_id INT PRIMARY KEY,
    name VARCHAR(20),
    password VARCHAR(20)
);