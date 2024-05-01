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

import com.chris.dto.AuthUser;
import com.chris.entity.AuthCommon;
import com.chris.entity.Role;
import com.chris.entity.UserStatus;
import com.chris.exception.AuthServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_DAO_BEAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


//ToDo: update h2 test to integration test with docker during maven build
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@TestPropertySource("/application.properties")
@SpringBootTest
class AuthAccessDaoImplTest {
    @Autowired
    private JdbcTemplate _template;

    @Autowired
    @Qualifier(value = AUTH_ACCESS_DAO_BEAN)
    private AuthAccessDao _dao;

    private final String DEFAULT_USERNAME = "chris-test";
    private final String DEFAULT_PASSWORD = "1234";
    private final String DEFAULT_EMAIL = "chris-test@chris-test.ca";

    @AfterEach
    public void afterEachTest() {
        _template.execute("delete from users_roles");
        _template.execute("delete from user_status");
        _template.execute("delete from role");
        _template.execute("delete from auth_user");
    }

    @Order(1)
    //@Sql("/insert-auth-data.sql")
    @Test
    public void testFindAllUsers() {
        List<com.chris.entity.AuthUser> users = _dao.findAllUsers();
        assertTrue(users.size() == 10);
        users.stream().forEach(x -> System.out.println(x.toString()));
        users.stream().forEach(x -> assertNull(x.getStatus()));
    }

