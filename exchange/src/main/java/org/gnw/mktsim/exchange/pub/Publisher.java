package org.gnw.mktsim.exchange.pub;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.gnw.mktsim.common.OrderBookEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

public class Publisher implements Runnable {

    private BlockingQueue<OrderBookEvent> inQueue;
    private final Thread                  runner;
    private boolean                       isInitialised = false;
    private boolean                       isAlive       = false;
    private final Logger                  log           = LoggerFactory.getLogger(this.getClass());
    private ZMQ.Context                   context;
    private ZMQ.Socket                    publisher;

    public Publisher() {
        super();
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
            publisher.bind("tcp://*:5556");
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

    private void publish(OrderBookEvent e) {
        // Msg format is a byte array containing:
        // symbol [space] msgType protobuf
        byte[] b_sym = e.getSymbol().getBytes();
        byte[] b_msgType = e.getMsgType().getBytes();
        byte[] b_pb = e.toProtoBuf().toByteArray();
        // Now put it into a single array
        byte[] b_msg = new byte[b_sym.length + b_pb.length + 3];
        System.arraycopy(b_sym, 0, b_msg, 0, b_sym.length);
        b_msg[b_sym.length] = 32;
        System.arraycopy(b_msgType, 0, b_msg, b_sym.length + 1, 2);
        System.arraycopy(b_pb, 0, b_msg, b_sym.length + 3, b_pb.length);
        if (log.isDebugEnabled()) {
            log.debug("Sending: {}", new String(b_msg));
        }
        // Now publish it to the correct clients.
        publisher.send(b_msg);
    }
}
