package src.entity;

public class VIPCustomer extends Customer {
    private static final int LOSS_RATE = 5;
    private static final double DISCOUNT_RATE = 0.10;

    // Constructor
    public VIPCustomer(String id) {
        super(id, LOSS_RATE, DISCOUNT_RATE);
    }

    public AlaCarteItem chooseItem(Menu menu) {
        return chooseBySeed(menu);
    }

    public String getTypeName() {
        return "VIP";
    }
}
