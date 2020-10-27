package io.github.kieckegard.outbox.demo

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.kieckegard.outbox.DomainEventPublisher
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
    fun registerProduct(product: Product) {

        this.productRepository.save(product)

        val evtPayload = this.objectMapper.writeValueAsString(product)

        val event = CreatedProductDomainEvent(evtPayload, product.id.toString())
        this.domainEventPublisher.publish(event)
    }
}