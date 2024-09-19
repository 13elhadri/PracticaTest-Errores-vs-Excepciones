package org.example.banco.tarjeta.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.banco.tarjeta.Tarjeta
import org.example.banco.tarjeta.errors.TarjetaError
import org.lighthousegames.logging.logging
import java.time.YearMonth
import java.time.format.DateTimeParseException

private val logger= logging()

class TarjetaValidator {


    fun validarTarjeta(tarjeta: Tarjeta): Result<Tarjeta,TarjetaError> {
        logger.debug { "Validando tarjeta $tarjeta" }

        if (!validarNumero(tarjeta.numero)) return Err( TarjetaError.NumeroError(tarjeta.numero))
        if (!validarCaducidad(tarjeta.fecCad)) return Err( TarjetaError.CaducidadError(tarjeta.fecCad))

        return Ok(tarjeta)
    }

    fun validarNumero(numero:String):Boolean{
        logger.debug { "Validando numero de tarjeta $numero" }
        val tarjetaFormateada = numero.replace(" ", "").replace("-", "")

        if (!tarjetaFormateada.matches(Regex("\\d+"))) return false

        var sumaTotal = 0
        var esSegundoDigito = false

        for (i in tarjetaFormateada.length - 1 downTo 0) {
            var digito = tarjetaFormateada[i].toString().toInt()

            if (esSegundoDigito) {
                digito *= 2
                if (digito > 9) {
                    digito -= 9
                }
            }
            sumaTotal += digito
            esSegundoDigito = !esSegundoDigito
        }

        return sumaTotal % 10 == 0
    }

    fun validarCaducidad(fecha:String):Boolean{
        logger.debug { "Validando fecha de caducidad $fecha" }

        if (!fecha.matches(Regex("^\\d{2}/\\d{2}$"))) return false

        val partes = fecha.split("/")
        val mes = partes[0].toIntOrNull()
        val ano = partes[1].toIntOrNull()

        if (mes == null || ano == null || mes !in 1..12) return false

        val fechaCaducidad: YearMonth = try {
            YearMonth.of(2000 + ano, mes)
        } catch (e: DateTimeParseException) {
            return false
        }

        val fechaActual = YearMonth.now()

        return !fechaCaducidad.isBefore(fechaActual)
    }
}