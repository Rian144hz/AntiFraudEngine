package org.antifraudengine.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "transacoes")
class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null;
    @Column(nullable = false)
    var compradorId: Long? = null;
    @Column(nullable = false)
    var valor: Double? = null;
    @Column(nullable = false)
    var cartaoNumero: String? = null;
    @Column(nullable = false)
    var categoria: String? = null;
    @Column(nullable = false)
    var dataHora: Long? = null;
    @Column(nullable = false)
    var status: String? = null;
    @Column(nullable = false)
    var tipoStatus: String? = null;

    constructor()
    constructor(
        id: Long?,
        compradorId: Long?,
        valor: Double?,
        cartaoNumero: String?,
        categoria: String?,
        dataHora: Long?,
        status: String?,
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