package co.edu.javeriana.arqui.service

import co.edu.javeriana.arqui.messaging.OrderEvent
import co.edu.javeriana.arqui.messaging.OrderEmitter
import co.edu.javeriana.arqui.model.Purchase
import co.edu.javeriana.arqui.repository.ProductRepository
import co.edu.javeriana.arqui.repository.PurchaseRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Event
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.BadRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ApplicationScoped
class OrderService(
    private val productRepo: ProductRepository,
    private val purchaseRepo: PurchaseRepository,
    private val cdiEvent: Event<OrderEvent>


) {
    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(OrderService::class.java)
    }
    @Transactional
    fun placeOrder(request: OrderRequest): Purchase {
        LOG.debug("placeOrder recibido: productId=${request.productId}, quantity=${request.quantity}, userEmail=${request.userEmail}")
        val product = productRepo.findById(request.productId)
        if (product == null) {
            LOG.warn("Producto no encontrado con id=${request.productId}")
            throw BadRequestException("Producto no encontrado")
        }
        LOG.debug("Producto recuperado: id=${product.id}, stock=${product.stock}")

        if (product.stock < request.quantity) {
            LOG.warn("Stock insuficiente: stock=${product.stock}, quantity=${request.quantity}")
            throw BadRequestException("Stock insuficiente")
        }

        // Actualizar stock
        product.stock -= request.quantity
        product.status = if (product.stock > 0) "available" else "out_of_stock"
        productRepo.persist(product)
        LOG.debug("Stock actualizado: id=${product.id}, nuevoStock=${product.stock}, status=${product.status}")


        // Registrar compra
        val purchase = Purchase(
            productId = product.id!!,
            quantity = request.quantity,
            userEmail = request.userEmail
        )
        purchaseRepo.persist(purchase)
        LOG.debug("Purchase persistida: id=${purchase.id}, productId=${purchase.productId}, quantity=${purchase.quantity}")


        // Enviar evento asíncrono
        val event = OrderEvent(
            purchaseId = purchase.id!!,
            productName = product.name,
            quantity = request.quantity,
            userEmail = request.userEmail
        )
        cdiEvent.fire(event)
        LOG.debug("OrderEvent enviado: {}", event)


        return purchase
    }
}
