package org.gnw.mktsim.exchange;

import java.util.concurrent.LinkedTransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all the orders on one particular side of the order book, all the
 * buys for example. The ladder is an array of {@link OrderLadderPricePoint}
 * each of which is a collection of unsatisfied orders in time priority. The
 * array is ordered with the better prices at the lower bounds of the range
 * where 'better' depends on if this is a buy or a sell ladder.
 * 
 * @author Gerard Whitehead
 */
class OrderLadder {

    private final int                                 arrayScaler = 100;
    private final String                              senderId;
    private final Instrument                          imnt;
    private final boolean                             isBuy;
    private final LinkedTransferQueue<OrderBookEvent> outbound;
    private OrderLadderPricePoint[]                   ladder;
    private int                                       bestPriceIndex;
    private double                                    ladderBottom;
    private double                                    ladderTick;
    private int                                       numOrders   = 0;
    private final Logger                              log         = LoggerFactory.getLogger(this.getClass());

    OrderLadder(String senderId, Instrument imnt, boolean isBuy, LinkedTransferQueue<OrderBookEvent> outbound) {
        super();
        this.senderId = senderId;
        this.imnt = imnt;
        this.isBuy = isBuy;
        this.outbound = outbound;
        build(arrayScaler);
        this.bestPriceIndex = Integer.MAX_VALUE;
    }

    /**
     * If this is on the same side, then we simply add it to the right price
     * point. Otherwise we look to see if we can trade and any volume that can't
     * be traded remains in the input object. Note, this is destructive to the
     * passed order object.
     * 
     * @param order
     *            A new order. If we can trade, then this order will be reduced
     *            the amount that cannot be traded.
     * @return True if there is a residual amount of order left.
     */
    public boolean addOrder(Order order) {
        int i = getLadderPoint(order.getPrice());
        if (this.isBuy == order.isBuy()) {
            ladder[i].add(order);
            numOrders++;
            if (i < this.bestPriceIndex) {
                this.bestPriceIndex = i;
            }
        } else {
            hit(order, i);
        }
        return (order.getQuantity() != 0);
    }

    /**
     * Hit the order book with an opposing order. This could create a trade but
     * it could leave a residual amount of order to put on the opposite side of
     * the book.
     * 
     * @param newOrder
     *            A new order on the opposite side than this order book.
     * @param newOrderIndex
     *            The price point where this order would be naturally place if
     *            unmatched
     */
    private void hit(Order newOrder, int newOrderIndex) {
        // First check for no hit.
        if (newOrderIndex < this.bestPriceIndex) {
            return;
        }
        // So we're obviously going to hit something. Start walking the ladder.
        Order best = ladder[this.bestPriceIndex].peek();
        // There is a matching order. Several possibilities:
        // 1. Inbound order is smaller, therefore residual of best remains.
        // 2. Inbound order is the same size, therefore all goes.
        // 3. Inbound order is bigger, so we hit this and have another go.
        // In all cases, we trade the min of buy and sell size.
        long tradedQuantity = Math.min(best.getQuantity(), newOrder.getQuantity());
        trade(newOrder, best, tradedQuantity);
        // If any size remains, go again.
        if (newOrder.getQuantity() > 0) {
            this.hit(newOrder, newOrderIndex);
        }
    }

    private void trade(Order incomingOrder, Order bookOrder, long quantity) {
        // First update the incomingOrder
        double price = bookOrder.getPrice();
        incomingOrder.trade(quantity, price);
        bookOrder.trade(quantity, price);
        // Update the instrument
        this.imnt.setLastPrice(price);
        // If the bookOrder is now cleaned, then we need to remove it from the
        // book.
        if (bookOrder.getQuantity() == 0) {
            ladder[bestPriceIndex].pop();
            numOrders--;
            // Check to see where the best price is now.
            while (bestPriceIndex < ladder.length && ladder[bestPriceIndex].size() == 0) {
                bestPriceIndex++;
            }
        }
        // Report the trade
        addToOutboundQueue(new Trade(this.senderId, incomingOrder, bookOrder, quantity, price));
    }

