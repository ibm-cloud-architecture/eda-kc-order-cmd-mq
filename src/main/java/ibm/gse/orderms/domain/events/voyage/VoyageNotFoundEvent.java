package ibm.gse.orderms.domain.events.voyage;

import ibm.gse.orderms.domain.events.EventBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class VoyageNotFoundEvent extends EventBase {

  
	VoyageNotFoundPayload payload;
	
    public VoyageNotFoundEvent(long timestampMillis, String version, VoyageNotFoundPayload payload) {
    	this.timestampMillis = timestampMillis;
    	this.version = version;
    	this.type = EventBase.TYPE_VOYAGE_NOT_FOUND;
    	this.payload = payload;
    }

    public VoyageNotFoundEvent() {
    }

	public VoyageNotFoundPayload getPayload() {
		return payload;
	}

    

}
