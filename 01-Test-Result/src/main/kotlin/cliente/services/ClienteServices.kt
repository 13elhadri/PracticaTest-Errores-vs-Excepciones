package org.example.cliente.services

import com.github.michaelbull.result.Result
import org.example.cliente.model.Cliente
import org.example.dni.Dni
import org.example.error.GeneralError

interface ClienteServices {
    fun getAll(): Result<List<Cliente>,GeneralError>
    fun getByDni(dni: Dni): Result<Cliente,GeneralError>
    fun save(cliente: Cliente): Result<Cliente,GeneralError>
    fun update(dni: Dni, cliente: Cliente): Result<Cliente,GeneralError>
    fun delete(dni: Dni): Result<Cliente,GeneralError>
}