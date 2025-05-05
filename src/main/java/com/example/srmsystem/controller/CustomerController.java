package com.example.srmsystem.controller;

import com.example.srmsystem.dto.CreateCustomerDto;
import com.example.srmsystem.dto.DisplayCustomerDto;
import com.example.srmsystem.exception.BadRequestException;
import com.example.srmsystem.exception.EntityNotFoundException;
import com.example.srmsystem.exception.NoContentException;
import com.example.srmsystem.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Slf4j
@Tag(name = "Customer Controller", description = "Управление клиентами: создание, обновление, удаление, просмотр")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Получить список всех клиентов", description = "Возвращает список всех клиентов. Если клиентов нет — возвращает 204 No Content.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список клиентов получен"),
            @ApiResponse(responseCode = "204", description = "Список клиентов пуст")
    })
    @GetMapping
    public ResponseEntity<List<DisplayCustomerDto>> getAllCustomers() {
        log.info("Запрос на получение всех клиентов");
        List<DisplayCustomerDto> customers = customerService.getAllCustomers();
        if (customers == null || customers.isEmpty()) {
            log.info("List of clients is empty");
            throw new NoContentException("List of clients is empty");
        }
        log.info("Found {} clients", customers.size());
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Получить клиента по ID", description = "Позволяет получить клиента по его идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный ID клиента"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisplayCustomerDto> getCustomerById(@PathVariable Long id) {
        log.info("Запрос на получение клиента с ID: {}", id);
        if (id == null || id <= 0) {
            log.warn("Incorrect ID of client: {}", id);
            throw new BadRequestException("Incorrect ID of client");
        }
        DisplayCustomerDto customer = customerService.getCustomerById(id);
        if (customer == null) {
            log.warn("Client with id {} not found:", id);
            throw new EntityNotFoundException(String.format("Client with id %d not found", id));
        }
        log.info("Client with ID {} found", id);
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Создать нового клиента", description = "Создает нового клиента на основе переданных данных.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные клиента")
    })
    @PostMapping
    public ResponseEntity<DisplayCustomerDto> addCustomer(@RequestBody @Valid CreateCustomerDto createCustomerDto) {
        log.info("Request to add client: {}", createCustomerDto);
        DisplayCustomerDto createdCustomer = customerService.createCustomer(createCustomerDto);
        log.info("Client created: {}", createdCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @Operation(summary = "Создать нескольких клиентов (bulk)", description = "Позволяет создать несколько клиентов одним запросом.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиенты успешно созданы"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные одного или нескольких клиентов"),
            @ApiResponse(responseCode = "409", description = "Один или несколько клиентов уже существуют"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/bulk")
    public ResponseEntity<List<DisplayCustomerDto>> addCustomersBulk(@RequestBody @Valid List<CreateCustomerDto> customerDtos) {
        log.info("Request to bulk add {} clients", customerDtos.size());

        boolean anyExists = customerService.anyCustomerExistsByUsername(customerDtos);

        if (anyExists) {
            log.error("One or more customers already exist based on username");
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
        }

        List<DisplayCustomerDto> createdCustomers = customerDtos.stream()
                .map(customerService::createCustomer)
                .collect(Collectors.toList());

        log.info("{} clients successfully created", createdCustomers.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomers);
    }

    @Operation(summary = "Обновить информацию о клиенте", description = "Обновляет информацию о клиенте по идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные клиента"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DisplayCustomerDto> updateCustomer(@PathVariable Long id, @Valid @RequestBody CreateCustomerDto createCustomerDto) {
        log.info("Request to update client with id {}: {}", id, createCustomerDto);
        try {
            DisplayCustomerDto updatedCustomer = customerService.updateCustomer(id, createCustomerDto);
            log.info("Customer with ID {} successfully updated", id);
            return ResponseEntity.ok(updatedCustomer);
        } catch (EntityNotFoundException ex) {
            log.error("Error updating customer with ID {}: {}", id, ex.getMessage());
            throw new EntityNotFoundException("Customer with id " + id + " not found");
        }
    }

    @Operation(summary = "Удалить клиента по ID", description = "Удаляет клиента по указанному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Клиент успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("Request to delete client with id {}", id);
        try {
            customerService.deleteCustomer(id);
            log.info("Client with id {} successfully deleted", id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            log.error("Client with id {} not found", id);
            throw new EntityNotFoundException("Client with id " + id + " not found");
        }
    }
}
