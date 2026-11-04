package src.staff;

import src.entity.Customer;
import src.entity.Restaurant;
import src.entity.Table;
import src.exception.InvalidActionException;

public abstract class FrontStaff extends Staff {
    // Constructor
    protected FrontStaff(String roleName) {
        super(roleName);
    }

    // Seat the chosen waiting customer at the first free table
    protected String seatCustomer(Restaurant restaurant, String targetId) throws InvalidActionException {
        Table table = restaurant.getFreeTable();
        if (table == null) {
            throw new InvalidActionException("No free table is available.");
        }
        Customer customer = findCustomer(restaurant.getCustomersWaitingToBeSeated(), targetId, "the waiting queue");
        table.seatCustomer(customer);
        restaurant.getWaitingQueue().dequeue(customer);
        return "Seated " + customer.getId() + " at table " + table.getTableNumber() + ".";
    }
}
