package org.gnw.mktsim.exchange;

import org.junit.Test;

public class TestExchange {

    /**
     * On opening a market, we should expect to hear a status message declaring
     * the market open.
     */
    @Test
    public void testMarketOpenNoMsg() {
        Exchange exch = new Exchange("LSE", "Equity");
        exch.open("Equity");
        Market market = exch.getMarket("Equity");
        assert (Market.Status.OPEN == market.getStatus());
    }

}
