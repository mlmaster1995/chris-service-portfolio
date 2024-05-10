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
package com.chris.dao;


import com.chris.entity.AuthUser;
import com.chris.entity.Role;
import com.chris.entity.UserStatus;
import com.chris.exception.AuthServiceException;
import com.chris.util.AuthCommon;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_DAO_BEAN;

/**
 * dao impl with hibernate for '/api/v1/auth/*'
 */
@Repository(value = AUTH_ACCESS_DAO_BEAN)
public class AuthAccessDaoImpl implements AuthAccessDao {
    private final Logger _LOG = LoggerFactory.getLogger(AuthAccessDaoImpl.class);

    private final String GET_COUNT_AUTH_USER = "SELECT COUNT(*) FROM AuthUser";
    private final String GET_AUTH_USER = "FROM AuthUser";
    private final String GET_AUTH_USER_BY_EMAIL = "FROM AuthUser WHERE email=:data";
    private final String GET_AUTH_USER_BY_NAME = "FROM AuthUser WHERE username=:data";
    private final String GET_USER_ROLE_COUNT = "SELECT COUNT(*) FROM users_roles WHERE user_id=%s AND role_id=%s";
    private final String INSERT_USER_ROLE_MAP = "INSERT INTO users_roles VALUES (%s, %s)";
    private final String UPDATE_LOGIN_STATUS =
            "UPDATE user_status SET status='%s',login_timestamp=current_timestamp,session='%s' " +
                    "WHERE user_id='%s' " +
                    "AND (UNIX_TIMESTAMP(login_timestamp)+session<UNIX_TIMESTAMP(NOW()))" +
                    "AND status = 'LOG_OUT' " +
                    "AND session != null";
    private final String UPDATE_LOGOUT_STATUS =
            "UPDATE user_status SET status='%s',logout_timestamp=current_timestamp,session=null " +
                    "WHERE user_id='%s' " +
                    "AND status = 'LOG_IN'";
    private final String FLIP_USER_STATUS_TO_LOGOUT =
            "UPDATE user_status SET status = 'LOG_OUT', session = null, logout_timestamp=now() " +
                    "WHERE status = 'LOG_IN' AND UNIX_TIMESTAMP(login_timestamp) + session < UNIX_TIMESTAMP(NOW());";
    private final String UPDATE_USER_STATUS_ATOMIC =
            "UPDATE user_status SET status='%s', session=%s, login_timestamp=%s, " +
                    "logout_timestamp=%s WHERE user_id=(SELECT id FROM auth_user WHERE email= %s);";

    private final String GET_USER_COUNT = "SELECT count(*) FROM auth_user WHERE email='%s'";

    private final String DEFAULT_DATA = "data";

    private Map<String, Integer> _roleCache;

    @Value("${app.find.users.page.size:100}")
    private Integer _defaultPageSize;

    private final EntityManager _manager;

    @Autowired
    public AuthAccessDaoImpl(EntityManager manager) {
        _manager = manager;
        _roleCache = new HashMap<>();
    }

    @PostConstruct
    public void postConstruct() {
        _LOG.warn("{} is constructed...", AUTH_ACCESS_DAO_BEAN);

        _cacheAllRoles();
        _LOG.warn("all roles are cached...");
    }

    private void _cacheAllRoles() {
        try {
            TypedQuery<Role> query = _manager.createQuery("from Role", Role.class);
            List<Role> roles = query.getResultList();

            roles.stream().forEach(role -> {
                Integer id = role.getId();
                String name = role.getName();
                if (!_roleCache.containsKey(name)) {
                    _roleCache.put(name, id);
                }
            });
        } catch (Exception exp) {
            throw new AuthServiceException("fails to load all roles from db: " + exp);
        }
    }

    @Override
    public List<AuthUser> findAllUsers() {
        List<AuthUser> authUsers = new ArrayList<>();
        try {
            TypedQuery<Long> countQuery = _manager.createQuery(GET_COUNT_AUTH_USER, Long.class);
            Long totalCount = countQuery.getSingleResult();
            if (totalCount > _defaultPageSize) {
                authUsers = findAllUsers(0, 1);
                _LOG.warn("all auth user is over the default page size at {}, only the data at 1st page is returned...",
                        _defaultPageSize);
            } else {
                TypedQuery<AuthUser> query = _manager.createQuery("from AuthUser", AuthUser.class);
                authUsers = query.getResultList();
                _LOG.warn("all auth user is under the default page size at {}, and all data is returned...",
                        _defaultPageSize);
            }
        } catch (Exception exp) {
            throw new AuthServiceException("fails to get all members...: " + exp);
        }

        return authUsers;
    }

