package com.example.srmsystem.repository;

import com.example.srmsystem.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    Order findByCustomerIdAndId(Long customerId, Long orderId);
}
