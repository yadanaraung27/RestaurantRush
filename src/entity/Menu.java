package src.entity;

import java.util.List;

public class Menu {
    private final List<MenuItem> items;

    // Constructor
    public Menu(List<MenuItem> items) {
        this.items = items;
    }

    public List<MenuItem> getMenuItems() {
        return items;
    }
}
