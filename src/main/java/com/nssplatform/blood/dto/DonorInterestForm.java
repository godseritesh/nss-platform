package com.nssplatform.blood.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DonorInterestForm {

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String phone;

    @Email(message = "Invalid email")
    private String email;

    @Size(max = 500)
    private String message;
}
