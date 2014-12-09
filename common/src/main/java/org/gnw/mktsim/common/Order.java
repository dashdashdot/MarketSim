package org.gnw.mktsim.common;

import org.gnw.mktsim.common.msg.Messages.OrderMsg;

import com.google.protobuf.GeneratedMessage;

public class Order extends OrderBookEvent implements Cloneable {

    private final String  partyOrderId;
    private final boolean isBuy;
    private long          quantity;
    private double        price;
    private long          tradedQuantity;
    private double        tradedValue;

    public Order(String partyId, String partyOrderId, String symbol, boolean isBuy, long quantity, double price) {
        super(partyId, symbol);
        this.partyOrderId = partyOrderId;
        this.isBuy = isBuy;
        this.quantity = quantity;
        this.price = price;
        this.tradedQuantity = 0L;
        this.tradedValue = 0.0;
    }

    public Order(String partyId, String partyOrderId, Instrument imnt, boolean isBuy, long quantity, double price) {
        this(partyId, partyOrderId, imnt.getSymbol(), isBuy, quantity, price);
    }

    public Order(Order order) {
        this(order.getSender().getId(), order.partyOrderId, order.getSymbol(), order.isBuy, order.quantity, order.price);
    }

    public Order(OrderMsg pb) {
        this(pb.getSender().getSenderId(), pb.getClientOrderId(), pb.getSymbol(), pb.getIsBuy(), pb.getQuantity(),
                pb.getPrice());
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

    public void trade(long quantity, double price) {
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
        output.append(this.getSymbol());
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

    public GeneratedMessage toProtoBuf() {
        OrderMsg pb = OrderMsg.newBuilder().setSender(this.getSender().toProtoBuf())
                .setClientOrderId(this.partyOrderId).setSymbol(this.getSymbol()).setIsBuy(this.isBuy)
                .setQuantity(this.quantity).setPrice(this.price).build();
        return pb;
    }

    protected String getMsgTypeUnsafe() {
        return "D ";
    }
}
