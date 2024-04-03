package com.chris.service;

import com.chris.dao.MemberDao;
import com.chris.dto.GymMemberDto;
import com.chris.entity.GymMemberEntity;
import com.chris.exception.AppServiceException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.chris.util.AppBeanConstant.MEMBER_DAO_IMPL_BEAN;
import static com.chris.util.AppBeanConstant.MEMBER_SERVICE_BEAN;

/**
 * call dao layer to get the dto and return to client
 */
@Transactional
@Service(value = MEMBER_SERVICE_BEAN)
public class MemberCrudServiceImpl implements MemberCrudService {
    private Logger _LOG = LoggerFactory.getLogger(MemberCrudServiceImpl.class);

    private final MemberDao _memberDao;

    @Autowired
    public MemberCrudServiceImpl(
            @Qualifier(value = MEMBER_DAO_IMPL_BEAN) MemberDao memberDao) {
        this._memberDao = memberDao;
    }

    @Override
    public List<GymMemberDto> findAllMembers() {
        List<GymMemberDto> memberDtos = null;
        try {
            List<GymMemberEntity> memberEntities = _memberDao.findAllMembers();
            memberDtos = memberEntities.stream().map(x -> x.toDto()).collect(Collectors.toList());
            _LOG.warn("all member dtos are fetched...");
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
            _LOG.warn("member dto with id ({}) is fetched...", memberId);
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
            _LOG.warn("member with email ({}) is fetched...", email);
        } catch (Exception exp) {
            throw new AppServiceException("fails to find member by email at service layer...: " + exp);
        }

        return dto;
    }

    @Override
    public void saveMember(GymMemberDto memberDto) {
        try {
            _memberDao.saveMember(memberDto.toEntity());
            _LOG.warn("member dto({}) is persisted...", memberDto.toString());
        } catch (Exception exp) {
            throw new AppServiceException("fails to save member at service layer...: " + exp);
        }
    }

    @Override
    public void updateMember(GymMemberDto memberDto) {
        try {
            if (memberDto.getId() == null) {
                throw new AppServiceException("member id is null");
            }
            _memberDao.updateMember(memberDto.toEntity());
            _LOG.warn("member dto({}) is updated...", memberDto.toString());
        } catch (Exception exp) {
            throw new AppServiceException("fails to update member at the service layer...: " + exp);
        }
    }

    @Override
    public void deleteMemberById(Integer memberId) {
        try {
            _memberDao.deleteMemberById(memberId);
            _LOG.warn("member dto with id({}) is removed, ", memberId);
        } catch (Exception exp) {
            throw new AppServiceException("fails to delete member at the service layer...: " + exp);
        }
    }
}
