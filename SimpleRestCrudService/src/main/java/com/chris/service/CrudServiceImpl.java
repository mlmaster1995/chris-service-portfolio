package com.chris.service;

import com.chris.dao.MemberDao;
import com.chris.entity.GymMemberEntity;
import com.chris.exception.CrudOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * call dao layer to get the dto and return back to client
 */
@Service
public class CrudServiceImpl implements CrudService {

    private MemberDao employeeOperation;

    @Autowired
    public CrudServiceImpl(MemberDao employeeOperation) {
        this.employeeOperation = employeeOperation;
    }

    @Override
    public List<GymMemberEntity> getAllEmployees() {
        return employeeOperation.getAllMembers();
    }

    @Override
    public GymMemberEntity getEmployee(Long employeeId) {
        GymMemberEntity tmpEmployee = employeeOperation.getMemberById(employeeId);
        if (tmpEmployee == null) {
            throw new CrudOperationException("invalid customer id");
        }
        return employeeOperation.getMemberById(employeeId);
    }

    @Override
    public Long addEmployee(GymMemberEntity employee) {
        return null;
    }

    @Override
    public GymMemberEntity updateEmployee(GymMemberEntity employee) {
        return null;
    }

    @Override
    public int deleteEmployee(int employeeId) {
        if (getEmployee(Long.valueOf(employeeId)) == null) {
            throw new CrudOperationException("invalid customer id");
        }
        return employeeOperation.deleteMember(employeeId);
    }
}
