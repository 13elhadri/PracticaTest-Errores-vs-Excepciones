package org.example.cliente.repository

import org.example.cliente.model.Cliente
import org.example.dni.Dni
import org.lighthousegames.logging.logging

private val logger= logging()

class ClienteRepositoryImpl:ClienteRepository {

    private val db = hashMapOf<Dni, Cliente>()

    override fun getAll(): List<Cliente> {
        logger.debug { "Obteniendo todos los clientes" }

        logger.info { "Total de clientes: ${db.size}" }
        return db.values.toList()
    }

    override fun getByDni(dni: Dni): Cliente? {
        logger.debug { "Obteniendo cliente por dni: $dni" }

        val cliente = db[dni]
        if (cliente == null) {
            logger.warn { "Cliente con Dni: $dni no encontrada" }
        } else {
            logger.info { "Cliente encontrado: $cliente" }
        }
        return cliente
    }

    override fun save(cliente: Cliente): Cliente {
        logger.debug { "Guardando cliente: $cliente" }
        db[cliente.dni] = cliente
        logger.info { "Cliente guardada: $cliente" }
        return cliente
    }

    override fun update(dni: Dni, c: Cliente): Cliente? {
        logger.debug { "Actualizando cliente con Dni: $dni" }
        val cliente = db[dni]
        if (cliente == null) {
            logger.warn { "Cliente con Dni: $dni no encontrada" }
            return null
        } else {
            val clienteAct = cliente.copy(
                nombre = c.nombre,
                tarjeta = c.tarjeta,
                cuenta = c.cuenta
            )
            db[dni] = clienteAct
            logger.info { "Cliente actualizado: $clienteAct" }
            return clienteAct
        }
    }

    override fun delete(dni: Dni): Cliente? {
        logger.debug { "Borrando cliente con dni: $dni" }
        val cliente = db[dni]
        if (cliente == null) {
            logger.warn { "Cliente con dni: $dni no encontrado" }
            return null
        } else {
            db.remove(dni)
            logger.info { "Cliente borrado: $cliente" }
            return cliente
        }
    }

}