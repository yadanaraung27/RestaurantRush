package src.control;

import src.boundary.GameCLI;
import src.exception.InvalidActionException;
import src.entity.Customer;
import src.entity.CustomerState;
import src.entity.GameResult;
import src.entity.Restaurant;
import src.staff.Cashier;
import src.staff.Staff;
import src.staff.StaffAction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameController {
    private static final int MAX_TURNS = 18;
    private int currentTurn = 1;
    private int servedCount;
    private int unhappyDepartures;
    private final Set<Customer> eatingAtTurnStart = new HashSet<>();
    private final Restaurant restaurant;
    private final GameCLI ui;
    private final ArrivalSchedule arrivalSchedule;

    public GameController(Restaurant restaurant, GameCLI ui, ArrivalSchedule arrivalSchedule) {
        this.restaurant = restaurant;
        this.ui = ui;
        this.arrivalSchedule = arrivalSchedule;
    }

    public void runGame() {
        ui.displayWelcome(MAX_TURNS);
        while (currentTurn <= MAX_TURNS) {
            startTurn();
            currentTurn++;
        }
        ui.displayFinalResult(evaluateGameResult());
    }

    private void startTurn() {
        eatingAtTurnStart.clear();
        // Get list of customers EATING at start of turn
        restaurant.getCustomers().stream()
                .filter(customer -> customer.getState() == CustomerState.EATING)
                .forEach(eatingAtTurnStart::add);
        // Start of turn summary
        ui.displayTurnState(restaurant, currentTurn, evaluateGameResult());
        // Process arrival of new actions
        processArrivals();
        // Collect waiter and chef actions
        int paymentStep = collectManagerActions();
        processEatingAndPayment(paymentStep);
        // Handle satisfaction loss
        handleAbandonment();
        // End of turn summary
        ui.displayTurnResult(evaluateGameResult());
    }

    private void processArrivals() {
        for (Customer customer : arrivalSchedule.getArrivalsForTurn(currentTurn)) {
            restaurant.addCustomer(customer);
            restaurant.getWaitingQueue().enqueue(customer);
            ui.displayMessage(customer.getTypeName() + " " + customer.getId() + " arrived and entered the waiting queue.");
        }
    }

    private int collectManagerActions() {
        int step = 1;
        for (Staff staff : restaurant.getStaffList()) {
            if (!staff.isManagerControlled()) {
                continue;
            }
            boolean acted = false;
            while (!acted) {
                try {
                    String command = ui.promptStaffAction(staff, step, restaurant);
                    String[] words = command.trim().split("\\s+");
                    if (words.length > 2) {
                        throw new InvalidActionException("Use '<action> <customer id>', for example 'seat C1'.");
                    }
                    StaffAction action = StaffAction.parse(words[0]);
                    String targetId = null;
                    if (words.length == 2) {
                        targetId = words[1].toUpperCase();
                    }
                    if (action == StaffAction.WAIT && targetId != null) {
                        throw new InvalidActionException("'wait' does not take a customer ID.");
                    }
                    String outcome = staff.performTask(restaurant, action, targetId);
                    ui.displayActionResult(outcome != null ? outcome : staff.getRoleName() + " waited.");
                    if (action == StaffAction.SERVE) {
                        servedCount++;
                    }
                    acted = true;
                } catch (InvalidActionException e) {
                    ui.displayError(e.getMessage());
                }
            }
            step++;
        }
        return step;
    }

    private void processEatingAndPayment(int paymentStep) {
        // Pay before advancing eaters so a customer who becomes READY_TO_PAY this turn can only pay on the next turn
        try {
            Cashier cashier = restaurant.getCashier();
            String outcome = cashier.processNextPayment(restaurant);
            ui.displayPayment(cashier, paymentStep, outcome);
        } catch (InvalidActionException e) {
            ui.displayError(e.getMessage());
        }
        // Only customers who were already EATING at the start of this turn finish eating
        // Someone served this turn stays EATING until next turn
        for (Customer customer : eatingAtTurnStart) {
            if (customer.getState() == CustomerState.EATING) {
                customer.setState(CustomerState.READY_TO_PAY);
            }
        }
    }

    private void handleAbandonment() {
        List<Customer> departed = restaurant.applyWaitPenalties();
        for (Customer customer : departed) {
            unhappyDepartures++;
            ui.displayEvent("Customer " + customer.getId() + " abandoned the queue/restaurant!");
        }
    }

    private GameResult evaluateGameResult() {
        List<Customer> paidCustomers = restaurant.getCustomers().stream()
                .filter(customer -> customer.getState() == CustomerState.PAID)
                .toList();
        double averageSatisfaction = paidCustomers.stream()
                .mapToInt(Customer::getSatisfaction)
                .average()
                .orElse(0.0);
        return new GameResult(paidCustomers.size(), servedCount, unhappyDepartures,
                restaurant.getTotalRevenue(), averageSatisfaction,
                Math.min(currentTurn, MAX_TURNS));
    }
}
