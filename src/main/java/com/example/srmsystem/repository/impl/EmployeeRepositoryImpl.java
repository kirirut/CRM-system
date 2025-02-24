package com.example.srmsystem.repository.impl;

import com.example.srmsystem.enums.EmployeeStatus;
import com.example.srmsystem.exception.ResourceNotFoundException;
import com.example.srmsystem.model.Employee;
import com.example.srmsystem.repository.EmployeeRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final List<Employee> employees = new ArrayList<>();

    @Override
    public List<Employee> getAllEmployees() {
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found");
        }
        return employees;
    }

    @Override
    public void addSampleEmployees() {
        employees.add(new Employee(1L, "John", "Doe", "john.doe@example.com", EmployeeStatus.ACTIVE));
        employees.add(new Employee(2L, "Jane", "Smith", "jane.smith@example.com", EmployeeStatus.OPEN_TO_OFFERS));
        employees.add(new Employee(3L, "Alex", "Johnson", "alex.johnson@example.com", EmployeeStatus.EMPLOYED));
        employees.add(new Employee(4L, "Emily", "Williams", "emily.williams@example.com", EmployeeStatus.FREELANCER));
        employees.add(new Employee(5L, "Michael", "Brown", "michael.brown@example.com", EmployeeStatus.INTERN));
        employees.add(new Employee(6L, "Sarah", "Davis", "sarah.davis@example.com", EmployeeStatus.UNAVAILABLE));
        employees.add(new Employee(7L, "David", "Miller", "david.miller@example.com", EmployeeStatus.RETIRED));
        employees.add(new Employee(8L, "Jessica", "Wilson", "jessica.wilson@example.com", EmployeeStatus.INTERVIEWING));
        employees.add(new Employee(9L, "James", "Moore", "james.moore@example.com", EmployeeStatus.OFFER_RECEIVED));
        employees.add(new Employee(10L, "Olivia", "Taylor", "olivia.taylor@example.com", EmployeeStatus.HIRED));
    }

    @Override
    public List<Employee> getEmployeesByStatus(EmployeeStatus status) {
        List<Employee> result = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getStatus() == status) {
                result.add(employee);
            }
        }
        return result;
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employees.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }
}
