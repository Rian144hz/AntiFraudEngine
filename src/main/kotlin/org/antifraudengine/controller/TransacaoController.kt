package org.antifraudengine.controller

import org.antifraudengine.domain.entities.Transacao
import org.antifraudengine.dto.TransacaoRequestDTO
import org.antifraudengine.repository.TransacaoRepository
import org.antifraudengine.service.rules.MotorRegrasFraude
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/transacoes")
class TransacaoController(
    private val motorRegras: MotorRegrasFraude,
    private val transacaoRepository: TransacaoRepository) {

    @PostMapping
    fun criarTransacao(@RequestBody request: TransacaoRequestDTO): ResponseEntity<Transacao> {
        val novaTransacao = Transacao(
            id = null,
            compradorId = request.buyerId!!,
            valor = request.amount!!,
            cartaoNumero = request.cardNumber!!,
            categoria = request.category!!,
            dataHora = request.timestamp?: LocalDateTime.now(),
            status = "PENDENTE",
            tipoStatus = "Aguardando processamento do motor de regras."

        )

        val transacaoProcessada = motorRegras.processarTransacao(novaTransacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(transacaoProcessada);
    }
    @GetMapping
    fun listarTodas(
        @RequestParam(required = false) buyerId: Long?,
        @RequestParam(required = false) status: String?,
    ):ResponseEntity<List<Transacao>> {
        val transacoes = transacaoRepository.findAll();

        var resultadoFiltrado = transacoes;
        if (buyerId != null) {
            resultadoFiltrado = resultadoFiltrado.filter { it.compradorId == buyerId }
        }

        if (status != null) {
            resultadoFiltrado = resultadoFiltrado.filter { it.status.equals(status,ignoreCase = true) }
        }

        return ResponseEntity.ok(resultadoFiltrado);
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<Transacao> {
        val transacaoOpcional = transacaoRepository.findById(id)

        return if (transacaoOpcional.isPresent) {
            ResponseEntity.ok(transacaoOpcional.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

