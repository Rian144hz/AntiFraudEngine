package org.antifraudengine.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class TransacaoRequestDTO(
    val buyerId: Long?,
    val amount: BigDecimal?,
    val cardNumber: String?,
    val category: String?,
    val timestamp: LocalDateTime?
) {
    init {
        require(buyerId != null) { "O compradorId é obrigatório" }
        require(amount != null) { "O amount é obrigatório" }
        require(amount > BigDecimal.ZERO) { "O valor deve ser maior que zero" }
        require(!cardNumber.isNullOrBlank()) { "O cardNumber é obrigatório" }
        require(!category.isNullOrBlank()) { "O category é obrigatório" }
    }
}