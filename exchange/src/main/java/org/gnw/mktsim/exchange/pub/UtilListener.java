package org.gnw.mktsim.exchange.pub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gnw.mktsim.common.OrderBookEvent;
import org.gnw.mktsim.common.msg.ExchangeMsgParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

public class UtilListener implements Runnable {

    private final int                  port;
    private final List<OrderBookEvent> events;
    private final ZMQ.Context          context;
    private ZMQ.Socket                 socket;
    private ZMQ.Poller                 poller;
    private boolean                    isAlive = false;
    private final Thread               runner;
    private final Logger                  log           = LoggerFactory.getLogger(this.getClass());
    
    public UtilListener(int port) {
        super();
        this.port = port;
        this.events = Collections.synchronizedList(new ArrayList<OrderBookEvent>());
        this.context = ZMQ.context(1);
        this.runner = new Thread(this, "UtilListener");
    }

    public void start() {
        log.info("Starting");
        this.isAlive = true;
        this.socket = context.socket(ZMQ.SUB);
        this.socket.connect("tcp://localhost:" + this.port);
        this.socket.subscribe(new byte[0]);
        this.poller = new ZMQ.Poller(1);
        this.poller.register(socket, ZMQ.Poller.POLLIN);
        this.runner.start();
    }

    public void run() {
        try {
            while (isAlive) {
                if (poller.poll(1000) > 0) {
                    byte[] msg = socket.recv(0);
                    if (log.isDebugEnabled()) {
                        log.debug("Received: ",new String(msg));
                    }
                    if (msg.length != 2) {
                        OrderBookEvent event = ExchangeMsgParser.parse(msg);
                        events.add(event);
                    }
                }
            }
        } finally {
            // Shut down nicely
            log.info("Shutting down");
            socket.close();
            context.term();
        }
    }

    /**
     * Get the latest set of events clearing down the internal list ready to
     * receive more.
     * 
     * @return
     */
    public List<OrderBookEvent> dump() {
        synchronized (events) {
            List<OrderBookEvent> output = new ArrayList<OrderBookEvent>();
            for (OrderBookEvent event : events) {
                output.add(event);
            }
            events.clear();
            return output;
        }
    }

    public int size() {
        return events.size();
    }

    public void stop() {
        this.isAlive = false;
    }

}
