package org.example.cliente.services.cache

import org.example.cliente.model.Cliente
import org.example.dni.Dni
import org.lighthousegames.logging.logging

private val logger= logging()
class ClienteCacheImpl:CacheCliente {


    private val cache: MutableMap<Dni, Cliente> = mutableMapOf()

    override fun get(dni: Dni): Cliente? {
        logger.debug { "Buscando cliente con dni $dni en Cache" }
        return cache[dni]
    }

    override fun put(dni: Dni, value: Cliente) {
        logger.debug { "Metiendo cliente con dni $dni en Cache" }
        cache[dni] = value
    }

    override fun remove(dni: Dni) {
        logger.debug { "Eliminando cliente con dni $dni de Cache" }
        cache.remove(dni)
    }

    override fun clear(){
        logger.debug { "Eliminando cache" }
        cache.clear()
    }
    override fun size() = cache.size
}