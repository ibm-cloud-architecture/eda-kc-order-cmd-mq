package ibm.gse.orderms.domain.events.freezer;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class FreezerNotFoundPayload {
	 private String orderID;
	 private String reason;
	 
	 public FreezerNotFoundPayload(String oid, String reason) {
		 this.orderID = oid;
		 this.reason = reason;
	 }

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
