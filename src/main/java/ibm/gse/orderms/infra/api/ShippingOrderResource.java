package ibm.gse.orderms.infra.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import ibm.gse.orderms.domain.events.EventBase;
import ibm.gse.orderms.domain.events.order.OrderCancelAndRejectPayload;
import ibm.gse.orderms.domain.events.order.OrderCancelledEvent;
import ibm.gse.orderms.domain.events.order.OrderEvent;
import ibm.gse.orderms.domain.events.order.OrderEventPayload;
import ibm.gse.orderms.domain.model.order.ShippingOrder;
import ibm.gse.orderms.domain.service.ShippingOrderService;
import ibm.gse.orderms.infra.api.dto.ShippingOrderCreateDTO;
import ibm.gse.orderms.infra.api.dto.ShippingOrderReference;
import ibm.gse.orderms.infra.jms.producer.JMSQueueWriter;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;
import java.util.Optional;

/**
 * Expose the commands and APIs used by external clients to manage
 * the ShippingOrder Entity
 *
 * @author jerome boyer
 *
 */
@ApplicationScoped
@Path("/api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
public class ShippingOrderResource {

	static final Logger logger = LoggerFactory.getLogger(ShippingOrderResource.class);

	@Inject
	public ShippingOrderService shippingOrderService;

	@Inject
	JMSQueueWriter<EventBase> jmsQueueWriter;

	public ShippingOrderResource() {
	}

	public ShippingOrderResource(ShippingOrderService shippingOrderService) {
		this.shippingOrderService = shippingOrderService;
	}

	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Request to create an order", description = "")
    @APIResponses(value = {
            @APIResponse(responseCode = "400", description = "Bad create order request", content = @Content(mediaType = "application/json")),
            @APIResponse(responseCode = "200", description = "Order created, return order unique identifier", content = @Content(mediaType = "application/json")) })
	public Response createShippingOrder(String createOrderParameters) throws Exception {
		ShippingOrderCreateDTO shippingOrderCreateDTO;
		if (createOrderParameters == null ) {
			return Response.status(400, "No parameter sent").build();
		}
		try {

			ObjectMapper mapper = new ObjectMapper();
			shippingOrderCreateDTO = mapper.readValue(createOrderParameters, ShippingOrderCreateDTO.class);
		   	ShippingOrderCreateDTO.validateInputData(shippingOrderCreateDTO);

		} catch(IllegalArgumentException iae) {
			logger.error("Error decoding request body", iae);
			return Response.status(400, iae.getMessage()).build();
		}
        ShippingOrder order = ShippingOrderCreateDTO.from(shippingOrderCreateDTO);
   	    try {
			shippingOrderService.createOrder(order);
		} catch(Exception e) {
			return Response.serverError().build();
		}

		try {

			OrderEventPayload orderEventPayload = new OrderEventPayload(order);
			OrderEvent orderEvent = new OrderEvent(System.currentTimeMillis(), EventBase.ORDER_CREATED_TYPE, "1.0", orderEventPayload);
			jmsQueueWriter.sendMessage(orderEvent, String.valueOf(System.getenv("VOYAGE_REQUEST_QUEUE")));

		} catch (Exception e) {
			logger.error("Error writing message to the " + System.getenv("VOYAGE_REQUEST_QUEUE") +
					" queue. Rolling back.", e);
			try {

				order.setStatus(ShippingOrder.CANCELLED_STATUS);
				shippingOrderService.updateOrder(order);

				OrderCancelAndRejectPayload payload = new OrderCancelAndRejectPayload(order, e.getMessage());
				OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(System.currentTimeMillis(), "1.0", payload);
				jmsQueueWriter.sendMessage(orderCancelledEvent,
						String.valueOf(System.getenv("VOYAGE_REQUEST_QUEUE")));

			} catch (Exception rollBackException) {
				logger.error("Could not rollback...", rollBackException);
				throw rollBackException;
			}
		}
		//return Response.ok().entity(order.getOrderID()).build();
	    //API contract expects a JSON Object and not just a plaintext string
	    return Response.ok().entity(order.getOrderID()).build();
	}


	@GET
    public List<ShippingOrderReference> getAllActiveOrders() {
        return shippingOrderService.getAllOrders();
    }


    @GET
    @Path("{Id}")
    @Operation(summary = "Query an order by id", description = "")
    @APIResponses(value = {
            @APIResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "text/plain")),
            @APIResponse(responseCode = "200", description = "Order found", content = @Content(mediaType = "application/json")) })
    public Response getOrderByOrderId(@PathParam("Id") String orderId) {
        logger.info("QueryService.getOrderByOrderId(" + orderId + ")");

        Optional<ShippingOrder> oo = shippingOrderService.getOrderByOrderID(orderId);
        if (oo.isPresent()) {
            return Response.ok().entity(oo.get()).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }


}
