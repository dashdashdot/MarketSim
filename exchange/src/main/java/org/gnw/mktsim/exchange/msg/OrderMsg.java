package org.gnw.mktsim.exchange.msg;

public class OrderMsg extends AbsInstrumentMessage {

    public enum Side {
        BUY, SELL
    };

    private long       orderId;
    private final Side side;
    private long       quantity;
    private double     price;

    public OrderMsg(String clientId, String symbol, long orderId, Side side, long quantity, double price) {
        super(clientId, symbol);
        this.orderId = orderId;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
    }

    public long getOrderId() {
        return this.orderId;
    }

    public Side getSide() {
        return this.side;
    }

    public long getQuantity() {
        return this.quantity;
    }

    public double getPrice() {
        return this.price;
    }

}
