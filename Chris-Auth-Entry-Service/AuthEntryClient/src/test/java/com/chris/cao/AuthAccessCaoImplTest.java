package com.chris.cao;

import com.chris.AuthEntryClientTestApplication;
import com.chris.token.BasicAuthAccessJwt;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static com.chris.util.AuthClientConstant.AUTH_ACCESS_CAO_BEAN;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest(classes = {BasicAuthAccessJwt.class, AuthEntryClientTestApplication.class})
class AuthAccessCaoImplTest {

    @Autowired
    @Qualifier(value = AUTH_ACCESS_CAO_BEAN)
    private AuthAccessCao _cao;

    @Order(1)
    @Test
    public void init() {
        System.out.println("hello world");
    }
}