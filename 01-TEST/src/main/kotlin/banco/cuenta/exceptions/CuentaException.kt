package org.example.banco.cuenta.exceptions

import java.lang.Exception

sealed class CuentaException(message:String):RuntimeException(message) {
    class IbanIncorrectoException(iban:String):CuentaException("El IBAN $iban no es valido")
    class SaldoException(iban: String):CuentaException("El saldo es insuficiente de la cuenta $iban")
}