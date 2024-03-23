package com.chris.dao;

import com.chris.entity.GymMemberEntity;
import com.chris.exception.AppServiceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.chris.util.AppBeanConstant.MEMBER_DAO_IMPL_BEAN;

/**
 * use entity manager loaded by spring-data-jpa to build the dao layer operation at low-level
 */
@Repository(value = MEMBER_DAO_IMPL_BEAN)
public class MemberDaoImpl implements MemberDao {
    private Logger _LOG = LoggerFactory.getLogger(MemberDaoImpl.class);

    private final EntityManager _manager;

    @Autowired
    public MemberDaoImpl(EntityManager entityManager) {
        this._manager = entityManager;
    }

    //ToDo: add pagination

    @Override
    public List<GymMemberEntity> findAllMembers() {
        List<GymMemberEntity> memberEntities = new ArrayList<>();
        try {
            TypedQuery<GymMemberEntity> query = _manager.createQuery("from GymMemberEntity", GymMemberEntity.class);
            memberEntities = query.getResultList();
        } catch (Exception exp) {
            throw new AppServiceException("fails to get all members: " + exp);
        }

        return memberEntities;
    }

    @Override
    public GymMemberEntity findMemberById(Integer memberId) {
        GymMemberEntity member = null;
        try {
            member = _manager.find(GymMemberEntity.class, memberId);
        } catch (Exception exp) {
            throw new AppServiceException("fails to find the member with id..." + memberId);
        }

        return member;
    }

    @Override
    public GymMemberEntity findMemberByEmail(String email) {
        GymMemberEntity entity = null;
        try {
            TypedQuery<GymMemberEntity> query =
                    _manager.createQuery("from GymMemberEntity where email=:theEmail", GymMemberEntity.class);

            query.setParameter("theEmail", email);

            entity = query.getSingleResult();

            if (entity == null) {
                throw new AppServiceException(String.format("member with email(%s) not exists...", email));
            }
        } catch (Exception exp) {
            throw new AppServiceException("fails to find the member with email: " + exp);
        }
        return entity;
    }

    @Override
    @Transactional
    public void saveMember(GymMemberEntity member) {
        try {
            _manager.persist(member);
            _LOG.warn("gym member entity({}) is persisted", member.toString());
        } catch (Exception exp) {
            throw new AppServiceException("failed to persist the member: " + exp);
        }
    }

    @Override
    @Transactional
    public void updateMember(GymMemberEntity employee) {
        try {
            _manager.merge(employee);
        } catch (Exception exp) {
            throw new AppServiceException("fails to update the member:" + exp);
        }
    }

    @Override
    @Transactional
    public void deleteMemberById(Integer memberId) {
        try {
            GymMemberEntity entity = this.findMemberById(memberId);
            _manager.remove(entity);
        } catch (Exception exp) {
            throw new AppServiceException("fails to delete member by id: " + exp);
        }
    }
}





























