package com.chris.dao;

import com.chris.entity.GymMemberEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * use entity manager loaded by spring-data-jpa to build the dao layer operation
 */
@Repository
public class MemberDaoImpl implements MemberDao {
    private Logger _LOG = LoggerFactory.getLogger(MemberDaoImpl.class);

    private final EntityManager _manager;

    @Autowired
    public MemberDaoImpl(EntityManager entityManager) {
        this._manager = entityManager;
    }

    //ToDo: add pagination
    @Override
    public List<GymMemberEntity> getAllMembers() {
        TypedQuery<GymMemberEntity> query = _manager.createQuery("from member", GymMemberEntity.class);
        return query.getResultList();
    }

    @Override
    public GymMemberEntity getMemberById(Long memberId) {
        Session session = _manager.unwrap(Session.class);
        GymMemberEntity employee = session.get(GymMemberEntity.class, memberId);
        return employee;
    }

    @Override
    public Long addMember(GymMemberEntity employee) {
        Session session = _manager.unwrap(Session.class);
        session.saveOrUpdate(employee);
        return 11L;
    }

    @Override
    public GymMemberEntity updateMember(GymMemberEntity employee) {
        Session session = _manager.unwrap(Session.class);

        GymMemberEntity tmpEmployee = getMemberById(employee.getId());
        tmpEmployee.setFirstName(employee.getFirstName());
        tmpEmployee.setLastName(employee.getLastName());
        tmpEmployee.setEmail(employee.getEmail());
        session.update(tmpEmployee);
        return getMemberById(employee.getId());
    }

    @Override
    public int deleteMember(int memberId) {
        Session session = _manager.unwrap(Session.class);
        Query query = session.createQuery("delete from Employee where " +
                "id=:employeeId");
        query.setParameter("employeeId", Long.valueOf(memberId));
        query.executeUpdate();
        return memberId;
    }
}
