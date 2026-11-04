package src.control;

import src.entity.Customer;

import java.util.List;

public class HappyHourPolicy {
    private static final double DISCOUNT_RATE = 0.20;
    private static final int SATISFACTION_BOOST = 15;
    private boolean usedThisGame;
    private int turnsRemaining;

    public boolean canActivate() {
        return !usedThisGame;
    }

    public void activate() {
        if (canActivate()) {
            usedThisGame = true;
            turnsRemaining = 2;
        }
    }

    public void advanceTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    public boolean isActive() {
        return turnsRemaining > 0;
    }

    public boolean hasBeenUsed() {
        return usedThisGame;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public double getDiscountRate() {
        return DISCOUNT_RATE;
    }

    // Waiting customers recover satisfaction during each active turn
    public void applyTurnBenefits(List<Customer> customers) {
        if (!isActive()) {
            return;
        }
        for (Customer customer : customers) {
            if (customer.isWaitingForTableOrFood()) {
                customer.boostSatisfaction(SATISFACTION_BOOST);
            }
        }
    }
}
