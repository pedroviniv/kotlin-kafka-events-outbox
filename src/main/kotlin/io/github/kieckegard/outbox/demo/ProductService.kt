package io.github.kieckegard.outbox.demo

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.kieckegard.outbox.DomainEventPublisher
import io.github.kieckegard.outbox.demo.events.CreatedProductDomainEvent
import io.github.kieckegard.outbox.demo.events.UpdatedProductDomainEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
        val productRepository: ProductRepository,
        val domainEventPublisher: DomainEventPublisher,
        val objectMapper: ObjectMapper) {

    /**
     * registers the given product in the repository
     * and publishes a "created product" event.
     */
    @Transactional
    fun registerNewProduct(product: Product) {

        this.productRepository.save(product)

        val evtPayload = this.objectMapper.writeValueAsString(product)

        val event = CreatedProductDomainEvent(evtPayload, product.id.toString())
        this.domainEventPublisher.publish(event)
    }

    /**
     * updates an existing product with the given product id
     * using the new title and the new price.
     *
     * after that, it publishes a "updated product" event.
     */
    @Transactional
    fun updateExistingProduct(product: Product) {

        this.productRepository.save(product)

        val evtPayload = this.objectMapper.writeValueAsString(product)

        val event = UpdatedProductDomainEvent(evtPayload, product.id.toString())
        this.domainEventPublisher.publish(event)
    }
}