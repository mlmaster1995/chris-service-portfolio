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
import com.chris.dto.UserStatusDto;
import com.chris.entity.AuthUser;
import com.chris.entity.UserStatus;
import com.chris.exception.AuthServiceException;
import com.chris.token.JwtGenerator;
import com.chris.util.AuthCommon;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_DAO_BEAN;
import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_PROCESS_BEAN;
import static com.chris.util.AuthAccessConstants.BCRYPT_ENCODER_BEAN;
import static com.chris.util.AuthClientConstant.BASIC_AUTH_ACCESS_JWT_BEAN;
import static com.chris.util.AuthClientUtil.validateEmailAddress;

/**
 * service for regular auth access like register, login, logout
 */
@Service(value = AUTH_ACCESS_PROCESS_BEAN)
public class AuthAccessProcessorImpl implements AuthAccessProcessor {
    private Logger _LOG = LoggerFactory.getLogger(AuthAccessProcessorImpl.class);

    private final Long DEFAULT_CHECK_PERIOD = 300L; //default check period is 5-min

    private final AuthAccessDao _accessDao;
    private final PasswordEncoder _encoder;
    private final JwtGenerator _basicAuthAccessJwt;
    private ScheduledExecutorService _executor;

    @Value("${app.auth.user.status.check.sec:300}")
    private Long _userStatusCheckPeriodSec;

    @Value("${app.auth.encoder.enabled:true}")
    private boolean _encoderEnabled;

    @Value("${app.auth.jwt.basic.duration.sec:600}")
    private Long _userLoginSession;

    @Autowired
    public AuthAccessProcessorImpl(
            @Qualifier(value = AUTH_ACCESS_DAO_BEAN) AuthAccessDao accessDao,
            @Qualifier(value = BCRYPT_ENCODER_BEAN) PasswordEncoder encoder,
            @Qualifier(value = BASIC_AUTH_ACCESS_JWT_BEAN) JwtGenerator basicAuthAccessJwt) {
        _accessDao = accessDao;
        _encoder = encoder;
        _basicAuthAccessJwt = basicAuthAccessJwt;
        _executor = Executors.newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    public void postConstruct() {
        if (_userStatusCheckPeriodSec < DEFAULT_CHECK_PERIOD) {
            _userStatusCheckPeriodSec = DEFAULT_CHECK_PERIOD;
            _LOG.warn("user status check period is lower than the min value at {}-sec", DEFAULT_CHECK_PERIOD);
        }
        _executor.scheduleAtFixedRate(new CheckUserStatus(), 0, _userStatusCheckPeriodSec, TimeUnit.SECONDS);
        _LOG.warn("user status check is scheduled with {}-sec/time", _userStatusCheckPeriodSec);

        _LOG.warn("{} is constructed...", AUTH_ACCESS_PROCESS_BEAN);
        _LOG.warn("password encoder enabled: {}", _encoderEnabled);
        _LOG.warn("login user session timeout: {}-sec", _userLoginSession);
        _LOG.warn("user status check period: {}-sec", _userStatusCheckPeriodSec);
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
     * user login -> generate jwt token
     *
     * @param email
     * @return
     */
    @Override
    @Transactional
    public String login(String email) {
        String jwt = null;
        try {
            //fetch user
            AuthUser user = _accessDao.findUserByEmail(email, true);

            //update status
            if (user.getStatus().getStatus().equals(AuthCommon.LOG_OUT.getVal())) {

                _accessDao.updateUserStatusAtomic(email, AuthCommon.LOG_IN, _userLoginSession);

                _LOG.warn("user with email({}) status is updated as {}", email, user.getStatus());
            } else {
                UserStatus status = user.getStatus();
                Date logInTime = status.getLogInTimestamp();
                throw new AuthServiceException(
                        String.format("user with email(%s) login already at %s", email,
                                logInTime.toString()));
            }


            //generate token
            jwt = String.valueOf(_basicAuthAccessJwt.generate(user));
        } catch (Exception exp) {
            throw new AuthServiceException("fails to login user: " + exp);
        }

        return jwt;
    }


    @Override
    @Transactional
    public void logout(String email) {
        try {
            //fetch user
            AuthUser user = _accessDao.findUserByEmail(email, true);

            //update status
            if (user.getStatus().getStatus().equals(AuthCommon.LOG_IN.getVal())) {

                _accessDao.updateUserStatusAtomic(email, AuthCommon.LOG_OUT, null);

                _LOG.warn("user with email({}) status is updated as {}", email, user.getStatus());
            } else {
                throw new AuthServiceException(
                        String.format("user with email(%s) logout already", email));
            }
        } catch (Exception exp) {
            throw new AuthServiceException("fails to log out user: " + exp);
        }
    }

    @Override
    public UserStatusDto getUserStatusByEmail(String email) {
        UserStatusDto status = null;

        try {
            AuthUser user = _accessDao.findUserByEmail(email);
            status = user.getStatus().toDto();
        } catch (Exception exp) {
            throw new AuthServiceException("fails to get the user status at the service layer:" + exp);
        }

        return status;
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


    /**
     * concurrent tasks to check if the user status is expired and the flip
     */
    private class CheckUserStatus implements Runnable {
        @Override
        public void run() {
            try {
                _accessDao.flipLoginUserStatus();
                _LOG.warn("user status is checked for flipping at {}", new Date());
            } catch (Exception exp) {
                _LOG.error("fails to check user status task: " + exp);
            }
        }
    }

}
