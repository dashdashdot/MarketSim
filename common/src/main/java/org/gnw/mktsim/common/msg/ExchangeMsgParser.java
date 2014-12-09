package org.gnw.mktsim.common.msg;

import org.gnw.mktsim.common.Order;
import org.gnw.mktsim.common.OrderBookEvent;
import org.gnw.mktsim.common.Trade;
import org.gnw.mktsim.common.msg.Messages.OrderMsg;
import org.gnw.mktsim.common.msg.Messages.TradeMsg;

import com.google.protobuf.InvalidProtocolBufferException;

public class ExchangeMsgParser {

    public static OrderBookEvent parse(byte[] b_msg) {
        String msg = new String(b_msg);
        String symbol = msg.substring(0, msg.indexOf(" "));
        String msgType = msg.substring(symbol.length() + 1, symbol.length() + 3);
        byte[] b_pb = new byte[b_msg.length - symbol.length() - 3];
        System.arraycopy(b_msg, symbol.length() + 3, b_pb, 0, b_msg.length - symbol.length() - 3);
        OrderBookEvent pbo = parsePb(msgType, b_pb);
        return pbo;
    }

    private static OrderBookEvent parsePb(String msgType, byte[] msg) {
        try {
            switch (msgType) {
            case "D ":
                return new Order(OrderMsg.parseFrom(msg));
            case "8 ":
                return new Trade(TradeMsg.parseFrom(msg));
            default:
                throw new IllegalArgumentException("Could not understand message type " + msgType);
            }
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
