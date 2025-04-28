package com.example.srmsystem.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerDto {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Email should be not blank")
    @Email(message = "Email should be valid")
    private String email;

    private String phone;
    private String address;
    private String companyName;

    public CreateCustomerDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
