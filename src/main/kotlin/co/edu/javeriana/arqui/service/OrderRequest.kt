package co.edu.javeriana.arqui.service

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
class OrderRequest {
    var productId: Long = 0
    var quantity: Int = 0
    var userEmail: String = ""
}
