package ibm.gse.orderms.infra.jms.consumer.abstr;

import ibm.gse.orderms.infra.jms.consumer.FreezerResponseListener;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public abstract class AbstractConsumer implements Runnable {

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    protected static final Logger log = Logger.getLogger(FreezerResponseListener.class);

    @Inject
    ConnectionFactory connectionFactory;

    void onStart(@Observes StartupEvent ev) {
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        log.info("Connecting to message queue " + getResponseQueue());
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            javax.jms.JMSConsumer consumer = context.createConsumer(
                    context.createQueue(getResponseQueue()));
            while (true) {
                Message message = consumer.receive();
                if (message == null) {
                    return;
                }
                log.info("received message from queue... " + message.getBody(String.class));
                processMessage(message.getBody(String.class));
            }
        } catch (JMSException e) {
            log.error("Error parsing message..", e);
        }
    }

    public abstract String getRequestQueue();
    public abstract String getResponseQueue();
    public abstract void processMessage(final String messageBody);

}
