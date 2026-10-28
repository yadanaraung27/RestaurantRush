package src.entity;

import java.util.Random;

public class Customer {
    private final String id;
    private int satisfaction = 100;
    private CustomerState state;
    private Order currentOrder;
    protected final int satisfactionLossRate = 8;

    // Constructor
    public Customer(String id) {
        this.id = id;
        this.state = CustomerState.QUEUED;
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

    public AlaCarteItem chooseItem(Menu menu) {
        // Seeded randomness for customer to choose their own food
        Random random = new Random(id.hashCode());
        int choice = random.nextInt(menu.getMenuItems().size());
        return menu.getMenuItems().get(choice);
    }

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
}
