package ibm.gse.orderms.infra.jms.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ibm.gse.orderms.domain.events.EventBase;
import ibm.gse.orderms.domain.events.voyage.VoyageAssignedEvent;
import ibm.gse.orderms.domain.events.voyage.VoyageCanceledEvent;
import ibm.gse.orderms.domain.events.voyage.VoyageCanceledPayload;
import ibm.gse.orderms.domain.events.voyage.VoyageNotFoundEvent;
import ibm.gse.orderms.domain.model.order.ShippingOrder;
import ibm.gse.orderms.domain.service.ShippingOrderService;
import ibm.gse.orderms.infra.jms.producer.JMSQueueWriter;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.json.JsonObject;
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

/**
 * A bean consuming prices from the JMS queue.
 */
@ApplicationScoped
public class VoyageResponseListener implements Runnable {

    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    ShippingOrderService shippingOrderService;

    @Inject
    JMSQueueWriter<EventBase> jmsQueueWriter;

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    private static final Logger log = Logger.getLogger(VoyageResponseListener.class);

    void onStart(@Observes StartupEvent ev) {
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        log.info("Connecting to message queue" + System.getenv("VOYAGE_RESPONSE_QUEUE"));
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            javax.jms.JMSConsumer consumer = context.createConsumer(
                    context.createQueue(System.getenv("VOYAGE_RESPONSE_QUEUE")));
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

    public void processMessage(String rawMessageBody) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            log.debug("received message from queue... " + rawMessageBody);
            JsonObject rawEvent = new JsonObject(rawMessageBody);

            if(rawEvent.getString("type").equals(EventBase.TYPE_VOYAGE_NOT_FOUND)) {

                VoyageNotFoundEvent voyageNotFoundEvent = mapper.readValue(rawEvent.toString(),
                        VoyageNotFoundEvent.class);

                String orderId = voyageNotFoundEvent.getPayload().getOrderID();

                ShippingOrder shippingOrder = shippingOrderService.getOrderByOrderID(orderId).orElseThrow();
                shippingOrder.setStatus(ShippingOrder.CANCELLED_STATUS);
                shippingOrderService.updateOrder(shippingOrder);

            } else if(rawEvent.getString("type").equals(EventBase.TYPE_VOYAGE_NOT_FOUND)){

                VoyageCanceledEvent voyageNotFoundEvent = mapper.readValue(rawEvent.toString(),
                        VoyageCanceledEvent.class);

                String orderId = voyageNotFoundEvent.getPayload().getOrderID();

                ShippingOrder shippingOrder = shippingOrderService.getOrderByOrderID(orderId).orElseThrow();
                shippingOrder.setStatus(ShippingOrder.CANCELLED_STATUS);
                shippingOrderService.updateOrder(shippingOrder);

            } else if(rawEvent.getString("type").equals(EventBase.TYPE_VOYAGE_ASSIGNED)) {

                VoyageAssignedEvent voyageAssignedEvent = mapper.readValue(rawEvent.toString(),
                        VoyageAssignedEvent.class);
                String orderId = voyageAssignedEvent.getPayload().getOrderID();

                ShippingOrder shippingOrder = shippingOrderService.getOrderByOrderID(orderId).orElseThrow();

                shippingOrder.setStatus(ShippingOrder.ON_HOLD);
                shippingOrder.assign(voyageAssignedEvent.getPayload());
                shippingOrderService.updateOrder(shippingOrder);

                jmsQueueWriter.sendMessage(voyageAssignedEvent, System.getenv("FREEZER_REQUEST_QUEUE"));
            }
        } catch (Exception e) {
            log.error("Error processing message..", e);
        }
    }
}
