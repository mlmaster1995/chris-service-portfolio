/**
 * MIT License
 *
 * Copyright (c) 2024 Chris Yang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.chris.auth;

import com.chris.exception.AppServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static com.chris.util.AppBeanConstant.APP_AUTH_DAO_BEAN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest
class AppAuthDaoImplTest {
    @Autowired
    private JdbcTemplate _template;

    @Autowired
    @Qualifier(value = APP_AUTH_DAO_BEAN)
    private AppAuthDao _authDao;

    @AfterEach
    public void afterEachTest() {
        _template.execute("delete from users_roles");
        _template.execute("delete from auth_user");
        _template.execute("delete from role");
    }

    @Order(1)
    @Test
    public void testFindUserByName1() {
        assertThrows(AppServiceException.class, () -> {
            AuthUser user = _authDao.findUserByUserName("chris");
        });
    }

    @Order(2)
    @Test
    @Sql("/insert-auth-data.sql")
    public void testFindUserByName2() {
        AuthUser user = _authDao.findUserByUserName("user");

        System.out.println("user: " + user.toString());

        assertTrue(user.getUsername().equals("user"));
        assertTrue(user.getEnabled());
        assertTrue(user.getEmail().equals("user2024@chrismemeber.ca"));

        user.getRoles().stream().forEach(x -> assertTrue(x.getName().equals("ROLE_USER")));
    }

    @Order(3)
    @Test
    @Sql("/insert-auth-data.sql")
    public void testFindRoleByName1() {
        Role role = _authDao.findRoleByRoleName("ROLE_ADMIN");
        System.out.println("role: " + role.toString());

        assertTrue(role.getName().equals("ROLE_ADMIN"));
        role.getUsers().stream().forEach(x -> x.getUsername().equals("admin"));
    }

    @Order(4)
    @Test
    public void testFindRoleByName2() {
        assertThrows(AppServiceException.class, () -> {
            Role role = _authDao.findRoleByRoleName("manager");
        });
    }
}






















