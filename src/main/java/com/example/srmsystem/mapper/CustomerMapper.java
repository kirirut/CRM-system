package com.example.srmsystem.mapper;

import com.example.srmsystem.dto.CreateCustomerDto;
import com.example.srmsystem.dto.DisplayCustomerDto;
import com.example.srmsystem.model.Customer;
import org.springframework.stereotype.Component;

@Component
public final class CustomerMapper {
    private CustomerMapper() {
    }

    public Customer fromCreateCustomerDto(final CreateCustomerDto createCustomerDto) {
        if (createCustomerDto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setUsername(createCustomerDto.getUsername());
        customer.setPassword(createCustomerDto.getPassword());
        customer.setEmail(createCustomerDto.getEmail());
        customer.setPhone(createCustomerDto.getPhone());
        customer.setAddress(createCustomerDto.getAddress());
        customer.setCompanyName(createCustomerDto.getCompanyName());

        return customer;
    }

    public DisplayCustomerDto toDisplayCustomerDto(final Customer customer) {
        if (customer == null) {
            return null;
        }

        return new DisplayCustomerDto(
                customer.getId(),
                customer.getUsername(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCompanyName()
        );
    }
}
