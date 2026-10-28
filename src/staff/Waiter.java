package src.staff;

import src.entity.AlaCarteItem;
import src.entity.Customer;
import src.entity.CustomerState;
import src.entity.Order;
import src.entity.OrderStatus;
import src.entity.Restaurant;
import src.entity.Table;
import src.exception.InvalidActionException;

import java.util.ArrayList;
import java.util.List;

public class Waiter extends Staff {
    // Constructor
    public Waiter() {
        super("Waiter");
    }

    public String performTask(Restaurant restaurant, StaffAction action, String targetId) throws InvalidActionException {
        return switch (action) {
            case SEAT -> seatCustomer(restaurant, targetId);
            case TAKE_ORDER -> takeOrder(restaurant, targetId);
            case SERVE -> serveOrder(restaurant, targetId);
            case WAIT -> null;
            default -> throw new InvalidActionException("Use seat, order, serve, or wait.");
        };
    }

    // Seat the chosen waiting customer at the first free table
    private String seatCustomer(Restaurant restaurant, String targetId) throws InvalidActionException {
        Table table = restaurant.getFreeTable();
        if (table == null) {
            throw new InvalidActionException("No free table is available.");
        }
        Customer customer = findCustomer(restaurant.getCustomersWaitingToBeSeated(), targetId, "the waiting queue");
        table.seatCustomer(customer);
        restaurant.getWaitingQueue().dequeue(customer);
        return "Seated " + customer.getId() + " at table " + table.getTableNumber() + ".";
    }

    // Take the chosen seated customer's order
    private String takeOrder(Restaurant restaurant, String targetId) throws InvalidActionException {
        Customer customer = findCustomer(restaurant.getCustomersWaitingToOrder(), targetId, "the customers waiting to order");
        AlaCarteItem item = customer.chooseItem(restaurant.getMenu());
        Order order = new Order(customer, item);
        customer.assignOrder(order);
        restaurant.addOrder(order);
        return customer.getId() + " chose " + item.getName() + " (" + item.getPrice() + ", " + prepUnitsText(item.getPrepUnits()) + ").";
    }

    // Serve the chosen customer's READY order
    private String serveOrder(Restaurant restaurant, String targetId) throws InvalidActionException {
        Customer customer = findCustomer(restaurant.getCustomersWithReadyOrders(), targetId, "the customers with a READY order");
        Order order = customer.getCurrentOrder();
        order.setStatus(OrderStatus.SERVED);
        customer.setState(CustomerState.EATING);
        return "Served " + order.getItem().getName() + " to " + customer.getId() + ". " + customer.getId() + " is now EATING.";
    }

    private String prepUnitsText(int count) {
        if (count == 1) {
            return "1 prep unit";
        }
        return count + " prep units";
    }

    public String getActionHint() {
        return "seat, order, serve, or wait";
    }

    public List<String> getAvailableActions(Restaurant restaurant) {
        List<String> actions = new ArrayList<>();
        if (restaurant.getFreeTable() != null) {
            for (Customer customer : restaurant.getCustomersWaitingToBeSeated()) {
                actions.add("seat " + customer.getId());
            }
        }
        for (Customer customer : restaurant.getCustomersWaitingToOrder()) {
            actions.add("order " + customer.getId());
        }
        for (Customer customer : restaurant.getCustomersWithReadyOrders()) {
            actions.add("serve " + customer.getId());
        }
        actions.add("wait");
        return actions;
    }
}
