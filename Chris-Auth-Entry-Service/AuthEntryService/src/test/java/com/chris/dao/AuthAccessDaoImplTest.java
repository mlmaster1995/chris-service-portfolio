/**
 * MIT License
 * <p>
 * Copyright (c) 2024 Chris Yang
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.chris.dao;

import com.chris.dto.AuthUserDto;
import com.chris.entity.AuthUser;
import com.chris.entity.Role;
import com.chris.entity.UserStatus;
import com.chris.exception.AuthServiceException;
import com.chris.util.AuthCommon;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MariaDBContainer;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_DAO_BEAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest
class AuthAccessDaoImplTest {
    @Value("${app.auth.find.users.page.size}")
    private Integer _pageSize;

    @Autowired
    private JdbcTemplate _template;

    @Autowired
    @Qualifier(value = AUTH_ACCESS_DAO_BEAN)
    private AuthAccessDao _dao;

    private final String DEFAULT_USERNAME = "chris-test";
    private final String DEFAULT_PASSWORD = "1234";
    private final String DEFAULT_EMAIL = "chris-test@chris-test.ca";

    //init test containers
    public static final MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:10.5.5");

    @BeforeAll
    public static void beforeAll() {
        mariadb.withDatabaseName("auth-api");
        mariadb.withUsername("root");
        mariadb.withPassword("");
        mariadb.start();
    }

    @AfterAll
    public static void afterAll() {
        mariadb.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
    }

    @BeforeEach
    public void beforeEachTest() {
        System.out.println("maridb url: " + mariadb.getJdbcUrl());
        System.out.println("mariadb user: " + mariadb.getUsername());
        System.out.println("mariadb password: " + mariadb.getPassword());
        System.out.println("mariadb database: " + mariadb.getDatabaseName());
        System.out.println("mariadb driver: " + mariadb.getDriverClassName());
    }


    @AfterEach
    public void afterEachTest() {
        _template.execute("delete from users_roles");
        _template.execute("delete from user_status");
        _template.execute("delete from auth_user");
    }

    private void _insertAuthUsers() {
        _template.execute("INSERT INTO `auth_user` (`username`,`password`,`email`,`enabled`) VALUES ('chris-test-1','1234','chris-test-1@chris-test.ca',1);");
        _template.execute("INSERT INTO `auth_user` (`username`,`password`,`email`,`enabled`) VALUES ('chris-test-2','1234','chris-test-2@chris-test.ca',1);");
        _template.execute("INSERT INTO `auth_user` (`username`,`password`,`email`,`enabled`) VALUES ('chris-test-3','1234','chris-test-3@chris-test.ca',1);");
        _template.execute("INSERT INTO `auth_user` (`username`,`password`,`email`,`enabled`) VALUES ('chris-test-4','1234','chris-test-4@chris-test.ca',1);");
        _template.execute("INSERT INTO `auth_user` (`username`,`password`,`email`,`enabled`) VALUES ('chris-test-5','1234','chris-test-5@chris-test.ca',1);");
        _template.execute("INSERT INTO `auth_user` (`username`,`password`,`email`,`enabled`) VALUES ('chris-test-6','1234','chris-test-6@chris-test.ca',1);");
        _template.execute("INSERT INTO `auth_user` (`username`,`password`,`email`,`enabled`) VALUES ('chris-test-7','1234','chris-test-7@chris-test.ca',1);");
        _template.execute("INSERT INTO `auth_user` (`username`,`password`,`email`,`enabled`) VALUES ('chris-test-8','1234','chris-test-8@chris-test.ca',1);");
    }


    private Integer _persistDefaultUser() {
        //persist one user
        AuthUserDto dto = AuthUserDto.builder()
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .enabled(true)
                .build();
        System.out.println("dto: " + dto.toString());

        Integer id = null;
        if (dto.isValid()) {
            AuthUser entity = dto.toEntity();
            System.out.println("entity: " + entity.toString());

            id = _dao.saveAuthUser(entity);
            System.out.println("entity id: " + id);
        }

        return id;
    }

    @Order(1)
    @Test
    public void testFindAllUsers() throws IOException, InterruptedException {
        //init db
        _insertAuthUsers();

        List<AuthUser> users = _dao.findAllUsers();
        assertEquals(users.size(), (int) _pageSize);
    }

    @Order(2)
    @Test
    public void testFindAllUsersWithPage() {
        //init db
        _insertAuthUsers();

        //0-1 row
        List<AuthUser> users = _dao.findAllUsers(0, 1);
        assertEquals(2, users.size());

        //2-3 row
        users = _dao.findAllUsers(1, 1);
        assertEquals(2, users.size());

        //0-3 row
        users = _dao.findAllUsers(0, 2);
        assertEquals(4, users.size());
    }

    @Order(2)
    @Test
    public void testFindAllUsersWithPage2() {
        assertThrows(AuthServiceException.class, () -> {
            _dao.findAllUsers(-1, 2);
        });

        assertThrows(AuthServiceException.class, () -> {
            _dao.findAllUsers(1, 0);
        });

        assertThrows(AuthServiceException.class, () -> {
            _dao.findAllUsers(1, -1);
        });
    }

    @Order(3)
    @Test
    public void testFindUserByEmail() {
        //init db
        _insertAuthUsers();

        String email = "chris-test-1@chris-test.ca";
        AuthUser user = _dao.findUserByEmail(email);
        assertNotNull(user);
        assertEquals(user.getEmail(), email);
        assertNull(user.getStatus());
    }

    @Order(4)
    @Test
    public void testFindUserByEmail2() {
        String email = "abc@lakeheadu.ca";
        assertThrows(AuthServiceException.class, () -> _dao.findUserByEmail(email));
    }

    @Order(5)
    @Test
    public void testFindUserByName() {
        String username = "abc";
        assertTrue(_dao.findUserByName(username).isEmpty());
    }

    @Order(5)
    @Test
    public void testFindUserByName2() {
        //init db
        _insertAuthUsers();

        String username = "chris-test-1";
        List<AuthUser> users = _dao.findUserByName(username);
        assertEquals(users.size(), 1);
        System.out.println("user: " + users.get(0).toString());
    }

    @Order(6)
    @Test
    public void testSaveAuthUserWithDefaultStatus() {
        AuthUserDto dto = AuthUserDto.builder()
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .enabled(true)
                .build();
        System.out.println("dto: " + dto.toString());

        if (dto.isValid()) {
            AuthUser entity = dto.toEntity();

            System.out.println("entity: " + entity.toString());

            Integer id = _dao.saveAuthUser(entity);
            System.out.println("entity id: " + id);

            assertThrows(AuthServiceException.class, () -> _dao.saveAuthUser(entity));
        }

        AuthUser user = _dao.findUserByEmail(DEFAULT_EMAIL);
        System.out.println("user: " + user.toString());
        assertEquals(DEFAULT_EMAIL, user.getEmail());
        assertEquals(user.getStatus().getStatus(), AuthCommon.LOG_OUT.getVal());
        assertEquals(user.getRoles().get(0).getName(), AuthCommon.USER.getVal());
    }

    /**
     * link admin role to the persisted user and the user already has user role with 'merge'
     * -> throw an exception
     */
    @Order(7)
    @Test
    public void testUpdateUserRoleWithMerge() {
        //persist one user
        AuthUserDto dto = AuthUserDto.builder()
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .enabled(true)
                .build();
        System.out.println("dto: " + dto.toString());

        if (dto.isValid()) {
            AuthUser entity = dto.toEntity();
            System.out.println("entity: " + entity.toString());

            Integer id = _dao.saveAuthUser(entity);
            System.out.println("entity id: " + id);
        }

        assertThrows(AuthServiceException.class, () -> {
            //fetch same user
            AuthUser entity = _dao.findUserByEmail(DEFAULT_EMAIL);
            System.out.println("old entity: " + entity.toString());

            //add admin role
            entity.getRoles().add(new Role(AuthCommon.ADMIN.getVal()));
            _dao.updateAuthUser(entity);
        });
    }

    /**
     * link admin role to the persisted user and the user already has user role with 'merge'
     */
    @Order(7)
    @Test
    public void testUpdateUserRoleWithUserUpdate2() {
        //persist one user
        Integer id = _persistDefaultUser();

        //update role
        _dao.updateUserRole(id, AuthCommon.ADMIN);

        AuthUser entity = _dao.findUserById(id);
        assertTrue(entity.getRoles().stream().anyMatch(x -> x.getName().equals(AuthCommon.USER.getVal())));
        assertTrue(entity.getRoles().stream().anyMatch(x -> x.getName().equals(AuthCommon.ADMIN.getVal())));
    }


    /**
     * update an user without id -> throw exception
     */
    @Order(8)
    @Test
    public void testUpdateAuthUser() {
        AuthUserDto dto = AuthUserDto.builder()
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .enabled(true)
                .build();

        if (dto.isValid()) {
            AuthUser entity = dto.toEntity();
            System.out.println("entity: " + entity.toString());
            assertThrows(AuthServiceException.class, () -> _dao.updateAuthUser(entity));
        }
    }

    @Order(9)
    @Test
    public void testUpdateAuthUser2() {
        //persist one user
        Integer id = _persistDefaultUser();

        //update email
        String newEmail = "chris-test4@chris-test4.ca";
        AuthUser entity = _dao.findUserById(id);
        entity.setEmail(newEmail);
        _dao.updateAuthUser(entity);


        entity = _dao.findUserByEmail(newEmail);
        System.out.println("new entity: " + entity.toString());
        assertEquals(DEFAULT_USERNAME, entity.getUsername());
        assertEquals(DEFAULT_PASSWORD, entity.getPassword());
    }

    @Order(10)
    @Test
    @Disabled
    public void testUpdateUserStatus1() {
        //persist one user
        Integer id = _persistDefaultUser();

        //validate & pull out
        AuthUser userEntity = _dao.findUserById(id);
        System.out.println("user entity: " + userEntity);

        //update login
        Long session = 12 * 60 * 60 * 1000L;
        UserStatus status = userEntity.getStatus();
        status.setStatus(AuthCommon.LOG_IN.getVal());
        status.setSession(session);
        System.out.println("status entity: " + status);
        _dao.updateUserStatus(status);

        userEntity = _dao.findUserById(id);
        assertEquals(userEntity.getStatus().getStatus(), AuthCommon.LOG_IN.getVal());
        assertNotNull(userEntity.getStatus().getSession());
    }

    @Order(10)
    @Test
    public void testUpdateUserStatus2() {
        //persist one user
        Integer id = _persistDefaultUser();

        //validate
        AuthUser userEntity = _dao.findUserById(id);
        System.out.println("user entity: " + userEntity);

        //update login
        Long session = 12 * 60 * 60 * 1000L;
        UserStatus status = userEntity.getStatus();
        status.setStatus(AuthCommon.LOG_IN.getVal());
        status.setSession(session);
        System.out.println("status entity: " + status);
        _dao.updateUserStatus(status);
        assertEquals(userEntity.getStatus().getStatus(), AuthCommon.LOG_IN.getVal());
        assertNotNull(userEntity.getStatus().getSession());

        //update logout
        userEntity = _dao.findUserById(id);

        userEntity.getStatus().setStatus(AuthCommon.LOG_OUT.getVal());
        userEntity.getStatus().setSession(null);
        userEntity.getStatus().setLogOutTimestamp(new Date());
        _dao.updateAuthUser(userEntity);

        userEntity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertEquals(userEntity.getStatus().getStatus(), AuthCommon.LOG_OUT.getVal());
    }

    /**
     * update user status by flip
     */
    @Order(10)
    @Test
    public void testUpdateUserStatus3() throws InterruptedException {
        //persist one user
        Integer id = _persistDefaultUser();

        //validate
        AuthUser userEntity = _dao.findUserById(id);
        System.out.println("user entity: " + userEntity);

        //login
        userEntity.getStatus().setStatus(AuthCommon.LOG_IN.getVal());
        userEntity.getStatus().setSession(2L);
        userEntity.getStatus().setLogInTimestamp(new Date());
        _dao.updateAuthUser(userEntity);

        //wait for expire
        Thread.sleep(3_000L);

        //flip status
        _dao.flipLoginUserStatus();

        //check
        userEntity = _dao.findUserById(id);
        assertEquals(userEntity.getStatus().getStatus(), AuthCommon.LOG_OUT.getVal());
        assertNull(userEntity.getStatus().getSession());
    }

    @Order(11)
    @Test
    public void testDeleteAuthUser1() {
        //persist one user
        Integer id = _persistDefaultUser();

        //validate
        AuthUser userEntity = _dao.findUserById(id);
        System.out.println("user entity: " + userEntity);

        _dao.deleteAuthUserByEmail(DEFAULT_EMAIL);

        assertThrows(AuthServiceException.class, () -> {
            _dao.findUserByEmail(DEFAULT_EMAIL);
        });
    }


    @Order(12)
    @Test
    public void testSameUserExists() {
        //persist one user
        Integer id = _persistDefaultUser();

        boolean res = _dao.sameUserExists(DEFAULT_EMAIL);

        assertTrue(res);
    }

}