package src.staff;

import src.entity.Customer;
import src.entity.Restaurant;
import src.exception.InvalidActionException;

import java.util.ArrayList;
import java.util.List;

public class Host extends FrontStaff {
    // Constructor
    public Host() {
        super("Host");
    }

    public String performTask(Restaurant restaurant, StaffAction action, String targetId) throws InvalidActionException {
        return switch (action) {
            case SEAT -> seatCustomer(restaurant, targetId);
            case WAIT -> null;
            default -> throw new InvalidActionException("Use seat or wait.");
        };
    }

    public String getActionHint() {
        return "seat, or wait";
    }

    public List<String> getAvailableActions(Restaurant restaurant) {
        List<String> actions = new ArrayList<>();
        if (restaurant.getFreeTable() != null) {
            for (Customer customer : restaurant.getCustomersWaitingToBeSeated()) {
                actions.add("seat " + customer.getId());
            }
        }
        actions.add("wait");
        return actions;
    }
}
