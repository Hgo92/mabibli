package com.hugo.mabibli.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeleteAccountRequest(
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(max = 72)
        String password
) {
}
