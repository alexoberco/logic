package co.edu.javeriana.arqui.messaging

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import io.smallrye.reactive.messaging.annotations.Channel
import io.smallrye.reactive.messaging.annotations.Emitter
import jakarta.enterprise.event.Observes
import jakarta.enterprise.event.TransactionPhase
import jakarta.json.bind.Jsonb
import jakarta.json.bind.JsonbBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ApplicationScoped
class OrderEmitter @Inject constructor(
    @Channel("order-confirmations-out")
    private val emitter: Emitter<String>
) {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(OrderEmitter::class.java)
    }

    private val jsonb: Jsonb = JsonbBuilder.create()

    fun onOrderPlaced(
        @Observes(during = TransactionPhase.AFTER_SUCCESS) event: OrderEvent
    ) {
        LOG.info("OrderEmitter: evento recibido, purchaseId={}, productName={}, quantity={}, userEmail={}",
            event.purchaseId, event.productName, event.quantity, event.userEmail)

        val payload = jsonb.toJson(event)
        LOG.debug("OrderEmitter: payload JSON generado: {}", payload)

        emitter.send(payload)
        LOG.info("OrderEmitter: evento enviado al canal 'order-confirmations-out'")
    }
}
