package org.gnw.mktsim.exchange.msg;

public class TradeMsg extends AbsInstrumentMessage {

    private final String party1;
    private final String party2;
    private final long   quantity;
    private final double price;

    public TradeMsg(String senderId, String symbol, long quantity, double price, String party1, String party2) {
        super(senderId, symbol);
        this.quantity = quantity;
        this.price = price;
        this.party1 = party1;
        this.party2 = party2;
    }

    public String getParty1() {
        return this.party1;
    }

    public String getParty2() {
        return this.party2;
    }

    public long getQuantity() {
        return this.quantity;
    }

    public double getPrice() {
        return this.price;
    }
}
