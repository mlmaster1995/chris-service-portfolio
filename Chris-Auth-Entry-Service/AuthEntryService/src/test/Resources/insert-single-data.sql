INSERT INTO `role` (name) VALUES ('ROLE_USER'),('ROLE_ADMIN');

INSERT INTO `auth_user` (`username`,`password`,`email`,`enabled`)
VALUES ('chris-test','1234','chris-test@chris-test.ca', 1);

SET @user_id=0;
SELECT @user_id:=id FROM auth_user WHERE username = 'chris-test';

SET @user_role_id=0;
SELECT @user_role_id:=id FROM role WHERE name ='ROLE_USER';

INSERT INTO `users_roles` (user_id,role_id) VALUES (@user_id, @user_role_id);

INSERT INTO `user_status` (user_id) VALUES (@user_id);
