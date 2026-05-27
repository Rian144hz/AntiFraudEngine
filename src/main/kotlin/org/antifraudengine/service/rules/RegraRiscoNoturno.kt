package org.antifraudengine.service.rules

import org.antifraudengine.domain.entities.Transacao
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class RegraRiscoNoturno(
    @Value("\${antifraude.regras.noite-inicio}")
    private val horaInicio: Int,

    @Value("\${antifraude.regras.noite-fim}")
    private val horaFim: Int,

    @Value("\${antifraude.regras.noite-valor-limite}")
    private val valorLimite: BigDecimal
) : RegraFraude {

    override fun avaliar(transacao: Transacao): ResultadoRegra {
        val horaDaTransacao = transacao.dataHora.hour;
        val estaNaHoraDeRisco = horaDaTransacao >= horaInicio || horaDaTransacao < horaFim;

        val ehValorAlto = transacao.valor.compareTo(valorLimite) > 0;

        if (estaNaHoraDeRisco && ehValorAlto) {
            return ResultadoRegra(
                aprovado = false,
                motivo = "NEGADA! Transacao de alto valor em horario de risco noturno.",
            )
        }
        return ResultadoRegra(
            aprovado = true,
            motivo = "APROVADO!",

        )

    }

}