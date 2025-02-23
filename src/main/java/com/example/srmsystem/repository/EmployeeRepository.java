package com.example.srmsystem.repository;

import com.example.srmsystem.enums.EmployeeStatus;
import com.example.srmsystem.model.Employee;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository {

    public List<Employee> getAllEmployees();

    public List<Employee> getEmployeesByStatus(EmployeeStatus status);

    public Optional<Employee> getEmployeeById(Long id);

    public  void addSampleEmployees();
}
