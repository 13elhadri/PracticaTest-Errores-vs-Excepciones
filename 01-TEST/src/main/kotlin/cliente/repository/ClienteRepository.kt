package org.example.cliente.repository

import org.example.cliente.model.Cliente
import org.example.dni.Dni

interface ClienteRepository {
    fun getAll(): List<Cliente>
    fun getByDni(dni: Dni): Cliente?
    fun save(cliente: Cliente): Cliente
    fun update(dni: Dni, t: Cliente): Cliente?
    fun delete(dni: Dni): Cliente?
}