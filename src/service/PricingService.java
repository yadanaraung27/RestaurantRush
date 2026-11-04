package src.service;

import src.control.HappyHourPolicy;
import src.entity.Customer;
import src.entity.MenuItem;
import src.entity.Money;

public class PricingService {
    private final HappyHourPolicy happyHourPolicy;

    // Constructor
    public PricingService(HappyHourPolicy happyHourPolicy) {
        this.happyHourPolicy = happyHourPolicy;
    }

    // Apply the customer discount first, then Happy Hour, and return the price to lock
    public Money calculateLockedPrice(MenuItem item, Customer customer) {
        Money price = item.getPrice().multiply(1.0 - customer.getDiscountRate());
        if (happyHourPolicy.isActive()) {
            price = price.multiply(1.0 - happyHourPolicy.getDiscountRate());
        }
        return price;
    }
}
