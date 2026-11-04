package src.boundary;

import src.control.HappyHourPolicy;
import src.entity.Customer;
import src.entity.GameResult;
import src.entity.Money;
import src.entity.Order;
import src.entity.Restaurant;
import src.entity.Table;
import src.exception.InvalidActionException;
import src.staff.Staff;

import java.util.List;
import java.util.Scanner;

public class GameCLI {
    private final Scanner scanner = new Scanner(System.in);

    public void displayWelcome(int maxTurns) {
        System.out.println("============================================================");
        System.out.println("                 Welcome to Restaurant Rush!");
        System.out.println("============================================================");
        System.out.println("  Manage your restaurant for " + maxTurns + " turns.");
        System.out.println();
        System.out.println("  Each turn:");
        System.out.println("    1. Host    - seat one queued customer, or wait");
        System.out.println("    2. Waiter  - seat, order, or serve one customer you name, or wait");
        System.out.println("    3. Chef    - prepare one order you name, or wait");
        System.out.println("    4. Cashier - handle payment for oldest eligible customer");
        System.out.println();
        System.out.println("  Type an action and a customer ID, for example \"seat C1\".");
        System.out.println("  \"wait\" skips that staff member's action for the turn.");
        System.out.println();
        System.out.println("  Customers choose their own meal; you never choose it.");
        System.out.println("  VIP customers pay 10% less; Critic customers order the most expensive item.");
        System.out.println("  The menu includes a combo: two items at 90% of their combined price.");
        System.out.println("  Happy Hour can be activated once per game for this turn and the next.");
        System.out.println();
        System.out.println("  Victory conditions:");
        System.out.println("    1. At least " + GameResult.REQUIRED_PAID_CUSTOMERS + " paid customers");
        System.out.println("    2. Revenue at least " + Money.ofCents(GameResult.REQUIRED_REVENUE_CENTS));
        System.out.println("    3. Average paid satisfaction at least " + GameResult.REQUIRED_AVERAGE_SATISFACTION);
        System.out.println("============================================================");
    }

    public void displayTurnState(Restaurant restaurant, int turn, GameResult result, HappyHourPolicy happyHourPolicy) {
        System.out.println();
        System.out.println("============================================================");
        System.out.println(" TURN " + turn);
        System.out.println("============================================================");
        System.out.printf("  %-24s %8d%n", "Paid Customers", result.getPaidCustomers());
        System.out.printf("  %-24s %8s%n", "Total Revenue", result.getRevenue());
        System.out.printf("  %-24s %8s%n", "Avg. Paid Satisfaction", averageSatisfactionText(result));
        String happyHour;
        if (happyHourPolicy.isActive()) {
            int turns = happyHourPolicy.getTurnsRemaining();
            String unit = " turns)";
            if (turns == 1) {
                unit = " turn)";
            }
            happyHour = "active (" + turns + unit;
        } else if (happyHourPolicy.hasBeenUsed()) {
            happyHour = "used";
        } else {
            happyHour = "available";
        }
        System.out.println("  Happy Hour: " + happyHour);

        System.out.println();
        System.out.println("Waiting queue:");
        if (restaurant.getWaitingQueue().isEmpty()) {
            System.out.println("  (empty)");
        } else {
            for (Customer customer : restaurant.getWaitingQueue().getCustomers()) {
                System.out.println("  - " + customer.getId());
            }
        }

        System.out.println();
        System.out.println("Tables:");
        for (Table table : restaurant.getTables()) {
            String occupant = table.isFree() ? "free" : table.getSeatedCustomer().getId();
            System.out.printf("  Table %d: %s%n", table.getTableNumber(), occupant);
        }

        System.out.println();
        System.out.println("Customers:");
        if (restaurant.getCustomers().isEmpty()) {
            System.out.println("  (none yet)");
        } else {
            System.out.printf("  %-4s %-8s %-12s %-14s %s%n", "ID", "Type", "Satisfaction", "State", "Order");
            for (Customer customer : restaurant.getCustomers()) {
                Order order = customer.getCurrentOrder();
                String orderText = order == null ? "-" : order.getItem().getName() + "/" + order.getStatus();
                System.out.printf("  %-4s %-8s %-12d %-14s %s%n", customer.getId(), customer.getTypeName(), customer.getSatisfaction(), customer.getState(), orderText);
            }
        }

        System.out.println();
        System.out.println("Staff tasks:");
        for (Staff staff : restaurant.getStaffList()) {
            System.out.printf("  %-8s %s%n", staff.getRoleName() + ":", staff.getActionHint());
        }
        System.out.println("------------------------------------------------------------");
    }

    // Format average paid satisfaction
    private String averageSatisfactionText(GameResult result) {
        if (result.getPaidCustomers() == 0) {
            return "N/A";
        }
        return String.format("%.1f", result.getAverageSatisfaction());
    }

