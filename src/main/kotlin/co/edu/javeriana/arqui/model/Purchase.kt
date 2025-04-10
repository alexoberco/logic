package co.edu.javeriana.arqui.model

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Entity
import java.time.Instant

@Entity
class Purchase(
    var productId: Long = 0,
    var quantity: Int = 0,
    var userEmail: String = "",
    var timestamp: Instant = Instant.now()
) : PanacheEntity()