package com.example.srmsystem.service;

import com.example.srmsystem.enums.EmployeeStatus;
import com.example.srmsystem.exception.ResourceNotFoundException;
import com.example.srmsystem.model.Employee;
import com.example.srmsystem.repository.EmployeeRepository;
import java.util.List;
import org.springframework.stereotype.Service;





@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        employeeRepository.addSampleEmployees();

    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.getAllEmployees();
    }

    public List<Employee> getEmployeesByStatus(EmployeeStatus status) {
        if (status != null) {
            return employeeRepository.getEmployeesByStatus(status);
        }
        throw new ResourceNotFoundException("Employee status not found");

    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.getEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id:" + id));
    }
}