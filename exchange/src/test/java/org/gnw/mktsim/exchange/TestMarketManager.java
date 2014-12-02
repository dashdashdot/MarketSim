package org.gnw.mktsim.exchange;

import org.gnw.mktsim.common.Instrument;
import org.gnw.mktsim.common.Order;
import org.junit.Test;

public class TestMarketManager {

    @Test
    public void testInitialisation() {
        MarketManager mm = new MarketManager("London");
        mm.loadInstrument("VOD", "VODAFONE", 225.7, 0.05);
        mm.loadInstrument("TSCO", "TESCO", 191.15, 0.05);
    }

    @Test
    public void testPublications() throws Exception {
        MarketManager mm = new MarketManager("London");
        Instrument vod = new Instrument("VOD", 225.07, 0.05);
        mm.loadInstrument(vod);
        mm.start();
        mm.addOrder(new Order("testPublications", "1", vod, true, 100L, 225.0));
        mm.addOrder(new Order("testPublications", "2", vod, true, 100L, 225.1));
        mm.addOrder(new Order("testPublications", "3", vod, false, 150L, 225.0));
        try {
            Thread.sleep(500);
        } finally {
            mm.stop();
        }
    }
}
