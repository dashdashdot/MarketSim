package org.gnw.mktsim.exchange;

import java.util.Random;

import org.gnw.mktsim.common.Instrument;
import org.gnw.mktsim.common.Order;
import org.junit.Assert;
import org.junit.Test;

public class TestOrderBookPerf {

    @Test
    public void test1K() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.1);
        OrderBook book = new OrderBook("CBOT", imnt, null);
        Random numGen = new Random();
        for (int i = 0; i < 1000; i++) {
            boolean side = numGen.nextBoolean();
            long quantity = numGen.nextInt(300) + 1L;
            double price = imnt.getLastPrice() + (side ? 0.1 : -0.1) * (numGen.nextInt(10) - 6);
            Order order = new Order("test1K", Integer.toString(i), imnt, side, quantity, price);
            book.addOrder(order);
        }
        Assert.assertTrue(imnt.getLastPrice() > 0.0);
    }

}
