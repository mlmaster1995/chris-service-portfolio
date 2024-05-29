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
package com.chris.auth;

import com.chris.exception.AuthSchedulerException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.chris.util.AuthSchedulerConstants.AUTH_USER_STATUS_CHECK_BEAN;

/**
 * scheduled task to check auth user status periodically
 */
@Component(value = AUTH_USER_STATUS_CHECK_BEAN)
public class AuthUserStatusTask implements AuthTask {
    private Logger _LOG = LoggerFactory.getLogger(AuthUserStatusTask.class);

    private final String FLIP_USER_STATUS_TO_LOGOUT =
            "UPDATE user_status SET status = 'LOG_OUT', session = null, logout_timestamp=NOW() " +
                    "WHERE status = 'LOG_IN' AND UNIX_TIMESTAMP(login_timestamp) + session < UNIX_TIMESTAMP(NOW());";

    private final EntityManager _manager;

    @Value("${app.auth.user.status.check.cron}")
    private String _authUserStatusCheckCron;

    @Autowired
    public AuthUserStatusTask(EntityManager manager) {
        _manager = manager;
    }

    @PostConstruct
    public void postInit() {
        _LOG.warn("user status check cron: {}", _authUserStatusCheckCron);
        _LOG.warn("{} is constructed and all scheduled tasks starts....", AUTH_USER_STATUS_CHECK_BEAN);
    }

    @Scheduled(cron = "${app.auth.user.status.check.cron}")
    @Transactional
    public void checkUserStatus() {
        try {
            _flipLoginUserStatus();

            _LOG.warn("user status is checked for flipping at {}", new Date());
        } catch (Exception exp) {
            _LOG.error("fails to check user status task: " + exp);
        }
    }

    private void _flipLoginUserStatus() {
        try {
            Query flipLoginStatusQuery = _manager.createNativeQuery(FLIP_USER_STATUS_TO_LOGOUT);

            flipLoginStatusQuery.executeUpdate();
        } catch (Exception exp) {
            throw new AuthSchedulerException("fails to flip user status: " + exp);
        }
    }
}
