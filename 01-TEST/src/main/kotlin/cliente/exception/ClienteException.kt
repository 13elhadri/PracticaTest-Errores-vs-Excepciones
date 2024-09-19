package org.example.cliente.exception

import org.example.dni.Dni

sealed class ClienteException(message:String):RuntimeException(message) {
   class NombreException(nombre:String):ClienteException("El nombre $nombre no es valido")
   class ClienteNoEncontradoException(dni: String):ClienteException("El cliente con dni: $dni no se escuentra")
   class ClienteNoActalizadoException(dni: String):ClienteException("El cliente con dni: $dni no se a actualizado")
   class ClienteNoEliminadoException(dni: String):ClienteException("El cliente con dni: $dni no se a eliminado")
}