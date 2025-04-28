package com.example.srmsystem.repository;

import com.example.srmsystem.model.Order;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // JPQL — по имени клиента
    @Query("SELECT o FROM Order o WHERE o.customer.username = :name")
    List<Order> findByCustomerName(@Param("name") String name);


    // Native SQL — по дате (отбрасываем время)
    @SuppressWarnings("checkstyle:WhitespaceAround")
    @Query(value ="SELECT * FROM orders WHERE DATE(order_date)=:date", nativeQuery = true)
    List<Order> findByOrderDate(@Param("date") LocalDate date);

    List<Order> findByCustomerId(Long customerId);

    Order findByCustomerIdAndId(Long customerId, Long orderId);
}
