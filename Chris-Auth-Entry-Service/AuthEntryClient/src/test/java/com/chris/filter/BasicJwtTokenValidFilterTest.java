package com.chris.filter;

import com.chris.AuthEntryClientTestApplication;
import com.chris.token.BasicAuthAccessJwt;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest(classes = {BasicAuthAccessJwt.class, AuthEntryClientTestApplication.class})
class BasicJwtTokenValidFilterTest {

    private final String _skipEndpointsStr = "/api/v1/test/users,/api/v1/test/filters";

    @Autowired
    BasicJwtTokenValidFilter _jwtFilter;

    @Order(1)
    @Test
    public void initFilter() {
        assertNotNull(_jwtFilter);
    }

    @Order(2)
    @Test
    public void testSkipEndpoints() {
        String endpoints = _jwtFilter.getSkipEndpoints();
        assertTrue(endpoints.equals(_skipEndpointsStr));
    }

}