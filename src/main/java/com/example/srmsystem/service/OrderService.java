package com.example.srmsystem.service;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final String CUSTOMER_NOT_FOUND_LOG_MSG = "Customer with ID: {} not found";
    private static final String ORDER_NOT_FOUND_LOG_MSG = "Order with ID: {} not found for customer with ID: {}";

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    private final CacheConfig cacheConfig;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        OrderMapper orderMapper,
                        CacheConfig cacheConfig) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
        this.cacheConfig = cacheConfig;
    }

    public List<DisplayOrderDto> getAllOrdersByCustomerId(Long customerId) {
        log.info("Fetching orders for customer with ID: {}", customerId);

        List<DisplayOrderDto> cachedOrders = cacheConfig.getAllOrders();
        if (cachedOrders != null) {
            log.info("Orders found in cache for customer with ID: {}", customerId);
            return cachedOrders.stream()
                    .filter(order -> order.getCustomerId().equals(customerId))
                    .toList();
        }

        log.info("No cached orders found for customer with ID: {}. Fetching from database.", customerId);
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        List<DisplayOrderDto> displayOrderDtos = orders.stream()
                .map(orderMapper::toDisplayOrderDto)
                .toList();
        cacheConfig.putAllOrders(orders);
        log.info("Fetched {} orders from database for customer with ID: {}", displayOrderDtos.size(), customerId);
        return displayOrderDtos;
    }

    public DisplayOrderDto getOrderById(Long customerId, Long orderId) {
        log.info("Fetching order with ID: {} for customer with ID: {}", orderId, customerId);

        List<DisplayOrderDto> cachedOrders = cacheConfig.getAllOrders();
        if (cachedOrders != null) {
            DisplayOrderDto orderDto = cachedOrders.stream()
                    .filter(order -> order.getCustomerId().equals(customerId) && order.getId().equals(orderId))
                    .findFirst()
                    .orElse(null);
            if (orderDto != null) {
                log.info("Order with ID: {} found in cache for customer with ID: {}", orderId, customerId);
                return orderDto;
            }
        }

        log.info("Order with ID: {} not found in cache. Fetching from database for customer with ID: {}", orderId, customerId);
        Order order = orderRepository.findByCustomerIdAndId(customerId, orderId);
        if (order == null) {
            log.error(ORDER_NOT_FOUND_LOG_MSG, orderId, customerId);
            return null;
        }

        DisplayOrderDto displayOrderDto = orderMapper.toDisplayOrderDto(order);
        cacheConfig.putAllOrders(orderRepository.findByCustomerId(customerId));
        log.info("Order with ID: {} successfully fetched from database for customer with ID: {}", orderId, customerId);
        return displayOrderDto;
    }

    @Transactional
    public DisplayOrderDto createOrderForCustomer(Long customerId, CreateOrderDto createOrderDto) {
        log.info("Creating order for customer with ID: {}", customerId);

        List<String> errors = new ArrayList<>();
        if (createOrderDto.getDescription() == null || createOrderDto.getDescription().trim().isEmpty()) {
            errors.add("Description cannot be empty");
        }
        if (createOrderDto.getOrderDate() == null) {
            errors.add("Order date cannot be null");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error(CUSTOMER_NOT_FOUND_LOG_MSG, customerId);
                    return new EntityNotFoundException("Customer not found");
                });

        Order order = orderMapper.fromCreateOrderDto(createOrderDto, customer);
        Order savedOrder = orderRepository.save(order);

        cacheConfig.putAllOrders(orderRepository.findByCustomerId(customerId));
        log.info("Order with ID: {} created successfully for customer with ID: {}", savedOrder.getId(), customerId);
        return orderMapper.toDisplayOrderDto(savedOrder);
    }

    @Transactional
    public DisplayOrderDto updateOrder(Long customerId, Long orderId, CreateOrderDto createOrderDto) {
        log.info("Updating order with ID: {} for customer with ID: {}", orderId, customerId);

        if (!customerRepository.existsById(customerId)) {
            log.error(CUSTOMER_NOT_FOUND_LOG_MSG, customerId);
            throw new EntityNotFoundException("Customer not found");
        }

        List<String> errors = new ArrayList<>();
        if (createOrderDto.getDescription() == null || createOrderDto.getDescription().trim().isEmpty()) {
            errors.add("Description cannot be empty");
        }
        if (createOrderDto.getOrderDate() == null) {
            errors.add("Order date cannot be null");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Order order = orderRepository.findByCustomerIdAndId(customerId, orderId);
        if (order == null) {
            log.error(ORDER_NOT_FOUND_LOG_MSG, orderId, customerId);
            throw new EntityNotFoundException("Order not found");
        }

        order.setDescription(createOrderDto.getDescription());
        order.setOrderDate(createOrderDto.getOrderDate());
        order.setUpdatedAt(null);

        Order updatedOrder = orderRepository.save(order);
        cacheConfig.putAllOrders(orderRepository.findByCustomerId(customerId));

        log.info("Order with ID: {} successfully updated for customer with ID: {}", updatedOrder.getId(), customerId);
        return orderMapper.toDisplayOrderDto(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long customerId, Long orderId) {
        log.info("Deleting order with ID: {} for customer with ID: {}", orderId, customerId);

        if (!customerRepository.existsById(customerId)) {
            log.error(CUSTOMER_NOT_FOUND_LOG_MSG, customerId);
            throw new EntityNotFoundException("Customer not found");
        }

        Order order = orderRepository.findByCustomerIdAndId(customerId, orderId);
        if (order == null) {
            log.error(ORDER_NOT_FOUND_LOG_MSG, orderId, customerId);
            throw new EntityNotFoundException("Order not found");
        }

        orderRepository.delete(order);
        cacheConfig.removeAllOrders();

        log.info("Order with ID: {} successfully deleted for customer with ID: {}", orderId, customerId);
    }

    public List<Order> getOrdersByCustomerName(String name) {
        log.info("Searching for orders by customer name: {}", name);
        List<Order> orders = orderRepository.findByCustomerName(name);
        if (orders.isEmpty()) {
            log.warn("No orders found for customer with name '{}'", name);
            throw new EntityNotFoundException("Orders not found for customer with name: " + name);
        }
        log.info("Found {} orders for customer '{}'", orders.size(), name);
        return orders;
    }

    public List<Order> getOrdersByDate(LocalDate date) {
        log.info("Searching for orders by date: {}", date);
        List<Order> orders = orderRepository.findByOrderDate(date);
        if (orders.isEmpty()) {
            log.warn("No orders found for date '{}'", date);
            throw new EntityNotFoundException("Orders not found for date: " + date);
        }
        log.info("Found {} orders for date '{}'", orders.size(), date);
        return orders;
    }

}