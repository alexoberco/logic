package co.edu.javeriana.arqui.messaging

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class OrderEvent(
    val purchaseId: Long = 0,
    val productName: String = "",
    val quantity: Int = 0,
    val userEmail: String = ""
)
