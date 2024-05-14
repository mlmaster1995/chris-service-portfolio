package com.chris.api;

import com.chris.access.AuthAccessProcessorImpl;
import com.chris.dao.AuthAccessDaoImpl;
import com.chris.dto.AuthUserDto;
import com.chris.entity.AuthUser;
import com.chris.exception.AuthServiceException;
import com.chris.util.AuthCommon;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_CONTROL_BEAN;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest
class AuthAccessControllerTest {
    private final String DEFAULT_USERNAME = "chris-test";
    private final String DEFAULT_PASSWORD = "1234";
    private final String DEFAULT_EMAIL = "chris-test@chris-test.ca";

    @Autowired
    private MockMvc _mockMvc;

    @Autowired
    @Qualifier(value = AUTH_ACCESS_CONTROL_BEAN)
    private AuthAccessController _controller;

    @Autowired
    private JdbcTemplate _template;

    @SpyBean
    private AuthAccessProcessorImpl _service;

    @SpyBean
    private AuthAccessDaoImpl _dao;

    @AfterEach
    public void afterEachTest() {
        _template.execute("delete from users_roles");
        _template.execute("delete from user_status");
        _template.execute("delete from auth_user");
    }

    @Order(1)
    @Test
    public void testRegister() throws Exception {
        String registerBody = "{" +
                "\"username\":\"chris-20240506\"," +
                "\"password\":\"123456\"," +
                "\"email\":\"kyang-20240506@lakeheadu.ca\"" +
                "}";

        //from client
        _mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        MediaType.valueOf("text/plain;charset=UTF-8")));
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
    public void testRegister2() throws Exception {
        //mock exception
        doThrow(new AuthServiceException("mocked error at service layer"))
                .when(_service).register(any(AuthUserDto.class));

        //init body
        String registerBody = "{" +
                "\"username\":\"chris-20240506\"," +
                "\"password\":\"123456\"," +
                "\"email\":\"kyang-20240506@lakeheadu.ca\"" +
                "}";

        //from client
        _mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(
                        MediaType.valueOf("text/plain;charset=UTF-8")));
    }


    @Order(2)
    @Test
    @WithMockUser(password = DEFAULT_PASSWORD, username = DEFAULT_EMAIL, roles = {"ADMIN", "USER"})
    public void testLogin() throws Exception {
        //init user
        _registerDefaultUser();

        //from client
        _mockMvc.perform(get("/api/v1/auth/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        MediaType.valueOf("text/plain;charset=UTF-8")));
    }

    @Order(2)
    @Test
    @WithMockUser(password = DEFAULT_PASSWORD, username = DEFAULT_EMAIL, roles = {"ADMIN", "USER"})
    public void testLogin2() throws Exception {
        //mock exception
        doThrow(new AuthServiceException("mocked error at service layer"))
                .when(_service).login(any(String.class));

        //init user
        _registerDefaultUser();

        //from client
        _mockMvc.perform(get("/api/v1/auth/login"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(
                        MediaType.valueOf("text/plain;charset=UTF-8")));
    }

    @Order(2)
    @Test
    @WithMockUser(password = DEFAULT_PASSWORD, username = DEFAULT_EMAIL, roles = {"ADMIN", "USER"})
    public void testLogin3() throws Exception {
        //init user
        _registerDefaultUser();

        //from client
        _mockMvc.perform(get("/api/v1/auth/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        MediaType.valueOf("text/plain;charset=UTF-8")));

        AuthUser entity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertTrue(entity.getStatus().getStatus().equals(AuthCommon.LOG_IN.getVal()));
        assertTrue(entity.getStatus().getSession().equals(5L));
    }

    @Order(3)
    @Test
    @WithMockUser(password = DEFAULT_PASSWORD, username = DEFAULT_EMAIL, roles = {"ADMIN", "USER"})
    public void testLogout1() throws Exception {
        //init user
        _registerDefaultUser();

        //user login
        String jwt = _service.login(DEFAULT_EMAIL);
        System.out.println("jwt: " + jwt);

        AuthUser entity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertTrue(entity.getStatus().getStatus().equals(AuthCommon.LOG_IN.getVal()));
        assertNotNull(entity.getStatus().getSession());
        assertNotNull(jwt);

        //user logout
        _mockMvc.perform(get("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        MediaType.valueOf("text/plain;charset=UTF-8")));

        entity = _dao.findUserByEmail(DEFAULT_EMAIL);
        assertTrue(entity.getStatus().getStatus().equals(AuthCommon.LOG_OUT.getVal()));
        assertNull(entity.getStatus().getSession());
    }

    @Order(3)
    @Test
    @WithMockUser(password = DEFAULT_PASSWORD, username = DEFAULT_EMAIL, roles = {"ADMIN", "USER"})
    public void testLogout2() throws Exception {
        //mock exception
        doThrow(new AuthServiceException("mocked error at service layer"))
                .when(_service).login(any(String.class));

        //user logout
        _mockMvc.perform(get("/api/v1/auth/logout"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(
                        MediaType.valueOf("text/plain;charset=UTF-8")));
    }

}