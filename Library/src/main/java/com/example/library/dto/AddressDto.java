package com.example.library.dto;

import com.example.library.model.Customer;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class AddressDto {
    private long id;
    @NotEmpty(message = "Should be enter your state")
    private String state;
    @NotEmpty(message = "Should not blank")
    private String addressLine1;
    @NotEmpty(message = "Should not blank")
    private String addressLine2;
    @NotEmpty(message = "Should not blank")
    private  String district;
    @NotEmpty(message = "Should not blank")
    private String pin_code;
    @NotEmpty(message = "Should not blank")
    private String addressType;
    //private String AddressType;
    @Pattern(regexp = "^[0-9]+$", message = "String must contain only numeric characters")
    private String ContactNumber;
    private boolean Active= true;
    private Customer customer;

}
