package org.example.cliente.errors

import org.example.error.GeneralError

sealed class ClienteError(message:String): GeneralError(message)  {
    class NombreError(nombre:String):ClienteError("El nombre $nombre no es valido")
    class ClienteNoEncontradoError(dni: String):ClienteError("El cliente con dni: $dni no se escuentra")
    class ClienteNoActalizadoError(dni: String):ClienteError("El cliente con dni: $dni no se a actualizado")
    class ClienteNoEliminadoError(dni: String):ClienteError("El cliente con dni: $dni no se a eliminado")
}