package ibm.gse.orderms.domain.events.order;

import java.util.UUID;

import ibm.gse.orderms.domain.model.order.ShippingOrder;
import ibm.gse.orderms.infra.api.dto.ShippingOrderCreateDTO;
import ibm.gse.orderms.infra.api.dto.ShippingOrderUpdateParameters;
import ibm.gse.orderms.domain.model.order.Address;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Better to have a different model for the shipping order as event payload
 * than to use the shipping order as-is.
 * 
 * This is a DTO
 * @author jerome boyer
 *
 */
@RegisterForReflection
public class OrderEventPayload {
	private String orderID;
    private String productID;
    private String customerID;
    private int quantity;

    private Address pickupAddress;
    private String pickupDate;

    private Address destinationAddress;
    private String expectedDeliveryDate;

    private String status;
    
    

	public OrderEventPayload(String orderID, String productID, String customerID, int quantity,
			Address pickupAddress, String pickupDate, Address destinationAddress, String expectedDeliveryDate,
			String status) {
		super();
		this.orderID = orderID;
		this.productID = productID;
		this.customerID = customerID;
		this.quantity = quantity;
		this.pickupAddress = pickupAddress;
		this.pickupDate = pickupDate;
		this.destinationAddress = destinationAddress;
		this.expectedDeliveryDate = expectedDeliveryDate;
		this.status = status;
	}

	public OrderEventPayload(ShippingOrderCreateDTO createParams) {
		super();
		this.orderID = UUID.randomUUID().toString();
		this.productID = createParams.getProductID();
		this.customerID = createParams.getCustomerID();
		this.quantity = createParams.getQuantity();
		this.pickupAddress = createParams.getPickupAddress();
		this.pickupDate = createParams.getPickupDate();
		this.destinationAddress = createParams.getDestinationAddress();
		this.expectedDeliveryDate = createParams.getExpectedDeliveryDate();
		this.status = "toBeCreated";
	}

	public OrderEventPayload(ShippingOrder shippingOrder) {
		super();
		this.orderID = shippingOrder.getOrderID();
		this.productID = shippingOrder.getProductID();
		this.customerID = shippingOrder.getCustomerID();
		this.quantity = shippingOrder.getQuantity();
		this.pickupAddress = shippingOrder.getPickupAddress();
		this.pickupDate = shippingOrder.getPickupDate();
		this.destinationAddress = shippingOrder.getDestinationAddress();
		this.expectedDeliveryDate = shippingOrder.getExpectedDeliveryDate();
		this.status = "toBeCreated";
	}

	public OrderEventPayload(ShippingOrderUpdateParameters updateParams) {
		super();
		this.orderID = updateParams.getOrderID();
		this.productID = updateParams.getProductID();
		this.customerID = updateParams.getCustomerID();
		this.quantity = updateParams.getQuantity();
		this.pickupAddress = updateParams.getPickupAddress();
		this.pickupDate = updateParams.getPickupDate();
		this.destinationAddress = updateParams.getDestinationAddress();
		this.expectedDeliveryDate = updateParams.getExpectedDeliveryDate();
		this.status = updateParams.getStatus();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderID() {
		return orderID;
	}

	public String getProductID() {
		return productID;
	}

	public String getCustomerID() {
		return customerID;
	}

	public int getQuantity() {
		return quantity;
	}
	
	// This method is created for the unit test
	public void setQuantity(int quantity){
		this.quantity = quantity;
	}

	public Address getPickupAddress() {
		return pickupAddress;
	}

	public String getPickupDate() {
		return pickupDate;
	}

	public Address getDestinationAddress() {
		return destinationAddress;
	}

	public String getExpectedDeliveryDate() {
		return expectedDeliveryDate;
	}
    
}
