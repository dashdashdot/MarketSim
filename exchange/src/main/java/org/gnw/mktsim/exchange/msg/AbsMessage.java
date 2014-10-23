package org.gnw.mktsim.exchange.msg;

import java.time.LocalDateTime;

abstract class AbsMessage implements Message {

    private final LocalDateTime timestamp;
    private final String        senderId;
    private final long          msgSeqId;

    public AbsMessage(String senderId) {
        super();
        this.timestamp = LocalDateTime.now();
        this.senderId = senderId;
        this.msgSeqId = MsgSeqPump.next();
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public long getMsgSeqId() {
        return this.msgSeqId;
    }

}
