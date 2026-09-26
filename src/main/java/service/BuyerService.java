package service;

import exception.BusinessException;
import exception.EntityNotFoundException;
import model.Buyer;
import repository.BuyerRepository;

import java.util.List;

public class BuyerService {

    private final BuyerRepository repository = new BuyerRepository();

    public List<Buyer> findAll() {
        return repository.findAll();
    }

    public Buyer findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Покупатель с id=" + id + " не найден"));
    }

    public Buyer create(Buyer buyer) {
        validate(buyer);

        repository.findByEmail(buyer.getEmail()).ifPresent(existing -> {
            throw new BusinessException(
                    "Покупатель с email '" + buyer.getEmail() + "' уже существует");
        });

        return repository.save(buyer);
    }

    public void update(Buyer buyer) {
        if (buyer.getId() == null) {
            throw new BusinessException("Не указан id покупателя для обновления");
        }
        validate(buyer);

        findById(buyer.getId());

        repository.findByEmail(buyer.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(buyer.getId())) {
                throw new BusinessException(
                        "Email '" + buyer.getEmail() + "' уже занят другим покупателем");
            }
        });

        boolean updated = repository.update(buyer);
        if (!updated) {
            throw new EntityNotFoundException(
                    "Покупатель с id=" + buyer.getId() + " не найден");
        }
    }

    public void deleteById(Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(
                    "Покупатель с id=" + id + " не найден");
        }
    }

    private void validate(Buyer buyer) {
        if (buyer == null) {
            throw new BusinessException("Покупатель не может быть null");
        }
        if (isBlank(buyer.getFullName())) {
            throw new BusinessException("ФИО покупателя не может быть пустым");
        }
        if (isBlank(buyer.getEmail())) {
            throw new BusinessException("Email покупателя не может быть пустым");
        }
        if (!buyer.getEmail().contains("@")) {
            throw new BusinessException("Email должен содержать символ '@'");
        }
        if (isBlank(buyer.getPhone())) {
            throw new BusinessException("Телефон покупателя не может быть пустым");
        }
        if (isBlank(buyer.getAddress())) {
            throw new BusinessException("Адрес покупателя не может быть пустым");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}