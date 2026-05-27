package org.antifraudengine.service.rules

import org.antifraudengine.domain.entities.Transacao

interface RegraFraude {
    fun avaliar(transacao: Transacao):ResultadoRegra;
}