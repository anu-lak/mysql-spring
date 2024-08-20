package com.example.rest_api.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.rest_api.model.Employee;
import com.example.rest_api.projection.EmployeeProjection;

public interface SqlRepo extends JpaRepository<Employee,Integer> {

    @Query("SELECT e FROM Employee e WHERE e.department = :department AND e.managerId = 0")
    List<Employee> findByDepartment(String department);

    @Query("SELECT e FROM Employee e WHERE e.designation = 'account manager'")
    List<Employee> findManagers();

    @Query("SELECT e FROM Employee e WHERE e.managerId = :id")
    List<EmployeeProjection> findAllByManagerId(int id);

    @Query("SELECT e FROM Employee e WHERE e.managerId = :id AND e.yearsOfExperience >= :yearOfExperience")
    List<EmployeeProjection> findAllByManagerIdAndYearsOfExperienceGreaterThanEqual(int id, Integer yearOfExperience);

    @Query("SELECT e FROM Employee e WHERE e.yearsOfExperience >= :yearsOfExperience")
    List<EmployeeProjection> findAllByYearsOfExperienceGreaterThanEqual(int yearsOfExperience);

    List<Employee> findAllById(Integer employeeId);

    // `findAllById` is inherited from JpaRepository
    // `deleteById` is inherited from JpaRepository
    // `findById` is inherited from JpaRepository

}
