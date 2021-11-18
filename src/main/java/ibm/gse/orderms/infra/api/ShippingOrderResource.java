package ibm.gse.orderms.infra.api;

import java.util.List;
import java.util.Optional;

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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibm.gse.orderms.domain.model.order.ShippingOrder;
import ibm.gse.orderms.domain.service.ShippingOrderService;
import ibm.gse.orderms.infra.api.dto.ShippingOrderCreateDTO;
import ibm.gse.orderms.infra.api.dto.ShippingOrderReference;

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
	public Response createShippingOrder(ShippingOrderCreateDTO createOrderParameters) {
		if (createOrderParameters == null ) {
			return Response.status(400, "No parameter sent").build();
		}
		try {
		   ShippingOrderCreateDTO.validateInputData(createOrderParameters);

		} catch(IllegalArgumentException iae) {
			return Response.status(400, iae.getMessage()).build();
		}
        ShippingOrder order = ShippingOrderCreateDTO.from(createOrderParameters);
   	    try {
			shippingOrderService.createOrder(order);
		} catch(Exception e) {
			return Response.serverError().build();
		}
	    //return Response.ok().entity(order.getOrderID()).build();
	    //API contract expects a JSON Object and not just a plaintext string
	    return Response.ok().build();
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
