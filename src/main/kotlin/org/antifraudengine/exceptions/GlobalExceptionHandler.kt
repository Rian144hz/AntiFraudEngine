package org.antifraudengine.exceptions

import org.antifraudengine.dto.ErroResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErroResponseDTO> {
        val erroBody = ErroResponseDTO(
            status = HttpStatus.BAD_REQUEST.value(),
            erro = "Bad request",
            menssagem = e.message

        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroBody)

    }

        @ExceptionHandler(TransacaoNaoEncontradaException::class)
        fun handleTransacaoNaoEncontrada(e : TransacaoNaoEncontradaException): ResponseEntity<ErroResponseDTO> {
            val erroBody = ErroResponseDTO(
                status = HttpStatus.NOT_FOUND.value(),
                erro = "Not found.",
                menssagem = e.message
            )
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erroBody);
        }

        @ExceptionHandler(Exception::class)
        fun handleGenericException(ex: Exception): ResponseEntity<ErroResponseDTO> {
            val erroBody = ErroResponseDTO(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                erro = "Internal Server Error",
                menssagem = "Ocorreu um erro interno inesperado no servidor."
            )
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erroBody)
        }


}