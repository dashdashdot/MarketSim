package org.gnw.mktsim.exchange;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private final String  propsFile;
    private final Logger  log = LoggerFactory.getLogger(this.getClass());
    private MarketManager mm;

    public App(String propsFile) {
        super();
        this.propsFile = propsFile;
    }

    public void start() throws Exception {
        Configuration config = new PropertiesConfiguration(propsFile);
        String marketName = config.getString("marketName");
        int inPort = config.getInt("port_in");
        int outPort = config.getInt("port_out");
        this.mm = new MarketManager(marketName, inPort, outPort);
        this.mm.loadDictionary(config.getString("dictionary"));
        this.mm.start();
    }

    public void countDown(int numSecs) {
        for (int i = numSecs; i > 0; i--) {
            log.info("{}", i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void stop() {
        this.mm.stop();
    }

    public static void main(String[] args) throws Exception {
        String propsFile = (args.length > 0 ? args[0] : "exchange.properties");
        App app = new App(propsFile);
        app.start();
        app.countDown(5);
        app.stop();
    }
}
