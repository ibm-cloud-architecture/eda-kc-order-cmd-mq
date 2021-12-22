package ibm.gse.orderms.domain.events.voyage;


import ibm.gse.orderms.domain.events.EventBase;

public class VoyageCanceledEvent extends EventBase {


	VoyageCanceledPayload payload;

    public VoyageCanceledEvent(long timestampMillis, String version, VoyageCanceledPayload payload) {
    	this.timestampMillis = timestampMillis;
    	this.version = version;
    	this.type = EventBase.TYPE_VOYAGE_CANCELED;
    	this.payload = payload;
    }

    public VoyageCanceledEvent() {
    }

	public VoyageCanceledPayload getPayload() {
		return payload;
	}

    

}
