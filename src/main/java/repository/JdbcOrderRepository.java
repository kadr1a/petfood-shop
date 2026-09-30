package repository;

import model.Order;
import model.OrderStatus;
import util.DatabaseManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcOrderRepository implements OrderRepository {

    private static final String SELECT_BASE =
            "SELECT id, buyer_id, product_name, quantity, total_price, status, created_at FROM orders";

    @Override
    public List<Order> findAll() {
        String sql = SELECT_BASE + " ORDER BY id";
        return queryList(sql);
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = SELECT_BASE + " WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске заказа по id=" + id, e);
        }
    }

    @Override
    public Order save(Order order) {
        String sql = "INSERT INTO orders (buyer_id, product_name, quantity, total_price, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        LocalDateTime createdAt = order.getCreatedAt() != null
                ? order.getCreatedAt()
                : LocalDateTime.now();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, order.getBuyerId());
            st.setString(2, order.getProductName());
            st.setInt(3, order.getQuantity());
            st.setBigDecimal(4, order.getTotalPrice());
            st.setString(5, order.getStatus().name());
            st.setTimestamp(6, Timestamp.valueOf(createdAt));

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    order.setId(rs.getLong("id"));
                }
            }
            order.setCreatedAt(createdAt);
            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении заказа", e);
        }
    }

    @Override
    public boolean update(Order order) {
        String sql = "UPDATE orders SET buyer_id = ?, product_name = ?, quantity = ?, " +
                     "total_price = ?, status = ?, created_at = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, order.getBuyerId());
            st.setString(2, order.getProductName());
            st.setInt(3, order.getQuantity());
            st.setBigDecimal(4, order.getTotalPrice());
            st.setString(5, order.getStatus().name());
            st.setTimestamp(6, Timestamp.valueOf(order.getCreatedAt()));
            st.setLong(7, order.getId());

            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении заказа", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении заказа", e);
        }
    }

    @Override
    public List<Order> findByProductNameContaining(String fragment) {
        String sql = SELECT_BASE + " WHERE LOWER(product_name) LIKE LOWER(?) ORDER BY id";
        return queryList(sql, "%" + fragment + "%");
    }

    @Override
    public List<Order> findByBuyerId(Long buyerId) {
        String sql = SELECT_BASE + " WHERE buyer_id = ? ORDER BY id";
        return queryList(sql, buyerId);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        String sql = SELECT_BASE + " WHERE status = ? ORDER BY id";
        return queryList(sql, status.name());
    }

    @Override
    public List<Order> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to) {
        String sql = SELECT_BASE + " WHERE created_at >= ? AND created_at <= ? ORDER BY created_at";
        return queryListTimestamps(sql, from, to);
    }

    @Override
    public List<Order> findByTotalPriceBetween(BigDecimal min, BigDecimal max) {
        String sql = SELECT_BASE + " WHERE total_price >= ? AND total_price <= ? ORDER BY total_price";
        return queryListPrices(sql, min, max);
    }

    private List<Order> queryList(String sql, Object... params) {
        List<Order> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                Object p = params[i];
                if (p instanceof Long l) {
                    st.setLong(i + 1, l);
                } else if (p instanceof String s) {
                    st.setString(i + 1, s);
                }
            }

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при выполнении запроса заказов", e);
        }
        return result;
    }

    private List<Order> queryListTimestamps(String sql, LocalDateTime from, LocalDateTime to) {
        List<Order> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setTimestamp(1, Timestamp.valueOf(from));
            st.setTimestamp(2, Timestamp.valueOf(to));

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при фильтрации заказов по дате", e);
        }
        return result;
    }

    private List<Order> queryListPrices(String sql, BigDecimal min, BigDecimal max) {
        List<Order> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setBigDecimal(1, min);
            st.setBigDecimal(2, max);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при фильтрации заказов по сумме", e);
        }
        return result;
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;
        OrderStatus status = OrderStatus.valueOf(rs.getString("status"));

        return new Order(
                rs.getLong("id"),
                rs.getLong("buyer_id"),
                rs.getString("product_name"),
                rs.getInt("quantity"),
                rs.getBigDecimal("total_price"),
                status,
                createdAt
        );
    }
}
