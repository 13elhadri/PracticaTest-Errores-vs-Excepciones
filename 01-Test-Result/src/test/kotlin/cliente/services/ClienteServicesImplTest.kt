package cliente.services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.example.banco.cuenta.CuentaBancaria
import org.example.banco.cuenta.errors.CuentaError
import org.example.banco.tarjeta.Tarjeta
import org.example.banco.tarjeta.errors.TarjetaError
import org.example.cliente.errors.ClienteError
import org.example.cliente.model.Cliente
import org.example.cliente.repository.ClienteRepositoryImpl
import org.example.cliente.services.ClienteServicesImpl
import org.example.cliente.services.cache.CacheCliente
import org.example.cliente.validator.ClienteValidator
import org.example.dni.Dni
import org.example.dni.errors.DniError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class ClienteServicesImplTest {


    @Mock
    lateinit var repository: ClienteRepositoryImpl
    @Mock
    lateinit var cacheCliente: CacheCliente
    @Mock
    lateinit var clienteValidator: ClienteValidator
    @InjectMocks
    lateinit var services: ClienteServicesImpl




    @Test
    fun getAll() {
        val cliente= Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246431X")
        )
        val cliente2= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test2",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )
        val clientes= listOf(cliente,cliente2)

        whenever(repository.getAll()).thenReturn(clientes)

        val result = services.getAll().value

        org.junit.jupiter.api.assertAll(
            { assertEquals(2, result.size) },
            { assertEquals("Test1", result[0].nombre) },
            { assertEquals("Test2", result[1].nombre) }
        )
        verify(repository, times(1)).getAll()
    }

    @Test
    fun getByDniInCache() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )
        whenever(cacheCliente.get(cliente.dni)).thenReturn(cliente)

        val result = services.getByDni(cliente.dni).value

        org.junit.jupiter.api.assertAll(
            { assertEquals("Test1", result.nombre) },
            { assertEquals("04246432B", result.dni.dni) }
        )

        verify(cacheCliente, times(1)).get(cliente.dni)
        verify(repository, times(0)).getByDni(cliente.dni)

    }

    @Test
    fun getByDniInRepo() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )
        whenever(cacheCliente.get(cliente.dni)).thenReturn(null)
        whenever(repository.getByDni(cliente.dni)).thenReturn(cliente)

        val result = services.getByDni(cliente.dni).value

        org.junit.jupiter.api.assertAll(
            { assertEquals("Test1", result.nombre) },
            { assertEquals("04246432B", result.dni.dni) }
        )

        verify(cacheCliente, times(1)).get(cliente.dni)
        verify(repository, times(1)).getByDni(cliente.dni)

    }

    @Test
    fun getByDniException() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )
        whenever(cacheCliente.get(cliente.dni)).thenReturn(null)
        whenever(repository.getByDni(cliente.dni)).thenReturn(null)


        val result=services.getByDni(cliente.dni).error

        assertTrue(result is ClienteError.ClienteNoEncontradoError)

        verify(cacheCliente, times(1)).get(cliente.dni)
        verify(repository, times(1)).getByDni(cliente.dni)

    }

    @Test
    fun saveOk() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Ok(cliente))
        whenever(repository.save(cliente)).thenReturn(cliente)
        doNothing().`when`(cacheCliente).put(cliente.dni,cliente)

        val result = services.save(cliente).value

        org.junit.jupiter.api.assertAll(
            { assert(result.nombre == cliente.nombre) },
            { assert(result.dni == cliente.dni) },
            { assert(result.tarjeta == cliente.tarjeta) },
            { assert(result.cuenta == cliente.cuenta) }
        )

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(cacheCliente, times(1)).put(cliente.dni,cliente)
        verify(repository, times(1)).save(cliente)

    }

    @Test
    fun saveNombreNoValido() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Te",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(ClienteError.NombreError(cliente.nombre)))


        val result=services.save(cliente).error
        assertTrue(result is ClienteError.NombreError)

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)
        verify(repository, times(0)).save(cliente)

    }

    @Test
    fun saveDniNoValido() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246431b")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(DniError.DniNoValidoError(cliente.dni.dni)))


        val result=services.save(cliente).error
        assertTrue(result is DniError.DniNoValidoError)

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)
        verify(repository, times(0)).save(cliente)

    }

    @Test
    fun saveIbanNoValido() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246431X")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(CuentaError.IbanIncorrectoError(cliente.cuenta.iban)))

        val result=services.save(cliente).error
        assertTrue(result is CuentaError.IbanIncorrectoError)

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)
        verify(repository, times(0)).save(cliente)

    }

    @Test
    fun saveSaldoInsu() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",-100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246431X")
        )


        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(CuentaError.SaldoError(cliente.cuenta.iban)))

        val result=services.save(cliente).error
        assertTrue(result is CuentaError.SaldoError)

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)
        verify(repository, times(0)).save(cliente)

    }

    @Test
    fun saveTarjetaNumNoValido() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343","12/25"),
            dni = Dni("04246431X")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(TarjetaError.NumeroError(cliente.cuenta.iban)))

        val result=services.save(cliente).error
        assertTrue(result is TarjetaError.NumeroError)

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)
        verify(repository, times(0)).save(cliente)

    }

    @Test
    fun saveCadNoValida() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/20"),
            dni = Dni("04246431X")
        )


        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(TarjetaError.CaducidadError(cliente.cuenta.iban)))

        val result=services.save(cliente).error
        assertTrue(result is TarjetaError.CaducidadError)

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)
        verify(repository, times(0)).save(cliente)

    }



    @Test
    fun updateOk() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Ok(cliente))
        whenever(repository.update(cliente.dni,cliente)).thenReturn(cliente)
        doNothing().`when`(cacheCliente).put(cliente.dni,cliente)

        val result = services.update(cliente.dni,cliente).value

        org.junit.jupiter.api.assertAll(
            { assert(result.nombre == cliente.nombre) },
            { assert(result.dni == cliente.dni) },
            { assert(result.tarjeta == cliente.tarjeta) },
            { assert(result.cuenta == cliente.cuenta) }
        )

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(repository, times(1)).update(cliente.dni,cliente)
        verify(cacheCliente, times(1)).put(cliente.dni,cliente)

    }

    @Test
    fun updateRepoException() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Ok(cliente))
        whenever(repository.update(cliente.dni,cliente)).thenReturn(null)


        val result=services.update(cliente.dni, cliente).error
        assertTrue(result is ClienteError.ClienteNoActalizadoError)

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(repository, times(1)).update(cliente.dni,cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)

    }

    @Test
    fun updateNombreNoValido() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "T",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(ClienteError.NombreError(cliente.cuenta.iban)))

        val result= services.update(cliente.dni, cliente).error
        assertTrue(result is ClienteError.NombreError)

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(repository, times(0)).update(cliente.dni,cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)

    }

    @Test
    fun updateIbanNoValido() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(CuentaError.IbanIncorrectoError(cliente.cuenta.iban)))

        val result= services.update(cliente.dni, cliente).error
        assertTrue(result is CuentaError.IbanIncorrectoError)

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(repository, times(0)).update(cliente.dni,cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)

    }
    @Test
    fun updateSaldoInsu() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",-100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(CuentaError.SaldoError(cliente.cuenta.iban)))

        val result= services.update(cliente.dni, cliente).error
        assertTrue(result is CuentaError.SaldoError)


        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(repository, times(0)).update(cliente.dni,cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)

    }

    @Test
    fun updateCadNoValido() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/20"),
            dni = Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(TarjetaError.CaducidadError(cliente.cuenta.iban)))

        val result= services.update(cliente.dni, cliente).error
        assertTrue(result is TarjetaError.CaducidadError)


        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(repository, times(0)).update(cliente.dni,cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)

    }

    @Test
    fun updateTarjetaNumNoValido() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343","12/25"),
            dni = Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(TarjetaError.NumeroError(cliente.cuenta.iban)))

        val result= services.update(cliente.dni, cliente).error
        assertTrue(result is TarjetaError.NumeroError)

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(repository, times(0)).update(cliente.dni,cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)

    }

    @Test
    fun updateDniNoValido() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246431B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(Err(DniError.DniNoValidoError(cliente.cuenta.iban)))

        val result= services.update(cliente.dni, cliente).error
        assertTrue(result is DniError.DniNoValidoError)

        verify(clienteValidator, times(1)).validarCliente(cliente)
        verify(repository, times(0)).update(cliente.dni,cliente)
        verify(cacheCliente, times(0)).put(cliente.dni,cliente)

    }

    @Test
    fun deleteOk() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )


        whenever(repository.delete(cliente.dni)).thenReturn(cliente)
        doNothing().`when`(cacheCliente).remove(cliente.dni)

        val result = services.delete(cliente.dni).value

        org.junit.jupiter.api.assertAll(
            { assert(result.nombre == cliente.nombre) },
            { assert(result.dni == cliente.dni) },
            { assert(result.tarjeta == cliente.tarjeta) },
            { assert(result.cuenta == cliente.cuenta) }
        )


        verify(repository, times(1)).delete(cliente.dni)
        verify(cacheCliente, times(1)).remove(cliente.dni)
    }

    @Test
    fun deleteException() {
        val cliente= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246432B")
        )


        whenever(repository.delete(cliente.dni)).thenReturn(null)

        val result= services.delete(cliente.dni).error
        assertTrue(result is ClienteError.ClienteNoEliminadoError)


        verify(repository, times(1)).delete(cliente.dni)
        verify(cacheCliente, times(0)).remove(cliente.dni)
    }
}