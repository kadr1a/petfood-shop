import model.Buyer;
import repository.BuyerRepository;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        BuyerRepository repo = new BuyerRepository();
        List<Buyer> buyers = repo.findAll();

        System.out.println("Покупателей в базе: " + buyers.size());
        for (Buyer b : buyers) {
            System.out.println(b);
        }
    }
}