package org.gnw.mktsim.common;

import org.gnw.mktsim.common.msg.Messages.TradeMsg;

import com.google.protobuf.GeneratedMessage;

public class Trade extends OrderBookEvent {

    private long   quantity;
    private double price;
    private String clientId_buy;
    private String clientId_sell;
    private String clientOrderId_buy;
    private String clientOrderId_sell;

    public Trade(String senderId, Order orderA, Order orderB, long quantity, double price) {
        super(senderId, orderA.getSymbol());
        this.quantity = quantity;
        this.price = price;
        Order buy = (orderA.isBuy() ? orderA : orderB);
        Order sell = (orderA.isBuy() ? orderB : orderA);
        this.clientId_buy = buy.getSender().getId();
        this.clientId_sell = sell.getSender().getId();
        this.clientOrderId_buy = buy.getPartyOrderId();
        this.clientOrderId_sell = sell.getPartyOrderId();
    }

    public Trade(String senderId, String symbol, long quantity, double price, String clientId_buy,
            String clientId_sell, String clientOrderId_buy, String clientOrderId_sell) {
        super(senderId, symbol);
        this.quantity = quantity;
        this.price = price;
        this.clientId_buy = clientId_buy;
        this.clientId_sell = clientId_sell;
        this.clientOrderId_buy = clientOrderId_buy;
        this.clientOrderId_sell = clientOrderId_sell;
    }

    public String toString() {
        return String.format("%s: Trade of %,d at %,.2f between %s (buyer) and %s (seller)", this.getSymbol(),
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

    public GeneratedMessage toProtoBuf() {
        TradeMsg pb = TradeMsg.newBuilder().setSender(this.getSender().toProtoBuf()).setSymbol(this.getSymbol())
                .setClientIdBuy(this.clientId_buy).setClientIdSell(this.clientId_sell)
                .setClientOrderIdBuy(this.clientOrderId_buy).setClientOrderIdSell(this.clientOrderId_sell)
                .setQuantity(this.quantity).setPrice(this.price).build();
        return pb;
    }

    protected String getMsgTypeUnsafe() {
        return "8";
    }
}
