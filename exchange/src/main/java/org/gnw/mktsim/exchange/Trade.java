package org.gnw.mktsim.exchange;

public class Trade extends OrderBookEvent {

    private long   quantity;
    private double price;
    private String clientId_buy;
    private String clientId_sell;
    private String clientOrderId_buy;
    private String clientOrderId_sell;

    public Trade(String senderId, Order orderA, Order orderB, long quantity, double price) {
        super(senderId, orderA.getInstrument());
        this.quantity = quantity;
        this.price = price;
        Order buy = (orderA.isBuy() ? orderA : orderB);
        Order sell = (orderA.isBuy() ? orderB : orderA);
        this.clientId_buy = buy.getSender().getId();
        this.clientId_sell = sell.getSender().getId();
        this.clientOrderId_buy = buy.getPartyOrderId();
        this.clientOrderId_sell = sell.getPartyOrderId();
    }

    public String toString() {
        return String.format("%s: Trade of %,d at %,.2f between %s (buyer) and %s (seller)", getInstrument().getSymbol(),
                quantity, price, clientId_buy, clientId_sell);
    }

    public long getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getClientId_buy() {
        return clientId_buy;
    }

    public String getClientId_sell() {
        return clientId_sell;
    }

    public String getClientOrderId_buy() {
        return clientOrderId_buy;
    }

    public String getClientOrderId_sell() {
        return clientOrderId_sell;
    }
}
