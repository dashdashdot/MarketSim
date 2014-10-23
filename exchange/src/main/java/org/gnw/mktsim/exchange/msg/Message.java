package org.gnw.mktsim.exchange.msg;

import java.time.LocalDateTime;

public interface Message {

    public LocalDateTime getTimestamp();

    public String getSenderId();

    public long getMsgSeqId();

}
