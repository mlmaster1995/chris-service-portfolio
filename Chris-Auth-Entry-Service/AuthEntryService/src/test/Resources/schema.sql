CREATE TABLE IF NOT EXISTS `auth_user`
(
    `id`          INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `username`    VARCHAR(128)  NOT NULL,
    `password`    VARCHAR(1024) NOT NULL,
    `email`       VARCHAR(2048) NOT NULL UNIQUE,
    `enabled`     TINYINT       NOT NULL,
    `update_time` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_time` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `user_status`
(
    `id`               INT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`          INT      NOT NULL UNIQUE,
    `status`           char(10) NOT NULL DEFAULT 'LOG_OUT',
    `session`          INT               default null,
    `login_timestamp`  timestamp         default null,
    `logout_timestamp` timestamp         default null,

    CONSTRAINT `FK_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `role`
(
    `id`   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(128) DEFAULT NULL UNIQUE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `users_roles`
(
    `user_id` INT NOT NULL,
    `role_id` INT NOT NULL,

    PRIMARY KEY (`user_id`, `role_id`),

    KEY `FK_ROLE_idx` (`role_id`),
    CONSTRAINT `FK_USER_05` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `FK_ROLE` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `service-common`.`service_vars`
(
    `id`          int(11)       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `service`     varchar(256)  NOT NULL,
    `property`    varchar(1024) NOT NULL UNIQUE,
    `value`       VARCHAR(2048) NOT NULL,
    `update_time` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_time` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = latin1;

INSERT INTO `service-common`.`service_vars` (`service`, `property`, `value`)
VALUES ('chris-auth-entry-service', 'app.auth.jwt.basic.secret.key',
        '90a/eGwdGTrOyt+69NZXVdz/bB+nFmR0flUAsrqM4/G+T1GNJDiltOX4nOkBJxq/m4yjEpPYbWl3aZwC6q1ehA=='),
       ('chris-auth-entry-service', 'app.auth.jwt.basic.duration.sec', 600);