package co.edu.javeriana.arqui.messaging

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class OrderEvent(
    val purchaseId: Long,
    val productName: String,
    val quantity: Int,
    val userEmail: String
)