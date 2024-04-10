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

import com.chris.exception.AppServiceException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.chris.util.AppBeanConstant.APP_AUTH_DAO_BEAN;

@Repository(value = APP_AUTH_DAO_BEAN)
public class AppAuthDaoImpl implements AppAuthDao {
    private Logger _LOG = LoggerFactory.getLogger(AppAuthDaoImpl.class);
    private final EntityManager _manager;

    @Autowired
    public AppAuthDaoImpl(EntityManager manager) {
        _manager = manager;
    }

    @PostConstruct
    public void postConstruct() {
        _LOG.warn("{} is initiated...", APP_AUTH_DAO_BEAN);
    }

    @Override
    public AuthUser findUserByUserName(String username) {
        AuthUser user = null;
        try {
            TypedQuery<AuthUser> query = _manager.createQuery(
                    "from AuthUser where username=:data and enabled=true", AuthUser.class);
            query.setParameter("data", username);

            user = query.getSingleResult();

        } catch (Exception exp) {
            throw new AppServiceException("fails to find the user: " + exp);
        }

        return user;
    }

    @Override
    public Role findRoleByRoleName(String roleName) {
        Role role = null;
        try {
            TypedQuery<Role> query = _manager.createQuery("from Role where name=:data", Role.class);
            query.setParameter("data", roleName);

            role = query.getSingleResult();
        } catch (Exception exp) {
            throw new AppServiceException("fails to find the role: " + exp);
        }
        return role;
    }
}















