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
package com.chris.token;

import com.chris.AuthEntryClientTestApplication;
import com.chris.dto.UserStatusDto;
import com.chris.entity.AuthUser;
import com.chris.entity.Role;
import com.chris.rest.BasicAuthRestClient;
import com.chris.util.AuthCommon;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.chris.token.AuthAccessJwt.JWT_PAYLOAD_AUDIENCE;
import static com.chris.token.AuthAccessJwt.JWT_PAYLOAD_EXP;
import static com.chris.token.AuthAccessJwt.JWT_PAYLOAD_IAT;
import static com.chris.token.AuthAccessJwt.JWT_PAYLOAD_ISSUER;
import static com.chris.token.AuthAccessJwt.JWT_PAYLOAD_NBF;
import static com.chris.token.AuthAccessJwt.JWT_PAYLOAD_SUBJECT;
import static com.chris.token.AuthAccessJwt.JWT_TOKEN_ISSUER;
import static com.chris.token.AuthAccessJwt.JWT_TOKEN_SUBJECT;
import static com.chris.token.BasicAuthAccessJwt.JWT_PAYLOAD_AUTH;
import static com.chris.token.BasicAuthAccessJwt.JWT_PAYLOAD_EMAIL;
import static com.chris.token.BasicAuthAccessJwt.JWT_PAYLOAD_USERNAME;
import static com.chris.util.AuthClientConstant.AUTH_REST_CLIENT_BEAN;
import static com.chris.util.AuthClientConstant.BASIC_AUTH_ACCESS_JWT_BEAN;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest(classes = {BasicAuthRestClient.class, BasicAuthAccessJwt.class,
        AuthEntryClientTestApplication.class})
class BasicAuthAccessJwtTest {
    @MockBean
    @Qualifier(AUTH_REST_CLIENT_BEAN)
    private BasicAuthRestClient _basicAuthClient;

    @Autowired
    @Qualifier(value = BASIC_AUTH_ACCESS_JWT_BEAN)
    private BasicAuthAccessJwt _basicJwtToken;

    @Order(1)
    @Test
    public void testInit() {
        assertNotNull(_basicJwtToken);
    }

    @Order(1)
    @Test
    public void testJwtGenerate() {
        AuthUser user = new AuthUser("chris", "1234",
                "kyang3@lakeheadu.ca", true);

        String token = _basicJwtToken.generate(user);
        System.out.println("token: " + token);

        assertTrue(!token.isEmpty());
        assertNotNull(token);
    }

    @Order(2)
    @Test
    @Disabled
    public void testJwtValidateAuthWithoutRoles() throws JsonProcessingException {
        //mock response
        String username = "chris";
        String email = "kyang3@lakeheadu.ca";
        Boolean enabled = true;
        AuthUser user = new AuthUser(username, "1234",
                email, enabled);
        UserStatusDto statusDto = UserStatusDto.builder()
                .status(AuthCommon.LOG_IN.getVal())
                .session(300L)
                .id(1)
                .build();

        //any string[].class will return
        when(_basicAuthClient.validate(any(String[].class))).thenReturn(statusDto);

        String token = _basicJwtToken.generate(user);
        System.out.println("jwt token: " + token);

        assertTrue(!token.isEmpty());
        assertNotNull(token);

        _basicAuthClient.validate(new String[]{email, token});

        Claims payload = _basicJwtToken.validate(token);
        System.out.println("payload: " + payload);

        assertTrue(payload.get(JWT_PAYLOAD_USERNAME).equals(username));
        assertTrue(payload.get(JWT_PAYLOAD_EMAIL).equals(email));

        List<Role> list = (List<Role>) payload.get(JWT_PAYLOAD_AUTH);
        assertTrue(list.isEmpty());

        Long exp = (Long) payload.get(JWT_PAYLOAD_EXP);
        Long nbf = (Long) payload.get(JWT_PAYLOAD_NBF);
        Long iat = (Long) payload.get(JWT_PAYLOAD_IAT);
        assertTrue(nbf.equals(iat));

        Long session = exp - iat;
        assertTrue(session.equals(_basicJwtToken.getJwtDurationSec()));
    }

