package ibm.gse.orderms.infra.repository;

import ibm.gse.orderms.domain.model.order.ShippingOrder;

import java.util.List;
import java.util.Optional;


public interface ShippingOrderRepository {

    public void addOrUpdateNewShippingOrder(ShippingOrder order) throws OrderCreationException;
    public void updateShippingOrder(ShippingOrder order) throws OrderUpdateException;;
    public List<ShippingOrder> getAll();
    public Optional<ShippingOrder> getOrderByOrderID(String orderId);
	public void reset();
	

}
