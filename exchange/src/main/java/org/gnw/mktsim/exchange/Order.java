package org.gnw.mktsim.exchange;

public class Order extends OrderBookEvent implements Cloneable {

    private final String  partyOrderId;
    private final boolean isBuy;
    private long          quantity;
    private double        price;
    private long          tradedQuantity;
    private double        tradedValue;

    public Order(String partyId, String partyOrderId, Instrument imnt, boolean isBuy, long quantity, double price) {
        super(partyId, imnt);
        this.partyOrderId = partyOrderId;
        this.isBuy = isBuy;
        this.quantity = quantity;
        this.price = price;
        this.tradedQuantity = 0L;
        this.tradedValue = 0.0;
    }

    public Order(Order order) {
        this(order.getSender().getId(), order.partyOrderId, order.getInstrument(), order.isBuy, order.quantity,
                order.price);
    }

    @Override
    public Order clone() {
        return new Order(this);
    }

    public double getPrice() {
        return this.price;
    }

    public long getQuantity() {
        return this.quantity;
    }

    public boolean isBuy() {
        return isBuy;
    }

    void trade(long quantity, double price) {
        this.quantity -= quantity;
        this.tradedQuantity += quantity;
        this.tradedValue += (quantity * price);
    }

    public long getTradedQuantity() {
        return this.tradedQuantity;
    }

    public double getTradedPrice() {
        if (this.tradedQuantity == 0) {
            return 0.0;
        } else {
            return this.tradedValue / this.tradedQuantity;
        }
    }

    public String getPartyOrderId() {
        return partyOrderId;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(this.getSender().getId());
        output.append("[ID=");
        output.append(this.partyOrderId);
        output.append((this.isBuy ? "] B " : "] S "));
        output.append(this.getInstrument().getSymbol());
        output.append(" ");
        output.append(Long.toString(this.quantity));
        output.append("@");
        output.append(Double.toString(this.price));
        if (this.tradedQuantity > 0) {
            output.append(" (Done=");
            output.append(Long.toString(this.tradedQuantity));
            output.append("@");
            output.append(Double.toString(this.getTradedPrice()));
            output.append(")");
        }
        return output.toString();
    }

}
