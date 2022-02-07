package ibm.gse.orderms.infra.jms.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ibm.gse.orderms.domain.events.EventBase;
import ibm.gse.orderms.domain.events.voyage.VoyageAssignedEvent;
import ibm.gse.orderms.domain.events.voyage.VoyageCanceledEvent;
import ibm.gse.orderms.domain.events.voyage.VoyageNotFoundEvent;
import ibm.gse.orderms.domain.model.order.ShippingOrder;
import ibm.gse.orderms.domain.service.ShippingOrderService;
import ibm.gse.orderms.infra.jms.consumer.abstr.AbstractConsumer;
import ibm.gse.orderms.infra.jms.producer.JMSQueueWriter;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class VoyageResponseListener extends AbstractConsumer {

    @Inject
    ShippingOrderService shippingOrderService;

    @Inject
    JMSQueueWriter<EventBase> jmsQueueWriter;

    @Override
    public String getRequestQueue() {
        return String.valueOf(System.getenv("FREEZER_REQUEST_QUEUE"));
    }

    @Override
    public String getResponseQueue() {
        return String.valueOf(System.getenv("VOYAGE_RESPONSE_QUEUE"));
    }

    public void processMessage(String rawMessageBody) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            log.debug("received message from queue... " + rawMessageBody);
            JsonObject rawEvent = new JsonObject(rawMessageBody);

            if(rawEvent.getString("type").equals(EventBase.TYPE_VOYAGE_NOT_FOUND)) {

                log.info("Voyage canceled received, updating order");

                VoyageNotFoundEvent voyageNotFoundEvent = mapper.readValue(rawEvent.toString(),
                        VoyageNotFoundEvent.class);

                String orderId = voyageNotFoundEvent.getPayload().getOrderID();

                ShippingOrder shippingOrder = shippingOrderService.getOrderByOrderID(orderId).orElseThrow();
                shippingOrder.cancelVoyage();
                shippingOrderService.updateOrder(shippingOrder);

            } else if(rawEvent.getString("type").equals(EventBase.TYPE_VOYAGE_CANCELED)){

                log.info("Voyage canceled received, updating order");

                VoyageCanceledEvent voyageCanceledEvent = mapper.readValue(rawEvent.toString(),
                        VoyageCanceledEvent.class);

                String orderId = voyageCanceledEvent.getPayload().getOrderID();

                ShippingOrder shippingOrder = shippingOrderService.getOrderByOrderID(orderId).orElseThrow();

                shippingOrder.cancelVoyage();
                shippingOrderService.updateOrder(shippingOrder);

            } else if(rawEvent.getString("type").equals(EventBase.TYPE_VOYAGE_ASSIGNED)) {

                log.info("Voyage Assigned received, updating order");

                VoyageAssignedEvent voyageAssignedEvent = mapper.readValue(rawEvent.toString(),
                        VoyageAssignedEvent.class);
                String orderId = voyageAssignedEvent.getPayload().getOrderID();

                ShippingOrder shippingOrder = shippingOrderService.getOrderByOrderID(orderId).orElseThrow();

                shippingOrder.setStatus(ShippingOrder.ON_HOLD);
                shippingOrder.assign(voyageAssignedEvent.getPayload());
                shippingOrderService.updateOrder(shippingOrder);
                if(shippingOrder.getProductID().equals("REFEER_FAILS")) {
                    voyageAssignedEvent.getPayload().setProductId("REFEER_FAILS");
                }
                jmsQueueWriter.sendMessage(voyageAssignedEvent, getRequestQueue());
            }
        } catch (Exception e) {
            log.error("Error processing message..", e);
        }
    }
}
