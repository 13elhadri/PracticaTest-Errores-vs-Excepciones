package org.example.cliente.services

import com.github.michaelbull.result.Result
import org.example.cliente.exception.ClienteException
import org.example.cliente.model.Cliente
import org.example.dni.Dni

interface ClienteServices {
    fun getAll(): List<Cliente>
    fun getByDni(dni: Dni): Cliente
    fun save(cliente: Cliente): Cliente
    fun update(dni: Dni, cliente: Cliente): Cliente
    fun delete(dni: Dni): Cliente
}