package org.gnw.mktsim.exchange;

import java.util.HashMap;
import java.util.Map;

public class Exchange {

    public final String              name;
    public final Map<String, Market> markets;

    public Exchange(final String name) {
        super();
        this.name = name;
        this.markets = new HashMap<String, Market>();
    }

    public Exchange(final String name, final String firstMarket) {
        this(name, new Market(firstMarket));
    }

    public Exchange(final String name, Market firstMarket) {
        this(name);
        this.markets.put(firstMarket.getName(), firstMarket);
    }

    /**
     * Open one of the markets.
     * 
     * @param market
     *            The market to open.
     */
    public void open(String market) {
        if (this.markets.containsKey(market)) {
            this.markets.get(market).open();
        }
    }

    public Market getMarket(String name) {
        return this.markets.get(name);
    }
}
