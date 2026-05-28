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
        require(buyerId > 0) { "O compradorId deve ser maior que zero" }
        require(amount != null) { "O amount é obrigatório" }
        require(amount > BigDecimal.ZERO) { "O valor deve ser maior que zero" }
        require(!cardNumber.isNullOrBlank()) { "O cardNumber é obrigatório" }
        require(cardNumber.matches(Regex("\\d{13,19}"))) { "O numero do cartao deve conter apenas digitos numericos e ter entre 13 e 19 caracteres." }
        require(!category.isNullOrBlank()) { "O category é obrigatório" }

        if (timestamp != null) {
            require(!timestamp.isAfter(LocalDateTime.now())) { "A data e hora da transacao nao pode ser uma data futura." }
        }
    }
}