package src.entity;

import java.util.Random;

public abstract class Customer {
    private final String id;
    private int satisfaction = 100;
    private CustomerState state;
    private Order currentOrder;
    private final int satisfactionLossRate;
    private final double discountRate;

    // Constructor
    protected Customer(String id, int satisfactionLossRate, double discountRate) {
        this.id = id;
        this.state = CustomerState.QUEUED;
        this.satisfactionLossRate = satisfactionLossRate;
        this.discountRate = discountRate;
    }

    private void reduceSatisfaction(int amount) {
        this.satisfaction = Math.max(0, this.satisfaction - amount);
    }

    public void assignOrder(Order order) {
        this.currentOrder = order;
        this.state = CustomerState.WAITING_FOOD;
    }

    public void applyWaitPenalty() {
        if (state == CustomerState.QUEUED || state == CustomerState.SEATED || state == CustomerState.WAITING_FOOD) {
            reduceSatisfaction(satisfactionLossRate);
            if (satisfaction <= 0) {
                state = CustomerState.LEFT;
            }
        }
    }

    // Each customer type defines how they choose from the menu
    public abstract AlaCarteItem chooseItem(Menu menu);

    // Seeded randomness for a reproducible menu choice
    protected AlaCarteItem chooseBySeed(Menu menu) {
        Random random = new Random(id.hashCode());
        int choice = random.nextInt(menu.getMenuItems().size());
        return menu.getMenuItems().get(choice);
    }

    public abstract String getTypeName();

    public String getId() {
        return id;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerState getState() {
        return state;
    }

    public void setState(CustomerState state) {
        this.state = state;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public double getDiscountRate() {
        return discountRate;
    }
}
