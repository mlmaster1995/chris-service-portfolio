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