    private void addToOutboundQueue(OrderBookEvent event) {
        outbound.put(event);
        if (log.isDebugEnabled()) {
            log.debug("Adding to queue: {}", event);
        }
    }

    private void build(int ticksUpDown) {
        int n = 2 * ticksUpDown + 1;
        this.ladderTick = this.imnt.getTickSize();
        if (this.isBuy) {
            // The lower end of the array has high prices.
            this.ladderBottom = this.imnt.getLastPrice() + ticksUpDown * this.ladderTick;
        } else {
            // We're selling so the better prices are lower.
            this.ladderBottom = Math.max(this.ladderTick, this.imnt.getLastPrice() - ticksUpDown * this.ladderTick);
        }
        this.ladder = new OrderLadderPricePoint[n];
        for (int i = 0; i < n; i++) {
            double price = this.ladderBottom + (this.isBuy ? -i : i) * this.ladderTick;
            this.ladder[i] = new OrderLadderPricePoint(price);
        }
        if (log.isInfoEnabled()) {
            log.info("{} book initialised for {} with {} ticks across range {}-{} with tick size {}", (isBuy ? "Buy"
                    : "Sell"), imnt.getSymbol(), ladder.length, ladderBottom,
                    ladderBottom + ladder.length * ladderTick, ladderTick);
        }
    }

    private int getLadderPoint(double price) {
        int output;
        if (isBuy) {
            output = (int) ((ladderBottom - price) / ladderTick);
        } else {
            output = (int) ((price - ladderBottom) / ladderTick);
        }
        if (output < 0 || output >= ladder.length) {
            resize(output);
            if (output < 0) {
                output = getLadderPoint(price);
            }
        }
        return output;
    }

    private double getPriceAtPoint(int pointIndex) {
        if (isBuy) {
            return ladderBottom - pointIndex * ladderTick;
        } else {
            return ladderBottom + pointIndex * ladderTick;
        }
    }

    private void resize(int newPointIndex) {
        // We need to resize the array to give us an extra set of scaling points
        // in all directions.
        if (newPointIndex > ladder.length) {
            // Easier use case as we don't have to reset the pricing variables.
            int newLength = newPointIndex + this.arrayScaler;
            OrderLadderPricePoint[] newArray = new OrderLadderPricePoint[newLength];
            System.arraycopy(this.ladder, 0, newArray, 0, ladder.length);
            for (int i = ladder.length; i < newArray.length; i++) {
                newArray[i] = new OrderLadderPricePoint(getPriceAtPoint(i));
            }
            this.ladder = newArray;
        } else {
            if (this.isBuy) {
                this.ladderBottom += (this.arrayScaler - newPointIndex) * this.ladderTick;
            } else {
                this.ladderBottom -= (this.arrayScaler - newPointIndex) * this.ladderTick;
            }
            int newLength = ladder.length - newPointIndex + this.arrayScaler;
            OrderLadderPricePoint[] newArray = new OrderLadderPricePoint[newLength];
            for (int i = 0; i < this.arrayScaler - newPointIndex; i++) {
                newArray[i] = new OrderLadderPricePoint(getPriceAtPoint(i));
            }
            System.arraycopy(this.ladder, 0, newArray, this.arrayScaler - newPointIndex, this.ladder.length);
            this.bestPriceIndex += ladder.length - newPointIndex;
            this.ladder = newArray;
        }
        if (log.isInfoEnabled()) {
            log.info("{} book resized for {} with {} ticks across range {}-{} with tick size {}", (isBuy ? "Buy"
                    : "Sell"), imnt.getSymbol(), ladder.length, ladderBottom,
                    ladderBottom + ladder.length * ladderTick, ladderTick);
        }
    }

    /**
     * Returns the total number of outstanding orders in the ladder.
     * 
     * @return
     */
    int size() {
        return numOrders;
    }

    /**
     * Returns the best price in the ladder depending on the side of the market
     * that the ladder is sitting on.
     * 
     * @return
     */
    double getBestPrice() {
        return getPriceAtPoint(this.bestPriceIndex);
    }
}
