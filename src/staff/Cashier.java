package src.staff;

import src.entity.Customer;
import src.entity.CustomerState;
import src.entity.Money;
import src.entity.Order;
import src.entity.OrderStatus;
import src.entity.Restaurant;
import src.entity.Table;
import src.exception.InvalidActionException;

import java.util.Collections;
import java.util.List;

public class Cashier extends Staff {
    // Constructor
    public Cashier() {
        super("Cashier");
    }

    // Cashier is not manager controlled
    public String performTask(Restaurant restaurant, StaffAction action, String targetId) throws InvalidActionException {
        return processNextPayment(restaurant);
    }

    private Money processPayment(Customer customer, Table table, Restaurant restaurant) throws InvalidActionException {
        Order order = customer.getCurrentOrder();
        if (order == null || order.getStatus() != OrderStatus.SERVED) {
            throw new InvalidActionException("Customer has no served order ready for payment.");
        }
        Money bill = order.getOrderPrice();
        restaurant.recordPayment(bill);
        order.setStatus(OrderStatus.PAID);
        customer.setState(CustomerState.PAID);
        table.clear();
        return bill;
    }

    // Select the next oldest customer that is READY_TO_PAY
    public String processNextPayment(Restaurant restaurant) throws InvalidActionException {
        Customer customer = restaurant.getCustomers().stream()
                .filter(candidate -> candidate.getState() == CustomerState.READY_TO_PAY)
                .findFirst().orElse(null);
        if (customer == null) {
            return null;
        }
        Table table = restaurant.getTableFor(customer);
        if (table == null) {
            throw new InvalidActionException("Customer " + customer.getId() + " has no table to pay at.");
        }
        Money bill = processPayment(customer, table, restaurant);
        return "Handled " + customer.getId() + "'s payment of " + bill + ".";
    }

    public boolean isManagerControlled() {
        return false;
    }

    public String getActionHint() {
        return "handle payment automatically";
    }

    public List<String> getAvailableActions(Restaurant restaurant) {
        return Collections.singletonList("handle payment automatically");
    }
}
