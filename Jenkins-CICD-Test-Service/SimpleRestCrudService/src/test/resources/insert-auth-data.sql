INSERT INTO `auth_user` (`username`,`password`,`email`,`enabled`) VALUES
('user','$2a$10$C2peN3zp97wiwE9o.J4TsOymP0V1VLnxYEXCmFlHRjfpdcib5Odce','user2024@chrismemeber.ca',1),
('admin','$2a$10$RIZgsD934X/8z.1F0p/oSejj57YNaebezru3mDqusN7uGcB/lgO5u','admin2024@chrismember.ca',1),
('chris','$2a$10$2YM2tdnKmRsld825h2xXW.dH.7/wJ0S4arp2BembjwtjmBVYeTx8y','kyang3@lakeheadu.ca',1);

INSERT INTO `role` (name) VALUES
('ROLE_USER'), ('ROLE_ADMIN');

set @user_id=0;
set @user_role_id=0;
select @user_id:=id from auth_user where username = 'user';
select @user_role_id:=id from role where name ='ROLE_USER';
set @admin_id=0;
set @admin_role_id=0;
select @admin_id:=id from auth_user where username = 'admin';
select @admin_role_id:=id from role where name = 'ROLE_ADMIN';
INSERT INTO `users_roles` (user_id,role_id) VALUES
(@user_id, @user_role_id),
(@admin_id, @admin_role_id);