    @Order(2)
    @Test
    @Disabled
    public void testJwtValidateAuthWithRoles() {
        //mock response
        String username = "chris";
        String email = "kyang3@lakeheadu.ca";
        Boolean enabled = true;
        List<Role> roles = new ArrayList<>(Arrays.asList(
                new Role(AuthCommon.USER.getVal()),
                new Role(AuthCommon.ADMIN.getVal())));
        AuthUser user = new AuthUser(username, "1234",
                email, enabled, roles);
        UserStatusDto statusDto = UserStatusDto.builder()
                .status(AuthCommon.LOG_IN.getVal())
                .session(300L)
                .id(1)
                .build();

        //any string[].class will return
        when(_basicAuthClient.validate(any(String[].class))).thenReturn(statusDto);

        String token = _basicJwtToken.generate(user);
        System.out.println("jwt token: " + token);

        assertTrue(!token.isEmpty());
        assertNotNull(token);

        Claims payload = _basicJwtToken.validate(token);
        System.out.println("payload: " + payload);

        assertTrue(payload.get(JWT_PAYLOAD_USERNAME).equals(username));
        assertTrue(payload.get(JWT_PAYLOAD_EMAIL).equals(email));

        List<Role> list = (List<Role>) payload.get(JWT_PAYLOAD_AUTH);
        assertTrue(!list.isEmpty());
        assertTrue(list.contains(AuthCommon.ADMIN.getVal()));
        assertTrue(list.contains(AuthCommon.USER.getVal()));

        Long exp = (Long) payload.get(JWT_PAYLOAD_EXP);
        Long nbf = (Long) payload.get(JWT_PAYLOAD_NBF);
        Long iat = (Long) payload.get(JWT_PAYLOAD_IAT);
        assertTrue(nbf.equals(iat));

        Long session = exp - iat;
        assertTrue(session.equals(_basicJwtToken.getJwtDurationSec()));

        String issuer = (String) payload.get(JWT_PAYLOAD_ISSUER);
        String subject = (String) payload.get(JWT_PAYLOAD_SUBJECT);
        Set<String> audience = (Set<String>) payload.get(JWT_PAYLOAD_AUDIENCE);
        assertTrue(issuer.equals(JWT_TOKEN_ISSUER));
        assertTrue(subject.equals(JWT_TOKEN_SUBJECT));
        audience.stream().forEach(x -> assertTrue(x.equals(email)));
    }

    @Order(3)
    @Test
    @Disabled
    public void testJwtValidateAuthLogout() {
        //mock response
        String username = "chris";
        String email = "kyang3@lakeheadu.ca";
        Boolean enabled = true;
        List<Role> roles = new ArrayList<>(Arrays.asList(
                new Role(AuthCommon.USER.getVal()),
                new Role(AuthCommon.ADMIN.getVal())));
        AuthUser user = new AuthUser(username, "1234",
                email, enabled, roles);
        UserStatusDto statusDto = UserStatusDto.builder()
                .status(AuthCommon.LOG_OUT.getVal())
                .id(1)
                .build();

        //any string[].class will return
        when(_basicAuthClient.validate(any(String[].class))).thenReturn(statusDto);

        String token = _basicJwtToken.generate(user);
        System.out.println("jwt token: " + token);

        assertTrue(!token.isEmpty());
        assertNotNull(token);

        assertThrows(BadCredentialsException.class, () -> {
            _basicJwtToken.validate(token);
        });
    }

    @Order(4)
    @Test
    @Disabled
    public void testJwtValidateAuthWithNull() {
        //mock response
        String username = "chris";
        String email = "kyang3@lakeheadu.ca";
        Boolean enabled = true;
        List<Role> roles = new ArrayList<>(Arrays.asList(
                new Role(AuthCommon.USER.getVal()),
                new Role(AuthCommon.ADMIN.getVal())));
        AuthUser user = new AuthUser(username, "1234",
                email, enabled, roles);

        //any string[].class will return
        when(_basicAuthClient.validate(any(String[].class))).thenReturn(null);

        String token = _basicJwtToken.generate(user);
        System.out.println("jwt token: " + token);

        assertTrue(!token.isEmpty());
        assertNotNull(token);

        assertThrows(BadCredentialsException.class, () -> {
            _basicJwtToken.validate(token);
        });
    }
}