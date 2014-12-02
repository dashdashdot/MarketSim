package org.gnw.mktsim.exchange;

import org.gnw.mktsim.common.Order;
import org.gnw.mktsim.common.Trade;

public interface OrderBookListener {

    public void onNewOrder(Order newOrder);

    public void onOrderChange(Order oldOrder, Order newOrder);

    public void onOrderCancel(Order order);

    public void onNewTrade(Trade newTrade);

}
