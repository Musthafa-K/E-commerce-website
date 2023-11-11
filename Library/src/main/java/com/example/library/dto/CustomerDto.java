package com.example.library.dto;

import com.example.library.model.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
     private  Long id;
     @NotBlank
    @Size(min = 3,max = 10,message = "First name contains 3-10 charcters")
    private String firstName;
     @NotBlank
    @Size(min = 3,max=10,message = "Last name contains 3-10 characters")
    private String lastName;
     @Email

    private String username;

    private String password;
  //  @Pattern(regexp = "^[0-10]*$^[^\\s]+$",message = "Mobile number must contains only numeric digits")
    private String phoneNumber;

    private String addresses;
    private String confirmPassword;
    private boolean isBlocked;


}
