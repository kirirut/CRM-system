package com.example.srmsystem.dto;

import com.example.srmsystem.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {

    private String username;

    private String email;

    private String password;

    private Role role;
}
