package repository;

import model.Buyer;
import util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuyerRepository {

    public List<Buyer> findAll() {
        String sql = "SELECT id, full_name, email, phone, address FROM buyers ORDER BY id";
        List<Buyer> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка покупателей", e);
        }
        return result;
    }

    public Optional<Buyer> findById(Long id) {
        String sql = "SELECT id, full_name, email, phone, address FROM buyers WHERE id = ?";

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
            throw new RuntimeException("Ошибка при поиске покупателя по id=" + id, e);
        }
    }

    public Optional<Buyer> findByEmail(String email) {
        String sql = "SELECT id, full_name, email, phone, address FROM buyers WHERE email = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске покупателя по email=" + email, e);
        }
    }

    public Buyer save(Buyer buyer) {
        String sql = "INSERT INTO buyers (full_name, email, phone, address) " +
                     "VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, buyer.getFullName());
            st.setString(2, buyer.getEmail());
            st.setString(3, buyer.getPhone());
            st.setString(4, buyer.getAddress());

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    buyer.setId(rs.getLong("id"));
                }
            }
            return buyer;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении покупателя", e);
        }
    }

    public boolean update(Buyer buyer) {
        String sql = "UPDATE buyers SET full_name = ?, email = ?, phone = ?, address = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, buyer.getFullName());
            st.setString(2, buyer.getEmail());
            st.setString(3, buyer.getPhone());
            st.setString(4, buyer.getAddress());
            st.setLong(5, buyer.getId());

            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении покупателя", e);
        }
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM buyers WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setLong(1, id);

            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении покупателя", e);
        }
    }

    private Buyer mapRow(ResultSet rs) throws SQLException {
        return new Buyer(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address")
        );
    }
}