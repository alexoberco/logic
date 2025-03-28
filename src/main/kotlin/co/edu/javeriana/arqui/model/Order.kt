package co.edu.javeriana.arqui.proyecto.model

import java.io.Serializable
import java.time.LocalDateTime
import jakarta.persistence.*
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
@Entity
@Table(name = "orders")
class Order() : Serializable {

    @Id
    @SequenceGenerator(name = "orderSeq", sequenceName = "order_id_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "orderSeq", strategy = GenerationType.SEQUENCE)
    var id: Long? = null

    @Column(nullable = false)
    var userEmail: String? = null

    @Column(nullable = false)
    var productId: Long? = null

    @Column(nullable = false)
    var orderDate: LocalDateTime? = null

    // Constructor auxiliar para instanciar fácilmente
    constructor(userEmail: String, productId: Long, orderDate: LocalDateTime) : this() {
        this.userEmail = userEmail
        this.productId = productId
        this.orderDate = orderDate
    }
}
