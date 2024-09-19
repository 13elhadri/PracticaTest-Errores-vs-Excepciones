package org.example.banco.cuenta.errors

import org.example.error.GeneralError

sealed class CuentaError(message:String) : GeneralError(message) {
    class IbanIncorrectoError(iban:String):CuentaError("El IBAN $iban no es valido")
    class SaldoError(iban: String):CuentaError("El saldo es insuficiente de la cuenta $iban")
}