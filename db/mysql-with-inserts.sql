DROP DATABASE IF EXISTS `daaexample`;
CREATE DATABASE `daaexample`;

CREATE TABLE `daaexample`.`people` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(50) NOT NULL,
	`surname` varchar(100) NOT NULL,
	`login` varchar(100) NOT NULL,
	`login_creator` varchar(100) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT fk_user FOREIGN KEY (login_creator) REFERENCES users(login) ON DELETE CASCADE ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `daaexample`.`users` (
	`login` varchar(100) NOT NULL,
	`password` varchar(64) NOT NULL,
	`role` varchar(10) NOT NULL,
	PRIMARY KEY (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE pets (
    `pet_id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(64) NOT NULL,
    `type` VARCHAR(10) NOT NULL,
    `owner_id` INT NOT NULL,
    PRIMARY KEY (pet_id),
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES people(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE USER IF NOT EXISTS 'daa'@'localhost' IDENTIFIED WITH mysql_native_password BY 'daa';
GRANT ALL ON `daaexample`.* TO 'daa'@'localhost';

-- Insertar datos en la tabla people
INSERT INTO people (name, surname, login_creator) VALUES 
('Juan', 'Pérez', 'juan_admin'),
('María', 'Gómez', 'maria_creator'),
('Carlos', 'Fernández', 'carlos_dev'),
('Ana', 'López', 'ana_manager'),
('Pedro', 'Martínez', 'pedro_root');

-- Insertar datos en la tabla pets con valores completos y owner_id referenciando a people.id
INSERT INTO pets (pet_id, name, type, owner_id) VALUES 
(1, 'Firulais', 'Perro', 19),
(2, 'Michi', 'Gato', 20),
(3, 'Pepe', 'Loro', 21),
(4, 'Nemo', 'Pez', 22),
(5, 'Bunny', 'Conejo', 23);

-- The password for each user is its login suffixed with "pass". For example, user "admin" has the password "adminpass".
INSERT INTO `daaexample`.`users` (`login`,`password`,`role`)
VALUES ('admin', '713bfda78870bf9d1b261f565286f85e97ee614efe5f0faf7c34e7ca4f65baca','ADMIN');
INSERT INTO `daaexample`.`users` (`login`,`password`,`role`)
VALUES ('normal', '7bf24d6ca2242430343ab7e3efb89559a47784eea1123be989c1b2fb2ef66e83','USER');
