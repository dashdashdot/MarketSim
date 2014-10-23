package org.gnw.mktsim.exchange.msg;

public class MsgSeqPump {

    private static long seq = 0L;

    synchronized public static long next() {
        return seq++;
    }
}
