package org.gnw.mktsim.exchange;

import java.util.concurrent.LinkedTransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderBook {

    private final String                              senderId;
    private final Instrument                          imnt;
    private OrderLadder                               buys;
    private OrderLadder                               sells;
    private final LinkedTransferQueue<OrderBookEvent> outbound;

    private final Logger                              log = LoggerFactory.getLogger(this.getClass());

    public OrderBook(final String senderId, final Instrument imnt) {
        super();
        this.senderId = senderId;
        this.outbound = new LinkedTransferQueue<OrderBookEvent>();
        this.imnt = imnt;
        this.buys = new OrderLadder(senderId, imnt, true, outbound);
        this.sells = new OrderLadder(senderId, imnt, false, outbound);
    }

    public void addOrder(Order order) {
        if (log.isDebugEnabled()) {
            log.debug("{}: Received order - {}", imnt.getSymbol(), order);
        }
        if (isValidOrder(order)) {
            sendAck(order);
            addToOutboundQueue(order.clone());
            OrderLadder hitLadder = (order.isBuy() ? sells : buys);
            OrderLadder residualLadder = (order.isBuy() ? buys : sells);
            if (hitLadder.addOrder(order)) {
                residualLadder.addOrder(order);
            }
        } else {
            sendNack(order);
        }
    }

    private boolean isValidOrder(Order order) {
        // TODO implement some logic here!
        return true;
    }

    private void sendAck(Order order) {
        // TODO Send an ack
    }

    private void sendNack(Order order) {
        // TODO Send a Nack
    }

    public int size() {
        return buys.size() + sells.size();
    }

    private void addToOutboundQueue(OrderBookEvent event) {
        outbound.put(event);
        if (log.isTraceEnabled()) {
            log.trace("Adding to queue: {}", event);
        }
    }

    public double getBestBidPrice() {
        return buys.getBestPrice();
    }

    public double getBestSellPrice() {
        return sells.getBestPrice();
    }
}
