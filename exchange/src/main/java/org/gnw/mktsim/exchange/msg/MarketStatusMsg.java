package org.gnw.mktsim.exchange.msg;

import org.gnw.mktsim.exchange.Market;
import org.gnw.mktsim.exchange.Market.Status;

public class MarketStatusMsg extends AbsMessage {

    private final Market market;

    private Status       currentStatus = Status.CLOSED;

    public MarketStatusMsg(String clientId, Market market) {
        super(clientId);
        this.market = market;
    }

    public Status getStatus() {
        return this.currentStatus;
    }

    public Market getMarket() {
        return this.market;
    }

}
