package io.github.kieckegard.outbox.demo

import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "t_products")
class Product(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        val id: Long?,
        val title: String,
        val price: BigDecimal
)