package com.example.srmsystem.service;

import com.example.srmsystem.config.CacheConfig;
import com.example.srmsystem.dto.CreateCustomerDto;
import com.example.srmsystem.dto.DisplayCustomerDto;
import com.example.srmsystem.exception.NotFoundException;
import com.example.srmsystem.mapper.CustomerMapper;
import com.example.srmsystem.model.Customer;
import com.example.srmsystem.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private static final String CUSTOMER_NOT_FOUND_MESSAGE = "Customer not found with id ";
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CacheConfig cacheConfig;

    public CustomerService(CustomerRepository customerRepository,
                           CustomerMapper customerMapper,
                           CacheConfig cacheConfig) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.cacheConfig = cacheConfig;
    }

    public List<DisplayCustomerDto> getAllCustomers() {
        List<DisplayCustomerDto> cachedCustomers = cacheConfig.getAllCustomers();
        if (cachedCustomers != null) {
            return cachedCustomers;
        }
        List<Customer> customers = customerRepository.findAll();
        List<DisplayCustomerDto> displayCustomerDtos = customers.stream()
                .map(customerMapper::toDisplayCustomerDto)
                .collect(Collectors.toList());
        cacheConfig.putAllCustomers(customers);
        return displayCustomerDtos;
    }

    public DisplayCustomerDto getCustomerById(final Long id) {
        List<DisplayCustomerDto> cachedCustomers = cacheConfig.getAllCustomers();
        if (cachedCustomers != null) {
            DisplayCustomerDto customerDto = cachedCustomers.stream()
                    .filter(customer -> customer.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (customerDto != null) {
                return customerDto;
            }
        }
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND_MESSAGE + id));
        DisplayCustomerDto displayCustomerDto = customerMapper.toDisplayCustomerDto(customer);
        cacheConfig.putAllCustomers(customerRepository.findAll());
        return displayCustomerDto;
    }

    public DisplayCustomerDto createCustomer(final CreateCustomerDto customerDto) {
        Customer customer = customerMapper.fromCreateCustomerDto(customerDto);
        Customer saved = customerRepository.save(customer);
        cacheConfig.putAllCustomers(customerRepository.findAll());
        return customerMapper.toDisplayCustomerDto(saved);
    }

    public DisplayCustomerDto updateCustomer(final Long id, final CreateCustomerDto customerDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, CUSTOMER_NOT_FOUND_MESSAGE + id));

        customer.setUsername(customerDto.getUsername());
        customer.setEmail(customerDto.getEmail());
        customer.setPhone(customerDto.getPhone());
        customer.setAddress(customerDto.getAddress());
        customer.setCompanyName(customerDto.getCompanyName());

        Customer saved = customerRepository.save(customer);
        cacheConfig.putAllCustomers(customerRepository.findAll());
        return customerMapper.toDisplayCustomerDto(saved);
    }

    @Transactional
    public void deleteCustomer(final Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, CUSTOMER_NOT_FOUND_MESSAGE + id));
        customerRepository.delete(customer);
        cacheConfig.removeAllCustomers();
    }
}
