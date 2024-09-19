package banco.cuenta.validator

import org.example.banco.cuenta.CuentaBancaria
import org.example.banco.cuenta.errors.CuentaError
import org.example.banco.cuenta.validator.CuentaBancariaValidator
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class CuentaBancariaValidatorTest {

    private val validador= CuentaBancariaValidator()

    @Test
    fun validarIbanValido() {
        val iban="ES91 2100 0418 4502 0005 1332"

        assertTrue(validador.validarIban(iban))

    }

    @Test
    fun validarIbanNoValido() {
        val iban="ES00 1234 5678 9123 4567 8901"

        assertFalse(validador.validarIban(iban))

    }

    @Test
    fun validarSaldo() {
        val saldo=500.0

        assertTrue(validador.validarSaldo(saldo))
    }


    @Test
    fun validarSaldoNegativo() {
        val saldo=-500.0

        assertFalse(validador.validarSaldo(saldo))
    }

    @Test
    fun validarCuenta() {
        val cuenta= CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0)

        assertEquals(validador.validarCuenta(cuenta).value,cuenta)
    }

    @Test
    fun validarCuentaIbanNoValido() {
        val cuenta= CuentaBancaria("ES00 1234 5678 9123 4567 8901",100.0)

        val result=validador.validarCuenta(cuenta).error

        assertTrue(result is CuentaError.IbanIncorrectoError)

    }

    @Test
    fun validarCuentaSaldoNoValido() {
        val cuenta= CuentaBancaria("ES91 2100 0418 4502 0005 1332",-100.0)

        val result=validador.validarCuenta(cuenta).error

        assertTrue(result is CuentaError.SaldoError)
    }

}