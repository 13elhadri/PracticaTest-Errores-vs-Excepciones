package org.example.dni.validator

import org.example.dni.Dni
import org.example.dni.exception.DniException
import org.lighthousegames.logging.logging

private val logger= logging()

class DniValidator {

    fun validarDni(dni: Dni):Dni{
        logger.debug { "Validando dni $dni" }
        if (!validarNumeroDni(dni.dni)) throw DniException.DniNoValidoException(dni.dni)

        return dni
    }


    fun validarNumeroDni(dni:String):Boolean{
        logger.debug { "Validando numero de dni $dni" }
        val dniRegex = Regex ("[0-9]{8}[A-Z]$")

        if (!dniRegex.matches(dni)) return false

        val numeros = dni.slice(0..7).toInt()
        val letra = dni.slice(8..8).uppercase()

        val letrasDni = "TRWAGMYFPDXBNJZSQVHLCKE"

        val resto = numeros % 23

        val letraCalculada = letrasDni[resto].toString()

        if(letra == letraCalculada) {
            return true
        } else {
            return false
        }
    }
}