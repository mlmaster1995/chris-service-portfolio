package com.chris.controller;

import com.chris.entity.GymMemberEntity;
import com.chris.service.MemberCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppRestController {
    private MemberCrudService appService;

    @Autowired
    public AppRestController(MemberCrudService appService) {
        this.appService = appService;
    }

    @GetMapping("/employees")
    public List<GymMemberEntity> getAllEmployeeEndPoints() {
        return null;
    }

    @GetMapping("/employees/{employeeId}")
    public GymMemberEntity getEmployeeEndPoints(@PathVariable int employeeId) {
        return null;
    }

    @PostMapping("/employees")
    public String addEmployeeEndPoints(@RequestBody GymMemberEntity employee) {
        return null;
    }

    @PutMapping("/employees")
    public GymMemberEntity updateEmployeeEndPiont(@RequestBody GymMemberEntity employee) {
        return null;
    }

    @DeleteMapping("/employees/{employeeId}")
    public String deleteEmployeeEndPoint(@PathVariable int employeeId) {
        return null;
    }
}
