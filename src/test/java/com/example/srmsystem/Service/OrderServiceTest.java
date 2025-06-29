package com.example.srmsystem.Service;

import com.example.srmsystem.config.CacheConfig;
import com.example.srmsystem.dto.CreateOrderDto;
import com.example.srmsystem.dto.DisplayOrderDto;
import com.example.srmsystem.exception.EntityNotFoundException;
import com.example.srmsystem.exception.ValidationException;
import com.example.srmsystem.mapper.OrderMapper;
import com.example.srmsystem.model.Customer;
import com.example.srmsystem.model.Order;
import com.example.srmsystem.repository.CustomerRepository;
import com.example.srmsystem.repository.OrderRepository;
import com.example.srmsystem.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

    class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CacheConfig cacheConfig;

    @InjectMocks
    private OrderService orderService;

    private static final Long CUSTOMER_ID = 1L;
    private static final Long ORDER_ID = 100L;

    private Customer customer;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer(
                CUSTOMER_ID,
                "john_doe",
                "password123",
                "john.doe@example.com",
                "123-456-7890",
                "123 Main St",
                "Doe Corp",
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        order = new Order(
                ORDER_ID,
                "Order description",
                LocalDateTime.now(),
                customer,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createOrderForCustomer_whenValid_thenSuccess() {
        CreateOrderDto createOrderDto = new CreateOrderDto("New Order", LocalDateTime.now());

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(orderMapper.fromCreateOrderDto(createOrderDto, customer)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDisplayOrderDto(order)).thenReturn(new DisplayOrderDto());

        DisplayOrderDto result = orderService.createOrderForCustomer(CUSTOMER_ID, createOrderDto);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(order);
        verify(cacheConfig, times(1)).putAllOrders(anyList());
    }




    @Test
    void updateOrder_whenValid_thenSuccess() {
        CreateOrderDto createOrderDto = new CreateOrderDto("Updated Order", LocalDateTime.now());

        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
        when(orderRepository.findByCustomerIdAndId(CUSTOMER_ID, ORDER_ID)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDisplayOrderDto(order)).thenReturn(new DisplayOrderDto());

        DisplayOrderDto result = orderService.updateOrder(CUSTOMER_ID, ORDER_ID, createOrderDto);

        assertNotNull(result);
        assertEquals(createOrderDto.getDescription(), order.getDescription());
        assertEquals(createOrderDto.getOrderDate(), order.getOrderDate());
        verify(orderRepository, times(1)).save(order);
        verify(cacheConfig, times(1)).putAllOrders(anyList());
    }

    @Test
    void updateOrder_whenCustomerNotFound_thenThrowEntityNotFoundException() {
        CreateOrderDto createOrderDto = new CreateOrderDto("Update", LocalDateTime.now());

        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrder(CUSTOMER_ID, ORDER_ID, createOrderDto));
    }

    @Test
    void updateOrder_whenOrderNotFound_thenThrowEntityNotFoundException() {
        CreateOrderDto createOrderDto = new CreateOrderDto("Update", LocalDateTime.now());

        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
        when(orderRepository.findByCustomerIdAndId(CUSTOMER_ID, ORDER_ID)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrder(CUSTOMER_ID, ORDER_ID, createOrderDto));
    }

    @Test
    void deleteOrder_whenValid_thenSuccess() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
        when(orderRepository.findByCustomerIdAndId(CUSTOMER_ID, ORDER_ID)).thenReturn(order);

        orderService.deleteOrder(CUSTOMER_ID, ORDER_ID);

        verify(orderRepository, times(1)).delete(order);
        verify(cacheConfig, times(1)).removeAllOrders();
    }

    @Test
    void deleteOrder_whenCustomerNotFound_thenThrowEntityNotFoundException() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> orderService.deleteOrder(CUSTOMER_ID, ORDER_ID));
    }

    @Test
    void createOrderForCustomer_shouldUpdateCache() {
        CreateOrderDto dto = new CreateOrderDto("Order after cache", LocalDateTime.now());

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(orderMapper.fromCreateOrderDto(dto, customer)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDisplayOrderDto(order)).thenReturn(new DisplayOrderDto());

        orderService.createOrderForCustomer(CUSTOMER_ID, dto);

        verify(cacheConfig, times(1)).putAllOrders(anyList());
    }
    @Test
    void updateOrder_whenInvalid_thenDoNotSaveOrCache() {
        CreateOrderDto dto = new CreateOrderDto("", null);

        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
        when(orderRepository.findByCustomerIdAndId(CUSTOMER_ID, ORDER_ID)).thenReturn(order);

        assertThrows(ValidationException.class, () -> orderService.updateOrder(CUSTOMER_ID, ORDER_ID, dto));

        verify(orderRepository, never()).save(any());
        verify(cacheConfig, never()).putAllOrders(anyList());
    }

    @Test
    void getOrderById_whenCacheMissAndNotInDb_thenReturnNull() {
        when(cacheConfig.getAllOrders()).thenReturn(null);
        when(orderRepository.findByCustomerIdAndId(CUSTOMER_ID, ORDER_ID)).thenReturn(null);

        DisplayOrderDto result = orderService.getOrderById(CUSTOMER_ID, ORDER_ID);

        assertNull(result);
        verify(orderRepository, times(1)).findByCustomerIdAndId(CUSTOMER_ID, ORDER_ID);
    }


    @Test
    void deleteOrder_whenOrderNotFound_thenThrowEntityNotFoundException() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
        when(orderRepository.findByCustomerIdAndId(CUSTOMER_ID, ORDER_ID)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> orderService.deleteOrder(CUSTOMER_ID, ORDER_ID));
    }

    @Test
    void testGetOrderById_CacheMiss() {
        Order orderFromDb = new Order(ORDER_ID, "Order description", LocalDateTime.now(), customer, LocalDateTime.now(), LocalDateTime.now());
        DisplayOrderDto expectedOrderDto = new DisplayOrderDto(ORDER_ID, "Order description", LocalDateTime.now(),
                CUSTOMER_ID, "John Doe", LocalDateTime.now(), LocalDateTime.now());

        when(cacheConfig.getAllOrders()).thenReturn(Collections.emptyList());

        when(orderRepository.findByCustomerIdAndId(CUSTOMER_ID, ORDER_ID)).thenReturn(orderFromDb);

        when(orderMapper.toDisplayOrderDto(orderFromDb)).thenReturn(expectedOrderDto);

        DisplayOrderDto result = orderService.getOrderById(CUSTOMER_ID, ORDER_ID);

        assertNotNull(result);
        assertEquals(expectedOrderDto, result);

        verify(orderRepository, times(1)).findByCustomerIdAndId(CUSTOMER_ID, ORDER_ID);
        verify(orderMapper, times(1)).toDisplayOrderDto(orderFromDb);
        verify(cacheConfig, times(1)).putAllOrders(anyList());
    }

    @Test
    void testUpdateOrder_EmptyDescription() {
        CreateOrderDto createOrderDto = new CreateOrderDto(null, LocalDateTime.now());

        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
        when(orderRepository.findByCustomerIdAndId(CUSTOMER_ID, ORDER_ID)).thenReturn(new Order());

        ValidationException exception = assertThrows(ValidationException.class, () ->
                orderService.updateOrder(CUSTOMER_ID, ORDER_ID, createOrderDto));

        assertEquals("Description cannot be empty", exception.getErrors().get(0));
    }

    @Test
    void testGetOrdersByDate_Found() {
        // arrange
        LocalDate date = LocalDate.of(2024, 5, 5);
        List<Order> orders = List.of(order);

        when(orderRepository.findByOrderDate(date)).thenReturn(orders);

        // act
        List<Order> result = orderService.getOrdersByDate(date);

        // assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(order, result.get(0));

        verify(orderRepository, times(1)).findByOrderDate(date);
    }
    @Test
    void testGetOrdersByCustomerName_Found() {
        // arrange
        String customerName = "John Doe";
        List<Order> orders = List.of(order);

        when(orderRepository.findByCustomerName(customerName)).thenReturn(orders);

        // act
        List<Order> result = orderService.getOrdersByCustomerName(customerName);

        // assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(order, result.get(0));

        verify(orderRepository, times(1)).findByCustomerName(customerName);
    }


}
