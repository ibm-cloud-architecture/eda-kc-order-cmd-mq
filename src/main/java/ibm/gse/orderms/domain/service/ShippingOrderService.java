package ibm.gse.orderms.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import ibm.gse.orderms.infra.repository.OrderUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibm.gse.orderms.domain.model.order.ShippingOrder;
import ibm.gse.orderms.infra.api.dto.ShippingOrderReference;
import ibm.gse.orderms.infra.repository.OrderCreationException;
import ibm.gse.orderms.infra.repository.ShippingOrderRepository;
import ibm.gse.orderms.infra.repository.ShippingOrderRepositoryMock;

@ApplicationScoped
public class ShippingOrderService {
	static final Logger logger = LoggerFactory.getLogger(ShippingOrderService.class);
	   
	private ShippingOrderRepository orderRepository = null;
	
	public ShippingOrderService() {
		this.orderRepository = new ShippingOrderRepositoryMock();
	}
	
	
	public void createOrder(ShippingOrder order) throws OrderCreationException {
		 this.orderRepository.addOrUpdateNewShippingOrder(order);
	}

	public List<ShippingOrderReference> getAllOrders() {
		List<ShippingOrder> orders = this.orderRepository.getAll();
		List<ShippingOrderReference> orderReferences = new ArrayList<ShippingOrderReference>();
		for (ShippingOrder order : orders) {
			ShippingOrderReference ref = new ShippingOrderReference(order.getOrderID(),
					order.getCustomerID(),
					order.getProductID(),
					order.getVoyageID(),
					order.getContainerID(),
					order.getStatus()); 
			orderReferences.add(ref);
		}
		return  orderReferences;
	}

	public Optional<ShippingOrder> getOrderByOrderID(String orderId) {	
		return this.orderRepository.getOrderByOrderID(orderId);
	}

    public void updateOrder(ShippingOrder order) throws OrderUpdateException {
		this.orderRepository.updateShippingOrder(order);
    }
}
