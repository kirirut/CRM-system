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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private static final String USERNAME_BLANK = "Username must not be blank";
    private static final String USERNAME_LENGTH = "Username must be between 3 and 20 characters";
    private static final String PASSWORD_BLANK = "Password must not be blank";
    private static final String PASSWORD_LENGTH = "Password must be at least 6 characters";
    private static final String EMAIL_BLANK = "Email must not be blank";
    private static final String EMAIL_INVALID = "Email must be valid";

    private static final String EMAIL_REGEX = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    private static final String LOG_FETCHING_CUSTOMER = "Fetching customer with ID: {}";
    private static final String LOG_FOUND_IN_CACHE = "Customer with ID {} found in cache";
    private static final String LOG_NOT_FOUND = "Customer with ID {} not found";
    private static final String LOG_SUCCESSFULLY_FETCHED = "Customer with ID {} successfully fetched from database";
    private static final String LOG_CREATING_CUSTOMER = "Creating new customer with username: {}";
    private static final String LOG_CUSTOMER_CREATED = "Customer with ID {} created successfully";
    private static final String LOG_UPDATING_CUSTOMER = "Updating customer with ID: {}";
    private static final String LOG_CUSTOMER_UPDATED = "Customer with ID {} updated successfully";
    private static final String LOG_DELETING_CUSTOMER = "Deleting customer with ID: {}";
    private static final String LOG_CUSTOMER_DELETED = "Customer with ID {} successfully deleted";
    private static final String LOG_FETCHING_ALL = "Fetching all customers";
    private static final String LOG_FETCHED_ALL = "Fetched {} customers from database";

    private static final String CUSTOMER_NOT_FOUND = "Customer not found with id: %d";

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
        log.info(LOG_FETCHING_ALL);
        List<Customer> customers = customerRepository.findAll();
        List<DisplayCustomerDto> displayCustomerDtos = customers.stream()
                .map(customerMapper::toDisplayCustomerDto)
                .toList();
        log.info(LOG_FETCHED_ALL, displayCustomerDtos.size());
        return displayCustomerDtos;
    }

    public DisplayCustomerDto getCustomerById(final Long id) {
        log.info(LOG_FETCHING_CUSTOMER, id);
        List<DisplayCustomerDto> cachedCustomers = cacheConfig.getAllCustomers();
        if (cachedCustomers != null) {
            DisplayCustomerDto customerDto = cachedCustomers.stream()
                    .filter(customer -> customer.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (customerDto != null) {
                log.info(LOG_FOUND_IN_CACHE, id);
                return customerDto;
            }
        }
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(LOG_NOT_FOUND, id);
                    return new EntityNotFoundException(String.format(CUSTOMER_NOT_FOUND, id));
                });
        DisplayCustomerDto displayCustomerDto = customerMapper.toDisplayCustomerDto(customer);
        cacheConfig.putAllCustomers(customerRepository.findAll());
        log.info(LOG_SUCCESSFULLY_FETCHED, id);
        return displayCustomerDto;
    }

    public DisplayCustomerDto createCustomer(final CreateCustomerDto dto) {
        List<String> errors = validateCustomerDto(dto);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        log.info(LOG_CREATING_CUSTOMER, dto.getUsername());

        Customer customer = customerMapper.fromCreateCustomerDto(dto);
        Customer saved = customerRepository.save(customer);
        cacheConfig.putAllCustomers(customerRepository.findAll());

        log.info(LOG_CUSTOMER_CREATED, saved.getId());
        return customerMapper.toDisplayCustomerDto(saved);
    }

    public DisplayCustomerDto updateCustomer(final Long id, final CreateCustomerDto dto) {
        List<String> errors = validateCustomerDto(dto);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        log.info(LOG_UPDATING_CUSTOMER, id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(LOG_NOT_FOUND, id);
                    return new EntityNotFoundException(String.format(CUSTOMER_NOT_FOUND, id));
                });

        customer.setUsername(dto.getUsername());
        customer.setPassword(dto.getPassword());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setCompanyName(dto.getCompanyName());

        Customer saved = customerRepository.save(customer);
        cacheConfig.putAllCustomers(customerRepository.findAll());

        log.info(LOG_CUSTOMER_UPDATED, saved.getId());
        return customerMapper.toDisplayCustomerDto(saved);
    }

    public boolean anyCustomerExistsByUsername(List<CreateCustomerDto> customerDtos) {
        return customerDtos.stream()
                .anyMatch(dto -> customerRepository.existsByUsername(dto.getUsername()));
    }

    @Transactional
    public void deleteCustomer(final Long id) {
        log.info(LOG_DELETING_CUSTOMER, id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(LOG_NOT_FOUND, id);
                    return new EntityNotFoundException(String.format(CUSTOMER_NOT_FOUND, id));
                });
        customerRepository.delete(customer);
        cacheConfig.removeAllCustomers();
        log.info(LOG_CUSTOMER_DELETED, id);
    }

    private List<String> validateCustomerDto(CreateCustomerDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            errors.add(USERNAME_BLANK);
        } else if (dto.getUsername().length() < 3 || dto.getUsername().length() > 20) {
            errors.add(USERNAME_LENGTH);
        }

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            errors.add(PASSWORD_BLANK);
        } else if (dto.getPassword().length() < 6) {
            errors.add(PASSWORD_LENGTH);
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            errors.add(EMAIL_BLANK);
        } else if (!dto.getEmail().matches(EMAIL_REGEX)) {
            errors.add(EMAIL_INVALID);
        }

        return errors;
    }
}
