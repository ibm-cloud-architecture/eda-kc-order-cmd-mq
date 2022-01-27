package ibm.gse.orderms.domain.events.voyage;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class VoyageAssignmentPayload {

    private String orderID;
    private String voyageID;
    private String productId = "validProductId";

    public VoyageAssignmentPayload(String orderID, String voyageID) {
        this.orderID = orderID;
        this.voyageID = voyageID;
    }

    public VoyageAssignmentPayload() {}

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getVoyageID() {
        return voyageID;
    }

    public void setVoyageID(String voyageID) {
        this.voyageID = voyageID;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
