package ibm.gse.orderms.domain.events.order;

import ibm.gse.orderms.domain.model.order.Address;
import ibm.gse.orderms.domain.model.order.ShippingOrder;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class OrderCancelAndRejectPayload extends OrderEventPayload {

    private String containerID;
    private String voyageID;
    private String reason;

    public OrderCancelAndRejectPayload(String orderID, String productID, String customerID, String containerID, String voyageID, int quantity, Address pickupAddress,
            String pickupDate, Address destinationAddress, String expectedDeliveryDate, String status, String reason) {
        super(orderID,productID,customerID,quantity,pickupAddress,pickupDate,destinationAddress,expectedDeliveryDate,status);
        this.containerID = containerID;
        this.voyageID = voyageID;
        this.reason = reason;
    }

	public OrderCancelAndRejectPayload(ShippingOrder order, String reason) {
		super(order.getOrderID(), order.getProductID(), order.getCustomerID(), order.getQuantity(), order.getPickupAddress(),
				order.getPickupDate(), order.getDestinationAddress(), order.getExpectedDeliveryDate(), order.getStatus());
		this.containerID = order.getContainerID();
		this.voyageID = order.getVoyageID();
		this.reason = reason;
	}

	public String getContainerID() {
		return containerID;
	}

	public void setContainerID(String containerID) {
		this.containerID = containerID;
    }
    
    public String getVoyageID() {
		return voyageID;
	}

	public void setVoyageID(String voyageID) {
		this.voyageID = voyageID;
    }
    
    public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}