package org.example.cliente.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import org.example.cliente.errors.ClienteError
import org.example.cliente.model.Cliente
import org.example.cliente.repository.ClienteRepository
import org.example.cliente.services.cache.CacheCliente
import org.example.cliente.validator.ClienteValidator
import org.example.dni.Dni
import org.example.error.GeneralError
import org.lighthousegames.logging.logging

private val logger = logging()

class ClienteServicesImpl(
    private val repository: ClienteRepository,
    private val cache: CacheCliente,
    private val clienteValidator: ClienteValidator
):ClienteServices {
    override fun getAll(): Result<List<Cliente>, GeneralError> {
        logger.debug { "Obteniendo clientes del repositorio" }
        val clientes = repository.getAll()
        logger.info { "Clientes obtenidos: $clientes" }
        return Ok(clientes)
    }

    override fun getByDni(dni: Dni): Result<Cliente,GeneralError> {
        logger.debug { "Buscando por dni en cache" }
        return cache.get(dni)
                ?.let { Ok(it) }
                ?: repository.getByDni(dni)
                    ?.let { Ok(it) }
                    ?: Err(ClienteError.ClienteNoEncontradoError(dni.toString()))

    }

    override fun save(cliente: Cliente): Result<Cliente,GeneralError> {
        logger.debug { "Guardando cliente: $cliente" }

        return clienteValidator.validarCliente(cliente).mapBoth(
            success = {
                repository.save(cliente)
                logger.debug { "Guardando cliente en cache: $cliente" }
                cache.put(cliente.dni,cliente)
                Ok(cliente)
            },
            failure = {
                Err(it)
            }
        )
    }

    override fun update(dni: Dni, cliente: Cliente): Result<Cliente,GeneralError> {
        logger.debug { "Actualizando cliente con dni: $dni" }
       return clienteValidator.validarCliente(cliente).mapBoth(
            success = {
                repository.update(dni, cliente)?: return Err(ClienteError.ClienteNoActalizadoError(dni.toString()))
                cache.put(dni, cliente)
                Ok(it)
            }, failure = {
               Err(it)
            }
        )
    }

    override fun delete(dni: Dni): Result<Cliente,GeneralError> {
        logger.debug { "Borrando cliente con dni: $dni" }
        return repository.delete(dni)
            ?.let {
                cache.remove(dni)
                Ok(it)
            }
            ?: Err(ClienteError.ClienteNoEliminadoError(dni.dni))
    }
}