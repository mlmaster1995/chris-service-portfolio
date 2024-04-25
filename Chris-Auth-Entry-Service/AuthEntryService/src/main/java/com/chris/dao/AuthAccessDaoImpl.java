package com.chris.dao;


import com.chris.Exception.AppAuthException;
import com.chris.entity.AuthCommon;
import com.chris.entity.AuthUser;
import com.chris.entity.Role;
import com.chris.entity.UserStatus;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
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
 * dao impl for the auth user access
 */
@Repository(value = AUTH_ACCESS_DAO_BEAN)
public class AuthAccessDaoImpl implements AuthAccessDao {
    private final Logger _LOG = LoggerFactory.getLogger(AuthAccessDaoImpl.class);

    private final String GET_COUNT_AUTH_USER = "SELECT COUNT(*) FROM AuthUser";
    private final String GET_COUNT_TYPICAL_USER = "SELECT COUNT(*) FROM AuthUser WHERE email=:data";
    private final String GET_AUTH_USER = "FROM AuthUser";
    private final String GET_AUTH_USER_BY_EMAIL = "FROM AuthUser WHERE email=:data";
    private final String GET_AUTH_USER_BY_NAME = "FROM AuthUser WHERE username=:data";
    private final String GET_USER_ROLE_COUNT = "SELECT COUNT(*) FROM users_roles WHERE user_id=%s AND role_id=%s";
    private final String INSERT_USER_ROLE_MAP = "INSERT INTO users_roles VALUES (%s, %s)";
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
            throw new AppAuthException("fails to load all roles from db: " + exp);
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
            throw new AppAuthException("fails to get all members...: " + exp);
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
                throw new AppAuthException(String.format("invalid page number(%s) & count(%s)...",
                        startPage, pageCount));
            }

            TypedQuery<AuthUser> query = _manager.createQuery(GET_AUTH_USER, AuthUser.class);
            query.setFirstResult(_defaultPageSize * startPage);
            query.setMaxResults(_defaultPageSize * pageCount);

            authUsers = query.getResultList();
        } catch (Exception exp) {
            throw new AppAuthException("fails to get all users...: " + exp);
        }

        return authUsers;
    }

    @Override
    public List<AuthUser> findUserByName(String username) {
        List<AuthUser> users = new ArrayList<>();
        try {
            TypedQuery<AuthUser> theQuery = _manager.createQuery(GET_AUTH_USER_BY_NAME, AuthUser.class);

            theQuery.setParameter(DEFAULT_DATA, username);

            users = theQuery.getResultList();
        } catch (Exception exp) {
            throw new AppAuthException("fails to find the auth user by username:" + exp);
        }
        return users;
    }

    @Override
    public AuthUser findUserById(Integer id) {
        AuthUser user = null;
        try {
            user = _manager.find(AuthUser.class, id);
        } catch (Exception exp) {
            throw new AppAuthException("fails to find the auth user with id: " + exp);
        }

        return user;
    }

    @Override
    public AuthUser findUserByEmail(String email) {
        AuthUser user = null;
        try {
            TypedQuery<AuthUser> query = _manager.createQuery(GET_AUTH_USER_BY_EMAIL, AuthUser.class);

            query.setParameter(DEFAULT_DATA, email);

            user = query.getSingleResult();

            if (user == null) {
                throw new AppAuthException(String.format("auth user with email(%s) not exists...", email));
            }
        } catch (Exception exp) {
            throw new AppAuthException("fails to find the member with email...: " + exp);
        }

        return user;
    }

    /**
     * persisting user with role will throw exception for duplicate role, so updating the relationship between role
     * and auth user should be done in the service layer, NOT dao layer
     *
     * @param user
     */
    @Override
    @Transactional
    public void saveAuthUser(AuthUser user) {
        try {
            _manager.persist(user);
            _LOG.warn("auth user entity({}) is persisted", user.toString());
        } catch (Exception exp) {
            throw new AppAuthException("failed to persist the auth user...: " + exp);
        }
    }

    /**
     * link a role to a user in the user_role table
     *
     * @param user
     * @param roleType
     */
    @Override
    @Transactional
    public void updateUserRole(AuthUser user, AuthCommon roleType) {
        try {
            AuthUser entity = findUserByEmail(user.getEmail());
            Integer userId = entity.getId();
            Integer roleId = _roleCache.get(roleType.getVal());
            Query userRoleExistQuery =
                    _manager.createNativeQuery(String.format(GET_USER_ROLE_COUNT, userId, roleId));
            if ((Long) userRoleExistQuery.getSingleResult() == 0L) {
                Query insertUserRoleQuery =
                        _manager.createNativeQuery(String.format(INSERT_USER_ROLE_MAP, userId, roleId));
                insertUserRoleQuery.executeUpdate();
                _LOG.warn("auth_user ({}) is assigned with role({}) for auth access...", user, roleType.getVal());
            } else {
                _LOG.warn("auth_user ({}) is assigned with role({}) already...", user, roleType.getVal());
            }
        } catch (Exception exp) {
            throw new AppAuthException("fails to update user-role: " + exp);
        }
    }

    /**
     * to update an auth_user, the original id must be given
     * <p>
     * merging original id to the new auth user should be done in service layer
     *
     * @param user
     */
    @Override
    @Transactional
    public void updateAuthUser(AuthUser user) {
        try {
            if (user.getId() == 0) {
                throw new AppAuthException("invalid auth user to update, missing id...");
            }

            _manager.merge(user);
        } catch (Exception exp) {
            throw new AppAuthException("fails to update the auth user: " + exp);
        }
    }

    //ToDo: add session to the user status for JWT token; add all test cases for login/logout logics
    @Override
    @Transactional
    public void updateUserStatus(UserStatus status) {

    }

    @Override
    @Transactional
    public void deleteAuthUser(AuthUser user) {

    }
}
