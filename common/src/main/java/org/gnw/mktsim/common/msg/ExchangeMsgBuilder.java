package org.gnw.mktsim.common.msg;

import org.gnw.mktsim.common.OrderBookEvent;

/**
 * Creates the protobuf messages from the POJO.
 * 
 * @author Gerard Whitehead
 *
 */
public class ExchangeMsgBuilder {

    /**
     * Build the byte array that the {@link ExchangeMsgParser.parse(byte[])} can
     * understand.
     * 
     * @param e
     *            An order book event.
     * @return The matching byte array
     */
    public static byte[] build(OrderBookEvent e) {
        // Msg format is a byte array containing:
        // symbol [space] msgType protobuf
        byte[] b_sym = e.getSymbol().getBytes();
        byte[] b_msgType = e.getMsgType().getBytes();
        byte[] b_pb = e.toProtoBuf().toByteArray();
        // Now put it into a single array
        byte[] b_msg = new byte[b_sym.length + b_pb.length + 3];
        System.arraycopy(b_sym, 0, b_msg, 0, b_sym.length);
        b_msg[b_sym.length] = 32;
        System.arraycopy(b_msgType, 0, b_msg, b_sym.length + 1, 2);
        System.arraycopy(b_pb, 0, b_msg, b_sym.length + 3, b_pb.length);
        return b_msg;
    }
}