    /**
     * startPage is from 0
     *
     * @param startPage
     * @param pageCount
     * @return
     */
    @Override
    public List<AuthUser> findAllUsers(int startPage, int pageCount) {
        List<AuthUser> authUsers = new ArrayList<>();

        try {
            if (startPage < 0 || pageCount <= 0) {
                throw new AuthServiceException(String.format("invalid page number(%s) & count(%s)...",
                        startPage, pageCount));
            }

            TypedQuery<AuthUser> query = _manager.createQuery(GET_AUTH_USER, AuthUser.class);
            query.setFirstResult(_defaultPageSize * startPage);
            query.setMaxResults(_defaultPageSize * pageCount);

            authUsers = query.getResultList();
        } catch (Exception exp) {
            throw new AuthServiceException("fails to get all users...: " + exp);
        }

        return authUsers;
    }

    /**
     * if username not exists, not throw exceptions
     *
     * @param username
     * @return
     */
    @Override
    public List<AuthUser> findUserByName(String username) {
        List<AuthUser> users = new ArrayList<>();
        try {
            TypedQuery<AuthUser> theQuery = _manager.createQuery(GET_AUTH_USER_BY_NAME, AuthUser.class);

            theQuery.setParameter(DEFAULT_DATA, username);

            users = theQuery.getResultList();
        } catch (Exception exp) {
            throw new AuthServiceException("fails to find the auth user by username:" + exp);
        }
        return users;
    }

    @Override
    public AuthUser findUserById(Integer id) {
        AuthUser user = null;
        try {
            user = _manager.find(AuthUser.class, id);
        } catch (Exception exp) {
            throw new AuthServiceException("fails to find the auth user with id: " + exp);
        }

        return user;
    }

    /**
     * get single result will throw exception
     *
     * @param email
     * @return
     */
    @Override
    public AuthUser findUserByEmail(String email) {
        return findUserByEmail(email, false);
    }

    @Override
    public AuthUser findUserByEmail(String email, boolean lock) {
        AuthUser user = null;
        try {
            TypedQuery<AuthUser> query = _manager.createQuery(GET_AUTH_USER_BY_EMAIL, AuthUser.class);

            query.setParameter(DEFAULT_DATA, email);

            if (lock) {
                query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            }

            user = query.getSingleResult();

            if (user == null) {
                throw new AuthServiceException(String.format("auth user with email(%s) not exists...", email));
            }
        } catch (Exception exp) {
            throw new AuthServiceException("fails to find the member with email: " + exp);
        }

        return user;
    }


    /**
     * check if the user with same email exists
     *
     * @param email
     * @return
     */
    @Override
    public boolean sameUserExists(String email) {
        long count = 0L;
        try {
            Query userCountQuery =
                    _manager.createNativeQuery(String.format(GET_USER_COUNT, email));

            count = (Long) userCountQuery.getSingleResult();
        } catch (Exception exp) {
            throw new AuthServiceException("fails to find the user count by email: " + exp);
        }
        return count == 1L;
    }

    /**
     * persisting user with role will throw exception for duplicate role, so updating the relationship between role
     * and auth user should be done in the service layer, NOT dao layer
     *
     * @param user
     */
    @Override
    @Transactional
    public Integer saveAuthUser(AuthUser user) {
        try {
            //save user with default status
            if (user.getStatus() == null) {
                user.setStatus(
                        new UserStatus(AuthCommon.LOG_OUT.getVal(), user));
            }
            _manager.persist(user);

            //sync db with the context
            _manager.flush();

            //for testing
            if (_roleCache.size() == 0) {
                _cacheAllRoles();
            }

            //link user to role into db
            Integer userId = user.getId();
            Integer roleId = _roleCache.get(AuthCommon.USER.getVal());
            Query userRoleExistQuery =
                    _manager.createNativeQuery(String.format(GET_USER_ROLE_COUNT, userId, roleId));
            if ((Long) userRoleExistQuery.getSingleResult() == 0L) {
                Query insertUserRoleQuery =
                        _manager.createNativeQuery(String.format(INSERT_USER_ROLE_MAP, userId, roleId));

                insertUserRoleQuery.executeUpdate();

                _LOG.warn("auth_user with email ({}) is assigned with role({}) for auth access...",
                        user.getEmail(), AuthCommon.USER.getVal());
            } else {
                _LOG.warn("auth_user with email ({}) is assigned with role({}) already...",
                        user.getEmail(), AuthCommon.USER.getVal());
            }
        } catch (Exception exp) {
            throw new AuthServiceException("failed to persist the auth user: " + exp);
        }

        return user.getId();
    }

