package com.example;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import com.example.rest_api.model.Employee;
import com.example.rest_api.helpers.Helper;

public class HelperTest {

    @Test
    public void testCalculateYearsOfExperience() {
        // Create an employee with a known date of joining
        Employee employee = new Employee();
        employee.setDateOfJoining(LocalDateTime.of(2018, 8, 20, 0, 0));
        
        // Call the method to calculate years of experience
        Helper.calcualteYearsOFExperience(employee);

        // Assert that the years of experience are correctly calculated
        assertEquals(6, employee.getYearsOfExperience());  // Assuming the current year is 2024
    }

    @Test
    public void testCalculateYearsOfExperience_NullDateOfJoining() {
        // Create an employee with a null date of joining
        Employee employee = new Employee();
        employee.setDateOfJoining(null);

        // Call the method to calculate years of experience
        Helper.calcualteYearsOFExperience(employee);

    }

    @Test
    public void testCalculateYearsOfExperience_CurrentYearJoining() {
        // Create an employee who joined this year
        Employee employee = new Employee();
        employee.setDateOfJoining(LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0));

        // Call the method to calculate years of experience
        Helper.calcualteYearsOFExperience(employee);

        // Assert that the years of experience are correctly calculated as 0
        assertEquals(0, employee.getYearsOfExperience());
    }
}
