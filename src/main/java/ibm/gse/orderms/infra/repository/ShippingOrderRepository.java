package ibm.gse.orderms.infra.repository;

import java.util.List;
import java.util.Optional;

import ibm.gse.orderms.domain.model.order.ShippingOrder;


public interface ShippingOrderRepository {

    public void addOrUpdateNewShippingOrder(ShippingOrder order) throws OrderCreationException;
    public void updateShippingOrder(ShippingOrder order) throws OrderUpdateException;;
    public List<ShippingOrder> getAll();
    public Optional<ShippingOrder> getOrderByOrderID(String orderId);
	public void reset();
	

}
