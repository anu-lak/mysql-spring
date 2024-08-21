

package com.example.rest_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.rest_api.model.Employee;
import com.example.rest_api.projection.EmployeeProjection;
import com.example.rest_api.repository.EmployeeRepo;
import com.example.rest_api.response.ApiResponse;
import com.example.rest_api.Service.RestService;
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
        employee.setDepartment("sales");

        Employee existingManager = new Employee();
        existingManager.setId(2);
        existingManager.setManagerId(0);
        existingManager.setDepartment("sales");

        List<Employee> deptEmployees = new ArrayList<>();
        deptEmployees.add(existingManager);

        when(employeeRepo.existsById(anyInt())).thenReturn(false);
        when(employeeRepo.findByDepartment(anyString())).thenReturn(deptEmployees);

        // When
        ResponseEntity<?> response = restService.postEmployee(employee);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
        assertEquals("A manager already exists in the department: sales", ((ApiResponse) response.getBody()).getMessage());
    }

    @Test
    public void testPostEmployee_AssociateManagerNotFound() {
        // Given
        Employee employee = new Employee();
        employee.setId(1);
        employee.setManagerId(2);
        employee.setDesignation("associate");
        employee.setDepartment("sales");

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
        employee.setDepartment("sales");

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
public void testPostEmployee_EmployeeIdAlreadyExists() {
    // Given
    Employee employee = new Employee();
    employee.setId(1);
    employee.setDesignation("associate");

    when(employeeRepo.existsById(employee.getId())).thenReturn(true);

    // When
    ResponseEntity<?> response = restService.postEmployee(employee);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof ApiResponse);
    assertEquals("Employee ID already exists", ((ApiResponse) response.getBody()).getMessage());
}

@Test
public void testPostEmployee_ManagerCannotBeFound() {
    // Given
    Employee employee = new Employee();
    employee.setId(1);
    employee.setDesignation("associate");
    employee.setManagerId(100); // Assuming this manager ID does not exist

    when(employeeRepo.existsById(employee.getId())).thenReturn(false);
    when(employeeRepo.findById(employee.getManagerId())).thenReturn(Optional.empty());

    // When
    ResponseEntity<?> response = restService.postEmployee(employee);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof ApiResponse);
    assertEquals("Manager cannot be found", ((ApiResponse) response.getBody()).getMessage());
}

@Test
public void testPostEmployee_ManagerDepartmentDoesNotMatch() {
    // Given
    Employee employee = new Employee();
    employee.setId(1);
    employee.setDesignation("associate");
    employee.setManagerId(2);
    employee.setDepartment("HR");

    Employee manager = new Employee();
    manager.setId(2);
    manager.setManagerId(0); // Manager's manager ID is 0, meaning this is a manager
    manager.setDesignation("account manager");
    manager.setDepartment("Sales"); // Different department

    when(employeeRepo.existsById(employee.getId())).thenReturn(false);
    when(employeeRepo.findById(employee.getManagerId())).thenReturn(Optional.of(manager));

    // When
    ResponseEntity<?> response = restService.postEmployee(employee);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof ApiResponse);
    assertEquals("Given manager's department does not match", ((ApiResponse) response.getBody()).getMessage());
}

@Test
public void testPostEmployee_ManagerIdOrDesignationDoesNotMatch() {
    // Given
    Employee employee = new Employee();
    employee.setId(1);
    employee.setDesignation("associate");
    employee.setManagerId(2);

    Employee manager = new Employee();
    manager.setId(2);
    manager.setManagerId(3); // Not 0, so this is not a manager
    manager.setDesignation("associate"); // Not "account manager"

    when(employeeRepo.existsById(employee.getId())).thenReturn(false);
    when(employeeRepo.findById(employee.getManagerId())).thenReturn(Optional.of(manager));

    // When
    ResponseEntity<?> response = restService.postEmployee(employee);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof ApiResponse);
    assertEquals("Given manager's ID or designation does not match", ((ApiResponse) response.getBody()).getMessage());
}

