package co.edu.javeriana.arqui.messaging

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import io.smallrye.reactive.messaging.annotations.Channel
import io.smallrye.reactive.messaging.annotations.Emitter
import jakarta.enterprise.event.Observes
import jakarta.enterprise.event.TransactionPhase
import jakarta.json.bind.Jsonb
import jakarta.json.bind.JsonbBuilder

@ApplicationScoped
class OrderEmitter @Inject constructor(
    @Channel("order-confirmations-out")
    private val emitter: Emitter<String>
){

    private val jsonb: Jsonb = JsonbBuilder.create()

    fun onOrderPlaced(
        @Observes(during = TransactionPhase.AFTER_SUCCESS) event: OrderEvent
    ) {
        emitter.send(jsonb.toJson(event))
    }
}
