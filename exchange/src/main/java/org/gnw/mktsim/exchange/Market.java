package org.gnw.mktsim.exchange;

public class Market {

    private final String name;

    public enum Status {
        CLOSED, OPEN, AUCTION
    }

    private Status currentStatus = Status.CLOSED;

    public Market(String name) {
        super();
        this.name = name;
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

    public String toString() {
        return this.name;
    }
}
