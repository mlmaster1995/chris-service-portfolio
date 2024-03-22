package com.chris.dao;

import com.chris.entity.GymMemberEntity;

import java.util.List;

public interface MemberDao {
    List<GymMemberEntity> findAllMembers();

    GymMemberEntity getMemberById(Long memberId);

    void saveMember(GymMemberEntity member);

    GymMemberEntity updateMember(GymMemberEntity member);

    int deleteMember(int memberId);
}
