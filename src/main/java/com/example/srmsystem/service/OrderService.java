package com.example.srmsystem.service;

import com.example.srmsystem.dto.CreateOrderDto;
import com.example.srmsystem.dto.DisplayOrderDto;
import com.example.srmsystem.mapper.OrderMapper;
import com.example.srmsystem.model.Customer;
import com.example.srmsystem.model.Order;
import com.example.srmsystem.repository.CustomerRepository;
import com.example.srmsystem.repository.OrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
    }

    public List<DisplayOrderDto> getAllOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toDisplayOrderDto)
                .toList();
    }

    public DisplayOrderDto getOrderById(Long customerId, Long orderId) {
        Order order = orderRepository.findByCustomerIdAndId(customerId, orderId);
        if (order == null) {
            return null;
        }
        return orderMapper.toDisplayOrderDto(order);
    }

    @Transactional
    public DisplayOrderDto createOrderForCustomer(Long customerId, CreateOrderDto createOrderDto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Order order = orderMapper.fromCreateOrderDto(createOrderDto, customer);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDisplayOrderDto(savedOrder);
    }

    @Transactional
    public DisplayOrderDto updateOrder(Long customerId, Long orderId, CreateOrderDto createOrderDto) {
        Order order = orderRepository.findByCustomerIdAndId(customerId, orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        order.setDescription(createOrderDto.getDescription());
        order.setOrderDate(createOrderDto.getOrderDate());
        order.setUpdatedAt(null);

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDisplayOrderDto(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long customerId, Long orderId) {
        Order order = orderRepository.findByCustomerIdAndId(customerId, orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        orderRepository.delete(order);
    }
}
