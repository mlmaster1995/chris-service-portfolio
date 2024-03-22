package com.chris.service;

import com.chris.dao.MemberDao;
import com.chris.dto.GymMemberDto;
import com.chris.entity.GymMemberEntity;
import com.chris.exception.CrudOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * call dao layer to get the dto and return to client
 */
@Service
public class CrudServiceImpl implements CrudService {

    private MemberDao employeeOperation;

    @Autowired
    public CrudServiceImpl(MemberDao employeeOperation) {
        this.employeeOperation = employeeOperation;
    }

    @Override
    public List<GymMemberDto> findAllMembers() {
        return null;
    }

    @Override
    public GymMemberDto getMemberById(Long memberId) {
        GymMemberEntity tmpEmployee = employeeOperation.getMemberById(memberId);
        if (tmpEmployee == null) {
            throw new CrudOperationException("invalid customer id");
        }
        return null;
    }

    @Override
    public void saveMember(GymMemberDto member) {
    }

    @Override
    public GymMemberDto updateMember(GymMemberDto member) {
        return null;
    }

    @Override
    public int deleteMember(int memberId) {
        if (getMemberById(Long.valueOf(memberId)) == null) {
            throw new CrudOperationException("invalid customer id");
        }
        return employeeOperation.deleteMember(memberId);
    }
}
