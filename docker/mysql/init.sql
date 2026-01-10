CREATE DATABASE IF NOT EXISTS `keycloak` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'keycloak'@'%' IDENTIFIED BY 'keycloak123';
GRANT ALL PRIVILEGES ON `keycloak`.* TO 'keycloak'@'%';

CREATE DATABASE IF NOT EXISTS `bookingbadminton` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON `bookingbadminton`.* TO 'root'@'%';
FLUSH PRIVILEGES;
