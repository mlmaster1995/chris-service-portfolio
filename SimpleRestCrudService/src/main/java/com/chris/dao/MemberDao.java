package com.chris.dao;

import com.chris.entity.GymMemberEntity;

import java.util.List;

public interface MemberDao {
    List<GymMemberEntity> getAllMembers();

    GymMemberEntity getMemberById(Long memberId);

    Long addMember(GymMemberEntity member);

    GymMemberEntity updateMember(GymMemberEntity member);

    int deleteMember(int memberId);
}
