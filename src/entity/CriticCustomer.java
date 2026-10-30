package src.entity;

public class CriticCustomer extends Customer {
    private static final int LOSS_RATE = 12;
    private static final double DISCOUNT_RATE = 0.0;

    // Constructor
    public CriticCustomer(String id) {
        super(id, LOSS_RATE, DISCOUNT_RATE);
    }

    // Critics always order the most expensive item; ties keep menu order
    public AlaCarteItem chooseItem(Menu menu) {
        AlaCarteItem mostExpensive = null;
        for (AlaCarteItem item : menu.getMenuItems()) {
            if (mostExpensive == null || item.getPrice().getCents() > mostExpensive.getPrice().getCents()) {
                mostExpensive = item;
            }
        }
        return mostExpensive;
    }

    public String getTypeName() {
        return "Critic";
    }
}
