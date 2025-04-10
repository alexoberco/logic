package co.edu.javeriana.arqui.service

import co.edu.javeriana.arqui.messaging.OrderEvent
import co.edu.javeriana.arqui.messaging.OrderEmitter
import co.edu.javeriana.arqui.model.Purchase
import co.edu.javeriana.arqui.repository.ProductRepository
import co.edu.javeriana.arqui.repository.PurchaseRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.ws.rs.BadRequestException

@ApplicationScoped
class OrderService(
    private val productRepo: ProductRepository,
    private val purchaseRepo: PurchaseRepository,
    private val emitter: OrderEmitter
) {

    @Transactional
    fun placeOrder(request: OrderRequest): Purchase {
        val product = productRepo.findById(request.productId)
            ?: throw BadRequestException("Producto no encontrado")
        if (product.stock < request.quantity) {
            throw BadRequestException("Stock insuficiente")
        }

        // Actualizar stock
        product.stock -= request.quantity
        product.status = if (product.stock > 0) "available" else "out_of_stock"
        productRepo.persist(product)

        // Registrar compra
        val purchase = Purchase(
            productId = product.id!!,
            quantity = request.quantity,
            userEmail = request.userEmail
        )
        purchaseRepo.persist(purchase)

        // Enviar evento asíncrono
        val event = OrderEvent(
            purchaseId = purchase.id!!,
            productName = product.name,
            quantity = request.quantity,
            userEmail = request.userEmail
        )
        emitter.send(event)

        return purchase
    }
}
