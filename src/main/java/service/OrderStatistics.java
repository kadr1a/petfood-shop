package service;

import model.OrderStatus;

import java.math.BigDecimal;
import java.util.EnumMap;

public class OrderStatistics {

    private final long totalBuyers;
    private final long totalOrders;
    private final EnumMap<OrderStatus, Long> countByStatus;
    private final BigDecimal totalSum;
    private final BigDecimal averageSum;
    private final long cancelledCount;

    public OrderStatistics(long totalBuyers,
                           long totalOrders,
                           EnumMap<OrderStatus, Long> countByStatus,
                           BigDecimal totalSum,
                           BigDecimal averageSum,
                           long cancelledCount) {
        this.totalBuyers = totalBuyers;
        this.totalOrders = totalOrders;
        this.countByStatus = countByStatus;
        this.totalSum = totalSum;
        this.averageSum = averageSum;
        this.cancelledCount = cancelledCount;
    }

    public long getTotalBuyers() {
        return totalBuyers;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public EnumMap<OrderStatus, Long> getCountByStatus() {
        return countByStatus;
    }

    public BigDecimal getTotalSum() {
        return totalSum;
    }

    public BigDecimal getAverageSum() {
        return averageSum;
    }

    public long getCancelledCount() {
        return cancelledCount;
    }
}
