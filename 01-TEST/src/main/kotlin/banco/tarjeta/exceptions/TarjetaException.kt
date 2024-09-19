package org.example.banco.tarjeta.exceptions

import java.lang.Exception

sealed class TarjetaException(message:String):RuntimeException(message) {
    class NumeroExcepcion(numero:String):TarjetaException("Numero de tarjeta: $numero no valido")
    class CaducidadException(cad:String):TarjetaException("Fecha de caducidad no valida $cad")
}