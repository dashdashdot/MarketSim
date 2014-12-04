package org.gnw.mktsim.exchange.pub;

import java.util.concurrent.BlockingQueue;

import org.gnw.mktsim.common.OrderBookEvent;
import org.gnw.mktsim.common.msg.ExchangeMsgParser;
import org.gnw.mktsim.exchange.Market;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

/**
 * A market order receivers job is to listen to inbound orders and then place
 * them onto the appropriate order book queues for them to respond to in their
 * own threads.
 * 
 * @author Gerard Whitehead
 *
 */
public class OrderReceiver implements Runnable {

    private final int         port;
    private final Market      market;
    private final ZMQ.Context context;
    private final Logger      log     = LoggerFactory.getLogger(this.getClass());
    private ZMQ.Socket        socket;
    private ZMQ.Poller        poller;
    private boolean           isAlive = false;
    private final Thread      runner;

    public OrderReceiver(int port, Market market) {
        super();
        this.port = port;
        this.market = market;
        this.context = ZMQ.context(1);
        this.runner = new Thread(this, "Receiver");
    }

    public void start() {
        this.isAlive = true;
        this.socket = context.socket(ZMQ.SUB);
        this.socket.connect("tcp://localhost:" + this.port);
        this.socket.subscribe(new byte[0]);
        this.poller = new ZMQ.Poller(1);
        this.poller.register(socket, ZMQ.Poller.POLLIN);
        this.runner.start();
    }

    public void run() {
        if (log.isInfoEnabled()) {
            log.info("Receiver thread starting");
        }
        try {
            while (isAlive) {
                if (poller.poll(1000) > 0) {
                    byte[] msg = socket.recv(0);
                    if (msg.length != 2) {
                        OrderBookEvent event = ExchangeMsgParser.parse(msg);
                        if (log.isDebugEnabled()) {
                            log.debug("Received and adding to queue: {}", event);
                        }
                        market.addEvent(event);
                    }
                }
            }
        } finally {
            // Shut down nicely
            socket.close();
            context.term();
        }
    }

    public void stop() {
        log.info("Stopping order receiver");
        this.isAlive = false;
    }
}
