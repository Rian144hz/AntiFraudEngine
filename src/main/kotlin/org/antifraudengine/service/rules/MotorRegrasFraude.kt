package org.antifraudengine.service.rules

import org.antifraudengine.domain.entities.Transacao
import org.antifraudengine.repository.TransacaoRepository
import org.springframework.stereotype.Component

@Component
class MotorRegrasFraude(private val regras: List<RegraFraude>,
    private val transacaoRepository: TransacaoRepository) {


    fun processarTransacao(transacao: Transacao) : Transacao {

        for (regra in regras) {
            val resultado = regra.avaliar(transacao);

            if (!resultado.aprovado) {
                transacao.status = "NEGADA!"
                transacao.tipoStatus = resultado.motivo;
                return transacaoRepository.save(transacao);
            }
        }
        transacao.status = "APROVADA!"
        transacao.tipoStatus = "APROVADA!: Nenhuma irregularidade detectada."
        return transacaoRepository.save(transacao)
    }
}