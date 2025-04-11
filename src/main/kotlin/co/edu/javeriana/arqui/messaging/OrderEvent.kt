package co.edu.javeriana.arqui.messaging

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
class OrderEvent {
    var purchaseId: Long = 0
    var productName: String = ""
    var quantity: Int = 0
    var userEmail: String = ""
}