    @Order(2)
    //@Sql("/insert-auth-data.sql")
    @Test
    public void testFindAllUsersWithPage() {
        List<com.chris.entity.AuthUser> users = _dao.findAllUsers(1, 2);
        assertTrue(users.size() == 20);
        users.stream().forEach(x -> System.out.println(x.toString()));
        users.stream().forEach(x -> assertNull(x.getStatus()));
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
    //@Sql("/insert-auth-data.sql")
    @Test
    public void testFindUserByEmail() {
        String email = "phil2024sidhu@chrismember.ca";
        com.chris.entity.AuthUser user = _dao.findUserByEmail(email);
        assertNotNull(user);
        assertTrue(user.getEmail().equals(email));
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
    //@Sql("/insert-auth-data.sql")
    @Test
    public void testFindUserByName2() {
        String username = "philsidhu";
        List<com.chris.entity.AuthUser> users = _dao.findUserByName(username);
        assertEquals(users.size(), 1);
        System.out.println("user: " + users.get(0).toString());
    }

    /**
     * register a user with default logout status and user role
     */
    @Order(6)
    //@Sql("/insert-role-data.sql")
    @Test
    public void testSaveAuthUser() {
        AuthUser dto = AuthUser.builder()
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .enabled(true)
                .build();
        System.out.println("dto: " + dto.toString());

        if (dto.isValid()) {
            com.chris.entity.AuthUser entity = dto.toEntity();

            System.out.println("entity: " + entity.toString());

            Integer id = _dao.saveAuthUser(entity);
            System.out.println("entity id: " + id);

            assertThrows(AuthServiceException.class, () -> _dao.saveAuthUser(entity));
        }

        com.chris.entity.AuthUser user = _dao.findUserByEmail(DEFAULT_EMAIL);
        System.out.println("user: " + user.toString());
        assertTrue(user.getEmail().equals(DEFAULT_EMAIL));
        assertTrue(user.getStatus().getStatus().equals(AuthCommon.LOG_OUT.getVal()));
        assertTrue(user.getRoles().get(0).getName().equals(AuthCommon.USER.getVal()));
    }

    /**
     * link user role to the persisted user and the user already has user role
     * <p>
     * no exception but throw warning for the existing relationship
     */
    @Order(7)
    //@Sql("/insert-single-data.sql")
    @Test
    @Disabled
    public void testUpdateUserRole() {
        String email = DEFAULT_EMAIL;
        com.chris.entity.AuthUser entity = _dao.findUserByEmail(email);
        System.out.println("entity: " + entity.toString());
        _dao.updateUserRole(entity, AuthCommon.USER);
    }

    /**
     * link admin role to the persisted user and the user already has user role
     */
    @Order(7)
    //@Sql("/insert-single-data.sql")
    @Test
    public void testUpdateUserRole2() {
        String email = DEFAULT_EMAIL;
        com.chris.entity.AuthUser entity = _dao.findUserByEmail(email);
        System.out.println("old entity: " + entity.toString());

        //add admin role
        entity.getRoles().add(new Role(AuthCommon.ADMIN.getVal()));
        _dao.updateAuthUser(entity);

        //_dao.updateUserRole(entity, AuthCommon.ADMIN);

        entity = _dao.findUserByEmail(email);
        System.out.println("new entity: " + entity);

        assertTrue(entity.getRoles().stream().anyMatch(x -> x.getName().equals(AuthCommon.USER.getVal())));
        assertTrue(entity.getRoles().stream().anyMatch(x -> x.getName().equals(AuthCommon.ADMIN.getVal())));
    }


    /**
     * link a role to an entity without id -> throw exception
     */
    @Order(7)
    @Test
    public void testUpdateUserRole3() {
        AuthUser dto = AuthUser.builder()
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .enabled(true)
                .build();

        System.out.println("dto: " + dto.toString());

        if (dto.isValid()) {
            com.chris.entity.AuthUser entity = dto.toEntity();
            System.out.println("entity: " + entity.toString());
            assertThrows(AuthServiceException.class, () -> _dao.updateUserRole(entity, AuthCommon.USER));
        }
    }

    /**
     * update an user without id -> throw exception
     */
    @Order(8)
    @Test
    public void testUpdateAuthUser() {
        AuthUser dto = AuthUser.builder()
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .enabled(true)
                .build();

        if (dto.isValid()) {
            com.chris.entity.AuthUser entity = dto.toEntity();
            System.out.println("entity: " + entity.toString());
            assertThrows(AuthServiceException.class, () -> _dao.updateAuthUser(entity));
        }
    }

    @Order(9)
    //@Sql("/insert-single-data.sql")
    @Test
    public void testUpdateAuthUser2() {
        String newEmail = "chris-test4@chris-test4.ca";

        com.chris.entity.AuthUser entity = _dao.findUserByEmail(DEFAULT_EMAIL);
        entity.setEmail(newEmail);
        _dao.updateAuthUser(entity);


        entity = _dao.findUserByEmail(newEmail);
        System.out.println("new entity: " + entity.toString());
        assertTrue(entity.getUsername().equals(DEFAULT_USERNAME));
        assertTrue(entity.getPassword().equals(DEFAULT_PASSWORD));
    }

    /**
     * update the user status manually with mariadb, h2 NOT supported
     */
    @Order(10)
    @Test
    @Disabled
    public void testUpdateUserStatus1() {
        //register
        String username = "chris-test9";
        String password = "1234";
        String email = "chris-test9@chris-test9.ca";

        AuthUser dto = AuthUser.builder()
                .username(username)
                .email(email)
                .password(password)
                .enabled(true)
                .build();

        if (dto.isValid()) {
            com.chris.entity.AuthUser entity = dto.toEntity();
            _dao.saveAuthUser(entity);
        }

        //validate & pull out
        com.chris.entity.AuthUser userEntity = _dao.findUserByEmail(email);
        System.out.println("user entity: " + userEntity);

        //update login
        Long session = 12 * 60 * 60 * 1000L;
        UserStatus status = userEntity.getStatus();
        status.setStatus(AuthCommon.LOG_IN.getVal());
        status.setSession(session);
        System.out.println("status entity: " + status);
        _dao.updateUserStatus(status);
    }

    @Order(10)
    //@Sql("/insert-single-data.sql")
    @Test
    public void testUpdateUserStatus2() {
        //validate
        com.chris.entity.AuthUser userEntity = _dao.findUserByEmail(DEFAULT_EMAIL);
        System.out.println("user entity: " + userEntity);

        //logout
        userEntity.getStatus().setStatus(AuthCommon.LOG_OUT.getVal());
        userEntity.getStatus().setSession(null);
        userEntity.getStatus().setLogOutTimestamp(new Date());
        _dao.updateAuthUser(userEntity);

        userEntity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertTrue(userEntity.getStatus().getStatus().equals(AuthCommon.LOG_OUT.getVal()));
    }

    /**
     * update user status via auth user
     */
    @Order(10)
    //@Sql("/insert-single-data.sql")
    @Test
    public void testUpdateUserStatus3() {
        //validate
        com.chris.entity.AuthUser userEntity = _dao.findUserByEmail(DEFAULT_EMAIL);
        System.out.println("user entity: " + userEntity);

        //login
        userEntity.getStatus().setStatus(AuthCommon.LOG_IN.getVal());
        userEntity.getStatus().setSession(10 * 60 * 1000L);
        userEntity.getStatus().setLogInTimestamp(new Date());
        _dao.updateAuthUser(userEntity);

        userEntity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertTrue(userEntity.getStatus().getStatus().equals(AuthCommon.LOG_IN.getVal()));
    }

    @Order(11)
    //@Sql("/insert-single-data.sql")
    @Test
    public void testDeleteAuthUser1() {
        //validate
        com.chris.entity.AuthUser userEntity = _dao.findUserByEmail(DEFAULT_EMAIL);
        System.out.println("user entity: " + userEntity);

        _dao.deleteAuthUserByEmail(DEFAULT_EMAIL);

        assertThrows(AuthServiceException.class, () -> {
            _dao.findUserByEmail(DEFAULT_EMAIL);
        });
    }


    @Order(12)
    //@Sql("/insert-single-data.sql")
    @Test
    public void testSameUserExists() {
        boolean res = _dao.sameUserExists(DEFAULT_EMAIL);
        assertTrue(res);
    }

}