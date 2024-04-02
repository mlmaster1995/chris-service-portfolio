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
    public AuthUser findByUserName(String username) {
        AuthUser user = null;
        try {
            TypedQuery<AuthUser> query = _manager.createQuery("from AuthUser where username=:data and enabled=true", AuthUser.class);
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















