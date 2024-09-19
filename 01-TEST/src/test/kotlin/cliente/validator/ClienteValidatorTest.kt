package cliente.validator

import org.example.banco.cuenta.CuentaBancaria
import org.example.banco.cuenta.exceptions.CuentaException
import org.example.banco.cuenta.validator.CuentaBancariaValidator
import org.example.banco.tarjeta.Tarjeta
import org.example.banco.tarjeta.exceptions.TarjetaException
import org.example.banco.tarjeta.validator.TarjetaValidator
import org.example.cliente.exception.ClienteException
import org.example.cliente.model.Cliente
import org.example.cliente.validator.ClienteValidator
import org.example.dni.Dni
import org.example.dni.exception.DniException
import org.example.dni.validator.DniValidator
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class ClienteValidatorTest {

    @Mock
    lateinit var tarjetaValidator: TarjetaValidator
    @Mock
    lateinit var cuentaBancariaValidator: CuentaBancariaValidator
    @Mock
    lateinit var dniValidator: DniValidator
    @InjectMocks
    lateinit var clienteValidator: ClienteValidator


    @Test
    fun validarNombreOk() {
        val nombre="Test1"

        assertTrue(clienteValidator.validarNombre(nombre))
    }
    @Test
    fun validarNombreInvalido() {
        val nombre="T"

        assertFalse(clienteValidator.validarNombre(nombre))
    }


    @Test
    fun validarClienteOk() {
        val cuenta=CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0)
        val tarjeta=Tarjeta("4539 1488 0343 6467","12/25")
        val dni=Dni("04246431X")
        val nombre="Test1"
        val cliente= Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = nombre,
            cuenta = cuenta,
            tarjeta = tarjeta,
            dni = dni
        )


        whenever(tarjetaValidator.validarTarjeta(tarjeta)).thenReturn(tarjeta);

        whenever(cuentaBancariaValidator.validarCuenta(cuenta)).thenReturn(cuenta);

        whenever(dniValidator.validarDni(dni)).thenReturn(dni);

        val result= clienteValidator.validarCliente(cliente)

        assertAll(
            { assert(result.nombre == cliente.nombre) },
            { assert(result.dni == cliente.dni) },
            { assert(result.tarjeta == cliente.tarjeta) },
            { assert(result.cuenta == cliente.cuenta) }
        )


        verify(tarjetaValidator, times(1)).validarTarjeta(tarjeta)
        verify(cuentaBancariaValidator, times(1)).validarCuenta(cuenta)
        verify(dniValidator, times(1)).validarDni(dni)
    }

    @Test
    fun validarClienteNombreNoValido() {
        val cuenta=CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0)
        val tarjeta=Tarjeta("4539 1488 0343 6467","12/25")
        val dni=Dni("04246431X")
        val nombre="Te"
        val cliente= Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = nombre,
            cuenta = cuenta,
            tarjeta = tarjeta,
            dni = dni
        )



        assertThrows<ClienteException.NombreException> {
            clienteValidator.validarCliente(cliente)
        }


    }

    @Test
    fun validarClienteIbanNoValido() {
        val cuenta=CuentaBancaria("ES00 1234 5678 9123 4567 8901",100.0)
        val tarjeta=Tarjeta("4539 1488 0343 6467","12/25")
        val dni=Dni("04246431X")
        val nombre="Test1"
        val cliente= Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = nombre,
            cuenta = cuenta,
            tarjeta = tarjeta,
            dni = dni
        )


        whenever(tarjetaValidator.validarTarjeta(tarjeta)).thenReturn(tarjeta);

        //whenever(cuentaBancariaValidator.validarCuenta(cuenta)).thenThrow(CuentaException.IbanIncorrectoException(cuenta.iban));
        doThrow(CuentaException.IbanIncorrectoException(cuenta.iban)).`when`(cuentaBancariaValidator).validarCuenta(cuenta)



       assertThrows<CuentaException.IbanIncorrectoException> {
            clienteValidator.validarCliente(cliente)
        }


        verify(tarjetaValidator, times(1)).validarTarjeta(tarjeta)
        verify(cuentaBancariaValidator, times(1)).validarCuenta(cuenta)
        verify(dniValidator, times(0)).validarDni(dni)
    }

    @Test
    fun validarClienteSaldoNoValido() {
        val cuenta=CuentaBancaria("ES91 2100 0418 4502 0005 1332",-100.0)
        val tarjeta=Tarjeta("4539 1488 0343 6467","12/25")
        val dni=Dni("04246431X")
        val nombre="Test1"
        val cliente= Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = nombre,
            cuenta = cuenta,
            tarjeta = tarjeta,
            dni = dni
        )



        whenever(tarjetaValidator.validarTarjeta(tarjeta)).thenReturn(tarjeta);

        whenever(cuentaBancariaValidator.validarCuenta(cuenta)).thenThrow(CuentaException.SaldoException(cuenta.iban));




        assertThrows<CuentaException.SaldoException> {
            clienteValidator.validarCliente(cliente)
        }



        verify(tarjetaValidator, times(1)).validarTarjeta(tarjeta)
        verify(cuentaBancariaValidator, times(1)).validarCuenta(cuenta)
        verify(dniValidator, times(0)).validarDni(dni)
    }


    @Test
    fun validarClienteDniNoValido() {
        val cuenta=CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0)
        val tarjeta=Tarjeta("4539 1488 0343 6467","12/25")
        val dni=Dni("04246432X")
        val nombre="Test1"
        val cliente= Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = nombre,
            cuenta = cuenta,
            tarjeta = tarjeta,
            dni = dni
        )


        whenever(tarjetaValidator.validarTarjeta(tarjeta)).thenReturn(tarjeta);

        whenever(cuentaBancariaValidator.validarCuenta(cuenta)).thenReturn(cuenta);

        whenever(dniValidator.validarDni(dni)).thenThrow(DniException.DniNoValidoException(dni.dni));


        assertThrows<DniException.DniNoValidoException> {
            clienteValidator.validarCliente(cliente)
        }

        verify(tarjetaValidator, times(1)).validarTarjeta(tarjeta)
        verify(cuentaBancariaValidator, times(1)).validarCuenta(cuenta)
        verify(dniValidator, times(1)).validarDni(dni)
    }

    @Test
    fun validarClienteTarjetaNumNoValido() {
        val cuenta=CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0)
        val tarjeta=Tarjeta("4539 1488 0343 ","12/25")
        val dni=Dni("04246431X")
        val nombre="Test1"
        val cliente= Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = nombre,
            cuenta = cuenta,
            tarjeta = tarjeta,
            dni = dni
        )

        whenever(tarjetaValidator.validarTarjeta(tarjeta)).thenThrow(TarjetaException.NumeroExcepcion(tarjeta.numero));



        assertThrows<TarjetaException.NumeroExcepcion> {
            clienteValidator.validarCliente(cliente)
        }

        verify(tarjetaValidator, times(1)).validarTarjeta(tarjeta)
        verify(cuentaBancariaValidator, times(0)).validarCuenta(cuenta)
        verify(dniValidator, times(0)).validarDni(dni)
    }

    @Test
    fun validarClienteTarjetaCadNoValida() {
        val cuenta=CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0)
        val tarjeta=Tarjeta("4539 1488 0343 6467","17/20")
        val dni=Dni("04246431X")
        val nombre="Test1"
        val cliente= Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = nombre,
            cuenta = cuenta,
            tarjeta = tarjeta,
            dni = dni
        )


        whenever(tarjetaValidator.validarTarjeta(tarjeta)).thenThrow(TarjetaException.CaducidadException(tarjeta.numero));



        assertThrows<TarjetaException.CaducidadException> {
            clienteValidator.validarCliente(cliente)
        }

        verify(tarjetaValidator, times(1)).validarTarjeta(tarjeta)
        verify(cuentaBancariaValidator, times(0)).validarCuenta(cuenta)
        verify(dniValidator, times(0)).validarDni(dni)
    }
}