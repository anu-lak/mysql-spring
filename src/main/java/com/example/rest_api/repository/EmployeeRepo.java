package com.example.rest_api.repository;

import java.util.List;
import java.util.Optional;
import com.example.rest_api.model.Employee;
import com.example.rest_api.projection.EmployeeProjection;


public interface EmployeeRepo{

    List<Employee> findByDepartment(String department);

    public List<Employee> findManagers();

    List<EmployeeProjection> findAllByManagerId(int id);

    List<EmployeeProjection> findAllByManagerIdAndYearsOfExperienceGreaterThanEqual(int id, Integer yearOfExperience);

    List<EmployeeProjection> findAllByYearsOfExperienceGreaterThanEqual(int yearsOfExperience);

    Optional<Employee> findById(int id);

    List<Employee> findAllById(Integer employeeId);

    void deleteById(Integer employeeId);

    void save(Employee emp);

    boolean existsById(int id);
    
}
