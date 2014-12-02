package org.gnw.mktsim.exchange;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

public class App {

    public static void main(String[] args) throws Exception {
        String propsFile = (args.length>0?args[0]:"exchange.properties");
        Configuration config = new PropertiesConfiguration(propsFile);
        String marketName = config.getString("marketName");
        MarketManager mm = new MarketManager(marketName);
        mm.loadDictionary(config.getString("dictionary"));
        mm.start();
        Thread.sleep(10000);
        mm.stop();
    }

}
