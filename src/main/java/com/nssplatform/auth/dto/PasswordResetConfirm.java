package com.nssplatform.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordResetConfirm {
    @NotBlank
    private String token;

    @NotBlank @Size(min = 8, max = 100)
    private String newPassword;
}
