package ibm.gse.orderms.infra.jms.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ibm.gse.orderms.domain.events.EventBase;
import ibm.gse.orderms.domain.events.freezer.FreezerAllocatedEvent;
import ibm.gse.orderms.domain.events.freezer.FreezerNotFoundEvent;
import ibm.gse.orderms.domain.model.order.ShippingOrder;
import ibm.gse.orderms.domain.service.ShippingOrderService;
import ibm.gse.orderms.infra.jms.consumer.abstr.AbstractConsumer;
import ibm.gse.orderms.infra.jms.producer.JMSQueueWriter;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class FreezerResponseListener extends AbstractConsumer {

    @Inject
    ShippingOrderService shippingOrderService;

    @Inject
    JMSQueueWriter<EventBase> jmsQueueWriter;


    /*
    This is the last step of the saga, so is not needed to send any message on any queue at the end of the message processing
     */
    @Override
    public String getRequestQueue() {
        return String.valueOf(System.getenv("VOYAGE_REQUEST_QUEUE"));
    }

    @Override
    public String getResponseQueue() {
        return String.valueOf(System.getenv("FREEZER_RESPONSE_QUEUE"));
    }

    @Override
    public void processMessage(String rawMessageBody) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            log.debug("received message from queue... " + rawMessageBody);
            JsonObject rawEvent = new JsonObject(rawMessageBody);

            if(rawEvent.getString("type").equals(EventBase.TYPE_CONTAINER_ALLOCATED)) {

                FreezerAllocatedEvent freezerAllocatedEvent = mapper.readValue(rawEvent.toString(),
                        FreezerAllocatedEvent.class);

                String orderId = freezerAllocatedEvent.getPayload().getOrderID();

                ShippingOrder shippingOrder = shippingOrderService.getOrderByOrderID(orderId).orElseThrow();
                shippingOrder.assignContainer(freezerAllocatedEvent.getPayload());
                shippingOrderService.updateOrder(shippingOrder);

            } else if(rawEvent.getString("type").equals(EventBase.TYPE_CONTAINER_NOT_FOUND)
                || rawEvent.getString("type").equals(EventBase.TYPE_CONTAINER_CANCELED)) {

                FreezerNotFoundEvent freezerNotFoundEvent = mapper.readValue(rawEvent.toString(),
                        FreezerNotFoundEvent.class);

                String orderId = freezerNotFoundEvent.getPayload().getOrderID();
                ShippingOrder shippingOrder = shippingOrderService.getOrderByOrderID(orderId).orElseThrow();

                shippingOrder.setStatus(ShippingOrder.CANCELLED_STATUS);
                shippingOrderService.updateOrder(shippingOrder);

                jmsQueueWriter.sendMessage(freezerNotFoundEvent, getRequestQueue());

            }
        } catch (Exception e) {
            log.error("Error processing message..", e);
        }
    }
}
