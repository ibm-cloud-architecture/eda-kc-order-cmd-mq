package ibm.gse.orderms.domain.events.order;

import ibm.gse.orderms.domain.events.EventBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Order event for the state change of a shipping order
 * 
 * Events are data element, so limit inheritance and polymorphism to the minimum
 * 
 * @author jeromeboyer
 *
 */
@RegisterForReflection
public class OrderEvent extends EventBase {

   
    private OrderEventPayload payload;

    public OrderEvent(long timestampMillis, String type, String version, OrderEventPayload payload) {
        super(timestampMillis, type, version);
        this.payload = payload;
    }

    public OrderEvent() {}


    
    public OrderEventPayload getPayload() {
        return this.payload;
    }

}
