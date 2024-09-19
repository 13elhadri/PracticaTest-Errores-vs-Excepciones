package dni.validator

import org.example.dni.Dni
import org.example.dni.exception.DniException
import org.example.dni.validator.DniValidator
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class DniValidatorTest {

    private val validador=DniValidator()

    @Test
    fun validarNumeroDniValido() {
        val dni="04246431X"

        assertTrue(validador.validarNumeroDni(dni))
    }

    @Test
    fun validarNumeroDniNoValido() {
        val dni="04246431B"

        assertFalse(validador.validarNumeroDni(dni))
    }

    @Test
    fun validarDniValido() {
        val dni=Dni("04246431X")

        assertEquals(validador.validarDni(dni),dni)
    }
    @Test
    fun validarDniNoValido() {
        val dni=Dni("04246431B")

        assertThrows<DniException.DniNoValidoException>{
            validador.validarDni(dni)
        }
    }
}