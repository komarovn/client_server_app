CREATE DATABASE IF NOT EXISTS remotelauncher;
USE remotelauncher;

DROP TABLE IF EXISTS remotelauncher.tasks;
DROP TABLE IF EXISTS remotelauncher.output;
DROP TABLE IF EXISTS remotelauncher.users;

CREATE TABLE remotelauncher.users
(
    user_id INT PRIMARY KEY,
    name VARCHAR(20),
    password VARCHAR(20)
);

CREATE TABLE remotelauncher.output
(
    output_id INT PRIMARY KEY,
    file LONGBLOB,
    format_type VARCHAR(6)
);

CREATE TABLE remotelauncher.tasks
(
    task_id INT PRIMARY KEY,
    task LONGBLOB,
    is_completed BOOL,
    output_id INT,
    user_id INT,
    CONSTRAINT tasks_output_id_fk FOREIGN KEY (output_id) REFERENCES output (output_id),
    CONSTRAINT tasks_users_user_id_fk FOREIGN KEY (user_id) REFERENCES users (user_id)
);