    public String promptStaffAction(Staff staff, int step, Restaurant restaurant) throws InvalidActionException {
        List<String> actions = staff.getAvailableActions(restaurant);
        System.out.println();
        System.out.println("  " + step + ". " + staff.getRoleName() + " action (1 per turn)");
        for (int i = 0; i < actions.size(); i++) {
            System.out.println("     [" + (i + 1) + "] " + actions.get(i));
        }
        System.out.print("     > ");
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("  (no more input - treating as wait)");
            return "wait";
        }
        String command = scanner.nextLine().trim();
        if (System.console() == null) {
            System.out.println();
        }
        return resolveChoice(command, actions);
    }

    // Turn menu number into a command
    private String resolveChoice(String command, List<String> actions) throws InvalidActionException {
        if (command.isBlank()) {
            return "wait";
        }
        if (!command.matches("\\d{1,2}")) {
            return command;
        }
        int index = Integer.parseInt(command);
        if (index < 1 || index > actions.size()) {
            throw new InvalidActionException("Choose a number from 1 to " + actions.size() + ", or type the command, for example '" + actions.get(0) + "'.");
        }
        return actions.get(index - 1);
    }

    // Ask whether to activate Happy Hour at the start of a turn
    public boolean promptHappyHourActivation() {
        while (true) {
            System.out.println();
            System.out.print("  Activate Happy Hour for this turn and the next? (y/n): ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                return false;
            }
            String answer = scanner.nextLine().trim().toLowerCase();
            if (System.console() == null) {
                // Piped input is not echoed, so show the answer on the prompt line
                System.out.println(answer);
            }
            System.out.println();
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no") || answer.isBlank()) {
                return false;
            }
            System.out.println("  Please answer y or n.");
        }
    }

    // Show outcome of action player chose
    public void displayActionResult(String message) {
        if (message != null && !message.isBlank()) {
            System.out.println("  >> " + message);
        }
    }

    // Display automatic events that happen
    public void displayEvent(String message) {
        if (message != null && !message.isBlank()) {
            System.out.println();
            System.out.println("  " + message);
        }
    }

    // Display automatic Cashier step
    public void displayPayment(Staff staff, int step, String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        System.out.println();
        System.out.println("  " + step + ". " + staff.getRoleName() + " action [" + staff.getActionHint() + "]");
        System.out.println("  >> " + message);
    }

    public void displayMessage(String message) {
        if (message != null && !message.isBlank()) {
            System.out.println("  " + message);
        }
    }

    public void displayError(String message) {
        System.out.println("  >> [Error] " + message);
    }

    public void displayTurnResult(GameResult result) {
        System.out.println("------------------------------------------------------------");
        System.out.println(" End of turn");
        System.out.printf("  %-24s %8d%n", "Served Customers", result.getServedCustomers());
        System.out.printf("  %-24s %8d%n", "Unhappy Departures", result.getUnhappyDepartures());
        System.out.printf("  %-24s %8d%n", "Paid Customers", result.getPaidCustomers());
        System.out.printf("  %-24s %8s%n", "Total Revenue", result.getRevenue());
        System.out.printf("  %-24s %8s%n", "Avg. Paid Satisfaction", averageSatisfactionText(result));
        System.out.printf("  %-24s %8d%n", "Turns Used", result.getTurnsUsed());
        System.out.println("------------------------------------------------------------");
    }

    public void displayFinalResult(GameResult result) {
        int metTargets = 0;
        if (result.isPaidCustomersMet()) {
            metTargets++;
        }
        if (result.isRevenueMet()) {
            metTargets++;
        }
        if (result.isSatisfactionMet()) {
            metTargets++;
        }

        System.out.println();
        System.out.println("============================================================");
        System.out.println("                         GAME OVER");
        System.out.println("============================================================");
        System.out.println();
        System.out.println("                       FINAL RESULTS");
        System.out.println("------------------------------------------------------------");
        System.out.printf("  %-24s %8d%n", "Served Customers", result.getServedCustomers());
        System.out.printf("  %-24s %8d%n", "Unhappy Departures", result.getUnhappyDepartures());
        System.out.printf("  %-24s %8d%n", "Paid Customers", result.getPaidCustomers());
        System.out.printf("  %-24s %8s%n", "Total Revenue", result.getRevenue());
        System.out.printf("  %-24s %8s%n", "Avg. Paid Satisfaction", averageSatisfactionText(result));
        System.out.printf("  %-24s %8d%n", "Turns Used", result.getTurnsUsed());
        System.out.println();
        System.out.println("============================================================");
        System.out.println("                      VICTORY TARGETS");
        System.out.println("============================================================");
        System.out.printf("  %-24s %10s %10s %8s%n", "Objective", "Actual", "Target", "Status");
        System.out.println("------------------------------------------------------------");
        System.out.printf("  %-24s %10s %10s %8s%n", "Paid Customers",
                result.getPaidCustomers(),
                GameResult.REQUIRED_PAID_CUSTOMERS,
                metText(result.isPaidCustomersMet()));
        System.out.printf("  %-24s %10s %10s %8s%n", "Total Revenue",
                result.getRevenue(),
                Money.ofCents(GameResult.REQUIRED_REVENUE_CENTS),
                metText(result.isRevenueMet()));
        System.out.printf("  %-24s %10s %10s %8s%n", "Avg. Paid Satisfaction",
                averageSatisfactionText(result),
                GameResult.REQUIRED_AVERAGE_SATISFACTION,
                metText(result.isSatisfactionMet()));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        if (result.isVictory()) {
            System.out.println("                 *** VICTORY ACHIEVED! ***");
            System.out.println();
            System.out.println("                All 3 victory targets met.");
        } else {
            System.out.println("                       *** DEFEAT ***");
            System.out.println();
            System.out.println("                " + metTargets + " of 3 victory targets met.");
        }
        System.out.println("============================================================");
    }

    // Returns the status label for a victory target
    private String metText(boolean met) {
        if (met) {
            return "[PASS]";
        }
        return "[FAIL]";
    }
}
