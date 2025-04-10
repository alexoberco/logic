package co.edu.javeriana.arqui.resources

import co.edu.javeriana.arqui.model.Product
import co.edu.javeriana.arqui.model.Purchase
import co.edu.javeriana.arqui.repository.ProductRepository
import co.edu.javeriana.arqui.service.OrderRequest
import co.edu.javeriana.arqui.service.OrderService
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OrderResource @Inject constructor(
    private val service: OrderService,
    private val productRepo: ProductRepository
) {
    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(OrderResource::class.java)
    }
    @GET
    @Path("/products")
    fun listProducts(): List<Product> =
        productRepo.list("status", "available")

    @POST
    @Path("/orders")
    fun createOrder(request: OrderRequest): Purchase {
        LOG.info("POST /api/orders – payload: $request")
        return service.placeOrder(request)
    }

}
