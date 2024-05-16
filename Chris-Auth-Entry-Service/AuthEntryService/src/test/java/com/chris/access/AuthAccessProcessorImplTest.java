package com.chris.access;

import com.chris.dao.AuthAccessDaoImpl;
import com.chris.dto.AuthUserDto;
import com.chris.entity.AuthUser;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MariaDBContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest
class AuthAccessProcessorImplTest {
    private final String DEFAULT_USERNAME = "chris-test";
    private final String DEFAULT_PASSWORD = "1234";
    private final String DEFAULT_EMAIL = "chris-test@chris-test.ca";

    //init test containers
    public static final MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:10.5.5");

    @SpyBean
    AuthAccessDaoImpl _dao;

    @Autowired
    AuthAccessProcessorImpl _service;

    @Autowired
    PasswordEncoder _encoder;

    @Autowired
    private JdbcTemplate _template;

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

    private void _registerDefaultUser() {
        AuthUserDto dto = AuthUserDto.builder()
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .build();

        _service.register(dto);
    }

    @Order(1)
    @Test
    public void init() {
        assertNotNull(_service);
    }

    @Order(2)
    @Test
    public void testRegister() {
        doCallRealMethod().when(_dao).findUserByEmail(any(String.class));

        AuthUserDto dto = AuthUserDto.builder()
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .build();

        _service.register(dto);

        AuthUser entity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertEquals(entity.getStatus().getStatus(), AuthCommon.LOG_OUT.getVal());
        assertEquals(DEFAULT_USERNAME, entity.getUsername());
        assertEquals(DEFAULT_EMAIL, entity.getEmail());
        assertTrue(_encoder.matches(DEFAULT_PASSWORD, entity.getPassword()));
    }

    @Order(2)
    @Test
    public void testRegister2() {
        doThrow(new AuthServiceException("fails to save")).when(_dao).saveAuthUser(any(AuthUser.class));

        AuthUserDto dto = AuthUserDto.builder()
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_PASSWORD)
                .build();

        assertThrows(AuthServiceException.class, () -> {
            _service.register(dto);
        });
    }

    //default user session is 5-sec in the test
    @Order(3)
    @Test
    public void testLogin() {
        //init db
        _registerDefaultUser();

        //login
        String jwt = _service.login(DEFAULT_EMAIL);
        System.out.println("jwt: " + jwt);

        //validate
        AuthUser entity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertEquals(entity.getStatus().getStatus(), AuthCommon.LOG_IN.getVal());
        assertNotNull(entity.getStatus().getSession());
        assertNotNull(jwt);

        //re-login
        assertThrows(AuthServiceException.class, () -> _service.login(DEFAULT_EMAIL));
    }

    //default user session is 5-sec in the test
    @Order(3)
    @Test
    public void testLogin2() {
        assertThrows(AuthServiceException.class, () -> _service.login(DEFAULT_EMAIL));
    }

    @Order(4)
    @Test
    public void testLogout() {
        assertThrows(AuthServiceException.class, () -> _service.logout(DEFAULT_EMAIL));
    }

    @Order(4)
    @Test
    public void testLogout2() {
        //init db
        _registerDefaultUser();

        //logout without login at first
        assertThrows(AuthServiceException.class, () -> _service.logout(DEFAULT_EMAIL));
    }

    @Order(4)
    @Test
    public void testLogout3() {
        //init db
        _registerDefaultUser();

        //login
        String jwt = _service.login(DEFAULT_EMAIL);
        System.out.println("jwt: " + jwt);

        AuthUser entity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertEquals(entity.getStatus().getStatus(), AuthCommon.LOG_IN.getVal());
        assertNotNull(entity.getStatus().getSession());
        assertNotNull(jwt);

        //logout
        _service.logout(DEFAULT_EMAIL);
        entity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertEquals(entity.getStatus().getStatus(), AuthCommon.LOG_OUT.getVal());
        assertNull(entity.getStatus().getSession());

        //logout again
        assertThrows(AuthServiceException.class, () -> _service.logout(DEFAULT_EMAIL));
    }


    //default user session is 5-sec in the test
    //default check period is 5-sec in the test
    @Order(5)
    @Test
    public void testStatusFlip() throws InterruptedException {
        //init db
        _registerDefaultUser();

        //wait for session expired
        Thread.sleep(10_000L);

        //validate
        AuthUser entity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertEquals(entity.getStatus().getStatus(), AuthCommon.LOG_OUT.getVal());
        assertNull(entity.getStatus().getSession());
    }

    //default user session is 5-sec in the test
    //default check period is 5-sec in the test
    @Order(5)
    @Test
    @Disabled
    public void testStatusFlip2() throws InterruptedException {
        //init db
        _registerDefaultUser();

        //login
        String jwt = _service.login(DEFAULT_EMAIL);

        AuthUser entity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertEquals(entity.getStatus().getStatus(), AuthCommon.LOG_IN.getVal());
        assertNotNull(entity.getStatus().getSession());
        assertNotNull(jwt);

        //wait for session expired
        Thread.sleep(10_000L);

        //validate
        entity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertEquals(entity.getStatus().getStatus(), AuthCommon.LOG_OUT.getVal());
        assertNull(entity.getStatus().getSession());
    }


}