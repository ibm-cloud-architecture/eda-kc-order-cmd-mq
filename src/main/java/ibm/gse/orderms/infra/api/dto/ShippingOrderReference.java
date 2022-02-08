package ibm.gse.orderms.infra.api.dto;


import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ShippingOrderReference {
	 private String orderID;
	 private String customerID;
	 private String productID;
	 private String voyageID;
	 private String containerID;
	 private String status;
	 
	 
	public ShippingOrderReference(String orderID, 
			String customerID, 
			String productID, 
			String voyageID,
			String containerID,
			String status) {
		super();
		this.orderID = orderID;
		this.customerID = customerID;
		this.productID = productID;
		this.voyageID = voyageID;
		this.containerID = containerID;
		this.status = status;
	}
	
	public String getOrderID() {
		return orderID;
	}
	public String getCustomerID() {
		return customerID;
	}
	public String getProductID() {
		return productID;
	}
	public String getStatus() {
		return status;
	}

	public String getVoyageID() {
		return voyageID;
	}

	

	public String getContainerID() {
		return containerID;
	}

	
	 
}
