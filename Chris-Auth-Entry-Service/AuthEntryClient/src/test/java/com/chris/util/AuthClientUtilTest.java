package com.chris.util;

import com.chris.exception.AuthClientException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthClientUtilTest {
    @Order(1)
    @Test
    public void testEmail() {
        String email = "chris@test.com";
        assertDoesNotThrow(() -> AuthClientUtil.validateEmailAddress(email));
    }

    @Order(2)
    @Test
    public void testEmail2() {
        String email = "christest.com";
        assertThrows(AuthClientException.class,
                () -> AuthClientUtil.validateEmailAddress(email));
    }

    @Order(3)
    @Test
    public void testEmail3() {
        String email = "chris@test";
        assertThrows(AuthClientException.class,
                () -> AuthClientUtil.validateEmailAddress(email));
    }

    @Order(4)
    @Test
    public void testEmail4() {
        String email = "chris@.ca";
        assertThrows(AuthClientException.class,
                () -> AuthClientUtil.validateEmailAddress(email));
    }

    @Order(5)
    @Test
    public void testEmail5() {
        String email = "chris@.";
        assertThrows(AuthClientException.class,
                () -> AuthClientUtil.validateEmailAddress(email));
    }

    @Order(6)
    @Test
    public void testEmail6() {
        String email = "@.";
        assertThrows(AuthClientException.class,
                () -> AuthClientUtil.validateEmailAddress(email));
    }
}