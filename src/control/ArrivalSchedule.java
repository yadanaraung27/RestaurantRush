package src.control;

import src.entity.CriticCustomer;
import src.entity.Customer;
import src.entity.RegularCustomer;
import src.entity.VIPCustomer;

import java.util.List;

public class ArrivalSchedule {
    // Base setting: six arrivals, with C4 as VIP and C10 as Critic
    // List is returned so a turn may have more than one arrival
    public List<Customer> getArrivalsForTurn(int turn) {
        return switch (turn) {
            case 1 -> List.of(new RegularCustomer("C1"));
            case 4 -> List.of(new VIPCustomer("C4"));
            case 7 -> List.of(new RegularCustomer("C7"));
            case 10 -> List.of(new CriticCustomer("C10"));
            case 13 -> List.of(new RegularCustomer("C13"));
            case 16 -> List.of(new RegularCustomer("C16"));
            default -> List.of();
        };
    }
}
