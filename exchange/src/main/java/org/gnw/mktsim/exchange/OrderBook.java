package org.gnw.mktsim.exchange;

import java.util.ArrayDeque;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderBook {

    private final Instrument imnt;
    private double[]         priceLadder;
    private Queue<Order>[]   buyLadder;
    private Queue<Order>[]   sellLadder;
    private double           ladderBottom;
    private double           ladderTick;
    private int              bestBuy;
    private int              bestSell;
    private int              numBuys;
    private int              numSells;

    private final Logger     log = LoggerFactory.getLogger(this.getClass());

    public OrderBook(final Instrument imnt) {
        super();
        this.imnt = imnt;
        this.numBuys = 0;
        this.numSells = 0;
        buildPriceLadder(100);
        if (log.isInfoEnabled()) {
            log.info("{}: Order book initialised with {} ticks across range {}-{} with tick size {}", imnt.getSymbol(),
                    priceLadder.length, ladderBottom, ladderBottom + priceLadder.length * ladderTick, ladderTick);
        }
    }

    public void addOrder(Order order) {
        if (log.isDebugEnabled()) {
            log.debug("{}: Received order - {}", imnt.getSymbol(), order);
        }
        int i = getLadderPoint(order.getPrice());
        if (order.isBuy()) {
            if (i >= this.bestSell) {
                hitSells(this.bestSell, i, order);
            } else {
                joinBids(i, order);
            }
        } else {
            if (i <= this.bestBuy) {
                hitBuys(this.bestBuy, i, order);
            } else {
                joinSells(i, order);
            }
        }
    }

    private void hitBuys(int atPoint, int maxPoint, Order sell) {
        // Start hitting the bids from the best bid point
        if (buyLadder[atPoint].size() > 0) {
            // Starting hitting the buys at this point
            Order bestBuy = buyLadder[atPoint].peek();
            // There is a matching buy. Several possibilities:
            // 1. This order is smaller, therefore best remains.
            // 2. This order is the same size, therefore all goes.
            // 3. This order is bigger, so we hit this and have another go.
            // In all cases, we trade the min of buy and sell size.
            long tradedQuantity = Math.min(sell.getQuantity(), bestBuy.getQuantity());
            trade(sell, bestBuy, tradedQuantity);
            if (sell.getQuantity() > 0) {
                hitBuys(atPoint, maxPoint, sell);
            }
        } else {
            // There are no sells in the order book at this point.
            // If we've reached our highest buy price then we just join the
            // other buys here.
            if (atPoint == maxPoint) {
                joinSells(atPoint, sell);
            } else {
                // We hit the next level of offers.
                hitBuys(atPoint - 1, maxPoint, sell);
            }
        }
    }

    private void hitSells(int atPoint, int maxPoint, Order buy) {
        // Start hitting the sells from the best sell point
        if (sellLadder[atPoint].size() > 0) {
            // Starting buying the sells at this point
            Order bestSell = sellLadder[atPoint].peek();
            // There is a matching sell. Several possibilities:
            // 1. This order is smaller, therefore best remains.
            // 2. This order is the same size, therefore all goes.
            // 3. This order is bigger, so we hit this and have another go.
            // In all cases, we trade the min of buy and sell size.
            long tradedQuantity = Math.min(buy.getQuantity(), bestSell.getQuantity());
            trade(buy, bestSell, tradedQuantity);
            if (buy.getQuantity() > 0) {
                hitSells(atPoint, maxPoint, buy);
            }
        } else {
            // There are no sells in the order book at this point.
            // If we've reached our highest buy price then we just join the
            // other buys here.
            if (atPoint == maxPoint) {
                joinBids(atPoint, buy);
            } else {
                // We hit the next level of offers.
                hitSells(atPoint + 1, maxPoint, buy);
            }
        }
    }

    private void trade(Order incomingOrder, Order bookOrder, long quantity) {
        // First update the incomingOrder
        double price = bookOrder.getPrice();
        incomingOrder.trade(quantity, price);
        bookOrder.trade(quantity, price);
        // Report the trade
        if (log.isDebugEnabled()) {
            log.debug("{}: Trade of {} at {} between {} and {}", imnt.getSymbol(), quantity, price,
                    incomingOrder.getPartyId(), bookOrder.getPartyId());
        }
        // Update the instrument
        this.imnt.setLastPrice(price);
        // If the bookOrder is now cleaned, then we need to remove it from the
        // book.
        if (bookOrder.getQuantity() == 0) {
            if (bookOrder.isBuy()) {
                buyLadder[bestBuy].remove();
                numBuys--;
                while (bestBuy > -1 && buyLadder[bestBuy].size() == 0) {
                    bestBuy--;
                }
            } else {
                sellLadder[bestSell].remove();
                numSells--;
                while (bestSell < sellLadder.length && sellLadder[bestSell].size() == 0) {
                    bestSell++;
                }
            }
        }
    }

    private void joinBids(int i, Order buy) {
        this.buyLadder[i].add(buy);
        this.numBuys++;
        if (i > this.bestBuy) {
            this.bestBuy = i;
        }
    }

    private void joinSells(int i, Order sell) {
        this.sellLadder[i].add(sell);
        this.numSells++;
        if (i < this.bestSell) {
            this.bestSell = i;
        }
    }

    @SuppressWarnings("unchecked")
    private void buildPriceLadder(int ticksUpDown) {
        int n = 2 * ticksUpDown + 1;
        this.ladderTick = this.imnt.getTickSize();
        this.ladderBottom = Math.max(this.ladderTick, this.imnt.getLastPrice() - ticksUpDown * this.ladderTick);
        this.priceLadder = new double[n];
        this.buyLadder = new Queue[n];
        this.sellLadder = new Queue[n];
        for (int i = 0; i < n; i++) {
            this.priceLadder[i] = this.ladderBottom + i * this.ladderTick;
            this.buyLadder[i] = new ArrayDeque<Order>();
            this.sellLadder[i] = new ArrayDeque<Order>();
        }
        this.bestBuy = -1;
        this.bestSell = n;
    }

    private int getLadderPoint(double price) {
        int output = (int) ((price - ladderBottom) / ladderTick);
        return (output < 0 ? -1 : output);
    }

    public int size() {
        return numBuys + numSells;
    }

    public double getBestBidPrice() {
        if (this.bestBuy == -1) {
            return 0.0;
        } else {
            return this.priceLadder[this.bestBuy];
        }
    }

    public double getBestSellPrice() {
        if (this.bestSell == this.priceLadder.length) {
            return 0.0;
        } else {
            return this.priceLadder[this.bestSell];
        }

    }
}
