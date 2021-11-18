package ibm.gse.orderms.domain.events.freezer;

import ibm.gse.orderms.domain.events.EventBase;

public class FreezerAllocatedEvent extends EventBase {

	private String orderID;
	private FreezerAllocatedPayload payload;

	public FreezerAllocatedEvent(long timestampMillis, String version, String orderID, FreezerAllocatedPayload payload) {
		this.timestampMillis = timestampMillis;
    	this.version = version;
    	this.type = EventBase.TYPE_CONTAINER_ALLOCATED;
		this.payload = payload;
		this.orderID = orderID;
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
		return orderID;
	}
	
}
