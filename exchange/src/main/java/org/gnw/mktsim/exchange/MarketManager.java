package org.gnw.mktsim.exchange;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.gnw.mktsim.exchange.pub.OrderReceiver;
import org.gnw.mktsim.exchange.pub.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarketManager {

    private final Market        market;
    private final String        marketName;
    private final OrderReceiver receiver;
    private final Publisher     publisher;

    private final Logger        log = LoggerFactory.getLogger(this.getClass());

    public MarketManager(String marketName) {
        super();
        this.publisher = new Publisher();
        this.receiver = null;
        this.marketName = marketName;
        this.market = new Market(marketName, publisher);
    }

    public void loadDictionary(String filename) throws FileNotFoundException, IOException {
        FileReader file = null;
        BufferedReader reader = null;
        try {
            file = new FileReader(filename);
            reader = new BufferedReader(file);
            String line = "";
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                if (tokens.length < 5) {
                    throw new IOException(
                            String.format(
                                    "Cannot understand dictionary format on line {}, expecting symbol,name,currency,price,tickSize but received {}",
                                    lineNum, line));
                }
                loadInstrument(tokens[0], tokens[1], Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]));
                lineNum++;
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (file != null) {
                file.close();
            }
        }
    }

    public void loadInstrument(String symbol, String name, double price, double tickSize) {
        this.loadInstrument(new Instrument(symbol, name, price, tickSize));
    }

    public void loadInstrument(Instrument imnt) {
        market.addOrderBook(imnt, publisher);
        if (log.isInfoEnabled()) {
            log.info("Added {} to {}", imnt.getSymbol(), marketName);
        }
    }

    public void start() {
        // Must start the responder before the receiver to avoid any lost
        // messages.
        publisher.start();
        // receiver.start();
    }

    public void stop() {
        // receiver.stop();
        publisher.stop();
    }

    public void addOrder(Order order) {
        this.market.addOrder(order);
    }
}
