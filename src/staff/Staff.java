package src.staff;

import src.entity.Customer;
import src.entity.Restaurant;
import src.exception.InvalidActionException;

import java.util.List;

public abstract class Staff {
    private final String roleName;

    // Constructor
    protected Staff(String roleName) {
        this.roleName = roleName;
    }

    // To execute a manager chosen action on the chosen customer
    public abstract String performTask(Restaurant restaurant, StaffAction action, String targetId)
            throws InvalidActionException;

    // To list available actions for specific role
    public abstract String getActionHint();

    // To list the commands this staff member can use right now
    public abstract List<String> getAvailableActions(Restaurant restaurant);

    public boolean isManagerControlled() {
        return true;
    }

    public String getRoleName() {
        return roleName;
    }

    // Find the chosen customer in the list this action is allowed to use
    protected Customer findCustomer(List<Customer> customers, String targetId, String where) throws InvalidActionException {
        if (targetId == null) {
            throw new InvalidActionException("Choose a customer ID, for example 'seat C1'.");
        }
        if (customers.isEmpty()) {
            throw new InvalidActionException("No customer is available for that action right now.");
        }
        for (Customer customer : customers) {
            if (customer.getId().equals(targetId)) {
                return customer;
            }
        }
        throw new InvalidActionException(targetId + " is not in " + where + ". Choose from: " + ids(customers) + ".");
    }

    // Join customer IDs for an error message
    protected String ids(List<Customer> customers) {
        StringBuilder text = new StringBuilder();
        for (Customer customer : customers) {
            if (text.length() > 0) {
                text.append(", ");
            }
            text.append(customer.getId());
        }
        return text.toString();
    }
}
