package src.service;

import src.entity.AlaCarteItem;
import src.entity.Customer;
import src.entity.Money;

public class PricingService {
    // Apply the customer's discount and return the price to lock on the order
    public Money calculateLockedPrice(AlaCarteItem item, Customer customer) {
        return item.getPrice().multiply(1.0 - customer.getDiscountRate());
    }
}
