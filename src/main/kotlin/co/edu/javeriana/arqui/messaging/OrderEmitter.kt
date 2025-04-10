package co.edu.javeriana.arqui.messaging

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import io.smallrye.reactive.messaging.annotations.Channel
import io.smallrye.reactive.messaging.annotations.Emitter
import jakarta.json.bind.Jsonb
import jakarta.json.bind.JsonbBuilder

@ApplicationScoped
class OrderEmitter {
    @Inject
    @Channel("order-confirmations")
    lateinit var emitter: Emitter<String>

    private val jsonb: Jsonb = JsonbBuilder.create()

    fun send(event: OrderEvent) {
        emitter.send(jsonb.toJson(event))
    }
}
