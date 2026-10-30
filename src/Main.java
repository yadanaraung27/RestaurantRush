package src;

import src.boundary.GameCLI;
import src.control.ArrivalSchedule;
import src.control.GameController;
import src.entity.*;
import src.service.PricingService;
import src.staff.*;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Initialise Menu
        Menu menu = new Menu(Arrays.asList(
                new AlaCarteItem("Salad", Money.ofDollars(9.00), 1),
                new AlaCarteItem("Burger", Money.ofDollars(15.00), 1),
                new AlaCarteItem("Pasta", Money.ofDollars(18.00), 2)
        ));

        // Initialise pricing, arrivals, tables and staff
        PricingService pricingService = new PricingService();
        ArrivalSchedule arrivalSchedule = new ArrivalSchedule();
        Restaurant restaurant = new Restaurant(
                Arrays.asList(new Table(1), new Table(2)),
                menu,
                Arrays.asList(new Waiter(pricingService), new Chef(), new Cashier())
        );

        GameCLI cli = new GameCLI();
        GameController controller = new GameController(restaurant, cli, arrivalSchedule);

        controller.runGame();
    }
}
