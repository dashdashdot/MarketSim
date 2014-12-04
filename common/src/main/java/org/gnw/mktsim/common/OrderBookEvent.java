package org.gnw.mktsim.common;

import com.google.protobuf.GeneratedMessage;

public abstract class OrderBookEvent {

    private final SenderSig sender;
    private final String    symbol;
    private String          msgType = null;

    public OrderBookEvent() {
        this("Unknown", "Unknown");
    }

    public OrderBookEvent(String senderId) {
        this(senderId, "Unknown");
    }

    public OrderBookEvent(String senderId, Instrument imnt) {
        this(senderId, imnt.getSymbol());
    }

    public OrderBookEvent(String senderId, String symbol) {
        super();
        this.sender = new SenderSig(senderId);
        this.symbol = symbol;
    }

    public SenderSig getSender() {
        return sender;
    }

    public String getSymbol() {
        return this.symbol;
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
