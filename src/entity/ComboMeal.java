package src.entity;

public class ComboMeal implements MenuItem {
    private static final double COMBO_RATE = 0.90;
    private final String name;
    private final MenuItem componentA;
    private final MenuItem componentB;

    // Constructor
    public ComboMeal(String name, MenuItem componentA, MenuItem componentB) {
        this.name = name;
        this.componentA = componentA;
        this.componentB = componentB;
    }

    public Money getPrice() {
        return componentA.getPrice().add(componentB.getPrice()).multiply(COMBO_RATE);
    }

    public int getPrepUnits() {
        return componentA.getPrepUnits() + componentB.getPrepUnits();
    }

    public String getName() {
        return name;
    }

    public MenuItem getComponentA() {
        return componentA;
    }

    public MenuItem getComponentB() {
        return componentB;
    }
}
