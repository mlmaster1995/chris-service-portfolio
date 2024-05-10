package com.chris.rest;

import com.chris.dto.UserStatusDto;
import com.chris.util.AuthCommon;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static com.chris.util.AuthClientConstant.AUTH_REST_CLIENT_BEAN;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@RestClientTest(BasicAuthRestClient.class)
class BasicAuthRestClientTest {
    @Autowired
    ObjectMapper _mapper;

    @Autowired
    @Qualifier(value = AUTH_REST_CLIENT_BEAN)
    private BasicAuthRestClient _client;

    @Autowired
    private MockRestServiceServer _mockServer;

    @Order(1)
    @Test
    public void testInit() {
        assertNotNull(_client);
        assertNotNull(_mockServer);
    }

    @Order(2)
    @Test
    public void testValidate() throws JsonProcessingException {
        UserStatusDto dtoSource = UserStatusDto.builder()
                .status(AuthCommon.LOG_IN.getVal())
                .session(300L)
                .id(1)
                .build();

        _mockServer.expect(requestTo("http://127.0.0.1:8888/api/v1/auth/status"))
                .andRespond(withSuccess(_mapper.writeValueAsString(dtoSource), MediaType.APPLICATION_JSON));

        UserStatusDto dtoTarget = _client.validate(new String[]{"abc@test.ca", "123445"});
        System.out.println("dto target: " + dtoTarget.toString());

        assertTrue(dtoTarget.getStatus().equals(AuthCommon.LOG_IN.getVal()));
        assertTrue(dtoTarget.getSession().equals(300L));
    }
}