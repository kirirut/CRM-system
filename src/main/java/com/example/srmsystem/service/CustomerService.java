package com.example.srmsystem.service;

import com.example.srmsystem.config.CacheConfig;
import com.example.srmsystem.dto.CreateCustomerDto;
import com.example.srmsystem.dto.DisplayCustomerDto;
import com.example.srmsystem.exception.EntityNotFoundException;
import com.example.srmsystem.exception.ValidationException;
import com.example.srmsystem.mapper.CustomerMapper;
import com.example.srmsystem.model.Customer;
import com.example.srmsystem.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

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
        log.info("Fetching all customers");
        List<DisplayCustomerDto> cachedCustomers = cacheConfig.getAllCustomers();
        if (cachedCustomers != null) {
            log.info("Customers found in cache");
            return cachedCustomers;
        }
        List<Customer> customers = customerRepository.findAll();
        List<DisplayCustomerDto> displayCustomerDtos = customers.stream()
                .map(customerMapper::toDisplayCustomerDto)
                .collect(Collectors.toList());
        cacheConfig.putAllCustomers(customers);
        log.info("Fetched {} customers from database", displayCustomerDtos.size());
        return displayCustomerDtos;
    }

    public DisplayCustomerDto getCustomerById(final Long id) {
        log.info("Fetching customer with ID: {}", id);
        List<DisplayCustomerDto> cachedCustomers = cacheConfig.getAllCustomers();
        if (cachedCustomers != null) {
            DisplayCustomerDto customerDto = cachedCustomers.stream()
                    .filter(customer -> customer.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (customerDto != null) {
                log.info("Customer with ID {} found in cache", id);
                return customerDto;
            }
        }
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer with ID {} not found", id);
                    return new EntityNotFoundException(
                            String.format("Customer not found with id: %d", id)
                    );
                });
        DisplayCustomerDto displayCustomerDto = customerMapper.toDisplayCustomerDto(customer);
        cacheConfig.putAllCustomers(customerRepository.findAll());
        log.info("Customer with ID {} successfully fetched from database", id);
        return displayCustomerDto;
    }

    public DisplayCustomerDto createCustomer(final CreateCustomerDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            errors.add("Username must not be blank");
        } else if (dto.getUsername().length() < 3 || dto.getUsername().length() > 20) {
            errors.add("Username must be between 3 and 20 characters");
        }

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            errors.add("Password must not be blank");
        } else if (dto.getPassword().length() < 6) {
            errors.add("Password must be at least 6 characters");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            errors.add("Email must not be blank");
        } else if (!dto.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.add("Email must be valid");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        log.info("Creating new customer with username: {}", dto.getUsername());

        Customer customer = customerMapper.fromCreateCustomerDto(dto);
        Customer saved = customerRepository.save(customer);
        cacheConfig.putAllCustomers(customerRepository.findAll());

        log.info("Customer with ID {} created successfully", saved.getId());
        return customerMapper.toDisplayCustomerDto(saved);
    }


    public DisplayCustomerDto updateCustomer(final Long id, final CreateCustomerDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            errors.add("Username must not be blank");
        } else if (dto.getUsername().length() < 3 || dto.getUsername().length() > 20) {
            errors.add("Username must be between 3 and 20 characters");
        }

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            errors.add("Password must not be blank");
        } else if (dto.getPassword().length() < 6) {
            errors.add("Password must be at least 6 characters");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            errors.add("Email must not be blank");
        } else if (!dto.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.add("Email must be valid");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        log.info("Updating customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer with ID {} not found", id);
                    return new EntityNotFoundException(
                            String.format("Customer not found with id: %d", id)
                    );
                });

        customer.setUsername(dto.getUsername());
        customer.setPassword(dto.getPassword());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setCompanyName(dto.getCompanyName());

        Customer saved = customerRepository.save(customer);
        cacheConfig.putAllCustomers(customerRepository.findAll());

        log.info("Customer with ID {} updated successfully", saved.getId());
        return customerMapper.toDisplayCustomerDto(saved);
    }

    public boolean anyCustomerExistsByUsername(List<CreateCustomerDto> customerDtos) {
        return customerDtos.stream()
                .anyMatch(dto -> customerRepository.existsByUsername(dto.getUsername()));
    }

    @Transactional
    public void deleteCustomer(final Long id) {
        log.info("Deleting customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer with ID {} not found", id);
                    return new EntityNotFoundException(
                            String.format("Customer not found with id: %d", id)
                    );
                });
        customerRepository.delete(customer);
        cacheConfig.removeAllCustomers();
        log.info("Customer with ID {} successfully deleted", id);
    }
}
