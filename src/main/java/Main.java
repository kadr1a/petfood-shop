import exception.BusinessException;
import exception.EntityNotFoundException;
import model.Buyer;
import service.BuyerService;

public class Main {
    public static void main(String[] args) {
        BuyerService service = new BuyerService();

        System.out.println("=== Все покупатели ===");
        service.findAll().forEach(System.out::println);

        System.out.println("\n=== Создаём нового ===");
        Buyer newBuyer = new Buyer(
                "Тестов Тест Тестович",
                "test@mail.ru",
                "+79990000000",
                "Тестовый город, ул. Тестовая, 1"
        );
        Buyer saved = service.create(newBuyer);
        System.out.println("Создан: " + saved);

        System.out.println("\n=== Ищем по id ===");
        System.out.println(service.findById(saved.getId()));

        System.out.println("\n=== Дубликат email ===");
        try {
            service.create(new Buyer("Другой", "test@mail.ru", "+7999", "адрес"));
        } catch (BusinessException e) {
            System.out.println("Ожидаемая ошибка: " + e.getMessage());
        }

        System.out.println("\n=== Несуществующий id ===");
        try {
            service.findById(9999L);
        } catch (EntityNotFoundException e) {
            System.out.println("Ожидаемая ошибка: " + e.getMessage());
        }

        System.out.println("\n=== Удаляем тестового ===");
        service.deleteById(saved.getId());
        System.out.println("Удалён, всего осталось: " + service.findAll().size());
    }
}