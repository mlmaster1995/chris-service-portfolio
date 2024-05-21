# create user
DROP USER IF EXISTS chris;
CREATE OR REPLACE USER chris@'%' IDENTIFIED BY 'chris2024';

# grant privileges
GRANT ALL PRIVILEGES ON * . * TO 'chris'@'%';

# create database
CREATE DATABASE IF NOT EXISTS `auth-api`;
CREATE DATABASE IF NOT EXISTS `service-common`;
