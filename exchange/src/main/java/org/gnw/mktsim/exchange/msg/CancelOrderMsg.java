package org.gnw.mktsim.exchange.msg;

public class CancelOrderMsg extends AbsInstrumentMessage {

    public CancelOrderMsg(String senderId, String symbol, long orderId) {
        super(senderId, symbol);
    }
}
