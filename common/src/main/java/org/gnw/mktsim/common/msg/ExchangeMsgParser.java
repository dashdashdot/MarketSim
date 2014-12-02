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
        switch (msgType) {
        case "D ":
            return parseOrderPb(msg);
        case "8 ":
            return parseTradePb(msg);
        default:
            throw new IllegalArgumentException("Could not understand message type " + msgType);
        }
    }

    private static Order parseOrderPb(byte[] b_msg) {
        try {
            OrderMsg msg = OrderMsg.parseFrom(b_msg);
            Order order = new Order(msg.getSender().getSenderId(), msg.getClientOrderId(), msg.getSymbol(),
                    msg.getIsBuy(), msg.getQuantity(), msg.getPrice());
            return order;
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Trade parseTradePb(byte[] b_msg) {
        try {
            TradeMsg msg = TradeMsg.parseFrom(b_msg);
            Trade trade = new Trade(msg.getSender().getSenderId(), msg.getSymbol(), msg.getQuantity(), msg.getPrice(),
                    msg.getClientIdBuy(), msg.getClientIdSell(), msg.getClientOrderIdBuy(), msg.getClientOrderIdSell());
            return trade;
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
