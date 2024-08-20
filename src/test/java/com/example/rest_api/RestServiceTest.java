

package com.example.rest_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.rest_api.model.Employee;
import com.example.rest_api.projection.EmployeeProjection;
import com.example.rest_api.repository.EmployeeRepo;
import com.example.rest_api.response.ApiResponse;
import com.example.rest_api.Service.RestService;
import com.example.rest_api.helpers.Helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestServiceTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @InjectMocks
    private RestService restService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPostEmployee_ManagerAlreadyExists() {
        // Given
        Employee employee = new Employee();
        employee.setId(1);
        employee.setManagerId(0);
        employee.setDesignation("account manager");
        employee.setDepartment("IT");

        Employee existingManager = new Employee();
        existingManager.setId(2);
        existingManager.setManagerId(0);
        existingManager.setDepartment("IT");

        List<Employee> deptEmployees = new ArrayList<>();
        deptEmployees.add(existingManager);

        when(employeeRepo.existsById(anyInt())).thenReturn(false);
        when(employeeRepo.findByDepartment(anyString())).thenReturn(deptEmployees);

        // When
        ResponseEntity<?> response = restService.postEmployee(employee);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
        assertEquals("A manager already exists in the department: IT", ((ApiResponse) response.getBody()).getMessage());
    }

    @Test
    public void testPostEmployee_AssociateManagerNotFound() {
        // Given
        Employee employee = new Employee();
        employee.setId(1);
        employee.setManagerId(2);
        employee.setDesignation("associate");
        employee.setDepartment("IT");

        when(employeeRepo.existsById(anyInt())).thenReturn(false);
        when(employeeRepo.findById(anyInt())).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = restService.postEmployee(employee);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
        assertEquals("Manager cannot be found", ((ApiResponse) response.getBody()).getMessage());
    }

    @Test
    public void testPostEmployee_SuccessfulCreation() {
        // Given
        Employee employee = new Employee();
        employee.setId(1);
        employee.setManagerId(0);
        employee.setDesignation("account manager");
        employee.setDepartment("IT");

        when(employeeRepo.existsById(anyInt())).thenReturn(false);
        when(employeeRepo.findByDepartment(anyString())).thenReturn(new ArrayList<>());

        // When
        ResponseEntity<?> response = restService.postEmployee(employee);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
        assertEquals("Successfully created", ((ApiResponse) response.getBody()).getMessage());
        verify(employeeRepo, times(1)).save(any(Employee.class));
    }

    @Test
    public void testGetEmployee_SuccessfulFetch() {
        // Given
        Employee manager = new Employee();
        manager.setId(1);
        manager.setName("John Doe");
        manager.setManagerId(0);
        manager.setDesignation("account manager");
        manager.setDepartment("IT");

        List<Employee> managerList = new ArrayList<>();
        managerList.add(manager);

        EmployeeProjection employeeProjection = new EmployeeProjection() {
            @Override
            public String getName() {
                return "Jane Doe";
            }

            @Override
            public String getDesignation() {
                return "associate";
            }

            // @Override
            // public Integer getYearsOfExperience() {
            //     return 5;
            // }

            @Override
            public LocalDateTime getDateOfJoining() {
                return LocalDateTime.now();
            }

            @Override
            public String getId() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getId'");
            }

            @Override
            public String getDepartment() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getDepartment'");
            }

            @Override
            public String getEmail() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getEmail'");
            }

            @Override
            public String getMobile() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getMobile'");
            }

            @Override
            public String getLocation() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getLocation'");
            }

            @Override
            public LocalDateTime getCreatedTime() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getCreatedTime'");
            }
        };

        List<EmployeeProjection> employeeList = new ArrayList<>();
        employeeList.add(employeeProjection);

        when(employeeRepo.findManagers()).thenReturn(managerList);
        when(employeeRepo.findAllByManagerId(anyInt())).thenReturn(employeeList);

        // When
        Map<String, Object> result = restService.getEmployee(1, null);

        // Then
        assertEquals("successfully fetched", result.get("message"));
        List<?> details = (List<?>) result.get("details");
        assertFalse(details.isEmpty());
        Map<String, Object> managerEmpMap = (Map<String, Object>) details.get(0);
        assertEquals("John Doe", managerEmpMap.get("account manager"));
        assertEquals("IT", managerEmpMap.get("department"));
        assertEquals(employeeList, managerEmpMap.get("employeeList"));
    }
    @Test
    public void testGetEmployee_bymanageridandyearsofexperience() {
        // Given
        Employee manager = new Employee();
        manager.setId(1);
        manager.setName("John Doe");
        manager.setManagerId(0);
        manager.setDesignation("account manager");
        manager.setDepartment("IT");
        manager.setYearsOfExperience(9);

        List<Employee> managerList = new ArrayList<>();
        managerList.add(manager);

        EmployeeProjection employeeProjection = new EmployeeProjection() {
            @Override
            public String getName() {
                return "Jane Doe";
            }

            @Override
            public String getDesignation() {
                return "associate";
            }

            // @Override
            // public Integer getYearsOfExperience() {
            //     return 5;
            // }

            @Override
            public LocalDateTime getDateOfJoining() {
                return LocalDateTime.now();
            }

            @Override
            public String getId() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getId'");
            }

            @Override
            public String getDepartment() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getDepartment'");
            }

            @Override
            public String getEmail() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getEmail'");
            }

            @Override
            public String getMobile() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getMobile'");
            }

            @Override
            public String getLocation() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getLocation'");
            }

            @Override
            public LocalDateTime getCreatedTime() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getCreatedTime'");
            }
        };

        List<EmployeeProjection> employeeList = new ArrayList<>();
        employeeList.add(employeeProjection);

        when(employeeRepo.findManagers()).thenReturn(managerList);
        when(employeeRepo.findAllByManagerId(anyInt())).thenReturn(employeeList);

        // When
        Map<String, Object> result = restService.getEmployee(1, 4);

        // Then
        assertEquals("successfully fetched", result.get("message"));
        List<?> details = (List<?>) result.get("details");
        assertFalse(details.isEmpty());
        Map<String, Object> managerEmpMap = (Map<String, Object>) details.get(0);
        assertEquals("John Doe", managerEmpMap.get("account manager"));
        assertEquals("IT", managerEmpMap.get("department"));
        assertEquals(employeeList, managerEmpMap.get("employeeList"));
    }

    @Test
    public void testUpdateEmployee_ManagerChanged() {
        // Given
        Employee emp = new Employee();
        emp.setId(1);
        emp.setName("Jane Doe");
        emp.setManagerId(2);
        emp.setDepartment("IT");

        Employee newManager = new Employee();
        newManager.setId(3);
        newManager.setName("John Smith");

        Employee oldManager = new Employee();
        oldManager.setId(2);
        oldManager.setName("Old Manager");

        when(employeeRepo.findAllById(anyInt())).thenReturn(List.of(emp));
        when(employeeRepo.findById(3)).thenReturn(Optional.of(newManager));
        when(employeeRepo.findById(2)).thenReturn(Optional.of(oldManager));

        // When
        ResponseEntity<Map<String, String>> response = restService.updateEmployee(1, 3);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("message "));
        assertEquals("Jane Doe's manager has been successfully changed from Old Manager to John Smith.",
                response.getBody().get("message "));
        verify(employeeRepo, times(1)).save(emp);
    }

    @Test
    public void testDeleteEmployee_EmployeeNotFound() {
        // Given
        when(employeeRepo.findById(anyInt())).thenReturn(Optional.empty());

        // When
        ResponseEntity<Map<String, String>> response = restService.deleteEmployee(1);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Employee not found", response.getBody().get("message"));
    }

    @Test
    public void testDeleteEmployee_SuccessfulDeletion() {
        // Given
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Jane Doe");

        when(employeeRepo.findById(anyInt())).thenReturn(Optional.of(employee));
        when(employeeRepo.findAllByManagerId(anyInt())).thenReturn(new ArrayList<>());

        // When
        ResponseEntity<Map<String, String>> response = restService.deleteEmployee(1);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Successfully deleted Jane Doe from employee list of the organization", response.getBody().get("message"));
        verify(employeeRepo, times(1)).deleteById(1);
    }
}
