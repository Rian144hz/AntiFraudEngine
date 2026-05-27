package org.antifraudengine.service.rules

import org.antifraudengine.domain.entities.Transacao
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Component
class RegraLimiteValor(@Value("\${antifraude.regras.limite-valor}") private  val limiteMaximo: BigDecimal): RegraFraude {
    override fun avaliar(transacao: Transacao): ResultadoRegra {
        if(transacao.valor.compareTo(limiteMaximo) > 0) {
            return ResultadoRegra(
                aprovado = false,
                motivo = "NEGADA! Valor acima do limite permitido de: R$ $limiteMaximo.",
            )
        }
        return ResultadoRegra(
            aprovado = true,
            motivo = "ACEITA",
        )
    }

}