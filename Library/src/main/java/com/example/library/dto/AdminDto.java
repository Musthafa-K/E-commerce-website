package com.example.library.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminDto {
    @Size(min = 3,max=10,message = "First name contain 3-10 characters")
    private String firstName;
    @Size(min = 3, max = 10, message = "Last name contains 3-10 characters")
    private String lastName;
    private String username;
    private String password;
    private String repeatPassword;

}
