package org.gnw.mktsim.common;

import java.time.LocalDateTime;

import org.gnw.mktsim.common.msg.MsgSeqPump;
import org.gnw.mktsim.common.msg.Messages.Sender;
import org.gnw.mktsim.common.msg.Messages.Timestamp;

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
    private Sender        pb;

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

    public Sender toProtoBuf() {
        if (this.pb == null) {
            Timestamp ts = Timestamp.newBuilder().setYear(this.timestamp.getYear())
                    .setMonth(this.timestamp.getMonthValue()).setDay(this.timestamp.getDayOfMonth())
                    .setHours(this.timestamp.getHour()).setMinutes(this.timestamp.getMinute())
                    .setSeconds(this.timestamp.getSecond()).setNanoseconds(this.timestamp.getNano()).build();

            this.pb = Sender.newBuilder().setSenderId(this.id).setSenderSeqNum(this.seqNum).setSenderTimestamp(ts)
                    .build();
        }
        return this.pb;
    }
}
