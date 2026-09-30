package repository;

import model.Buyer;

import java.util.List;
import java.util.Optional;

public interface BuyerRepository {

    List<Buyer> findAll();

    Optional<Buyer> findById(Long id);

    Optional<Buyer> findByEmail(String email);

    Buyer save(Buyer buyer);

    boolean update(Buyer buyer);

    boolean deleteById(Long id);
}
