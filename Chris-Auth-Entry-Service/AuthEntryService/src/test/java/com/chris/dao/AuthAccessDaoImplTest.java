package com.chris.dao;

import com.chris.Exception.AppAuthException;
import com.chris.dto.AuthUserDto;
import com.chris.entity.AuthCommon;
import com.chris.entity.AuthUser;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_DAO_BEAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@TestPropertySource("/application.properties")
@SpringBootTest
class AuthAccessDaoImplTest {

    @Autowired
    private JdbcTemplate _template;

    @Autowired
    @Qualifier(value = AUTH_ACCESS_DAO_BEAN)
    private AuthAccessDao _dao;

    /**
     * application.properties for test gives default page size at 10
     */
    @Order(1)
    @Test
    public void testFindAllUsers() {
        List<AuthUser> users = _dao.findAllUsers();
        assertTrue(users.size() == 10);
        users.stream().forEach(x -> System.out.println(x.toString()));
        users.stream().forEach(x -> assertNull(x.getStatus()));
    }

    @Order(2)
    @Test
    public void testFindAllUsersWithPage() {
        List<AuthUser> users = _dao.findAllUsers(1, 2);
        assertTrue(users.size() == 20);
        users.stream().forEach(x -> System.out.println(x.toString()));
        users.stream().forEach(x -> assertNull(x.getStatus()));
    }

    @Order(2)
    @Test
    public void testFindAllUsersWithPage2() {
        assertThrows(AppAuthException.class, () -> {
            _dao.findAllUsers(-1, 2);
        });

        assertThrows(AppAuthException.class, () -> {
            _dao.findAllUsers(1, 0);
        });

        assertThrows(AppAuthException.class, () -> {
            _dao.findAllUsers(1, -1);
        });
    }

    @Order(3)
    @Test
    public void testFindUserByEmail() {
        String email = "kyang3@lakeheadu.ca";

        AuthUser user = _dao.findUserByEmail(email);

        assertNotNull(user);
        assertTrue(user.getEmail().equals(email));
        assertNull(user.getStatus());
    }

    @Order(4)
    @Test
    public void testFindUserByEmail2() {
        String email = "abc@lakeheadu.ca";
        assertThrows(AppAuthException.class, () -> _dao.findUserByEmail(email));
    }

    @Order(5)
    @Test
    public void testFindUserByName() {
        String username = "abc";
        assertThrows(AppAuthException.class, () -> _dao.findUserByName(username));
    }

    @Order(5)
    @Test
    public void testFindUserByName2() {
        String username = "chris";

        List<AuthUser> users = _dao.findUserByName(username);

        assertEquals(users.size(), 1);

        System.out.println("user: " + users.get(0).toString());
    }

    @Order(6)
    @Test
    public void testSaveAuthUser() {
        String email = "chris-test@chris-test.ca";
        AuthUserDto dto = AuthUserDto.builder()
                .username("chris-test")
                .email(email)
                .password("1234")
                .enabled(true)
                .build();
        System.out.println("dto: " + dto.toString());

        if (dto.isValid()) {
            AuthUser entity = dto.toEntity();

            System.out.println("entity: " + entity.toString());

            _dao.saveAuthUser(entity);

            assertThrows(AppAuthException.class, () -> _dao.saveAuthUser(entity));
        }

        AuthUser user = _dao.findUserByEmail(email);

        System.out.println("user: " + user.toString());

        assertTrue(user.getEmail().equals(email));
    }

    @Order(6)
    @Test
    public void testSaveAuthUser2() {
        String email = "chris-test2@chris-test2.ca";
        AuthUserDto dto = AuthUserDto.builder()
                .username("chris-test2")
                .email(email)
                .password("1234")
                .enabled(true)
                .build();
        System.out.println("dto: " + dto.toString());

        if (dto.isValid()) {
            AuthUser entity = dto.toEntity();
            System.out.println("entity: " + entity.toString());

            _dao.saveAuthUser(entity); //save user
            _dao.updateUserRole(entity, AuthCommon.USER); //link user to the role
        }

        AuthUser user = _dao.findUserByEmail(email);
        System.out.println("user: " + user.toString());

        assertTrue(user.getEmail().equals(email));
        assertTrue(user.getRoles().get(0).getName().equals(AuthCommon.USER.getVal()));
    }

    @Order(7)
    @Test
    public void testUpdateUserRole() {
        String email = "chris-test2@chris-test2.ca";

        AuthUserDto dto = AuthUserDto.builder()
                .username("chris-test2")
                .email(email)
                .password("1234")
                .enabled(true)
                .build();

        System.out.println("dto: " + dto.toString());

        if (dto.isValid()) {
            AuthUser entity = dto.toEntity();

            System.out.println("entity: " + entity.toString());

            assertThrows(AppAuthException.class, () -> _dao.updateUserRole(entity, AuthCommon.USER));
        }
    }

    @Order(8)
    @Test
    public void testUpdateAuthUser() {
        String email = "chris-test3@chris-test3.ca";

        AuthUserDto dto = AuthUserDto.builder()
                .username("chris-test3")
                .email(email)
                .password("1234")
                .enabled(true)
                .build();

        if (dto.isValid()) {
            AuthUser entity = dto.toEntity();

            System.out.println("entity: " + entity.toString());

            assertThrows(AppAuthException.class, () -> _dao.updateAuthUser(entity));
        }
    }

    @Order(9)
    @Test
    public void testUpdateAuthUser2() {
        String username = "chris-test3";
        String password = "1234";
        String email = "chris-test3@chris-test3.ca";
        String newEmail = "chris-test4@chris-test4.ca";

        AuthUserDto dto = AuthUserDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .enabled(true)
                .build();

        if (dto.isValid()) {
            AuthUser entity = dto.toEntity();
            System.out.println("old entity: " + entity.toString());

            _dao.saveAuthUser(entity);

            entity = _dao.findUserByEmail(email);
            entity.setEmail(newEmail);
            _dao.updateAuthUser(entity);
        }

        AuthUser entity = _dao.findUserByEmail(newEmail);
        System.out.println("new entity: " + entity.toString());
        assertTrue(entity.getUsername().equals(username));
        assertTrue(entity.getPassword().equals(password));
    }
}