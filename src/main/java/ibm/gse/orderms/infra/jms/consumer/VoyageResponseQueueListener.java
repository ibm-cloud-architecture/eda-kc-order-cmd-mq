package ibm.gse.orderms.infra.jms.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ibm.gse.orderms.domain.events.EventBase;
import ibm.gse.orderms.domain.events.voyage.VoyageAssignedEvent;
import ibm.gse.orderms.domain.events.voyage.VoyageNotFoundEvent;
import ibm.gse.orderms.domain.model.order.ShippingOrder;
import ibm.gse.orderms.domain.service.ShippingOrderService;
import ibm.gse.orderms.infra.jms.producer.JMSQueueWriter;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A bean consuming prices from the JMS queue.
 */
@ApplicationScoped
public class VoyageResponseQueueListener implements Runnable {

    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    JMSQueueWriter<ShippingOrder> jmsQueueWriter;

    @Inject
    public ShippingOrderService shippingOrderService;

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    private static final Logger log = Logger.getLogger(VoyageResponseQueueListener.class);

    void onStart(@Observes StartupEvent ev) {
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @Override
    public void run() {

        log.info("Started listener on "+System.getenv("VOYAGE_RESPONSE_QUEUE")+" queue");
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {

            ObjectMapper mapper = new ObjectMapper();
            JMSConsumer consumer = context.createConsumer(context.createQueue(System.getenv("VOYAGE_RESPONSE_QUEUE")));
            while (true) {
                Message message = consumer.receive();
                if (message == null) {
                    // receive returns `null` if the JMSConsumer is closed
                    return;
                }

                String rawMessageBody = message.getBody(String.class);
                log.debug("received message from queue... " + rawMessageBody);
                EventBase voyageEvent = mapper.readValue(rawMessageBody,
                        mapper.getTypeFactory().constructType(ShippingOrder.class));
                if(voyageEvent.getType().equals(EventBase.TYPE_VOYAGE_NOT_FOUND)) {

                    /* TODO: assuming that when EventBase.type is EventBase.TYPE_VOYAGE_NOT_FOUND we have on object of type VoyageNotFoundEvent
                    */
                    VoyageNotFoundEvent voyageNotFoundEvent = (VoyageNotFoundEvent) voyageEvent;
                    String orderId = voyageNotFoundEvent.getPayload().getOrderID();

                    ShippingOrder shippingOrder = shippingOrderService.getOrderByOrderID(orderId).orElseThrow();
                    shippingOrder.setStatus(ShippingOrder.CANCELLED_STATUS);
                    shippingOrderService.updateOrder(shippingOrder);

                } else if(voyageEvent.getType().equals(EventBase.TYPE_VOYAGE_ASSIGNED)) {

                    VoyageAssignedEvent voyageAssignedEvent = (VoyageAssignedEvent) voyageEvent;
                    String orderId = voyageAssignedEvent.getPayload().getOrderID();

                    ShippingOrder shippingOrder = shippingOrderService.getOrderByOrderID(orderId).orElseThrow();
                    jmsQueueWriter.sendMessage(shippingOrder, System.getenv("FREEZER_REQUEST_QUEUE"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
