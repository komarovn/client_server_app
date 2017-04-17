CREATE DATABASE IF NOT EXISTS remotelauncher;
USE remotelauncher;

DROP TABLE IF EXISTS remotelauncher.tasks;
DROP TABLE IF EXISTS remotelauncher.output;
DROP TABLE IF EXISTS remotelauncher.users;

CREATE TABLE remotelauncher.users
(
    `user_id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(20),
    `password` VARCHAR(20),
    PRIMARY KEY (`user_id`)
);

ALTER TABLE remotelauncher.users AUTO_INCREMENT = 10001;

CREATE TABLE remotelauncher.output
(
    `output_id` INT NOT NULL AUTO_INCREMENT,
    `file` LONGBLOB,
    `format_type` VARCHAR(6),
    PRIMARY KEY (`output_id`)
);

ALTER TABLE remotelauncher.output AUTO_INCREMENT = 30001;

CREATE TABLE remotelauncher.tasks
(
    `task_id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50),
    `task` LONGBLOB,
    `is_completed` BOOL,
    `output_id` INT,
    `user_id` INT,
    `format_type` VARCHAR(6),
    `created_when` DATETIME(3),
    PRIMARY KEY (`task_id`),
    CONSTRAINT tasks_output_id_fk FOREIGN KEY (`output_id`) REFERENCES output (`output_id`),
    CONSTRAINT tasks_users_user_id_fk FOREIGN KEY (`user_id`) REFERENCES users (`user_id`)
);

ALTER TABLE remotelauncher.tasks AUTO_INCREMENT = 20001;