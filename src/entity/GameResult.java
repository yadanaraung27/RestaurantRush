package src.entity;

public class GameResult {
    // Conditions for winning the game
    public static final int REQUIRED_PAID_CUSTOMERS = 4;
    public static final int REQUIRED_REVENUE_CENTS = 4500;
    public static final double REQUIRED_AVERAGE_SATISFACTION = 60.0;

    private final int paidCustomers;
    private final int servedCustomers;
    private final int unhappyDepartures;
    private final Money revenue;
    private final double averageSatisfaction;
    private final int turnsUsed;

    // Constructor
    public GameResult(int paidCustomers, int servedCustomers, int unhappyDepartures, Money revenue, double averageSatisfaction, int turnsUsed) {
        this.paidCustomers = paidCustomers;
        this.servedCustomers = servedCustomers;
        this.unhappyDepartures = unhappyDepartures;
        this.revenue = revenue;
        this.averageSatisfaction = averageSatisfaction;
        this.turnsUsed = turnsUsed;
    }

    public boolean isVictory() {
        return isPaidCustomersMet() && isRevenueMet() && isSatisfactionMet();
    }

    public boolean isPaidCustomersMet() {
        return paidCustomers >= REQUIRED_PAID_CUSTOMERS;
    }

    public boolean isRevenueMet() {
        return revenue.getCents() >= REQUIRED_REVENUE_CENTS;
    }

    public boolean isSatisfactionMet() {
        return averageSatisfaction >= REQUIRED_AVERAGE_SATISFACTION;
    }

    public int getPaidCustomers() {
        return paidCustomers;
    }

    public int getServedCustomers() {
        return servedCustomers;
    }

    public int getUnhappyDepartures() {
        return unhappyDepartures;
    }

    public Money getRevenue() {
        return revenue;
    }

    public double getAverageSatisfaction() {
        return averageSatisfaction;
    }

    public int getTurnsUsed() {
        return turnsUsed;
    }
}
