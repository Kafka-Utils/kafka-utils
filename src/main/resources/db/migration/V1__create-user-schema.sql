CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE IF NOT EXISTS user_data(
    id uuid primary key,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role varchar(50) NOT NULL,
    active boolean
);

INSERT INTO user_data values ('01b2d119-afe2-4c82-b93e-9633391a52dc', 'admin', 'admin', 'Admin', 'ADMIN', true);