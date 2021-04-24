CREATE TABLE IF NOT EXISTS user(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    date_created DATETIME NOT NULL,
    date_updated DATETIME,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role varchar(50) NOT NULL,
    active BOOLEAN
);

INSERT INTO user(username, password, name, role, active, date_created)
VALUES ('admin', '$2b$12$SwKT/LWOb86Crn6OCzheruEs2NDgACjHl5HLWN9B4939AlS.8BAki', 'Admin', 'ADMIN', TRUE, now());