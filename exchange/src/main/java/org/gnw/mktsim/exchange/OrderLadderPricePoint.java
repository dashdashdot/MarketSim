package org.gnw.mktsim.exchange;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * This is the FIFO queue representing all the orders at a specific price point.
 * 
 * @author Gerard Whitehead
 *
 */
class OrderLadderPricePoint {

    private final double       price;
    private final Queue<Order> orders;

    public OrderLadderPricePoint(double price) {
        super();
        this.price = price;
        this.orders = new ArrayDeque<Order>();
    }

    public void add(Order order) {
        this.orders.add(order);
    }

    public double getPrice() {
        return this.price;
    }

    public int size() {
        return orders.size();
    }

    public Order peek() {
        return orders.peek();
    }

    public Order pop() {
        return orders.remove();
    }

    public long getTotalOrderVolume() {
        long total = 0L;
        for (Order order : orders) {
            total += order.getQuantity();
        }
        return total;
    }
}
