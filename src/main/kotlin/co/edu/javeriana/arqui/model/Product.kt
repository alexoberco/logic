package co.edu.javeriana.arqui.proyecto.model

import java.io.Serializable
import jakarta.persistence.*

@Entity
@Table(name = "products")
class Product() : Serializable {

    @Id
    @SequenceGenerator(name = "productSeq", sequenceName = "product_id_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "productSeq", strategy = GenerationType.SEQUENCE)
    var id: Long? = null

    @Column(nullable = false)
    var productName: String? = null

    @Column(nullable = false)
    var stock: Int? = null

    @Column(nullable = false)
    var status: String? = null

    // Constructor auxiliar para instanciar fácilmente
    constructor(productName: String, stock: Int, status: String) : this() {
        this.productName = productName
        this.stock = stock
        this.status = status
    }
}
