package org.gnw.mktsim.exchange;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestOrderBook {

    @Test
    public void testAddSingleOrder() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.5);
        OrderBook book = new OrderBook("CBOT", imnt);
        Order buy1 = new Order("TEST1", "1", imnt, true, 100L, 98.0);
        book.addOrder(buy1);
        assertEquals(1, book.size());
    }

    @Test
    public void testAddTwoBuysLowThenHigh() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.5);
        OrderBook book = new OrderBook("CBOT", imnt);
        Order buy1 = new Order("TEST1", "1", imnt, true, 100L, 98.0);
        Order buy2 = new Order("TEST2", "1", imnt, true, 100L, 102.0);
        book.addOrder(buy1);
        book.addOrder(buy2);
        assertEquals(2, book.size());
        assertEquals(102.0, book.getBestBidPrice(), 0.01);
    }

    @Test
    public void testAddTwoBuysHighThenLow() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.5);
        OrderBook book = new OrderBook("CBOT", imnt);
        Order buy1 = new Order("TEST1", "1", imnt, true, 100L, 98.0);
        Order buy2 = new Order("TEST2", "1", imnt, true, 100L, 102.0);
        book.addOrder(buy2);
        book.addOrder(buy1);
        assertEquals(2, book.size());
        assertEquals(102.0, book.getBestBidPrice(), 0.01);
    }

    @Test
    public void testAddTwoSellsLowThenHigh() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.5);
        OrderBook book = new OrderBook("CBOT", imnt);
        Order sell1 = new Order("TEST1", "1", imnt, false, 100L, 98.0);
        Order sell2 = new Order("TEST2", "1", imnt, false, 100L, 102.0);
        book.addOrder(sell1);
        book.addOrder(sell2);
        assertEquals(2, book.size());
        assertEquals(98.0, book.getBestSellPrice(), 0.01);
    }

    @Test
    public void testAddTwoSellsHighThenLow() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.5);
        OrderBook book = new OrderBook("CBOT", imnt);
        Order sell1 = new Order("TEST1", "1", imnt, false, 100L, 98.0);
        Order sell2 = new Order("TEST2", "1", imnt, false, 100L, 102.0);
        book.addOrder(sell2);
        book.addOrder(sell1);
        assertEquals(2, book.size());
        assertEquals(98.0, book.getBestSellPrice(), 0.01);
    }

    @Test
    public void testBuySellNoMatch() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.5);
        OrderBook book = new OrderBook("CBOT", imnt);
        Order buy1 = new Order("TEST1", "1", imnt, false, 100L, 98.0);
        Order sell1 = new Order("TEST2", "1", imnt, false, 100L, 102.0);
        book.addOrder(buy1);
        book.addOrder(sell1);
        assertEquals(2, book.size());
    }

    @Test
    public void testBuySellPartialMatch() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.5);
        OrderBook book = new OrderBook("CBOT", imnt);
        Order buy1 = new Order("TEST1", "1", imnt, true, 100L, 98.0);
        Order sell1 = new Order("TEST2", "1", imnt, false, 50L, 98.0);
        book.addOrder(buy1);
        book.addOrder(sell1);
        assertEquals(1, book.size());
    }

    @Test
    public void testBuyBuySellPartialMatch() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.5);
        OrderBook book = new OrderBook("CBOT", imnt);
        Order buy1 = new Order("TEST1", "1", imnt, true, 25L, 98.0);
        Order buy2 = new Order("TEST2", "1", imnt, true, 100L, 98.0);
        Order sell1 = new Order("TEST3", "1", imnt, false, 50L, 98.0);
        book.addOrder(buy1);
        book.addOrder(buy2);
        book.addOrder(sell1);
        // First buy should be 100% done
        assertEquals(25L, buy1.getTradedQuantity());
        // Second buy will be partially filled
        assertEquals(25L, buy2.getTradedQuantity());
        assertEquals(75L, buy2.getQuantity());
        // Sell should be fully done
        assertEquals(50L, sell1.getTradedQuantity());
        assertEquals(0L, sell1.getQuantity());
        // And there should only be one order left on the book
        assertEquals(1, book.size());
    }

    @Test
    public void testImntPrice() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.5);
        OrderBook book = new OrderBook("CBOT", imnt);
        book.addOrder(new Order("TEST1", "1", imnt, true, 25L, 100.0));
        book.addOrder(new Order("TEST2", "1", imnt, true, 100L, 99.0));
        book.addOrder(new Order("TEST3", "1", imnt, false, 50L, 98.0));
        assertEquals(99.0, imnt.getLastPrice(), 0.01);
    }

    @Test
    public void testBuyBuyBuyBuySellDrill() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.5);
        OrderBook book = new OrderBook("CBOT", imnt);
        Order buy1 = new Order("TEST1", "1L", imnt, true, 25L, 98.0);
        Order buy2 = new Order("TEST2", "1L", imnt, true, 100L, 98.0);
        Order buy3 = new Order("TEST3", "1L", imnt, true, 100L, 99.0); // Best bid
        Order buy4 = new Order("TEST4", "1L", imnt, true, 100L, 97.0);
        Order buy5 = new Order("TEST5", "1L", imnt, true, 100L, 97.5);
        Order sell1 = new Order("TEST6", "1L", imnt, false, 300L, 98.0);
        book.addOrder(buy1);
        book.addOrder(buy2);
        book.addOrder(buy3);
        book.addOrder(buy4);
        book.addOrder(buy5);
        book.addOrder(sell1);
        // First, second, and third buys should be 100% done
        assertEquals(100L, buy3.getTradedQuantity());
        assertEquals(100L, buy2.getTradedQuantity());
        assertEquals(25L, buy1.getTradedQuantity());
        // Sell should be partially filled
        assertEquals(225L, sell1.getTradedQuantity());
        assertEquals(75L, sell1.getQuantity());
        // And there should be three orders left on the book
        assertEquals(3, book.size());
    }

    @Test
    public void testBuyBuyBuyBuySellDrillAllThru() {
        Instrument imnt = new Instrument("VOD.L", 100, 0.5);
        OrderBook book = new OrderBook("CBOT", imnt);
        Order buy1 = new Order("TEST1", "1L", imnt, true, 25L, 98.0);
        Order buy2 = new Order("TEST2", "1L", imnt, true, 100L, 98.0);
        Order buy3 = new Order("TEST3", "1L", imnt, true, 100L, 99.0); // Best bid
        Order buy4 = new Order("TEST4", "1L", imnt, true, 100L, 97.0);
        Order buy5 = new Order("TEST5", "1L", imnt, true, 100L, 97.5);
        Order sell1 = new Order("TEST6", "1L", imnt, false, 500L, 96.0);
        book.addOrder(buy1);
        book.addOrder(buy2);
        book.addOrder(buy3);
        book.addOrder(buy4);
        book.addOrder(buy5);
        book.addOrder(sell1);
        // All buys should be 100% done
        assertEquals(0L, buy1.getQuantity());
        assertEquals(0L, buy2.getQuantity());
        assertEquals(0L, buy3.getQuantity());
        assertEquals(0L, buy4.getQuantity());
        assertEquals(0L, buy5.getQuantity());
        // The sell should be partially done
        assertEquals(425L, sell1.getTradedQuantity());
        assertEquals(75L, sell1.getQuantity());
        assertEquals((25 * 98 + 100 * 98 + 100 * 99 + 100 * 97 + 100 * 97.5) / 425.0, sell1.getTradedPrice(), 0.01);
        // And there should only be the one remaining sell order on the book
        assertEquals(1, book.size());
    }

}
