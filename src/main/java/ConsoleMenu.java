import java.util.Scanner;
import model.Buyer;
import model.Order;
import model.OrderStatus;
import exception.BusinessException;
import exception.EntityNotFoundException;

public class ConsoleMenu {

    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public void run() {
        while (running) {
            printMainMenu();
            int choice = readInt("Выберите действие: ");
            handleMainMenuChoice(choice);
        }
        System.out.println("Программа завершена. До свидания!");
        scanner.close();
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("   МАГАЗИН КОРМОВ ДЛЯ ЖИВОТНЫХ");
        System.out.println("========================================");
        System.out.println("1. Покупатели");
        System.out.println("2. Заказы");
        System.out.println("3. Поиск");
        System.out.println("4. Фильтрация");
        System.out.println("5. Статистика");
        System.out.println("6. Экспорт данных");
        System.out.println("7. Вывести таблицы базы данных");
        System.out.println("0. Выход");
        System.out.println("========================================");
    }

    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1 -> buyersMenu();
            case 2 -> ordersMenu();
            case 3 -> System.out.println("[Поиск] — раздел в разработке");
            case 4 -> System.out.println("[Фильтрация] — раздел в разработке");
            case 5 -> System.out.println("[Статистика] — раздел в разработке");
            case 6 -> System.out.println("[Экспорт данных] — раздел в разработке");
            case 7 -> System.out.println("[Вывод таблиц БД] — раздел в разработке");
            case 0 -> running = false;
            default -> System.out.println("Ошибка: нет такого пункта меню. Попробуйте снова.");
        }
    }

    private void buyersMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- ПОКУПАТЕЛИ -----");
            System.out.println("1. Создать покупателя");
            System.out.println("2. Список покупателей");
            System.out.println("3. Найти покупателя по ID");
            System.out.println("4. Изменить покупателя");
            System.out.println("5. Удалить покупателя");
            System.out.println("0. Назад");

            int choice = readInt("Выберите действие: ");
            switch (choice) {
                case 1 -> System.out.println("создание покупателя");
                case 2 -> System.out.println("вывод списка покупателей");
                case 3 -> System.out.println("поиск покупателя по ID");
                case 4 -> System.out.println("изменение покупателя");
                case 5 -> System.out.println("удаление покупателя");
                case 0 -> back = true;
                default -> System.out.println("Ошибка: нет такого пункта меню.");
            }
        }
    }

    private void ordersMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- ЗАКАЗЫ -----");
            System.out.println("1. Создать заказ");
            System.out.println("2. Список заказов");
            System.out.println("3. Найти заказ по ID");
            System.out.println("4. Изменить заказ");
            System.out.println("5. Удалить заказ");
            System.out.println("6. Поиск заказов");
            System.out.println("7. Фильтрация заказов");
            System.out.println("8. Сортировка заказов");
            System.out.println("0. Назад");

            int choice = readInt("Выберите действие: ");
            switch (choice) {
                case 1 -> System.out.println("создание заказа");
                case 2 -> System.out.println("вывод списка заказов");
                case 3 -> System.out.println("поиск заказа по ID");
                case 4 -> System.out.println("изменение заказа");
                case 5 -> System.out.println("удаление заказа");
                case 6 -> System.out.println("поиск заказов");
                case 7 -> System.out.println("фильтрация заказов");
                case 8 -> System.out.println("сортировка заказов");
                case 0 -> back = true;
                default -> System.out.println("Ошибка: нет такого пункта меню.");
            }
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно ввести целое число. Попробуйте снова.");
            }
        }
    }
}