package org.example.cliente.services.cache

import org.example.cliente.model.Cliente
import org.example.dni.Dni

interface CacheCliente {
    fun get(dni: Dni): Cliente?
    fun put(dni: Dni, value: Cliente)
    fun remove(dni: Dni)
    fun clear()
    fun size(): Int
}