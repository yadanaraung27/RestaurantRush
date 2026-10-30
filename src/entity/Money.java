package src.entity;

public class Money {
    private final int cents;

    // Constructor
    private Money(int cents) {
        if (cents < 0) {
            throw new IllegalArgumentException("Money cannot be negative: " + cents);
        }
        this.cents = cents;
    }

    // Create Money from cents
    public static Money ofCents(int cents) {
        return new Money(cents);
    }

    // Create Money from dollars
    public static Money ofDollars(double dollars) {
        return new Money((int) Math.round(dollars * 100.0));
    }

    public Money add(Money other) {
        return new Money(this.cents + other.cents);
    }

    // Multiply by a rate, rounding to the nearest cent
    public Money multiply(double rate) {
        return new Money((int) Math.round(cents * rate));
    }

    public int getCents() {
        return cents;
    }

    public String toString() {
        return String.format("$%.2f", cents / 100.0);
    }
}
