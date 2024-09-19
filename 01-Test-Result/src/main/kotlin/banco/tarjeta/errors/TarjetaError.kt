package org.example.banco.tarjeta.errors

import org.example.error.GeneralError

sealed class TarjetaError (message:String): GeneralError(message)  {
    class NumeroError(numero:String):TarjetaError("Numero de tarjeta: $numero no valido")
    class CaducidadError(cad:String):TarjetaError("Fecha de caducidad no valida $cad")
}