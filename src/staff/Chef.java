package src.staff;

import src.entity.Customer;
import src.entity.Order;
import src.entity.OrderStatus;
import src.entity.Restaurant;
import src.exception.InvalidActionException;

import java.util.ArrayList;
import java.util.List;

public class Chef extends Staff {
    // Constructor
    public Chef() {
        super("Chef");
    }

    public String performTask(Restaurant restaurant, StaffAction action, String targetId) throws InvalidActionException {
        return switch (action) {
            case PREPARE -> prepareOrder(restaurant, targetId);
            case WAIT -> null;
            default -> throw new InvalidActionException("Use prepare or wait.");
        };
    }

    // Advance the chosen order by one preparation unit
    private String prepareOrder(Restaurant restaurant, String targetId) throws InvalidActionException {
        List<Customer> customers = restaurant.getCustomersWithPreparableOrders();
        Customer customer = findCustomer(customers, targetId, "the orders that need preparation");
        Order order = customer.getCurrentOrder();
        order.setStatus(OrderStatus.PREPARING);
        order.advancePrep();
        String progress = order.getPreparedUnits() + "/" + order.getItem().getPrepUnits() + " units";
        if (order.isReady()) {
            return "Prepared " + targetId + "'s " + order.getItem().getName() + " (" + progress + "). Status: READY, can be served from next turn.";
        }
        return "Prepared " + targetId + "'s " + order.getItem().getName() + " (" + progress + "). Status: PREPARING.";
    }

    public String getActionHint() {
        return "prepare, or wait";
    }

    public List<String> getAvailableActions(Restaurant restaurant) {
        List<String> actions = new ArrayList<>();
        for (Customer customer : restaurant.getCustomersWithPreparableOrders()) {
            actions.add("prepare " + customer.getId());
        }
        actions.add("wait");
        return actions;
    }
}
