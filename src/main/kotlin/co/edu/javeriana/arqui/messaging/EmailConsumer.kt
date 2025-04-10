package co.edu.javeriana.arqui.messaging

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.quarkus.mailer.Mail
import io.quarkus.mailer.reactive.ReactiveMailer
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.json.bind.Jsonb
import jakarta.json.bind.JsonbBuilder
import org.eclipse.microprofile.reactive.messaging.Incoming

@ApplicationScoped
class EmailConsumer {
    @Inject
    lateinit var reactiveMailer: ReactiveMailer

    // Inyectamos la plantilla confirmation.html
    @Inject
    @Location("confirmation.html")
    lateinit var template: Template

    private val jsonb: Jsonb = JsonbBuilder.create()

    @Incoming("order-confirmations")
    fun process(message: String) {
        val event = jsonb.fromJson(message, OrderEvent::class.java)
        val body: TemplateInstance = template.data("event", event)
        reactiveMailer.send(
            Mail.withHtml(
                event.userEmail,
                "Confirmación de Pedido #${event.purchaseId}",
                body.render()
            )
        ).subscribe()
    }
}
