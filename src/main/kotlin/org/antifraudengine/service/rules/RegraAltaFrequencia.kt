package org.antifraudengine.service.rules

import org.antifraudengine.domain.entities.Transacao
import org.antifraudengine.repository.TransacaoRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RegraAltaFrequencia(
    private val transacaoRepository: TransacaoRepository,
    @Value("\${antifraude.regras.frequencia-minutos}")
    private val minutosJanela: Long,
    @Value("\${antifraude.regras.frequencia-limite-compras}")
    private val limiteCompras: Long) : RegraFraude {

    override fun avaliar(transacao: Transacao): ResultadoRegra {
        val agora = transacao.dataHora;
        val inicioDaJanela = agora.minusMinutes(minutosJanela);

        val totalDeComprasNaJanela = transacaoRepository.countByCompradorIdAndDataHoraBetween(
            compradorId = transacao.compradorId,
            inicio = inicioDaJanela,
            fim = agora,

        )

        if (totalDeComprasNaJanela > limiteCompras) {
            return ResultadoRegra(
                aprovado = false,
                motivo = "NEGADA! Alta frequencia de transacoes detectada: \$totalComprasNaJanela compras nos ultimos \$minutosJanela minutos.",
            )
        }
        return ResultadoRegra(
            aprovado = true,
            motivo = "ACEITA"
        )

    }


}