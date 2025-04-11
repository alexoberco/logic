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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ApplicationScoped
class EmailConsumer {
    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(EmailConsumer::class.java)
    }

    @Inject
    lateinit var reactiveMailer: ReactiveMailer

    @Inject
    @Location("confirmation.html")
    lateinit var template: Template

    private val jsonb: Jsonb = JsonbBuilder.create()

    @Incoming("order-confirmations-in")
    fun process(message: String) {
        LOG.info("[EmailConsumer] Mensaje recibido en order-confirmations-in: {}", message)

        val event = try {
            jsonb.fromJson(message, OrderEvent::class.java).also {
                LOG.info("[EmailConsumer] Evento deserializado: {}", it)
            }
        } catch (e: Exception) {
            LOG.info("[EmailConsumer] Error deserializando OrderEvent", e)
            return
        }

        val body: TemplateInstance = template.data("event", event)
        LOG.info("[EmailConsumer] Plantilla procesada para purchaseId={}", event.purchaseId)

        reactiveMailer.send(
            Mail.withHtml(
                event.userEmail,
                "Confirmación de Pedido #${event.purchaseId}",
                body.render()
            )
        )
            .subscribe()
            .with(
                { result -> LOG.info("[EmailConsumer] Correo enviado a {} para pedido #{}", event.userEmail, event.purchaseId) },
                { err -> LOG.info("[EmailConsumer] Falló el envío de correo a ${event.userEmail}", err) }
            )
    }
}
