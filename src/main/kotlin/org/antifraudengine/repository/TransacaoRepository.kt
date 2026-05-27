package org.antifraudengine.repository

import org.antifraudengine.domain.entities.Transacao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TransacaoRepository: JpaRepository<Transacao, Long> {

    fun countByCompradorIdAndDataHoraBetween(
        compradorId: Long,
        inicio: LocalDateTime,
        fim: LocalDateTime
    ): Long
}