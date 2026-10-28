package src.entity;

public class Order {
    private final Customer customer;
    private final AlaCarteItem item;
    private OrderStatus status;
    private int prepUnitsRemaining;
    private final Money orderPrice;

    // Constructor
    public Order(Customer customer, AlaCarteItem item) {
        this.customer = customer;
        this.item = item;
        this.status = OrderStatus.PENDING;
        this.prepUnitsRemaining = item.getPrepUnits();
        this.orderPrice = item.getPrice();
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

    public Money getOrderPrice() {
        return orderPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public AlaCarteItem getItem() {
        return item;
    }
}
