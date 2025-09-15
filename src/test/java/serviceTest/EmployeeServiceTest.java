package com.example.service.impl;

import com.example.entity.Employee;
import com.example.repository.EmployeeRepository;
import com.example.service.implementation.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee testEmployee;
    private Employee updatedEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee("John Doe", "Engineering", 50000.0);
        testEmployee.setId(1L);

        updatedEmployee = new Employee("Jane Smith", "Marketing", 60000.0);
    }

    @Test
    void createEmployee_validInput_success() {
        // Arrange
        Employee inputEmployee = new Employee("John Doe", "Engineering", 50000.0);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        // Act
        Employee result = employeeService.createEmployee(inputEmployee);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("Engineering", result.getDepartment());
        assertEquals(50000.0, result.getSalary());
        assertEquals(1L, result.getId());

        // Verify repository interaction
        verify(employeeRepository, times(1)).save(inputEmployee);
    }

    @Test
    void createEmployee_nullName_throwsException() {
        // Arrange
        Employee invalidEmployee = new Employee(null, "Engineering", 50000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.createEmployee(invalidEmployee)
        );

        assertEquals("Employee name cannot be null or empty", exception.getMessage());

        // Verify repository is never called
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployee_emptyName_throwsException() {
        // Arrange
        Employee invalidEmployee = new Employee("", "Engineering", 50000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.createEmployee(invalidEmployee)
        );

        assertEquals("Employee name cannot be null or empty", exception.getMessage());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployee_invalidSalary_throwsException() {
        // Arrange
        Employee invalidEmployee = new Employee("John Doe", "Engineering", -1000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.createEmployee(invalidEmployee)
        );

        assertEquals("Employee salary must be greater than 0", exception.getMessage());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void getEmployeeById_validId_success() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

        // Act
        Employee result = employeeService.getEmployeeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testEmployee.getId(), result.getId());
        assertEquals(testEmployee.getName(), result.getName());
        assertEquals(testEmployee.getDepartment(), result.getDepartment());
        assertEquals(testEmployee.getSalary(), result.getSalary());

        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getEmployeeById_invalidId_throwsException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> employeeService.getEmployeeById(999L)
        );

        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository, times(1)).findById(999L);
    }

    @Test
    void getAllEmployees_success() {
        // Arrange
        Employee employee2 = new Employee("Jane Smith", "Marketing", 60000.0);
        employee2.setId(2L);
        List<Employee> mockEmployees = Arrays.asList(testEmployee, employee2);

        when(employeeRepository.findAll()).thenReturn(mockEmployees);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testEmployee.getName(), result.get(0).getName());
        assertEquals(employee2.getName(), result.get(1).getName());

        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void updateEmployee_validId_success() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        // Act
        Employee result = employeeService.updateEmployee(1L, updatedEmployee);

        // Assert
        assertNotNull(result);
        assertEquals("Jane Smith", result.getName());
        assertEquals("Marketing", result.getDepartment());
        assertEquals(60000.0, result.getSalary());

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(testEmployee);
    }

    @Test
    void updateEmployee_invalidId_throwsException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> employeeService.updateEmployee(999L, updatedEmployee)
        );

        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository, times(1)).findById(999L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployee_invalidName_throwsException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        Employee invalidUpdate = new Employee(null, "Marketing", 60000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.updateEmployee(1L, invalidUpdate)
        );

        assertEquals("Employee name cannot be null or empty", exception.getMessage());
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployee_invalidSalary_throwsException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        Employee invalidUpdate = new Employee("Jane Smith", "Marketing", -5000.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.updateEmployee(1L, invalidUpdate)
        );

        assertEquals("Employee salary must be greater than 0", exception.getMessage());
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_validId_success() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        doNothing().when(employeeRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> employeeService.deleteEmployee(1L));

        // Assert
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteEmployee_invalidId_throwsException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> employeeService.deleteEmployee(999L)
        );

        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository, times(1)).findById(999L);
        verify(employeeRepository, never()).deleteById(anyLong());
    }
}
