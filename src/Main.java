package src;

import src.boundary.GameCLI;
import src.control.GameController;
import src.entity.*;
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

        // Initialise Tables & Staff
        Restaurant restaurant = new Restaurant(
                Arrays.asList(new Table(1), new Table(2)),
                menu,
                Arrays.asList(new Waiter(), new Chef(), new Cashier())
        );

        GameCLI cli = new GameCLI();
        GameController controller = new GameController(restaurant, cli);

        controller.runGame();
    }
}
