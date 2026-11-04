package src;

import src.boundary.GameCLI;
import src.control.ArrivalSchedule;
import src.control.GameController;
import src.control.HappyHourPolicy;
import src.entity.*;
import src.service.PricingService;
import src.staff.*;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Initialise Menu
        AlaCarteItem salad = new AlaCarteItem("Salad", Money.ofDollars(9.00), 1);
        AlaCarteItem burger = new AlaCarteItem("Burger", Money.ofDollars(15.00), 1);
        AlaCarteItem pasta = new AlaCarteItem("Pasta", Money.ofDollars(18.00), 2);
        ComboMeal saladPasta = new ComboMeal("Salad + Pasta", salad, pasta);
        ComboMeal saladBurger = new ComboMeal("Salad + Burger", salad, burger);
        ComboMeal pastaBurger = new ComboMeal("Pasta + Burger", pasta, burger);
        Menu menu = new Menu(Arrays.asList(salad, pasta, burger, saladPasta, saladBurger, pastaBurger));

        // Initialise pricing, arrivals, tables and staff
        HappyHourPolicy happyHourPolicy = new HappyHourPolicy();
        PricingService pricingService = new PricingService(happyHourPolicy);
        ArrivalSchedule arrivalSchedule = new ArrivalSchedule();
        Restaurant restaurant = new Restaurant(
                Arrays.asList(new Table(1), new Table(2)),
                menu,
                Arrays.asList(new Host(), new Waiter(pricingService), new Chef(), new Cashier())
        );

        GameCLI cli = new GameCLI();
        GameController controller = new GameController(restaurant, cli, arrivalSchedule, happyHourPolicy);

        controller.runGame();
    }
}
