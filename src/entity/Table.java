package src.entity;

public class Table {
    private final int tableNumber;
    private Customer seatedCustomer;

    // Constructor
    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
        this.seatedCustomer = null;
    }

    public boolean isFree() {
        return seatedCustomer == null;
    }

    public void seatCustomer(Customer customer) {
        this.seatedCustomer = customer;
        customer.setState(CustomerState.SEATED);
    }

    public void clear() {
        this.seatedCustomer = null;
    }

    public Customer getSeatedCustomer() {
        return seatedCustomer;
    }

    public int getTableNumber() {
        return tableNumber;
    }
}
