package com.example.srmsystem.controller;

import com.example.srmsystem.dto.CreateCustomerDto;
import com.example.srmsystem.dto.DisplayCustomerDto;
import com.example.srmsystem.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Customer Controller", description = "Управление клиентами: создание, обновление, удаление, просмотр")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Получить список всех клиентов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список клиентов получен"),
            @ApiResponse(responseCode = "204", description = "Список клиентов пуст")
    })
    @GetMapping
    public ResponseEntity<List<DisplayCustomerDto>> getAllCustomers() {
        log.info("Запрос на получение всех клиентов");
        List<DisplayCustomerDto> customers = customerService.getAllCustomers();
        if (customers == null || customers.isEmpty()) {
            log.info("Список клиентов пуст");
            return ResponseEntity.noContent().build();
        }
        log.info("Найдено {} клиентов", customers.size());
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Получить клиента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный ID"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisplayCustomerDto> getCustomerById(@PathVariable Long id) {
        log.info("Запрос на получение клиента с ID: {}", id);
        if (id == null || id <= 0) {
            log.warn("Некорректный ID клиента: {}", id);
            return ResponseEntity.badRequest().build();
        }
        DisplayCustomerDto customer = customerService.getCustomerById(id);
        if (customer == null) {
            log.warn("Клиент с ID {} не найден", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Клиент с ID {} найден", id);
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Создать нового клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент успешно создан")
    })
    @PostMapping
    public ResponseEntity<DisplayCustomerDto> addCustomer(@RequestBody CreateCustomerDto createCustomerDto) {
        log.info("Запрос на создание клиента: {}", createCustomerDto);
        DisplayCustomerDto createdCustomer = customerService.createCustomer(createCustomerDto);
        log.info("Клиент успешно создан: {}", createdCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @Operation(summary = "Обновить информацию о клиенте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DisplayCustomerDto> updateCustomer(@PathVariable Long id, @RequestBody CreateCustomerDto createCustomerDto) {
        log.info("Запрос на обновление клиента с ID {}: {}", id, createCustomerDto);
        DisplayCustomerDto updatedCustomer = customerService.updateCustomer(id, createCustomerDto);
        log.info("Клиент с ID {} успешно обновлён", id);
        return ResponseEntity.ok(updatedCustomer);
    }

    @Operation(summary = "Удалить клиента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Клиент успешно удален")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("Запрос на удаление клиента с ID {}", id);
        customerService.deleteCustomer(id);
        log.info("Клиент с ID {} успешно удалён", id);
        return ResponseEntity.noContent().build();
    }
}
