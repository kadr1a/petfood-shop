package model;

public enum OrderStatus {
    CREATED,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    /**
     * Проверяет, допустим ли переход из текущего статуса в новый.
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        if (newStatus == null) {
            return false;
        }
        if (this == newStatus) {
            return true;
        }
        return switch (this) {
            case CREATED -> newStatus == PAID || newStatus == CANCELLED;
            case PAID -> newStatus == SHIPPED || newStatus == CANCELLED;
            case SHIPPED -> newStatus == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
