package com.hugo.mabibli.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min=3, max=50, message="Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    String username,

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min=8, max=72, message = "Le mot de passe doit contenir entre 8 et 72 caractères")
    String password
    ) {
}
