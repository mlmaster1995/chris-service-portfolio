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
package com.chris.cao;

import com.chris.entity.AuthUser;
import com.chris.exception.AuthClientException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import static com.chris.util.AuthClientConstant.AUTH_ACCESS_CAO_BEAN;

/**
 * cache operations layer impl
 */
@Repository(value = AUTH_ACCESS_CAO_BEAN)
public class AuthUserCaoImpl implements AuthAccessCao {
    private Logger _LOG = LoggerFactory.getLogger(AuthUserCaoImpl.class);

    private final String AUTH_CACHE_KEY = "auth_user";
    private final RedisTemplate _operations;
    private HashOperations _hashOperation;

    @Autowired
    public AuthUserCaoImpl(RedisTemplate operations) {
        _operations = operations;
    }

    @PostConstruct
    public void postCacheRepoInit() {
        _LOG.warn("{} is constructed...");
    }

    @Override
    public void cacheAuthUser(AuthUser user) {
        try {
            _hashOperation.put(AUTH_CACHE_KEY, user.getEmail(), user);

            _LOG.warn("auth user is cached({}:{})", user.getEmail(), user);
        } catch (Exception exp) {
            throw new AuthClientException("fails to cache auth user: " + exp);
        }
    }

    @Override
    public AuthUser fetchAuthUserByEmail(String email) {
        AuthUser user = null;
        try {
            user = (AuthUser) _hashOperation.get(AUTH_CACHE_KEY, email);

            if (user == null) {
                throw new AuthClientException(
                        String.format("auth user with email({}) not found...", email));
            }

            _LOG.warn("auth user is cached({}:{})", user.getEmail(), user);
        } catch (Exception exp) {
            throw new AuthClientException("fails to fetch auth user by email: " + exp);
        }

        return user;
    }
}
