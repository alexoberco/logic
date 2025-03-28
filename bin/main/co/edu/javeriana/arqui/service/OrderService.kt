package co.edu.javeriana.arqui.proyecto.service

import co.edu.javeriana.arqui.proyecto.model.Order
import co.edu.javeriana.arqui.proyecto.model.Product
import io.smallrye.reactive.messaging.annotations.Channel
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.json.bind.Jsonb
import jakarta.json.bind.JsonbBuilder
import java.util.logging.Logger

@ApplicationScoped
class OrderService {

    @Inject
    lateinit var em: EntityManager

    @Inject
    lateinit var mailService: MailService

    // Inyectamos el canal de salida de RabbitMQ como Emitter<String>
    @Inject
    @Channel("quote-requests") Emitter<String> quoteRequestEmitter;


    private val logger = Logger.getLogger(OrderService::class.java.name)

    fun listOrders(): List<Order> {
        return em.createQuery("SELECT o FROM Order o", Order::class.java).resultList
    }

    @Transactional
    fun createOrder(orderRequest: Order): Order {
        // Buscar el producto asociado
        val product = em.find(Product::class.java, orderRequest.productId)
            ?: throw RuntimeException("Producto no encontrado")
        if (product.stock == null || product.stock!! <= 0) {
            throw RuntimeException("Producto sin stock")
        }

        // Actualizar stock y estado
        product.stock = product.stock!! - 1
        if (product.stock == 0) {
            product.status = "out of stock"
        }
        em.merge(product)

        // Establecer la fecha del pedido si no se ha proporcionado
        if (orderRequest.orderDate == null) {
            orderRequest.orderDate = java.time.LocalDateTime.now()
        }
        em.persist(orderRequest)

        // Convertir el objeto Order a JSON usando JSON-B
        val jsonb: Jsonb = JsonbBuilder.create()
        val jsonOrder = jsonb.toJson(orderRequest)

        // Enviar el mensaje a RabbitMQ a través del canal "quote-requests"
        quoteRequestEmitter.send(jsonOrder)
        logger.info("Orden enviada a RabbitMQ, id: ${orderRequest.id}")

        // Enviar correo de confirmación
        mailService.sendOrderConfirmation(orderRequest.userEmail ?: "", orderRequest)

        return orderRequest
    }
}
