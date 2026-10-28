package src.entity;

import java.util.ArrayList;
import java.util.List;

public class WaitingQueue {
    private final List<Customer> queue = new ArrayList<>();

    public void enqueue(Customer customer) {
        queue.add(customer);
    }

    public Customer dequeue(Customer customer) {
        if (queue.remove(customer)) {
            return customer;
        }
        return null;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public List<Customer> getCustomers() {
        return queue;
    }
}
