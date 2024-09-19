package banco.tarjeta.validators

import org.example.banco.tarjeta.Tarjeta
import org.example.banco.tarjeta.errors.TarjetaError
import org.example.banco.tarjeta.validators.TarjetaValidator
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class TarjetaValidatorTest {

    private val validador= TarjetaValidator()

    @Test
    fun validarNumeroValido() {
        val numero="4539 1488 0343 6467"

        assertTrue(validador.validarNumero(numero))
    }

    @Test
    fun validarNumeroNoValido() {
        val numero="4539 1488 0343"

        assertFalse(validador.validarNumero(numero))
    }

    @Test
    fun validarCaducidadValida() {
        val fecha="01/25"

        assertTrue(validador.validarCaducidad(fecha))
    }
    @Test
    fun validarCaducidadAñoNoValido() {
        val fecha="01/23"

        assertFalse(validador.validarCaducidad(fecha))
    }

    @Test
    fun validarCaducidadMesNoValido() {
        val fecha="17/25"

        assertFalse(validador.validarCaducidad(fecha))
    }
    @Test
    fun validarTarjetaValida() {
        val tarjeta= Tarjeta("4539 1488 0343 6467","12/25")

        assertEquals(validador.validarTarjeta(tarjeta).value,tarjeta)
    }

    @Test
    fun validarTarjetaNoValidoNumero() {
        val tarjeta= Tarjeta("4539 1488 0343 ","12/25")

        val result=validador.validarTarjeta(tarjeta).error

        assertTrue(result is TarjetaError.NumeroError)
    }

    @Test
    fun validarTarjetaNoValidaCaducidad() {
        val tarjeta= Tarjeta("4539 1488 0343 6467","17/20")

        val result=validador.validarTarjeta(tarjeta).error

        assertTrue(result is TarjetaError.CaducidadError)
    }
}