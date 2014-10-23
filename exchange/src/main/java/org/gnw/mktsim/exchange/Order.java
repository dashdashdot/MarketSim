package org.gnw.mktsim.exchange;

public class Order {

    private final String     partyId;
    private final long       partyOrderId;
    private final Instrument imnt;
    private final boolean    isBuy;
    private long             quantity;
    private double           price;
    private long             tradedQuantity;
    private double           tradedValue;

    public Order(String partyId, long partyOrderId, Instrument imnt, boolean isBuy, long quantity, double price) {
        super();
        this.partyId = partyId;
        this.partyOrderId = partyOrderId;
        this.imnt = imnt;
        this.isBuy = isBuy;
        this.quantity = quantity;
        this.price = price;
        this.tradedQuantity = 0L;
        this.tradedValue = 0.0;
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

    public String getPartyId() {
        return partyId;
    }

    public long getPartyOrderId() {
        return partyOrderId;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(this.imnt.getSymbol());
        output.append((this.isBuy ? " B " : " S "));
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
