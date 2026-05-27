package org.antifraudengine.service.rules

data class ResultadoRegra(
    val aprovado: Boolean,
    val motivo: String? = null,
)
