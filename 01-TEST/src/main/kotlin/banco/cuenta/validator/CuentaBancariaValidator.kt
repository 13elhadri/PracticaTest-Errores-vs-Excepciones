package org.example.banco.cuenta.validator

import org.example.banco.cuenta.CuentaBancaria
import org.example.banco.cuenta.exceptions.CuentaException
import org.lighthousegames.logging.logging
import java.math.BigInteger

private val logger=logging()

class CuentaBancariaValidator {

    fun validarCuenta(cuenta:CuentaBancaria):CuentaBancaria{
        logger.debug { "Validando cuenta ${cuenta.iban}" }
        if (!validarIban(cuenta.iban)) throw CuentaException.IbanIncorrectoException(cuenta.iban)
        if (!validarSaldo(cuenta.saldo)) throw CuentaException.SaldoException(cuenta.iban)

        return cuenta
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