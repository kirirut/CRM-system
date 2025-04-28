package com.example.srmsystem.Service;

import com.example.srmsystem.config.CacheConfig;
import com.example.srmsystem.dto.CreateCustomerDto;
import com.example.srmsystem.dto.DisplayCustomerDto;
import com.example.srmsystem.exception.EntityNotFoundException;
import com.example.srmsystem.exception.ValidationException;
import com.example.srmsystem.mapper.CustomerMapper;
import com.example.srmsystem.model.Customer;
import com.example.srmsystem.repository.CustomerRepository;
import com.example.srmsystem.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CacheConfig cacheConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- getAllCustomers() ---

    @Test
    void getAllCustomers_whenCacheExists_thenReturnCachedCustomers() {
        List<DisplayCustomerDto> cachedCustomers = List.of(new DisplayCustomerDto());
        when(cacheConfig.getAllCustomers()).thenReturn(cachedCustomers);

        List<DisplayCustomerDto> result = customerService.getAllCustomers();

        assertEquals(cachedCustomers, result);
        verifyNoInteractions(customerRepository);
    }

    @Test
    void getAllCustomers_whenCacheEmpty_thenFetchFromDb() {
        when(cacheConfig.getAllCustomers()).thenReturn(null);
        List<Customer> customers = List.of(new Customer());
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toDisplayCustomerDto(any())).thenReturn(new DisplayCustomerDto());

        List<DisplayCustomerDto> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        verify(customerRepository).findAll();
        verify(cacheConfig).putAllCustomers(customers);
    }

    // --- getCustomerById() ---

    @Test
    void getCustomerById_whenCustomerInCache_thenReturnFromCache() {
        DisplayCustomerDto cachedCustomer = new DisplayCustomerDto();
        cachedCustomer.setId(1L);
        when(cacheConfig.getAllCustomers()).thenReturn(List.of(cachedCustomer));

        DisplayCustomerDto result = customerService.getCustomerById(1L);

        assertEquals(cachedCustomer, result);
        verifyNoInteractions(customerRepository);
    }

    @Test
    void getCustomerById_whenNotInCache_thenFetchFromDb() {
        when(cacheConfig.getAllCustomers()).thenReturn(null);
        Customer customer = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toDisplayCustomerDto(customer)).thenReturn(new DisplayCustomerDto());

        DisplayCustomerDto result = customerService.getCustomerById(1L);

        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(cacheConfig).putAllCustomers(anyList());
    }

    @Test
    void getCustomerById_whenNotFound_thenThrowException() {
        when(cacheConfig.getAllCustomers()).thenReturn(null);
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.getCustomerById(1L));
    }

    // --- createCustomer() ---

    @Test
    void createCustomer_whenValidInput_thenSaveAndReturn() {
        CreateCustomerDto dto = new CreateCustomerDto();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setEmail("test@example.com");

        Customer customer = new Customer();
        when(customerMapper.fromCreateCustomerDto(dto)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDisplayCustomerDto(customer)).thenReturn(new DisplayCustomerDto());

        DisplayCustomerDto result = customerService.createCustomer(dto);

        assertNotNull(result);
        verify(customerRepository).save(customer);
        verify(cacheConfig).putAllCustomers(anyList());
    }

    @Test
    void createCustomer_whenInvalidInput_thenThrowValidationException() {
        CreateCustomerDto dto = new CreateCustomerDto(); // пустые поля

        assertThrows(ValidationException.class, () -> customerService.createCustomer(dto));
    }

    // --- updateCustomer() ---

    @Test
    void updateCustomer_whenCustomerExists_thenUpdateAndReturn() {
        CreateCustomerDto dto = new CreateCustomerDto();
        dto.setUsername("newname");
        dto.setPassword("newpass123");
        dto.setEmail("new@example.com");

        Customer existing = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(existing)).thenReturn(existing);
        when(customerMapper.toDisplayCustomerDto(existing)).thenReturn(new DisplayCustomerDto());

        DisplayCustomerDto result = customerService.updateCustomer(1L, dto);

        assertNotNull(result);
        verify(customerRepository).save(existing);
    }

    @Test
    void updateCustomer_whenCustomerNotFound_thenThrowException() {
        CreateCustomerDto dto = new CreateCustomerDto();
        dto.setUsername("name");
        dto.setPassword("pass123");
        dto.setEmail("test@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.updateCustomer(1L, dto));
    }

    @Test
    void updateCustomer_whenInvalidInput_thenThrowValidationException() {
        CreateCustomerDto dto = new CreateCustomerDto(); // пустой dto

        assertThrows(ValidationException.class, () -> customerService.updateCustomer(1L, dto));
    }

    // --- anyCustomerExistsByUsername() ---

    @Test
    void anyCustomerExistsByUsername_whenUsernameExists_thenReturnTrue() {
        CreateCustomerDto dto = new CreateCustomerDto();
        dto.setUsername("user");

        when(customerRepository.existsByUsername("user")).thenReturn(true);

        boolean exists = customerService.anyCustomerExistsByUsername(List.of(dto));

        assertTrue(exists);
    }

    @Test
    void anyCustomerExistsByUsername_whenNoUsernameExists_thenReturnFalse() {
        CreateCustomerDto dto = new CreateCustomerDto();
        dto.setUsername("user");

        when(customerRepository.existsByUsername("user")).thenReturn(false);

        boolean exists = customerService.anyCustomerExistsByUsername(List.of(dto));

        assertFalse(exists);
    }

    // --- deleteCustomer() ---

    @Test
    void deleteCustomer_whenCustomerExists_thenDelete() {
        Customer customer = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(1L);

        verify(customerRepository).delete(customer);
        verify(cacheConfig).removeAllCustomers();
    }

    @Test
    void deleteCustomer_whenCustomerNotFound_thenThrowException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.deleteCustomer(1L));
    }
    @Test
    void testCreateCustomer_EmptyUsername() {
        CreateCustomerDto createCustomerDto = new CreateCustomerDto("", "password123", "email@example.com");

        ValidationException exception = assertThrows(ValidationException.class, () ->
                customerService.createCustomer(createCustomerDto));

        assertEquals("Username must not be blank", exception.getErrors().get(0));
    }

    @Test
    void testCreateCustomer_UsernameTooShort() {
        CreateCustomerDto createCustomerDto = new CreateCustomerDto("ab", "password123", "email@example.com");

        ValidationException exception = assertThrows(ValidationException.class, () ->
                customerService.createCustomer(createCustomerDto));

        assertEquals("Username must be between 3 and 20 characters", exception.getErrors().get(0));
    }

    @Test
    void testCreateCustomer_UsernameTooLong() {
        CreateCustomerDto createCustomerDto = new CreateCustomerDto("a".repeat(21), "password123", "email@example.com");

        ValidationException exception = assertThrows(ValidationException.class, () ->
                customerService.createCustomer(createCustomerDto));

        assertEquals("Username must be between 3 and 20 characters", exception.getErrors().get(0));
    }

    @Test
    void testCreateCustomer_EmptyPassword() {
        CreateCustomerDto createCustomerDto = new CreateCustomerDto("username", "", "email@example.com");

        ValidationException exception = assertThrows(ValidationException.class, () ->
                customerService.createCustomer(createCustomerDto));

        assertEquals("Password must not be blank", exception.getErrors().get(0));
    }

    @Test
    void testCreateCustomer_PasswordTooShort() {
        CreateCustomerDto createCustomerDto = new CreateCustomerDto("username", "short", "email@example.com");

        ValidationException exception = assertThrows(ValidationException.class, () ->
                customerService.createCustomer(createCustomerDto));

        assertEquals("Password must be at least 6 characters", exception.getErrors().get(0));
    }

    @Test
    void testCreateCustomer_EmptyEmail() {
        CreateCustomerDto createCustomerDto = new CreateCustomerDto("username", "password123", "");

        ValidationException exception = assertThrows(ValidationException.class, () ->
                customerService.createCustomer(createCustomerDto));

        assertEquals("Email must not be blank", exception.getErrors().get(0));
    }

    @Test
    void testCreateCustomer_InvalidEmail() {
        CreateCustomerDto createCustomerDto = new CreateCustomerDto("username", "password123", "invalid-email");

        ValidationException exception = assertThrows(ValidationException.class, () ->
                customerService.createCustomer(createCustomerDto));

        assertEquals("Email must be valid", exception.getErrors().get(0));
    }

}
