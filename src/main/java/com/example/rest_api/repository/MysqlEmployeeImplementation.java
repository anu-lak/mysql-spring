package com.example.rest_api.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import com.example.rest_api.model.Employee;
import com.example.rest_api.projection.EmployeeProjection;



@Repository
@Profile("mysql")
public class MysqlEmployeeImplementation implements EmployeeRepo {
    
    @Autowired
    SqlRepo sqlRepo;

    @Override
    public List<Employee> findByDepartment(String department) {
        return sqlRepo.findByDepartment(department);
    }

    @Override
    public List<Employee> findManagers() {
        return sqlRepo.findManagers();
    }

    @Override
    public List<EmployeeProjection> findAllByManagerId(int id) {
        return sqlRepo.findAllByManagerId(id);
    }

    @Override
    public List<EmployeeProjection> findAllByManagerIdAndYearsOfExperienceGreaterThanEqual(int id, Integer yearOfExperience) {
        return sqlRepo.findAllByManagerIdAndYearsOfExperienceGreaterThanEqual(id, yearOfExperience);
    }

    @Override
    public List<EmployeeProjection> findAllByYearsOfExperienceGreaterThanEqual(int yearsOfExperience) {
        return sqlRepo.findAllByYearsOfExperienceGreaterThanEqual(yearsOfExperience);
    }

    @Override
    public Optional<Employee> findById(int id) {
        return sqlRepo.findById(id);
    }

    @Override
    public List<Employee> findAllById(Integer employeeId) {
        return sqlRepo.findAllById(employeeId); // Adjust for proper handling if needed
    }

    @Override
    public void deleteById(Integer employeeId) {
        sqlRepo.deleteById(employeeId);
    }

    @Override
    public void save(Employee emp) {
        sqlRepo.save(emp);
    }

    @Override
    public boolean existsById(int id) {
        return sqlRepo.existsById(id);
    }
}
