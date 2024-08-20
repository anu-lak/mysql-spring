package com.example.rest_api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonFormat;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table(name = "employee")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Employee {
    @Id
    private int id;
    
    @NotNull(message="Name cannot be null")
    //@NotBlank(message="Name cannot be blank")
    private String name;


    @NotNull(message="Designation cannot be null")
    @Pattern(regexp= "^(account manager||associate)$", message = "Designation have to account manager or associate")
    private String designation;

    @NotNull(message="Designation cannot be null")
    @Pattern(regexp = "^(BA||QA||engineering||sales||delivery)$",message="Invalid department")
    private String department;

    @Email(message ="Invalid email address")
    private String email;

    @Pattern(regexp = "(^\\d{10}$)")
    private String mobile;

    @NotBlank(message = "Location cannot be null")
    private String location;

    //@NotBlank(message = "manager id is required field")
    private int managerId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateOfJoining;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdTime;

    private int yearsOfExperience;


}
