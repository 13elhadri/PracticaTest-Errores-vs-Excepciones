package org.example.dni.errors

import org.example.cliente.errors.ClienteError
import org.example.error.GeneralError

sealed class DniError(message:String):GeneralError(message) {
    class DniNoValidoError(dni:String):DniError("El DNI $dni no es valido")
}