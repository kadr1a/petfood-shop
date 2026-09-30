package ui;

import exception.BusinessException;
import exception.EntityNotFoundException;
import model.Buyer;
import model.Order;
import model.OrderStatus;
import service.BuyerService;
import service.OrderService;
import service.OrderStatistics;
import util.CsvExporter;
import util.ExcelExporter;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Scanner scanner = new Scanner(System.in);
    private final BuyerService buyerService;
    private final OrderService orderService;
    private boolean running = true;

    public ConsoleMenu(BuyerService buyerService, OrderService orderService) {
        this.buyerService = buyerService;
        this.orderService = orderService;
    }

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
        System.out.println("4. Фильтрация и сортировка");
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
            case 3 -> searchMenu();
            case 4 -> filterAndSortMenu();
            case 5 -> showStatistics();
            case 6 -> exportMenu();
            case 7 -> printDatabaseTables();
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
                case 1 -> runAction(this::createBuyer);
                case 2 -> runAction(this::listBuyers);
                case 3 -> runAction(this::findBuyerById);
                case 4 -> runAction(this::updateBuyer);
                case 5 -> runAction(this::deleteBuyer);
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
            System.out.println("0. Назад");

            int choice = readInt("Выберите действие: ");
            switch (choice) {
                case 1 -> runAction(this::createOrder);
                case 2 -> runAction(this::listOrders);
                case 3 -> runAction(this::findOrderById);
                case 4 -> runAction(this::updateOrder);
                case 5 -> runAction(this::deleteOrder);
                case 0 -> back = true;
                default -> System.out.println("Ошибка: нет такого пункта меню.");
            }
        }
    }

    private void searchMenu() {
        System.out.println();
        System.out.println("----- ПОИСК -----");
        System.out.println("1. Заказы по названию товара");
        System.out.println("2. Заказы по ID покупателя");
        System.out.println("3. Покупатель по email");
        System.out.println("0. Назад");

        int choice = readInt("Выберите действие: ");
        switch (choice) {
            case 1 -> runAction(this::searchOrdersByProduct);
            case 2 -> runAction(this::searchOrdersByBuyer);
            case 3 -> runAction(this::searchBuyerByEmail);
            case 0 -> { }
            default -> System.out.println("Ошибка: нет такого пункта меню.");
        }
    }

    private void filterAndSortMenu() {
        System.out.println();
        System.out.println("----- ФИЛЬТРАЦИЯ И СОРТИРОВКА -----");
        System.out.println("1. Фильтр по статусу");
        System.out.println("2. Фильтр по диапазону дат");
        System.out.println("3. Фильтр по диапазону суммы");
        System.out.println("4. Сортировка по дате");
        System.out.println("5. Сортировка по сумме");
        System.out.println("6. Сортировка по названию товара");
        System.out.println("0. Назад");

        int choice = readInt("Выберите действие: ");
        switch (choice) {
            case 1 -> runAction(this::filterByStatus);
            case 2 -> runAction(this::filterByDates);
            case 3 -> runAction(this::filterByPrice);
            case 4 -> runAction(() -> printOrders(orderService.sortByCreatedAt()));
            case 5 -> runAction(() -> printOrders(orderService.sortByTotalPrice()));
            case 6 -> runAction(() -> printOrders(orderService.sortByProductName()));
            case 0 -> { }
            default -> System.out.println("Ошибка: нет такого пункта меню.");
        }
    }

    private void exportMenu() {
        System.out.println();
        System.out.println("----- ЭКСПОРТ -----");
        System.out.println("1. Excel (.xlsx)");
        System.out.println("2. CSV");
        System.out.println("0. Назад");

        int choice = readInt("Выберите действие: ");
        switch (choice) {
            case 1 -> runAction(this::exportExcel);
            case 2 -> runAction(this::exportCsv);
            case 0 -> { }
            default -> System.out.println("Ошибка: нет такого пункта меню.");
        }
    }

    private void createBuyer() {
        System.out.println("--- Создание покупателя ---");
        String fullName = readLine("ФИО: ");
        String email = readLine("Email: ");
        String phone = readLine("Телефон: ");
        String address = readLine("Адрес: ");

        Buyer buyer = new Buyer(fullName, email, phone, address);
        Buyer saved = buyerService.create(buyer);
        System.out.println("Покупатель создан: " + saved);
    }

    private void listBuyers() {
        List<Buyer> buyers = buyerService.findAll();
        if (buyers.isEmpty()) {
            System.out.println("Покупателей нет.");
            return;
        }
        System.out.println("--- Список покупателей ---");
        buyers.forEach(System.out::println);
    }

    private void findBuyerById() {
        Long id = readLong("ID покупателя: ");
        Buyer buyer = buyerService.findById(id);
        System.out.println(buyer);
    }

    private void updateBuyer() {
        Long id = readLong("ID покупателя для изменения: ");
        Buyer existing = buyerService.findById(id);

        String fullName = readLine("ФИО [" + existing.getFullName() + "]: ");
        String email = readLine("Email [" + existing.getEmail() + "]: ");
        String phone = readLine("Телефон [" + existing.getPhone() + "]: ");
        String address = readLine("Адрес [" + existing.getAddress() + "]: ");

        if (!fullName.isEmpty()) existing.setFullName(fullName);
        if (!email.isEmpty()) existing.setEmail(email);
        if (!phone.isEmpty()) existing.setPhone(phone);
        if (!address.isEmpty()) existing.setAddress(address);

        buyerService.update(existing);
        System.out.println("Покупатель обновлён.");
    }

    private void deleteBuyer() {
        Long id = readLong("ID покупателя для удаления: ");
        buyerService.deleteById(id);
        System.out.println("Покупатель удалён.");
    }

    private void createOrder() {
        System.out.println("--- Создание заказа ---");
        Long buyerId = readLong("ID покупателя: ");
        String product = readLine("Название товара: ");
        int quantity = readInt("Количество: ");
        BigDecimal total = readBigDecimal("Сумма заказа: ");

        Order order = new Order(buyerId, product, quantity, total, OrderStatus.CREATED);
        Order saved = orderService.create(order);
        System.out.println("Заказ создан: " + saved);
    }

    private void listOrders() {
        printOrders(orderService.findAll());
    }

    private void findOrderById() {
        Long id = readLong("ID заказа: ");
        System.out.println(orderService.findById(id));
    }

    private void updateOrder() {
        Long id = readLong("ID заказа для изменения: ");
        Order existing = orderService.findById(id);
        System.out.println("Текущие данные: " + existing);

        Long buyerId = readLongOptional("ID покупателя [" + existing.getBuyerId() + "]: ", existing.getBuyerId());
        String product = readLine("Название товара [" + existing.getProductName() + "]: ");
        String qtyLine = readLine("Количество [" + existing.getQuantity() + "]: ");
        String priceLine = readLine("Сумма [" + existing.getTotalPrice() + "]: ");
        OrderStatus status = readOrderStatus("Новый статус", existing.getStatus());

        if (!product.isEmpty()) existing.setProductName(product);
        if (!qtyLine.isEmpty()) existing.setQuantity(Integer.parseInt(qtyLine.trim()));
        if (!priceLine.isEmpty()) existing.setTotalPrice(new BigDecimal(priceLine.trim().replace(',', '.')));
        existing.setBuyerId(buyerId);
        existing.setStatus(status);

        orderService.update(existing);
        System.out.println("Заказ обновлён.");
    }

    private void deleteOrder() {
        Long id = readLong("ID заказа для удаления: ");
        orderService.deleteById(id);
        System.out.println("Заказ удалён.");
    }

    private void searchOrdersByProduct() {
        String fragment = readLine("Фрагмент названия товара: ");
        printOrders(orderService.searchByProductName(fragment));
    }

    private void searchOrdersByBuyer() {
        Long buyerId = readLong("ID покупателя: ");
        printOrders(orderService.searchByBuyerId(buyerId));
    }

    private void searchBuyerByEmail() {
        String email = readLine("Email: ");
        Buyer buyer = buyerService.findByEmail(email);
        System.out.println(buyer);
    }

    private void filterByStatus() {
        OrderStatus status = readOrderStatusRequired();
        printOrders(orderService.filterByStatus(status));
    }

    private void filterByDates() {
        LocalDate fromDate = readLocalDate("Дата начала (yyyy-MM-dd): ");
        LocalDate toDate = readLocalDate("Дата окончания (yyyy-MM-dd): ");
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);
        printOrders(orderService.filterByDateRange(from, to));
    }

    private void filterByPrice() {
        BigDecimal min = readBigDecimal("Минимальная сумма: ");
        BigDecimal max = readBigDecimal("Максимальная сумма: ");
        printOrders(orderService.filterByPriceRange(min, max));
    }

    private void showStatistics() {
        OrderStatistics stats = orderService.calculateStatistics();
        System.out.println();
        System.out.println("----- СТАТИСТИКА -----");
        System.out.println("Всего покупателей: " + stats.getTotalBuyers());
        System.out.println("Всего заказов: " + stats.getTotalOrders());
        System.out.println("Сумма всех заказов: " + stats.getTotalSum());
        System.out.println("Средняя сумма заказа: " + stats.getAverageSum());
        System.out.println("Отменённых заказов: " + stats.getCancelledCount());
        System.out.println("Заказы по статусам:");
        stats.getCountByStatus().forEach((status, count) ->
                System.out.println("  " + status + ": " + count));
    }

    private void exportExcel() {
        try {
            Path path = ExcelExporter.exportOrders(orderService.findAll());
            System.out.println("Excel сохранён: " + path);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить Excel: " + e.getMessage(), e);
        }
    }

    private void exportCsv() {
        try {
            Path path = CsvExporter.exportOrders(orderService.findAll());
            System.out.println("CSV сохранён: " + path);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить CSV: " + e.getMessage(), e);
        }
    }

    private void printDatabaseTables() {
        runAction(() -> {
            System.out.println();
            System.out.println("========== ТАБЛИЦА buyers ==========");
            listBuyers();
            System.out.println();
            System.out.println("========== ТАБЛИЦА orders ==========");
            listOrders();
        });
    }

    private void printOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("Заказы не найдены.");
            return;
        }
        System.out.println("--- Заказы ---");
        orders.forEach(System.out::println);
    }

    private void runAction(Runnable action) {
        try {
            action.run();
        } catch (BusinessException | EntityNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Ошибка базы данных или системы: " + e.getMessage());
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
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

    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно ввести целое число. Попробуйте снова.");
            }
        }
    }

    private Long readLongOptional(String prompt, Long defaultValue) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(line);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: нужно ввести целое число. Использовано значение по умолчанию.");
            return defaultValue;
        }
    }

    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().replace(',', '.');
            try {
                return new BigDecimal(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно ввести число. Попробуйте снова.");
            }
        }
    }

    private LocalDate readLocalDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return LocalDate.parse(line, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: дата в формате yyyy-MM-dd. Попробуйте снова.");
            }
        }
    }

    private OrderStatus readOrderStatusRequired() {
        printStatusList();
        while (true) {
            String line = readLine("Статус (CREATED, PAID, ...): ").toUpperCase();
            try {
                return OrderStatus.valueOf(line);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неизвестный статус. Попробуйте снова.");
            }
        }
    }

    private OrderStatus readOrderStatus(String prompt, OrderStatus current) {
        printStatusList();
        String line = readLine(prompt + " [" + current + "]: ").toUpperCase();
        if (line.isEmpty()) {
            return current;
        }
        try {
            return OrderStatus.valueOf(line);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: неизвестный статус. Оставлен прежний.");
            return current;
        }
    }

    private void printStatusList() {
        System.out.println("Доступные статусы: CREATED, PAID, SHIPPED, DELIVERED, CANCELLED");
    }
}
