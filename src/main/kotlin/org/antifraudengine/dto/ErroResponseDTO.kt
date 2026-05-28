package org.antifraudengine.dto

import java.time.LocalDateTime

data class ErroResponseDTO(
    var timestamp: LocalDateTime? = LocalDateTime.now(),
    var status: Int? = null,
    var erro: String? = null,
    val menssagem: String? = null,
)
