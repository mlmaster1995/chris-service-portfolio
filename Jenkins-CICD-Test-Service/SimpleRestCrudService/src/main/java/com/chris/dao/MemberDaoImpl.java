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

package com.chris.dao;

import com.chris.entity.GymMemberEntity;
import com.chris.exception.AppServiceException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.find.member.page.size:100}")
    private Integer _defaultPageSize;

    private final EntityManager _manager;

    @Autowired
    public MemberDaoImpl(EntityManager entityManager) {
        this._manager = entityManager;
    }

    @PostConstruct
    public void postConstruct() {
        _LOG.warn("member dao has default page size at " + _defaultPageSize);
    }

    /**
     * if the total count is over the default page size, it will only return the first page
     *
     * @return
     */
    @Override
    public List<GymMemberEntity> findAllMembers() {
        List<GymMemberEntity> memberEntities = new ArrayList<>();
        try {
            TypedQuery<Long> countQuery = _manager.createQuery("select count(*) from GymMemberEntity", Long.class);
            Long totalCount = countQuery.getSingleResult();
            if (totalCount > _defaultPageSize) {
                memberEntities = findAllMembers(0, 1);
                _LOG.warn("all member count is over the default page size at {}, only the data at 1st page is returned...",
                        _defaultPageSize);
            } else {
                TypedQuery<GymMemberEntity> query = _manager.createQuery("from GymMemberEntity", GymMemberEntity.class);
                memberEntities = query.getResultList();
                _LOG.warn("all member count is under the default page size at {}, and all data is returned...",
                        _defaultPageSize);
            }
        } catch (Exception exp) {
            throw new AppServiceException("fails to get all members...: " + exp);
        }

        return memberEntities;
    }

    @Override
    public List<GymMemberEntity> findAllMembers(int startPageNumber, int totalPageCount) {
        List<GymMemberEntity> memberEntities = new ArrayList<>();

        try {
            if (startPageNumber < 0 || totalPageCount <= 0) {
                throw new AppServiceException(String.format("invalid page number(%s) & count(%s)...", startPageNumber, totalPageCount));
            }

            TypedQuery<GymMemberEntity> query = _manager.createQuery("from GymMemberEntity", GymMemberEntity.class);
            query.setFirstResult(_defaultPageSize * startPageNumber);
            query.setMaxResults(_defaultPageSize * totalPageCount);

            memberEntities = query.getResultList();
        } catch (Exception exp) {
            throw new AppServiceException("fails to get all members...: " + exp);
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
            throw new AppServiceException("fails to find the member with email...: " + exp);
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
            throw new AppServiceException("failed to persist the member...: " + exp);
        }
    }

    @Override
    @Transactional
    public void updateMember(GymMemberEntity member) {
        try {
            _manager.merge(member);
            _LOG.warn("gym member entity({}) is updated", member.toString());
        } catch (Exception exp) {
            throw new AppServiceException("fails to update the member...:" + exp);
        }
    }

    @Override
    @Transactional
    public void deleteMemberById(Integer memberId) {
        try {
            GymMemberEntity entity = this.findMemberById(memberId);
            _manager.remove(entity);
            _LOG.warn("gym member entity with id({}) is removed", memberId);
        } catch (Exception exp) {
            throw new AppServiceException("fails to delete member by id...: " + exp);
        }
    }
}





























