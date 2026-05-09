package com.nssplatform.blood.dto;

import com.nssplatform.blood.entity.BloodRequest;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BloodRequestForm {

    @NotBlank(message = "Patient name is required")
    @Size(max = 100)
    private String patientName;

    @NotNull(message = "Blood group is required")
    private BloodRequest.BloodGroup bloodGroup;

    @NotNull @Min(1) @Max(20)
    private Integer unitsNeeded;

    @NotBlank(message = "Hospital is required")
    @Size(max = 200)
    private String hospital;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "District is required")
    @Size(max = 100)
    private String district;

    private Double latitude;
    private Double longitude;

    @NotBlank(message = "Contact name is required")
    @Size(max = 100)
    private String contactName;

    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String contactPhone;

    @Email(message = "Invalid email")
    private String contactEmail;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "Urgency is required")
    private BloodRequest.Urgency urgency;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;
}
