package org.antifraudengine.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transacoes")
class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null;
    @Column(nullable = false)
    var compradorId: Long = 0;
    @Column(nullable = false)
    var valor: BigDecimal = BigDecimal.ZERO;
    @Column(nullable = false)
    var cartaoNumero: String = ""
    @Column(nullable = false)
    var categoria: String = ""
    @Column(nullable = false)
    var dataHora: LocalDateTime = LocalDateTime.now()
    @Column(nullable = false)
    var status: String = "PENDENTE";
    @Column(nullable = false)
    var tipoStatus: String? = null;

    constructor()
    constructor(
        id: Long?,
        compradorId: Long,
        valor: BigDecimal,
        cartaoNumero: String,
        categoria: String,
        dataHora: LocalDateTime,
        status: String,
        tipoStatus: String?
    ) {
        this.id = id
        this.compradorId = compradorId
        this.valor = valor
        this.cartaoNumero = cartaoNumero
        this.categoria = categoria
        this.dataHora = dataHora
        this.status = status
        this.tipoStatus = tipoStatus
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transacao

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}