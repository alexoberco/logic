package co.edu.javeriana.arqui.repository

import co.edu.javeriana.arqui.model.Product
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductRepository : PanacheRepository<Product>