package com.example.service.implementation;

import com.example.entity.Employee;
import com.example.repository.EmployeeRepository;
import com.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee createEmployee(Employee employee) {
        // Business Rule: Throw IllegalArgumentException if name is null
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be null or empty");
        }

        // Business Rule: Salary must be > 0
        if (employee.getSalary() == null || employee.getSalary() <= 0) {
            throw new IllegalArgumentException("Employee salary must be greater than 0");
        }

        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            throw new NoSuchElementException("Employee not found with id: " + id);
        }
        return employee.get();
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        // Check if employee exists
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(id);
        if (existingEmployeeOpt.isEmpty()) {
            throw new NoSuchElementException("Employee not found with id: " + id);
        }

        // Business Rule: Validate updated employee data
        if (updatedEmployee.getName() == null || updatedEmployee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be null or empty");
        }

        if (updatedEmployee.getSalary() == null || updatedEmployee.getSalary() <= 0) {
            throw new IllegalArgumentException("Employee salary must be greater than 0");
        }

        Employee existingEmployee = existingEmployeeOpt.get();
        existingEmployee.setName(updatedEmployee.getName());
        existingEmployee.setDepartment(updatedEmployee.getDepartment());
        existingEmployee.setSalary(updatedEmployee.getSalary());

        return employeeRepository.save(existingEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        // Check if employee exists before deleting
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            throw new NoSuchElementException("Employee not found with id: " + id);
        }

        employeeRepository.deleteById(id);
    }
}
