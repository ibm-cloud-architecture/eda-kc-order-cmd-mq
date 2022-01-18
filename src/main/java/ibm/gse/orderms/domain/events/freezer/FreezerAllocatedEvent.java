package ibm.gse.orderms.domain.events.freezer;

import ibm.gse.orderms.domain.events.EventBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class FreezerAllocatedEvent extends EventBase {

	private String orderId;
	private FreezerAllocatedPayload payload;

	public FreezerAllocatedEvent(long timestampMillis, String version, String orderId, FreezerAllocatedPayload payload) {
		this.timestampMillis = timestampMillis;
    	this.version = version;
    	this.type = EventBase.TYPE_CONTAINER_ALLOCATED;
		this.payload = payload;
		this.orderId = orderId;
	}

	public FreezerAllocatedEvent() {
		
	}
	
	public FreezerAllocatedPayload getPayload() {
		return payload;
	}

	public void setPayload(FreezerAllocatedPayload payload) {
		this.payload = payload;
	}

	public String getOrderId() {
		return orderId;
	}
	
}
