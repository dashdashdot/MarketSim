package org.gnw.mktsim.exchange;

import static org.junit.Assert.*;

import org.gnw.mktsim.common.msg.MsgSeqPump;
import org.junit.Test;

public class TestMessageSeqPump {

    @Test
    public void test() {
        // This is the lamest test ever
        long id1 = MsgSeqPump.next();
        long id2 = MsgSeqPump.next();
        assertNotEquals("Shouldn't have received identical messagq sequence IDs", id1, id2);
    }

}
