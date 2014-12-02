package org.gnw.mktsim.exchange;

import org.gnw.mktsim.common.Order;

public interface IOrderBook {

    public void addOrder(Order order);

    public void cancelOrder(Order order);

    public void addListener(OrderBookListener listener);

}
