package io.github.kieckegard.outbox.demo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RequestMapping("/products")
@RestController
class ProductController(val productService: ProductService) {

    class NewProduct(
            val title: String,
            val price: BigDecimal
    )

    class UpdateProduct(
            val title: String,
            val price: BigDecimal
    )

    @PostMapping
    fun registerNewProduct(@RequestBody payload: NewProduct): ResponseEntity<Void> {

        val product = Product(null, payload.title, payload.price)
        this.productService.registerNewProduct(product)

        return ResponseEntity.ok().build()
    }

    @PutMapping("/{product_id}")
    fun updateExistingProduct(
            @PathVariable("product_id") productId: Long,
            @RequestBody payload: UpdateProduct): ResponseEntity<Void> {

        val productToUpdate = Product(productId, payload.title, payload.price)
        this.productService.updateExistingProduct(productToUpdate)

        return ResponseEntity.ok().build()
    }
}