package co.edu.javeriana.arqui.proyecto.resource

import co.edu.javeriana.arqui.proyecto.model.Order
import co.edu.javeriana.arqui.proyecto.service.OrderService
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OrderResource {

    @Inject
    lateinit var orderService: OrderService

    @GET
    fun listOrders(): List<Order> {
        return orderService.listOrders()
    }

    @POST
    fun createOrder(orderRequest: Order): Response {
        val order = orderService.createOrder(orderRequest)
        return Response.status(Response.Status.CREATED).entity(order).build()
    }
}
