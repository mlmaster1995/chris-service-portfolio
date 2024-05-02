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
package com.chris.access;

import com.chris.dao.AuthAccessDao;
import com.chris.dto.AuthUserDto;
import com.chris.exception.AuthServiceException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_DAO_BEAN;
import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_PROCESS_BEAN;
import static com.chris.util.AuthAccessConstants.BCRYPT_ENCODER_BEAN;
import static com.chris.util.AuthClientUtil.validateEmailAddress;

/**
 * service for regular auth access like register, login, logout
 */
@Service(value = AUTH_ACCESS_PROCESS_BEAN)
public class AuthAccessProcessorImpl implements AuthAccessProcessor {
    private Logger _LOG = LoggerFactory.getLogger(AuthAccessProcessorImpl.class);

    private final AuthAccessDao _accessDao;
    private final PasswordEncoder _encoder;

    @Value("${app.auth.encoder.enabled:true}")
    private boolean _encoderEnabled;

    @Autowired
    public AuthAccessProcessorImpl(
            @Qualifier(value = AUTH_ACCESS_DAO_BEAN) AuthAccessDao accessDao,
            @Qualifier(value = BCRYPT_ENCODER_BEAN) PasswordEncoder encoder) {
        _accessDao = accessDao;
        _encoder = encoder;
    }

    @PostConstruct
    public void postConstruct() {
        _LOG.warn("{} is constructed...", AUTH_ACCESS_PROCESS_BEAN);
        _LOG.warn("password encoder enabled: {}", _encoderEnabled);
    }

    /**
     * persist new user into db without any authentication
     *
     * @param userDto
     */
    @Override
    @Transactional
    public void register(AuthUserDto userDto) {
        try {
            if (!userDto.isValid()) {
                throw new AuthServiceException("auth user has invalid data...");
            }

            validateEmailAddress(userDto.getEmail());

            if (!_userExists(userDto)) {
                //encode the password
                if (_encoderEnabled) {
                    String encryptedPassword = _encoder.encode(userDto.getPassword());
                    userDto.setPassword(encryptedPassword);
                }
                _accessDao.saveAuthUser(userDto.toEntity());
            } else {
                throw new AuthServiceException("auth user with same email exists already...");
            }
        } catch (Exception exp) {
            throw new AuthServiceException("fails to register the user at service layer: " + exp);
        }

    }

    /**
     * check if the user exists in the system or not
     *
     * @param userDto
     * @return
     */
    private boolean _userExists(AuthUserDto userDto) {
        return _accessDao.sameUserExists(userDto.getEmail());
    }

}
