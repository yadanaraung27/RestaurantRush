package src.entity;

import java.util.List;

public class Menu {
    private final List<AlaCarteItem> items;

    // Constructor
    public Menu(List<AlaCarteItem> items) {
        this.items = items;
    }

    public List<AlaCarteItem> getMenuItems() {
        return items;
    }
}
