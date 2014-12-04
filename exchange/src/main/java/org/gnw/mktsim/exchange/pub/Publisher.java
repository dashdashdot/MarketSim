package org.gnw.mktsim.exchange.pub;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.gnw.mktsim.common.OrderBookEvent;
import org.gnw.mktsim.common.msg.ExchangeMsgBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

/**
 * The publisher works by segregating the generation of events in the market
 * from their publication by using a publication queue.
 * 
 * @author Gerard Whitehead
 *
 */
public class Publisher implements Runnable {

    private BlockingQueue<OrderBookEvent> inQueue;
    private final Thread                  runner;
    private final int                     port;
    private boolean                       isInitialised = false;
    private boolean                       isAlive       = false;
    private final Logger                  log           = LoggerFactory.getLogger(this.getClass());
    private ZMQ.Context                   context;
    private ZMQ.Socket                    publisher;

    public Publisher(int port) {
        super();
        this.port = port;
        this.inQueue = new ArrayBlockingQueue<OrderBookEvent>(1000);
        this.runner = new Thread(this, "Publisher");
    }

    public void add(OrderBookEvent e) {
        inQueue.add(e);
        if (log.isTraceEnabled()) {
            log.trace("Adding to queue: {}", e);
        }
    }

    public void init() {
        if (!isInitialised) {
            if (log.isInfoEnabled()) {
                log.info("Publisher initialising with ZMQ version: {}", ZMQ.getVersionString());
            }
            this.context = ZMQ.context(1);
            this.publisher = context.socket(ZMQ.PUB);
            publisher.bind("tcp://*:" + port);
            publisher.bind("ipc://market");
            this.isInitialised = true;
        }
    }

    public void start() {
        if (!isInitialised) {
            init();
        }
        if (!runner.isAlive()) {
            this.isAlive = true;
            runner.start();
        }
    }

    public void stop() {
        this.isAlive = false;
        this.runner.interrupt();
    }

    public void run() {
        if (log.isInfoEnabled()) {
            log.info("Publisher thread starting");
        }
        publishBlip();
        while (this.isAlive) {
            try {
                OrderBookEvent e = inQueue.take();
                log.debug("Found on queue: {}", e);
                publish(e);
            } catch (InterruptedException e) {
                // Being told to shut down.
                log.debug("Publisher thread interrupted");
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Publisher thread terminating");
        }
        this.publisher.close();
        this.context.term();
    }

    private void publishBlip() {
        byte[] b_msg = "HI".getBytes();
        publisher.send(b_msg);
    }
    
    private void publish(OrderBookEvent e) {
        byte[] b_msg = ExchangeMsgBuilder.build(e);
        if (log.isDebugEnabled()) {
            log.debug("Sending: {}", new String(b_msg));
        }
        // Now publish it to the correct clients.
        publisher.send(b_msg);
    }
}
