package ru.examle.edu.dto;

import lombok.Data;

@Data
public class PersonRegisterDto {
    private String name;
    private String username;
    private String password;
    private String email;
    private String departmentName;
}
