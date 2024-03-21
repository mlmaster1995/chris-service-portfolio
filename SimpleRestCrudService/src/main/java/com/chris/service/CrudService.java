package com.chris.service;

import com.chris.entity.GymMemberEntity;

import java.util.List;

public interface CrudService {
    List<GymMemberEntity> getAllEmployees();
    GymMemberEntity getEmployee(Long employeeId);
    Long addEmployee(GymMemberEntity employee);
    GymMemberEntity updateEmployee(GymMemberEntity employee);
    int deleteEmployee(int employeeId);
}
