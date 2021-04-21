CREATE TABLE IF NOT EXISTS user_data(
    id INT NOT NULL AUTO_INCREMENT primary key,
    date_created TIMESTAMP NOT NULL,
    date_updated TIMESTAMP,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role varchar(50) NOT NULL,
    active boolean
);

INSERT INTO user_data(username, password, name, role, active) values ('admin', 'admin', 'Admin', 'ADMIN', true);