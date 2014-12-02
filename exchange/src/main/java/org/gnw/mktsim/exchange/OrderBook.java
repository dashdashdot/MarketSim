package org.gnw.mktsim.exchange;

import org.gnw.mktsim.common.Instrument;
import org.gnw.mktsim.common.Order;
import org.gnw.mktsim.common.OrderBookEvent;
import org.gnw.mktsim.exchange.pub.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderBook {

    private final String     senderId;
    private final Instrument imnt;
    private OrderLadder      buys;
    private OrderLadder      sells;
    private Publisher        publisher;

    private final Logger     log = LoggerFactory.getLogger(this.getClass());

    public OrderBook(final String senderId, final Instrument imnt, Publisher publisher) {
        super();
        this.senderId = senderId;
        this.imnt = imnt;
        this.publisher = publisher;
        this.buys = new OrderLadder(senderId, imnt, true, publisher);
        this.sells = new OrderLadder(senderId, imnt, false, publisher);
    }

    public void addOrder(Order order) {
        if (log.isDebugEnabled()) {
            log.debug("{}: Received order - {}", imnt.getSymbol(), order);
        }
        if (isValidOrder(order)) {
            sendAck(order);
            publish(order.clone());
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

    private void publish(OrderBookEvent event) {
        if (publisher != null) {
            publisher.add(event);
        }
    }

    public double getBestBidPrice() {
        return buys.getBestPrice();
    }

    public double getBestSellPrice() {
        return sells.getBestPrice();
    }

    public int getNumTrades() {
        return buys.getNumTrades() + sells.getNumTrades();
    }

    public long getVolumeTraded() {
        return buys.getVolumeTraded() + sells.getVolumeTraded();
    }

    public double getNotionalTraded() {
        return buys.getNotionalTraded() + sells.getNotionalTraded();
    }

    public double getAvgTradePrice() {
        return getNotionalTraded() / getVolumeTraded();
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.toStringTopLine());
        s.append(System.lineSeparator());
        s.append("------------------------------------------------------------");
        s.append(System.lineSeparator());
        s.append(sells.toString());
        s.append(buys.toString());
        return s.toString();
    }

    public String toStringTopLine() {
        return String.format("Instrument: %s Last=%,.2f VWAP=%,.4f n=%,d v=%,d", imnt.getSymbol(), imnt.getLastPrice(),
                getAvgTradePrice(), getNumTrades(), getVolumeTraded());
    }
}
