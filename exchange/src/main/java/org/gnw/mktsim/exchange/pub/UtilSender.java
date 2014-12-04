package org.gnw.mktsim.exchange.pub;

import org.gnw.mktsim.common.OrderBookEvent;
import org.gnw.mktsim.common.msg.ExchangeMsgBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

public class UtilSender {

    private final int   port;
    private boolean     initialised = false;
    private ZMQ.Context context;
    private ZMQ.Socket  publisher;
    private final Logger                  log           = LoggerFactory.getLogger(this.getClass());
    
    public UtilSender(int port) {
        super();
        this.port = port;
    }

    public boolean send(OrderBookEvent event) {
        synchronized (this) {
            if (!initialised) {
                init();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Sending: {}",event.toString());
        }
        byte[] b_msg = ExchangeMsgBuilder.build(event);
        return publisher.send(b_msg);
    }

    public void init() {
        this.context = ZMQ.context(1);
        this.publisher = context.socket(ZMQ.PUB);
        publisher.bind("tcp://*:" + port);
        publisher.bind("ipc://utilsender");
        this.initialised = true;
        if (log.isDebugEnabled()) {
            log.debug("Sending: HI");
        }
        publisher.send("HI".getBytes());
    }

    public synchronized void stop() {
        log.info("Shutting down");
        publisher.close();
        context.term();
        initialised = false;
    }

    public void finalize() {
        stop();
    }
}
