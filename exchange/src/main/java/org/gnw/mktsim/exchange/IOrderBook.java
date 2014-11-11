package org.gnw.mktsim.exchange;

public interface IOrderBook {

    public void addOrder ( Order order );
    public void cancelOrder ( Order order );
    public void addListener ( OrderBookListener listener );
    
}
