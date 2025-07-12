package com.codewithmosh.store.DTO;

import com.codewithmosh.store.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterUserRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 Character.")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Lowercase(message = "Email must be in lower case")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 25, message = "password must be between 6 to 25 Characters long.")
    private String password;
}
