package org.gnw.mktsim.exchange;

import java.util.List;

import org.gnw.mktsim.common.Instrument;
import org.gnw.mktsim.common.Order;
import org.gnw.mktsim.common.OrderBookEvent;
import org.gnw.mktsim.common.Trade;
import org.gnw.mktsim.exchange.pub.UtilListener;
import org.gnw.mktsim.exchange.pub.UtilSender;
import org.junit.Assert;
import org.junit.Test;

public class TestMarketManager {

    @Test
    public void testInitialisation() {
        MarketManager mm = new MarketManager("London", 5555, 5556);
        mm.loadInstrument("VOD", "VODAFONE", 225.7, 0.05);
        mm.loadInstrument("TSCO", "TESCO", 191.15, 0.05);
    }

    @Test
    public void testPublications() throws Exception {
        MarketManager mm = new MarketManager("London", 5555, 5556);
        Instrument vod = new Instrument("VOD", 225.07, 0.05);
        mm.loadInstrument(vod);
        mm.start();
        mm.addOrder(new Order("testPublications", "1", vod, true, 100L, 225.0));
        mm.addOrder(new Order("testPublications", "2", vod, true, 100L, 225.1));
        mm.addOrder(new Order("testPublications", "3", vod, false, 150L, 225.0));
        try {
            Thread.sleep(500);
        } finally {
            mm.stop();
        }
    }

    @Test
    public void testSingleConcurrent() throws InterruptedException {
        MarketManager mm = new MarketManager("London", 5555, 5556);
        mm.loadInstrument("VOD", "VODAFONE", 225.7, 0.05);
        mm.start();
        UtilSender sender = new UtilSender(5555);
        UtilListener listener = new UtilListener(5556);
        try {
            listener.start();
            Thread.sleep(100);
            sender.init();
            Thread.sleep(100);
            Order orderIn = new Order("testSingleConcurrent", "1", "VOD", true, 100L, 225.0);
            sender.send(orderIn);
            Thread.sleep(100);
            List<OrderBookEvent> events = listener.dump();
            Assert.assertEquals(1, events.size());
            Order orderOut = (Order) events.get(0);
            Assert.assertEquals(orderIn.toString(), orderOut.toString());
        } finally {
            listener.stop();
            sender.stop();
            mm.stop();
        }
    }

    @Test
    public void testTradeConcurrent() throws InterruptedException {
        MarketManager mm = new MarketManager("London", 5555, 5556);
        mm.loadInstrument("VOD", "VODAFONE", 225.7, 0.05);
        mm.start();
        UtilSender sender = new UtilSender(5555);
        UtilListener listener = new UtilListener(5556);
        try {
            listener.start();
            Thread.sleep(100);
            sender.init();
            Thread.sleep(100);
            Order orderBuy = new Order("testTradeConcurrent", "theBuy", "VOD", true, 100L, 225.0);
            Order orderSell = new Order("testTradeConcurrent", "theSell", "VOD", false, 50L, 224.0);
            sender.send(orderBuy);
            Thread.sleep(100);
            sender.send(orderSell);
            Thread.sleep(100);
            List<OrderBookEvent> events = listener.dump();
            // Sequence of events should be:
            // 1. The buy order
            // 2. The sell order
            // 3. The trade
            // 4. Modified sell order, fully done
            // 5. Modified buy order, partially done
            Assert.assertEquals(5, events.size());
            Assert.assertEquals(orderBuy.toString(), events.get(0).toString());
            Assert.assertEquals(orderSell.toString(), events.get(1).toString());
            Assert.assertTrue(events.get(2) instanceof Trade);
            Trade trade = (Trade) events.get(2);
            Assert.assertEquals(225.0, trade.getPrice(), 0.01);
            Assert.assertEquals(50L, trade.getQuantity());
        } finally {
            listener.stop();
            sender.stop();
            mm.stop();
        }
    }

}
