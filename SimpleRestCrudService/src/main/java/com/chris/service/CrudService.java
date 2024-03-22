package com.chris.service;

import com.chris.dto.GymMemberDto;

import java.util.List;

public interface CrudService {
    List<GymMemberDto> findAllMembers();

    GymMemberDto getMemberById(Long memberId);

    void saveMember(GymMemberDto member);

    GymMemberDto updateMember(GymMemberDto member);

    int deleteMember(int memberId);
}
