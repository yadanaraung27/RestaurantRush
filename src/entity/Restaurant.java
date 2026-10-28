package src.entity;

import src.staff.Cashier;
import src.staff.Staff;
import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private final List<Table> tables;
    private final WaitingQueue waitingQueue;
    private final List<Order> activeOrders;
    private final Menu menu;
    private final List<Staff> staffList;
    private final List<Customer> customers;
    private Money totalRevenue;

    // Constructor
    public Restaurant(List<Table> tables, Menu menu, List<Staff> staffList) {
        this.tables = tables;
        this.waitingQueue = new WaitingQueue();
        this.activeOrders = new ArrayList<>();
        this.menu = menu;
        this.staffList = staffList;
        this.customers = new ArrayList<>();
        this.totalRevenue = Money.ofCents(0);
    }

    public Table getFreeTable() {
        for (Table table : tables) {
            if (table.isFree()) {
                return table;
            }
        }
        return null;
    }

    public Table getTableFor(Customer customer) {
        for (Table table : tables) {
            if (table.getSeatedCustomer() == customer) {
                return table;
            }
        }
        return null;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Customer> getCustomersWaitingToBeSeated() {
        return waitingQueue.getCustomers();
    }

    public List<Customer> getCustomersWaitingToOrder() {
        List<Customer> result = new ArrayList<>();
        for (Customer customer : customers) {
            if (customer.getState() == CustomerState.SEATED && customer.getCurrentOrder() == null) {
                result.add(customer);
            }
        }
        return result;
    }

    public List<Customer> getCustomersWithReadyOrders() {
        List<Customer> result = new ArrayList<>();
        for (Customer customer : customers) {
            Order order = customer.getCurrentOrder();
            if (order != null && order.isReady()) {
                result.add(customer);
            }
        }
        return result;
    }

    public List<Customer> getCustomersWithPreparableOrders() {
        List<Customer> result = new ArrayList<>();
        for (Customer customer : customers) {
            Order order = customer.getCurrentOrder();
            if (order == null) {
                continue;
            }
            if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PREPARING) {
                result.add(customer);
            }
        }
        return result;
    }

    public WaitingQueue getWaitingQueue() {
        return waitingQueue;
    }

    public void addOrder(Order order) {
        activeOrders.add(order);
    }

    private void cancelOrder(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        activeOrders.remove(order);
    }

    public void recordPayment(Money amount) {
        totalRevenue = totalRevenue.add(amount);
    }

    public Menu getMenu() {
        return menu;
    }

    public List<Staff> getStaffList() {
        return staffList;
    }

    // Get Cashier from staff list to process payments
    public Cashier getCashier() {
        for (Staff staff : staffList) {
            if (staff instanceof Cashier) {
                return (Cashier) staff;
            }
        }
        throw new IllegalStateException("Restaurant has no Cashier.");
    }

    public Money getTotalRevenue() {
        return totalRevenue;
    }

    public List<Customer> applyWaitPenalties() {
        List<Customer> departed = new ArrayList<>();
        for (Customer customer : customers) {
            CustomerState state = customer.getState();
            if (state == CustomerState.QUEUED || state == CustomerState.SEATED || state == CustomerState.WAITING_FOOD) {
                customer.applyWaitPenalty();
                if (customer.getState() == CustomerState.LEFT) {
                    departed.add(customer);
                }
            }
        }
        for (Customer customer : departed) {
            waitingQueue.dequeue(customer);
            Table table = getTableFor(customer);
            if (table != null) {
                table.clear();
            }
            Order order = customer.getCurrentOrder();
            if (order != null) {
                cancelOrder(order);
            }
        }
        return departed;
    }
}
