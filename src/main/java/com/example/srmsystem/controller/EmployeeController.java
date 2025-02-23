package com.example.srmsystem.controller;

import com.example.srmsystem.enums.EmployeeStatus;
import com.example.srmsystem.model.Employee;
import com.example.srmsystem.service.EmployeeService;
import java.util.List;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }


    @GetMapping("/status")
    public List<Employee> getEmployeesByStatus(@RequestParam EmployeeStatus status) {
        return employeeService.getEmployeesByStatus(status);
    }


    @GetMapping("/all")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
}

