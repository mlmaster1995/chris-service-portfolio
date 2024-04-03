package com.chris.auth;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Arrays;

import static com.chris.util.AppBeanConstant.APP_AUTH_SERVICE_BEAN;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest
class AppAuthServiceImplTest {
    @SpyBean
    private AppAuthDaoImpl _appAuthDao;

    @Autowired
    @Qualifier(value = APP_AUTH_SERVICE_BEAN)
    private AppAuthService _appAuthService;

    @Order(1)
    @Test
    public void testFindUserByUserName() {
        String username = "user";

        doReturn(new AuthUser("user", "12345", "user@chrismemeber.ca",
                true, new ArrayList<Role>(Arrays.asList(new Role("user")))))
                .when(_appAuthDao).findUserByUserName(username);

        AuthUser user = _appAuthService.findUserByUserName(username);

        assertTrue(user.getUsername().equals("user"));
        assertTrue(user.getPassword().equals("12345"));
        assertTrue(user.getEmail().equals("user@chrismemeber.ca"));
    }

    @Order(1)
    @Test
    public void testLoadUserDetails() {
        String username = "user";

        doReturn(new AuthUser("user", "12345", "user@chrismemeber.ca",
                true, new ArrayList<Role>(Arrays.asList(new Role("user")))))
                .when(_appAuthDao).findUserByUserName(username);

        UserDetails user = _appAuthService.loadUserByUsername(username);

        assertTrue(user.getUsername().equals(username));
        assertTrue(user.getPassword().equals("12345"));
        user.getAuthorities().stream().forEach(x -> assertTrue(x.getAuthority().equals("user")));
    }

}