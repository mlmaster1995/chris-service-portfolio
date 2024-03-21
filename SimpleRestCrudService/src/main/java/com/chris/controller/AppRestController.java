package com.chris.controller;

import com.chris.service.CrudService;
import com.chris.entity.GymMemberEntity;
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
    private CrudService appService;

    @Autowired
    public AppRestController(CrudService appService) {
        this.appService = appService;
    }

    @GetMapping("/employees")
    public List<GymMemberEntity> getAllEmployeeEndPoints(){
        return appService.getAllEmployees();
    }

    @GetMapping("/employees/{employeeId}")
    public GymMemberEntity getEmployeeEndPoints(@PathVariable int employeeId){
        return appService.getEmployee(Long.valueOf(employeeId));
    }

    @PostMapping("/employees")
    public String addEmployeeEndPoints(@RequestBody GymMemberEntity employee){
        return "employee "+appService.addEmployee(employee)+ " has been added";
    }

    @PutMapping("/employees")
    public GymMemberEntity updateEmployeeEndPiont(@RequestBody GymMemberEntity employee){
        return appService.updateEmployee(employee);
    }

    @DeleteMapping("/employees/{employeeId}")
    public String deleteEmployeeEndPoint(@PathVariable int employeeId){
        return "employee "+appService.deleteEmployee(employeeId)+ " has been added";
    }
}