@Test
public void testPostEmployee_InvalidEmployeeDesignationOrData() {
    // Given
    Employee employee = new Employee();
    employee.setId(1);
    employee.setDesignation("intern"); // Invalid designation

    when(employeeRepo.existsById(employee.getId())).thenReturn(false);

    // When
    ResponseEntity<?> response = restService.postEmployee(employee);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof ApiResponse);
    assertEquals("Invalid employee designation or data", ((ApiResponse) response.getBody()).getMessage());
}


    @Test
    public void testGetEmployee_SuccessfulFetch() {
        // Given
        Employee manager = new Employee();
        manager.setId(1);
        manager.setName("John Doe");
        manager.setManagerId(0);
        manager.setDesignation("account manager");
        manager.setDepartment("sales");

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

            @Override
            public LocalDateTime getDateOfJoining() {
                return LocalDateTime.now();
            }

            @Override
            public int getId() {
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
        assertEquals("sales", managerEmpMap.get("department"));
        assertEquals(employeeList, managerEmpMap.get("employeeList"));
    }
   
    @Test
public void testGetEmployee_WithManagerIdAndYearsOfExperience() {
    // Given
    Integer managerId = 1;
    Integer yearsOfExperience = 5;

    // Create a mock list of EmployeeProjection to be returned by the repository method
    List<EmployeeProjection> mockEmployeeList = new ArrayList<>();
    
    EmployeeProjection emp1 = mock(EmployeeProjection.class);
    when(emp1.getName()).thenReturn("John Doe");
    mockEmployeeList.add(emp1);
    
    EmployeeProjection emp2 = mock(EmployeeProjection.class);
    when(emp2.getName()).thenReturn("Jane Doe");
    mockEmployeeList.add(emp2);
    
    // Mock the repository method call
    when(employeeRepo.findAllByManagerIdAndYearsOfExperienceGreaterThanEqual(managerId, yearsOfExperience))
        .thenReturn(mockEmployeeList);

    // Create a mock manager employee
    Employee manager = new Employee();
    manager.setId(managerId);
    manager.setName("Manager");
    manager.setDesignation("account manager");
    manager.setDepartment("IT");

    // Mock the manager repository call
    when(employeeRepo.findManagers()).thenReturn(List.of(manager));

    // When
    Map<String, Object> result = restService.getEmployee(managerId, yearsOfExperience);

    // Then
    assertEquals("successfully fetched", result.get("message"));
    List<?> details = (List<?>) result.get("details");
    assertEquals(1, details.size());
    
    Map<?, ?> managerEmpMap = (Map<?, ?>) details.get(0);
    assertEquals("Manager", managerEmpMap.get("account manager"));
    assertEquals("IT", managerEmpMap.get("department"));
    assertEquals(managerId, managerEmpMap.get("id"));
    
    List<?> employeeList = (List<?>) managerEmpMap.get("employeeList");
    assertEquals(2, employeeList.size());

    // Verify that the repository method was called with the correct parameters
    verify(employeeRepo).findAllByManagerIdAndYearsOfExperienceGreaterThanEqual(managerId, yearsOfExperience);
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
public void testUpdateEmployee_NewManagerNotFound() {
    // Given
    Integer employeeId = 1;
    Integer newManagerId = 2;

    // Create a mock employee with the given ID
    Employee employee = new Employee();
    employee.setId(employeeId);
    employee.setName("John Doe");
    employee.setManagerId(3); // Setting to a different managerId

    // Mock the repository methods
    when(employeeRepo.findAllById(employeeId)).thenReturn(Collections.singletonList(employee));
    when(employeeRepo.findById(newManagerId)).thenReturn(Optional.empty());

    // When
    ResponseEntity<Map<String, String>> response = restService.updateEmployee(employeeId, newManagerId);

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals("New manager not found", responseBody.get("message"));

    // Verify that the repository methods were called
    verify(employeeRepo).findAllById(employeeId);
    verify(employeeRepo).findById(newManagerId);
}


@Test
public void testUpdateEmployee_EmployeeUnderGivenManager() {
    // Given
    Integer employeeId = 1;
    Integer newManagerId = 2;

    // Create a mock employee with the given ID
    Employee employee = new Employee();
    employee.setId(employeeId);
    employee.setName("John Doe");
    employee.setManagerId(newManagerId);

    // Mock the repository methods
    when(employeeRepo.findAllById(employeeId)).thenReturn(Collections.singletonList(employee));

    // When
    ResponseEntity<Map<String, String>> response = restService.updateEmployee(employeeId, newManagerId);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals("Employee is currently under the given manager.No changes required", responseBody.get("message"));

    // Verify that the repository methods were called
    verify(employeeRepo).findAllById(employeeId);
}

@Test
public void testUpdateEmployee_EmployeeNotFound() {
    // Given
    Integer employeeId = 1;
    Integer newManagerId = 2;

    // Mock the repository methods
    when(employeeRepo.findAllById(employeeId)).thenReturn(Collections.emptyList());

    // When
    ResponseEntity<Map<String, String>> response = restService.updateEmployee(employeeId, newManagerId);

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = response.getBody();
    System.out.println(response);
    assertNotNull(responseBody);
    assertEquals("Employee with ID " + employeeId + " not found.", responseBody.get("message "));

    // Verify that the repository methods were called
    verify(employeeRepo).findAllById(employeeId);
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
public void testDeleteEmployee_EmployeeHasSubordinates() {
    // Given
    Integer employeeId = 1;

    // Create a mock employee with the given ID
    Employee employee = new Employee();
    employee.setId(employeeId);
    employee.setName("John Doe");

    // Create a mock list of EmployeeProjection to simulate subordinates
    List<EmployeeProjection> mockSubordinates = new ArrayList<>();
    EmployeeProjection subordinate = mock(EmployeeProjection.class);
    mockSubordinates.add(subordinate);

    // Mock the repository methods
    when(employeeRepo.findById(employeeId)).thenReturn(Optional.of(employee));
    when(employeeRepo.findAllByManagerId(employeeId)).thenReturn(mockSubordinates);

    // When
    ResponseEntity<Map<String, String>> response = restService.deleteEmployee(employeeId);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals("Cannot delete employee as the employee has subordinates", responseBody.get("message"));

    // Verify that the repository methods were called
    verify(employeeRepo).findById(employeeId);
    verify(employeeRepo).findAllByManagerId(employeeId);
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
