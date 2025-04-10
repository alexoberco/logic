package co.edu.javeriana.arqui.service

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class OrderRequest(
    val productId: Long = 0,
    val quantity: Int = 0,
    val userEmail: String = ""
)
