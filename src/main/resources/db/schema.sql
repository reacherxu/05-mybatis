CREATE TABLE IF NOT EXISTS users (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age  INT
);

CREATE TABLE IF NOT EXISTS cron (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    cron            VARCHAR(100),
    description     VARCHAR(255)
);
