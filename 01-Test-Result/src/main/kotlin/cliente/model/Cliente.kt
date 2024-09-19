package org.example.cliente.model

import org.example.banco.cuenta.CuentaBancaria
import org.example.banco.tarjeta.Tarjeta
import org.example.dni.Dni
import java.util.*

data class Cliente(
    val id: UUID = UUID.randomUUID(),
    val nombre:String,
    val dni: Dni,
    val cuenta: CuentaBancaria,
    val tarjeta: Tarjeta
)