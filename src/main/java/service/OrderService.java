package service;

import exception.BusinessException;
import exception.EntityNotFoundException;
import model.Order;
import model.OrderStatus;
import repository.JdbcOrderRepository;
import repository.OrderRepository;
import util.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {

    private final OrderRepository repository = new JdbcOrderRepository();
    private final BuyerService buyerService;

    public OrderService(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Order findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Заказ с id=" + id + " не найден"));
    }

    public Order create(Order order) {
        if (order == null) {
            throw new BusinessException("Заказ не может быть null");
        }
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.CREATED);
        }
        validateOrderFields(order);
        buyerService.findById(order.getBuyerId());
        validateStatusTransition(null, order.getStatus());

        return repository.save(order);
    }

    public void update(Order order) {
        if (order.getId() == null) {
            throw new BusinessException("Не указан id заказа для обновления");
        }
        Order existing = findById(order.getId());
        validateOrderFields(order);
        buyerService.findById(order.getBuyerId());
        validateStatusTransition(existing.getStatus(), order.getStatus());

        boolean updated = repository.update(order);
        if (!updated) {
            throw new EntityNotFoundException("Заказ с id=" + order.getId() + " не найден");
        }
    }

    public void deleteById(Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException("Заказ с id=" + id + " не найден");
        }
    }

    public List<Order> searchByProductName(String fragment) {
        if (StringUtil.isBlank(fragment)) {
            throw new BusinessException("Фрагмент названия товара не может быть пустым");
        }
        return repository.findByProductNameContaining(fragment.trim());
    }

    public List<Order> searchByBuyerId(Long buyerId) {
        buyerService.findById(buyerId);
        return repository.findByBuyerId(buyerId);
    }

    public List<Order> filterByStatus(OrderStatus status) {
        if (status == null) {
            throw new BusinessException("Статус не может быть пустым");
        }
        return repository.findByStatus(status);
    }

    public List<Order> filterByDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new BusinessException("Даты диапазона должны быть указаны");
        }
        if (from.isAfter(to)) {
            throw new BusinessException("Начало диапазона не может быть позже конца");
        }
        return repository.findByCreatedAtBetween(from, to);
    }

    public List<Order> filterByPriceRange(BigDecimal min, BigDecimal max) {
        if (min == null || max == null) {
            throw new BusinessException("Границы суммы должны быть указаны");
        }
        if (min.compareTo(max) > 0) {
            throw new BusinessException("Минимальная сумма не может быть больше максимальной");
        }
        return repository.findByTotalPriceBetween(min, max);
    }

    /** Сортировка через Stream API (по дате создания). */
    public List<Order> sortByCreatedAt() {
        return findAll().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public List<Order> sortByTotalPrice() {
        return findAll().stream()
                .sorted(Comparator.comparing(Order::getTotalPrice))
                .collect(Collectors.toList());
    }

    public List<Order> sortByProductName() {
        return findAll().stream()
                .sorted(Comparator.comparing(o -> o.getProductName().toLowerCase()))
                .collect(Collectors.toList());
    }

    public OrderStatistics calculateStatistics() {
        List<Order> orders = findAll();
        long totalBuyers = buyerService.countAll();
        long totalOrders = orders.size();

        EnumMap<OrderStatus, Long> countByStatus = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            countByStatus.put(status, 0L);
        }

        BigDecimal totalSum = BigDecimal.ZERO;
        long cancelled = 0;

        for (Order order : orders) {
            countByStatus.merge(order.getStatus(), 1L, Long::sum);
            if (order.getTotalPrice() != null) {
                totalSum = totalSum.add(order.getTotalPrice());
            }
            if (order.getStatus() == OrderStatus.CANCELLED) {
                cancelled++;
            }
        }

        BigDecimal average = totalOrders == 0
                ? BigDecimal.ZERO
                : totalSum.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);

        return new OrderStatistics(
                totalBuyers,
                totalOrders,
                countByStatus,
                totalSum,
                average,
                cancelled
        );
    }

    private void validateOrderFields(Order order) {
        if (order.getBuyerId() == null) {
            throw new BusinessException("Не указан покупатель для заказа");
        }
        if (StringUtil.isBlank(order.getProductName())) {
            throw new BusinessException("Название товара не может быть пустым");
        }
        if (order.getQuantity() <= 0) {
            throw new BusinessException("Количество должно быть больше 0");
        }
        if (order.getTotalPrice() == null || order.getTotalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Сумма заказа не может быть отрицательной");
        }
        if (order.getStatus() == null) {
            throw new BusinessException("Статус заказа не указан");
        }
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus target) {
        if (current == null) {
            if (target != OrderStatus.CREATED) {
                throw new BusinessException(
                        "Новый заказ может быть только со статусом CREATED");
            }
            return;
        }
        if (!current.canTransitionTo(target)) {
            throw new BusinessException(
                    "Переход статуса " + current + " → " + target + " запрещён");
        }
    }
}
