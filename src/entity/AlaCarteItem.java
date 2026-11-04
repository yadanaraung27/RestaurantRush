package src.entity;

public class AlaCarteItem implements MenuItem {
    private final String name;
    private final Money price;
    private final int prepUnits;

    // Constructor
    public AlaCarteItem(String name, Money price, int prepUnits) {
        this.name = name;
        this.price = price;
        this.prepUnits = prepUnits;
    }

    public Money getPrice() {
        return price;
    }

    public int getPrepUnits() {
        return prepUnits;
    }

    public String getName() {
        return name;
    }
}