    /**
     * link a role to a user in the user_role table manually
     *
     * @param user
     * @param roleType
     */
    @Override
    @Transactional
    public void updateUserRole(AuthUser user, AuthCommon roleType) {
        try {
            if (user.getId() <= 0) {
                throw new AuthServiceException("invalid auth user to update, missing id...");
            }

            //for testing
            if (_roleCache.size() == 0) {
                _cacheAllRoles();
            }

            //link user with the role
            Integer userId = user.getId();
            Integer roleId = _roleCache.get(roleType.getVal());
            Query userRoleExistQuery =
                    _manager.createNativeQuery(String.format(GET_USER_ROLE_COUNT, userId, roleId));
            if ((Long) userRoleExistQuery.getSingleResult() == 0L) {
                Query insertUserRoleQuery =
                        _manager.createNativeQuery(String.format(INSERT_USER_ROLE_MAP, userId, roleId));
                insertUserRoleQuery.executeUpdate();
                _LOG.warn("auth_user with email ({}) is assigned with role({}) for auth access...", user.getEmail(), roleType.getVal());
            } else {
                _LOG.warn("auth_user with email ({}) is assigned with role({}) already...", user.getEmail(), roleType.getVal());
            }
        } catch (Exception exp) {
            throw new AuthServiceException("fails to update user-role: " + exp);
        }
    }

    /**
     * to update an auth_user, the original id must be given
     * <p>
     * merging original id to the new auth user should be done in service layer
     * <p>
     * update login/logout status, role list all here
     *
     * @param user
     */
    @Override
    @Transactional
    public void updateAuthUser(AuthUser user) {
        try {
            if (user.getId() <= 0) {
                throw new AuthServiceException("invalid auth user to update, missing id...");
            }

            _manager.merge(user);
        } catch (Exception exp) {
            throw new AuthServiceException("fails to update the auth user: " + exp);
        }
    }

    /**
     * update status manually
     *
     * @param status
     */
    @Override
    @Transactional
    public void updateUserStatus(UserStatus status) {
        try {
            if (status.getAuthUser().getId() <= 0) {
                throw new AuthServiceException("invalid auth user to update status, missing id...");
            }

            if (status.getStatus().equals(AuthCommon.LOG_IN.getVal())) {
                Query loginUpdateQuery = _manager.createNativeQuery(String.format(UPDATE_LOGIN_STATUS,
                        status.getStatus(), status.getSession(), status.getAuthUser().getId()));
                loginUpdateQuery.executeUpdate();

                _manager.flush();
                _LOG.warn("login status is updated as {}", status.toString());
            } else if (status.getStatus().equals(AuthCommon.LOG_OUT.getVal())) {
                Query logoutUpdateQuery = _manager.createNativeQuery(String.format(UPDATE_LOGOUT_STATUS,
                        status.getStatus(), status.getSession(), status.getAuthUser().getId()));
                logoutUpdateQuery.executeUpdate();

                _manager.flush();
                _LOG.warn("logout status is updated as {}", status.toString());
            } else {
                throw new AuthServiceException("invalid user status value...");
            }
        } catch (Exception exp) {
            throw new AuthServiceException("fails to update the user status: " + exp);
        }
    }

    @Override
    @Transactional
    public void flipLoginUserStatus() {
        try {
            Query flipLoginStatusQuery = _manager.createNativeQuery(FLIP_USER_STATUS_TO_LOGOUT);
            flipLoginStatusQuery.executeUpdate();
        } catch (Exception exp) {
            throw new AuthServiceException("fails to flip user status: " + exp);
        }
    }

    @Override
    @Transactional
    public void updateUserStatusAtomic(String email, AuthCommon status, Long session) {
        try {
            String updateQuery = null;

            if (status.equals(AuthCommon.LOG_IN)) {
                updateQuery = String.format(UPDATE_LOGOUT_STATUS, status.getVal(), session,
                        "NOW()", null, email);
            } else if (status.equals(AuthCommon.LOG_OUT)) {
                updateQuery = String.format(UPDATE_LOGOUT_STATUS, status.getVal(), session,
                        null, "NOW()", email);
            } else {
                throw new AuthServiceException(String.format("invalid user status: %s", status.getVal()));
            }


            Query flipLoginStatusQuery = _manager.createNativeQuery(UPDATE_USER_STATUS_ATOMIC);
            flipLoginStatusQuery.executeUpdate();

            _LOG.warn("user status update query: {}", updateQuery);
        } catch (Exception exp) {
            throw new AuthServiceException("fails to update user status: " + exp);
        }
    }

    @Override
    @Transactional
    public void deleteAuthUserById(Integer userId) {
        try {
            if (userId == null) {
                throw new AuthServiceException("user id is null...");
            }

            AuthUser user = findUserById(userId);
            _manager.remove(user);
            _LOG.warn("auth user({}) is removed...", user.toString());
        } catch (Exception exp) {
            throw new AuthServiceException("fails to delete auth user by id: " + exp);
        }
    }

    @Override
    @Transactional
    public void deleteAuthUserByEmail(String email) {
        try {
            if (email == null || email.isEmpty()) {
                throw new AuthServiceException("invalid email...");
            }

            AuthUser user = findUserByEmail(email);
            _manager.remove(user);
            _LOG.warn("auth user({}) is removed...", user.toString());
        } catch (Exception exp) {
            throw new AuthServiceException("fails to delete auth user by id: " + exp);
        }
    }
}
