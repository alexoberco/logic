package co.edu.javeriana.arqui.service

data class OrderRequest(
    val productId: Long,
    val quantity: Int,
    val userEmail: String
)
