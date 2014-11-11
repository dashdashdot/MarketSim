package org.gnw.mktsim.exchange;

import java.time.LocalDateTime;

import org.gnw.mktsim.exchange.msg.MsgSeqPump;

/**
 * A message sender's signature.
 * 
 * @author Gerard Whitehead
 *
 */
public class SenderSig {

    private String        id;
    private long          seqNum;
    private LocalDateTime timestamp;

    public SenderSig(String id, long seqNum, LocalDateTime timestamp) {
        super();
        this.id = id;
        this.seqNum = seqNum;
        this.timestamp = timestamp;
    }

    public SenderSig(String id) {
        this(id, MsgSeqPump.next(), LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public long getSeqNum() {
        return seqNum;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String toString() {
        return String.format("Message {} from {} at {}", this.seqNum, this.id, this.timestamp);
    }
}
