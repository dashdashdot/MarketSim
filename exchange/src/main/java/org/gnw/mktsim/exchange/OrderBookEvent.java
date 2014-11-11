package org.gnw.mktsim.exchange;

public abstract class OrderBookEvent {

    private final SenderSig  sender;
    private final Instrument imnt;

    public OrderBookEvent(String senderId) {
        this(senderId, null);
    }

    public OrderBookEvent(String senderId, Instrument imnt) {
        super();
        this.sender = new SenderSig(senderId);
        this.imnt = imnt;
    }

    public SenderSig getSender() {
        return sender;
    }

    public Instrument getInstrument() {
        return imnt;
    }
}
