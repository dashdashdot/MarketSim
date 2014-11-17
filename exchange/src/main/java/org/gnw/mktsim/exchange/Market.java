package org.gnw.mktsim.exchange;

import java.util.HashMap;
import java.util.Map;

import org.gnw.mktsim.exchange.pub.Publisher;

public class Market {

    private final String                 name;
    private final Map<String, OrderBook> books;
    private final Publisher              publisher;

    public enum Status {
        CLOSED, OPEN, AUCTION
    }

    private Status currentStatus = Status.CLOSED;

    public Market(String name, Publisher publisher) {
        super();
        this.name = name;
        this.books = new HashMap<String, OrderBook>();
        this.publisher = publisher;
    }

    public void addOrderBook(Instrument imnt, Publisher publisher) {
        OrderBook book = new OrderBook(this.name, imnt, publisher);
        books.put(imnt.getSymbol(), book);
    }

    public void addOrder(Order order) {
        String symbol = order.getInstrument().getSymbol();
        if (books.containsKey(symbol)) {
            books.get(symbol).addOrder(order);
        } else {

        }
    }

    public String getName() {
        return this.name;
    }

    public Status getStatus() {
        return this.currentStatus;
    }

    public void open() {
        this.currentStatus = Status.OPEN;
        broadcast();
    }

    public void close() {
        this.currentStatus = Status.CLOSED;
        broadcast();
    }

    public void auction() {
        this.currentStatus = Status.AUCTION;
        broadcast();
    }

    private void broadcast() {
        // TODO implement this!
    }

    private void publish(OrderBookEvent event) {
        if (publisher != null) {
            publisher.add(event);
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Market: ");
        s.append(this.name);
        s.append(System.lineSeparator());
        s.append("--------------------------------------------");
        s.append(System.lineSeparator());
        for (OrderBook book : books.values()) {
            s.append(book.toStringTopLine());
            s.append(System.lineSeparator());
        }
        return s.toString();
    }
}
