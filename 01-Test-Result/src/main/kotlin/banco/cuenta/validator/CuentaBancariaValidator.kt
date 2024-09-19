package org.example.banco.cuenta.validator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.banco.cuenta.CuentaBancaria
import org.example.banco.cuenta.errors.CuentaError
import org.lighthousegames.logging.logging
import java.math.BigInteger

private val logger= logging()

class CuentaBancariaValidator {

    fun validarCuenta(cuenta: CuentaBancaria): Result<CuentaBancaria,CuentaError>{
        logger.debug { "Validando cuenta ${cuenta.iban}" }
        if (!validarIban(cuenta.iban)) return Err( CuentaError.IbanIncorrectoError(cuenta.iban))
        if (!validarSaldo(cuenta.saldo))  return Err( CuentaError.SaldoError(cuenta.iban))

        return Ok( cuenta)
    }
    fun validarIban(iban:String): Boolean {
        logger.debug { "Validando iban $iban" }
        val ibanFormateado = iban.replace(" ", "").uppercase()

        val ibanReordenado = ibanFormateado.substring(4) + ibanFormateado.substring(0, 4)

        val ibanNumerico = StringBuilder()
        for (char in ibanReordenado) {
            ibanNumerico.append(
                if (char.isDigit()) char else (char.code - 'A'.code + 10).toString()
            )
        }
        val modulo = ibanNumerico.toString().toBigInteger().mod(BigInteger("97"))

        return modulo == BigInteger.ONE
    }
   fun validarSaldo(saldo:Double):Boolean{
        logger.debug { "Validando saldo" }
        return saldo>=0.0
    }
}