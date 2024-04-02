package com.chris.service;

import com.chris.dto.GymMemberDto;

import java.util.List;

/**
 * general interface for service layer with dto for client
 */
public interface MemberCrudService {
    List<GymMemberDto> findAllMembers();

    GymMemberDto findMemberById(Integer memberId);

    GymMemberDto findMemberByEmail(String email);

    void saveMember(GymMemberDto member);

    void updateMember(GymMemberDto member);

    void deleteMemberById(Integer memberId);
}
