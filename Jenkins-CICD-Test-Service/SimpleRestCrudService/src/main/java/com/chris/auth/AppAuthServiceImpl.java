/**
 * MIT License
 *
 * Copyright (c) 2024 Chris Yang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.chris.auth;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.chris.util.AppBeanConstant.APP_AUTH_DAO_BEAN;
import static com.chris.util.AppBeanConstant.APP_AUTH_SERVICE_BEAN;

@Service(value = APP_AUTH_SERVICE_BEAN)
public class AppAuthServiceImpl implements AppAuthService {
    private Logger _LOG = LoggerFactory.getLogger(AppAuthServiceImpl.class);
    private final AppAuthDao _authDao;

    @Autowired
    public AppAuthServiceImpl(
            @Qualifier(value = APP_AUTH_DAO_BEAN) AppAuthDao authDao) {
        _authDao = authDao;
    }

    @PostConstruct
    public void postConstruct() {
        _LOG.warn("{} is initiated...", APP_AUTH_SERVICE_BEAN);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AuthUser authUser = findUserByUserName(username);

        if (authUser == null) {
            throw new UsernameNotFoundException(
                    String.format("fails to find the username(%s) at the service layer...", username));
        }

        List<SimpleGrantedAuthority> authorities =
                authUser.getRoles().stream().map(x ->
                        new SimpleGrantedAuthority(x.getName())).collect(Collectors.toList());

        return new User(authUser.getUsername(), authUser.getPassword(), authorities);
    }

    @Override
    public AuthUser findUserByUserName(String userName) {
        AuthUser user = null;
        try {
            user = _authDao.findUserByUserName(userName);
        } catch (Exception exp) {
            _LOG.error("fails to find the user at the service layer...:" + exp);
        }

        return user;
    }

    @Override
    public Role findRoleByRoleName(String roleName) {
        Role role = null;

        try {
            role = _authDao.findRoleByRoleName(roleName);
        } catch (Exception exp) {
            _LOG.error("fails to find the role at the service layer...:" + exp);
        }

        return role;
    }
}
























