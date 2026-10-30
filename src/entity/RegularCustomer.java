package src.entity;

public class RegularCustomer extends Customer {
    private static final int LOSS_RATE = 8;
    private static final double DISCOUNT_RATE = 0.0;

    // Constructor
    public RegularCustomer(String id) {
        super(id, LOSS_RATE, DISCOUNT_RATE);
    }

    public AlaCarteItem chooseItem(Menu menu) {
        return chooseBySeed(menu);
    }

    public String getTypeName() {
        return "Regular";
    }
}
