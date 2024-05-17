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
package com.chris.api;

import com.chris.access.AuthAccessProcessor;
import com.chris.dto.AuthUserDto;
import com.chris.dto.UserStatusDto;
import com.chris.exception.AuthServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_CONTROL_BEAN;
import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_PROCESS_BEAN;

@RestController(value = AUTH_ACCESS_CONTROL_BEAN)
@RequestMapping("/api/v1/auth")
public class AuthAccessController extends BaseController<ResponseEntity<Object>> {
    private Logger _LOG = LoggerFactory.getLogger(AuthAccessController.class);

    private final ObjectMapper _mapper;
    private final AuthAccessProcessor _processor;


    @Autowired
    public AuthAccessController(
            @Qualifier(value = AUTH_ACCESS_PROCESS_BEAN) AuthAccessProcessor processor,
            ObjectMapper mapper) {
        _mapper = mapper;
        _processor = processor;
    }

    @PostConstruct
    public void postConstruct() {
        _LOG.warn("{} is constructed...", AUTH_ACCESS_CONTROL_BEAN);
    }


    /**
     * without any authentication
     *
     * @param userDto
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(@RequestBody AuthUserDto userDto) {
        ResponseEntity<String> responseEntity = null;

        try {
            _processor.register(userDto);

            String repMsg = String.format("user with email (%s) is registered successfully",
                    userDto.getEmail());
            _LOG.warn(repMsg);
            responseEntity = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(repMsg);

        } catch (Exception exp) {
            String errMsg = String.format("fails to register user with email(%s): %s",
                    userDto.getEmail(), exp);
            _LOG.error(errMsg);
            responseEntity = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errMsg);
        }

        return responseEntity;
    }

    /**
     * with authentication and authorization
     *
     * @param authentication
     * @return
     */
    @GetMapping("/login")
    public ResponseEntity<String> userLogin(Authentication authentication) {
        ResponseEntity<String> responseEntity = null;

        try {
            if (authentication == null) {
                throw new AuthServiceException("auth is null");
            }

            String jwtToken = _processor.login(authentication.getName());
            _LOG.warn("basic jwt token is generated for user with email({})", authentication.getName());

            responseEntity = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(jwtToken);
        } catch (Exception exp) {
            String errMsg = String.format("fails to log in user: %s", exp);

            _LOG.error(errMsg);

            responseEntity = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errMsg);
        }

        return responseEntity;
    }

    /**
     * with authentication and authorization
     *
     * @param authentication
     * @return
     */
    @GetMapping("/logout")
    public ResponseEntity<String> userLogout(Authentication authentication) {
        ResponseEntity<String> responseEntity = null;

        try {
            _processor.logout(authentication.getName());

            String repMsg = String.format("user with email (%s) log out successfully",
                    authentication.getName());

            _LOG.warn(repMsg);
            responseEntity = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(repMsg);

        } catch (Exception exp) {
            String errMsg = String.format("fails to log out user: %s", exp);

            _LOG.error(errMsg);

            responseEntity = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errMsg);
        }

        return responseEntity;
    }

    /**
     * simple endpoint to test jwt token
     *
     * @return
     */
    @GetMapping("/token")
    public ResponseEntity<String> validateJWT() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("token is valid and here is DATA!");
    }

    /**
     * user status endpoint for backend service to call validating the JWT token
     *
     * @param emailJson
     * @return
     */
    @PostMapping("/status")
    public ResponseEntity<UserStatusDto> validateUserStatus(@RequestBody String emailJson) {
        UserStatusDto status = null;

        try {
            JsonNode node = _mapper.readTree(emailJson);
            String email = node.get("email").asText();

            status = _processor.getUserStatusByEmail(email);
            _LOG.warn("user status is fetched: {}", status.toString());
        } catch (Exception exp) {
            _LOG.error("fails to find the user status by email({}) at the controller layer...",
                    emailJson);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
