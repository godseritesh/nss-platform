package com.nssplatform.auth.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateProfileRequest {
    @Size(min = 1, max = 100)
    private String name;

    @Size(min = 8, max = 100)
    private String currentPassword;

    @Size(min = 8, max = 100)
    private String newPassword;
}
