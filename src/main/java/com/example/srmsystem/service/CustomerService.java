package com.example.srmsystem.service;

import com.example.srmsystem.dto.CreateCustomerDto;
import com.example.srmsystem.dto.DisplayCustomerDto;
import com.example.srmsystem.exception.NotFoundException;
import com.example.srmsystem.mapper.CustomerMapper;
import com.example.srmsystem.model.Customer;
import com.example.srmsystem.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class CustomerService {
    private static final String CUSTOMER_NOT_FOUND_MESSAGE = "Customer not found with id ";
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository,
                           CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<DisplayCustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDisplayCustomerDto)
                .toList();
    }

    public DisplayCustomerDto getCustomerById(final Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND_MESSAGE + id));
        return customerMapper.toDisplayCustomerDto(customer);
    }

    public DisplayCustomerDto createCustomer(final CreateCustomerDto customerDto) {
        Customer customer = customerMapper.fromCreateCustomerDto(customerDto);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toDisplayCustomerDto(saved);
    }

    public DisplayCustomerDto updateCustomer(final Long id, final CreateCustomerDto customerDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, CUSTOMER_NOT_FOUND_MESSAGE + id));

        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setPhone(customerDto.getPhone());
        customer.setAddress(customerDto.getAddress());
        customer.setCompanyName(customerDto.getCompanyName());

        Customer saved = customerRepository.save(customer);
        return customerMapper.toDisplayCustomerDto(saved);
    }

    @Transactional
    public void deleteCustomer(final Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, CUSTOMER_NOT_FOUND_MESSAGE + id));
        customerRepository.delete(customer);
    }
}
