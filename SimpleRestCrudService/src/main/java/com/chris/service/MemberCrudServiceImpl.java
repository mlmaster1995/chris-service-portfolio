package com.chris.service;

import com.chris.dao.MemberDao;
import com.chris.dto.GymMemberDto;
import com.chris.entity.GymMemberEntity;
import com.chris.exception.AppServiceException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.chris.util.AppBeanConstant.APP_SERVICE_BEAN;

/**
 * call dao layer to get the dto and return to client
 */
@Transactional
@Service(value = APP_SERVICE_BEAN)
public class MemberCrudServiceImpl implements MemberCrudService {
    private Logger _LOG = LoggerFactory.getLogger(MemberCrudServiceImpl.class);

    private final MemberDao _memberDao;

    @Autowired
    public MemberCrudServiceImpl(MemberDao memberDao) {
        this._memberDao = memberDao;
    }

    @Override
    public List<GymMemberDto> findAllMembers() {
        List<GymMemberDto> memberDtos = null;
        try {
            List<GymMemberEntity> memberEntities = _memberDao.findAllMembers();

            memberDtos = memberEntities.stream().map(x -> x.toDto()).collect(Collectors.toList());
        } catch (Exception exp) {
            throw new AppServiceException("fails to find all members at service layer...: " + exp);
        }

        return memberDtos;
    }

    @Override
    public GymMemberDto findMemberById(Integer memberId) {
        GymMemberDto dto = null;

        try {
            GymMemberEntity entity = _memberDao.findMemberById(memberId);
            dto = entity.toDto();
        } catch (Exception exp) {
            throw new AppServiceException("fails to find the member by id at service layer...: " + exp);
        }

        return dto;
    }

    @Override
    public GymMemberDto findMemberByEmail(String email) {
        GymMemberDto dto = null;

        try {
            GymMemberEntity entity = _memberDao.findMemberByEmail(email);
            dto = entity.toDto();
        } catch (Exception exp) {
            throw new AppServiceException("fails to find member by email at service layer...: " + exp);
        }

        return dto;
    }

    @Override
    public void saveMember(GymMemberDto member) {
        try {
            _memberDao.saveMember(member.toEntity());
        } catch (Exception exp) {
            throw new AppServiceException("fails to save member at service layer...: " + exp);
        }
    }

    @Override
    public void updateMember(GymMemberDto member) {

    }

    @Override
    public void deleteMemberById(Integer memberId) {
    }
}
