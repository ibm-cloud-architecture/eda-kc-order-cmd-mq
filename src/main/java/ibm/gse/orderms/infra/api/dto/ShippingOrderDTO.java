package ibm.gse.orderms.infra.api.dto;

import ibm.gse.orderms.domain.model.order.Address;
import ibm.gse.orderms.domain.model.order.ShippingOrder;

public class ShippingOrderDTO {

    public String customerID;
    public String productID;
    public int quantity;
    public String expectedDeliveryDate;
    public String pickupDate;
    public Address pickupAddress;
    public Address destinationAddress;


    public static ShippingOrder from(ShippingOrderDTO inDTO) {
        return new ShippingOrder("",
        inDTO.productID,
        inDTO.customerID,
        inDTO.quantity,
        inDTO.pickupAddress,
        inDTO.pickupDate,
        inDTO.destinationAddress,
        inDTO.expectedDeliveryDate,
        ShippingOrder.PENDING_STATUS);
    }
    
    public String getProductID() {
        return productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public Address getPickupAddress() {
        return pickupAddress;
    }

    public Address getDestinationAddress() {
        return destinationAddress;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public void setPickupAddress(Address pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public void setDestinationAddress(Address destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public static ShippingOrderDTO fromEntity(ShippingOrder order) {

        ShippingOrderDTO shippingOrderDTO = new ShippingOrderDTO();
        shippingOrderDTO.setCustomerID(order.getCustomerID());
        shippingOrderDTO.setDestinationAddress(order.getDestinationAddress());
        shippingOrderDTO.setPickupAddress(order.getPickupAddress());
        shippingOrderDTO.setQuantity(order.getQuantity());
        shippingOrderDTO.setPickupDate(order.getPickupDate());
        shippingOrderDTO.setExpectedDeliveryDate(order.getExpectedDeliveryDate());
        shippingOrderDTO.setProductID(order.getProductID());

        return shippingOrderDTO;
    }


}
