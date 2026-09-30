package repository;

import model.Order;
import model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    List<Order> findAll();

    Optional<Order> findById(Long id);

    Order save(Order order);

    boolean update(Order order);

    boolean deleteById(Long id);

    List<Order> findByProductNameContaining(String fragment);

    List<Order> findByBuyerId(Long buyerId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    List<Order> findByTotalPriceBetween(BigDecimal min, BigDecimal max);
}
