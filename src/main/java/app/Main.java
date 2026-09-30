package app;

import service.BuyerService;
import service.OrderService;
import ui.ConsoleMenu;

public class Main {

    public static void main(String[] args) {
        BuyerService buyerService = new BuyerService();
        OrderService orderService = new OrderService(buyerService);
        ConsoleMenu menu = new ConsoleMenu(buyerService, orderService);
        menu.run();
    }
}
