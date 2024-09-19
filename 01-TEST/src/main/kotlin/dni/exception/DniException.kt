package org.example.dni.exception

sealed class DniException(message:String):RuntimeException(message) {
    class DniNoValidoException(dni:String):DniException("El DNI $dni no es valido")
}