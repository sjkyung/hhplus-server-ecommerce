package kr.hhplus.be.server.infrastructure.product


import org.springframework.data.jpa.repository.JpaRepository

interface JpaProductRepository: JpaRepository<ProductEntity, Long> {
}