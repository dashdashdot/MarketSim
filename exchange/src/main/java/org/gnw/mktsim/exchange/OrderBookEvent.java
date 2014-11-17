package org.gnw.mktsim.exchange;

import com.google.protobuf.GeneratedMessage;

public abstract class OrderBookEvent {

    private final SenderSig  sender;
    private final Instrument imnt;
    private String           msgType = null;

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

    /**
     * A maximum 2 character code representing the message type.
     * 
     * @return
     */
    public String getMsgType() {
        if (this.msgType == null) {
            String s_msgType = this.getMsgTypeUnsafe();
            switch (s_msgType.length()) {
            case 0:
                this.msgType = "  ";
                break;
            case 1:
                this.msgType = s_msgType + " ";
                break;
            case 2:
                this.msgType = s_msgType;
                break;
            default:
                this.msgType = s_msgType.substring(0, 2);
            }
        }
        return this.msgType;
    }

    protected abstract String getMsgTypeUnsafe();

    public abstract GeneratedMessage toProtoBuf();
}
