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
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_CONTROL_BEAN;
import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_PROCESS_BEAN;
import static com.chris.util.AuthAccessConstants.BCRYPT_ENCODER_BEAN;

@RestController(value = AUTH_ACCESS_CONTROL_BEAN)
@RequestMapping("/api/v1/auth")
public class AuthAccessController extends BaseController<ResponseEntity<Object>> {
    private Logger _LOG = LoggerFactory.getLogger(AuthAccessController.class);

    private final AuthAccessProcessor _processor;


    @Autowired
    public AuthAccessController(
            @Qualifier(value = AUTH_ACCESS_PROCESS_BEAN) AuthAccessProcessor processor,
            @Qualifier(value = BCRYPT_ENCODER_BEAN) PasswordEncoder encoder) {
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
    public ResponseEntity<Object> registerNewUser(@RequestBody AuthUserDto userDto) {
        ResponseEntity<Object> responseEntity = null;

        try {
            _processor.register(userDto);

            String repMsg = String.format("user with email (%s) is registered successfully", userDto.getEmail());
            _LOG.warn(repMsg);
            responseEntity = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(repMsg);

        } catch (Exception exp) {
            String errMsg = String.format("fails to register user with email(%s): %s", userDto.getEmail(), exp);
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
    public ResponseEntity<Object> userLogin(Authentication authentication) {
        //auth -> update db -> pull data into cache -> return jtw token
        return null;
    }

    /**
     * with authentication and authorization
     *
     * @param authentication
     * @return
     */
    @GetMapping("/logout")
    public ResponseEntity<Object> userLogout(Authentication authentication) {
        //update db -> clear cache -> return logout status
        return null;
    }


}
