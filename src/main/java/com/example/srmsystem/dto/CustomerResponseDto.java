package com.example.srmsystem.dto;

public class CustomerResponseDto {

    private DisplayCustomerDto customer;
    private String jwtToken;

    public CustomerResponseDto(DisplayCustomerDto customer, String jwtToken) {
        this.customer = customer;
        this.jwtToken = jwtToken;
    }

    public DisplayCustomerDto getCustomer() {
        return customer;
    }

    public void setCustomer(DisplayCustomerDto customer) {
        this.customer = customer;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
