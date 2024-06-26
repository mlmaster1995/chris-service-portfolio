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
package com.chris.filter;

import com.chris.AuthEntryClientTestApplication;
import com.chris.token.BasicAuthAccessJwt;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MariaDBContainer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest(classes = {AuthEntryClientTestApplication.class})
class BasicJwtTokenValidFilterTest {

    private final String _skipEndpointsStr = "/api/v1/test/users,/api/v1/test/filters";

    //init test containers
    public static final MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:10.5.5");

    @BeforeAll
    public static void beforeAll() {
        mariadb.withDatabaseName("service-common");
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