package com.chris.auth;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest
class AppAuthServiceImplTest {
    @SpyBean
    private AppAuthDaoImpl _appAuthDao;

    @Autowired
    private AppAuthService _appAuthService;

    @Test
    public void initTest(){
        ????
    }

}