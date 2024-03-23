package com.chris.dao;

import com.chris.entity.GymMemberEntity;

import java.util.List;

/**
 * general interface for dao layer
 */
public interface MemberDao {
    List<GymMemberEntity> findAllMembers();

    GymMemberEntity findMemberById(Integer memberId);

    GymMemberEntity findMemberByEmail(String email);

    void saveMember(GymMemberEntity member);

    void updateMember(GymMemberEntity member);

    void deleteMemberById(Integer memberId);
}
