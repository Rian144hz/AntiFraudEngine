package org.antifraudengine.repository

import org.antifraudengine.entities.Transacao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransacaoRepository: JpaRepository<Transacao, Long> {

}