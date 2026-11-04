package src.entity;

public class Order {
    private final Customer customer;
    private final MenuItem item;
    private OrderStatus status;
    private int prepUnitsRemaining;
    private final Money lockedPrice; // Price is locked when the order is taken, unaffected by later menu or pricing changes

    // Constructor
    public Order(Customer customer, MenuItem item, Money lockedPrice) {
        this.customer = customer;
        this.item = item;
        this.status = OrderStatus.PENDING;
        this.prepUnitsRemaining = item.getPrepUnits();
        this.lockedPrice = lockedPrice;
    }

    public void advancePrep() {
        if (prepUnitsRemaining > 0) {
            prepUnitsRemaining--;
        }
        if (prepUnitsRemaining == 0 && status == OrderStatus.PREPARING) {
            status = OrderStatus.READY;
        }
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean isReady() {
        return status == OrderStatus.READY;
    }

    // How many preparation units the Chef has already done
    public int getPreparedUnits() {
        return item.getPrepUnits() - prepUnitsRemaining;
    }

    public Money getLockedPrice() {
        return lockedPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public MenuItem getItem() {
        return item;
    }
}
