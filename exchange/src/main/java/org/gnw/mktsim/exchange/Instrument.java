package org.gnw.mktsim.exchange;

public class Instrument {

    private final String symbol;
    private double       lastPrice;
    private double       tickSize;

    public Instrument(String symbol) {
        this(symbol, 100.0, 1.0);
    }

    public Instrument(String symbol, double lastPrice, double tickSize) {
        super();
        this.symbol = symbol;
        this.lastPrice = lastPrice;
        this.tickSize = tickSize;
    }

    public void setLastPrice(final double price) {
        this.lastPrice = price;
    }

    public double getLastPrice() {
        return this.lastPrice;
    }

    public void setTickSize(final double tickSize) {
        this.tickSize = tickSize;
    }

    public double getTickSize() {
        return this.tickSize;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String toString() {
        return String.format("%s=%.2f", this.symbol, this.lastPrice);
    }
}
