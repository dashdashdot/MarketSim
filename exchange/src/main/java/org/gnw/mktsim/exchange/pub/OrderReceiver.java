package org.gnw.mktsim.exchange.pub;

import java.util.concurrent.BlockingQueue;

import org.gnw.mktsim.common.OrderBookEvent;
import org.gnw.mktsim.common.msg.ExchangeMsgParser;
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

    private final int                           port;
    private final BlockingQueue<OrderBookEvent> queue;
    private final ZMQ.Context                   context;
    private final Logger                        log     = LoggerFactory.getLogger(this.getClass());
    private ZMQ.Socket                          subscriber;
    private boolean                             isAlive = false;
    private final Thread                        runner;

    public OrderReceiver(int port, BlockingQueue<OrderBookEvent> queue) {
        super();
        this.port = port;
        this.queue = queue;
        this.context = ZMQ.context(1);
        this.runner = new Thread(this, "Receiver");
    }

    public void start() {
        this.isAlive = true;
        this.subscriber = context.socket(ZMQ.SUB);
        this.subscriber.connect("tcp://localhost:" + this.port);
        this.runner.start();
    }

    public void run() {
        try {
            while (isAlive) {
                byte[] msg = subscriber.recv(0);
                OrderBookEvent event = ExchangeMsgParser.parse(msg);
                if (log.isDebugEnabled()) {
                    log.debug("Received and adding to queue: {}", event);
                }
                queue.add(event);
            }
        } finally {
            // Shut down nicely
            subscriber.close();
            context.term();
        }
    }

    public void stop() {
        this.isAlive = false;
        this.runner.interrupt();
    }
}
