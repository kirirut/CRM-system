package com.example.srmsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerDto {

    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String companyName;
}
