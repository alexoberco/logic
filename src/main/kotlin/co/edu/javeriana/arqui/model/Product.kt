package co.edu.javeriana.arqui.model

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Entity

@Entity
class Product(
    var name: String = "",
    var stock: Int = 0,
    var status: String = "available"
) : PanacheEntity()