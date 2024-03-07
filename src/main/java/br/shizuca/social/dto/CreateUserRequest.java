package br.shizuca.social.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotNull(message = "Age is required.")
    private Integer age;
}
