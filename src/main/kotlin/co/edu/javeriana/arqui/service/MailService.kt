package co.edu.javeriana.arqui.proyecto.service

import co.edu.javeriana.arqui.proyecto.model.Order
import io.quarkus.mailer.Mail
import io.quarkus.mailer.reactive.ReactiveMailer
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class MailService {

    @Inject
    lateinit var reactiveMailer: ReactiveMailer

    fun sendOrderConfirmation(email: String, order: Order) {
        val mail = Mail.withText(
            email,
            "Confirmación de Pedido",
            "Tu orden con ID ${order.id} ha sido recibida y está en proceso."
        )
        reactiveMailer.send(mail)
            .subscribe().with(
                { println("Correo enviado exitosamente.") },
                { error -> println("Error al enviar correo: ${error.message}") }
            )
    }
}
