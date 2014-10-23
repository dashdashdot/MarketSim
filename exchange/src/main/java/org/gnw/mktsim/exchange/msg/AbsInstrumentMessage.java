package org.gnw.mktsim.exchange.msg;

public abstract class AbsInstrumentMessage extends AbsMessage {

    private final String symbol;

    public AbsInstrumentMessage(final String senderId, final String symbol) {
        super(senderId);
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}
