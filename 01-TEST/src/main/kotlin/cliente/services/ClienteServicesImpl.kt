package org.example.cliente.services

import org.example.banco.cuenta.validator.CuentaBancariaValidator
import org.example.banco.tarjeta.validator.TarjetaValidator
import org.example.cliente.exception.ClienteException
import org.example.cliente.model.Cliente
import org.example.cliente.repository.ClienteRepository
import org.example.cliente.services.cache.CacheCliente
import org.example.cliente.validator.ClienteValidator
import org.example.dni.Dni
import org.example.dni.validator.DniValidator
import org.lighthousegames.logging.logging

private val logger = logging()

class ClienteServicesImpl(
    private val repository:ClienteRepository,
    private val cache:CacheCliente,
    private val clienteValidator:ClienteValidator
):ClienteServices {
    override fun getAll(): List<Cliente> {
        logger.debug { "Obteniendo clientes del repositorio" }
        val clientes = repository.getAll()
        logger.info { "Clientes obtenidos: $clientes" }
        return clientes
    }

    override fun getByDni(dni: Dni): Cliente {
        logger.debug { "Buscando por dni en cache" }
            return cache.get(dni)
                ?: repository.getByDni(dni)
                    ?: throw ClienteException.ClienteNoEncontradoException(dni.toString())

    }

    override fun save(cliente: Cliente):Cliente{
        logger.debug { "Guardando cliente: $cliente" }

        clienteValidator.validarCliente(cliente)
        repository.save(cliente)
        logger.debug { "Guardando cliente en cache: $cliente" }
        cache.put(cliente.dni,cliente)
        return cliente
    }

    override fun update(dni: Dni, cliente: Cliente): Cliente {
        logger.debug { "Actualizando cliente con dni: $dni" }
        clienteValidator.validarCliente(cliente)
        repository.update(dni, cliente)?: throw ClienteException.ClienteNoActalizadoException(dni.toString())
        cache.put(dni, cliente)
        return cliente
    }

    override fun delete(dni: Dni): Cliente {
        logger.debug { "Borrando cliente con dni: $dni" }
        val cliente= repository.delete(dni)
        if (cliente!=null) cache.remove(dni)
        else throw ClienteException.ClienteNoEliminadoException(dni.toString())

        return cliente
    }
}