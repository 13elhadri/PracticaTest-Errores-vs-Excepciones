package cliente.services


import org.example.banco.cuenta.CuentaBancaria
import org.example.banco.cuenta.exceptions.CuentaException
import org.example.banco.cuenta.validator.CuentaBancariaValidator
import org.example.banco.tarjeta.Tarjeta
import org.example.banco.tarjeta.exceptions.TarjetaException
import org.example.banco.tarjeta.validator.TarjetaValidator
import org.example.cliente.exception.ClienteException
import org.example.cliente.model.Cliente
import org.example.cliente.repository.ClienteRepository
import org.example.cliente.repository.ClienteRepositoryImpl
import org.example.cliente.services.ClienteServices
import org.example.cliente.services.ClienteServicesImpl
import org.example.cliente.services.cache.CacheCliente
import org.example.cliente.validator.ClienteValidator
import org.example.dni.Dni
import org.example.dni.exception.DniException
import org.example.dni.validator.DniValidator
import org.junit.jupiter.api.Assertions.assertEquals


import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doThrow
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
        dni =Dni("04246431X")
    )
        val cliente2= Cliente(
            id = UUID.fromString("9f4b28b2-69c6-43a4-8c2b-8cb6046e2d27"),
            nombre = "Test2",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni =Dni("04246432B")
        )
        val clientes= listOf(cliente,cliente2)

        whenever(repository.getAll()).thenReturn(clientes)

        val result = services.getAll()

        assertAll(
            { assertEquals(2, result.size)},
            { assertEquals("Test1", result[0].nombre)},
            { assertEquals("Test2", result[1].nombre)}
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
            dni =Dni("04246432B")
        )
        whenever(cacheCliente.get(cliente.dni)).thenReturn(cliente)

        val result = services.getByDni(cliente.dni)

        assertAll(
            { assertEquals("Test1", result.nombre)},
            { assertEquals("04246432B", result.dni.dni)}
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
            dni =Dni("04246432B")
        )
        whenever(cacheCliente.get(cliente.dni)).thenReturn(null)
        whenever(repository.getByDni(cliente.dni)).thenReturn(cliente)

        val result = services.getByDni(cliente.dni)

        assertAll(
            { assertEquals("Test1", result.nombre)},
            { assertEquals("04246432B", result.dni.dni)}
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
            dni =Dni("04246432B")
        )
        whenever(cacheCliente.get(cliente.dni)).thenReturn(null)
        whenever(repository.getByDni(cliente.dni)).thenReturn(null)

        assertThrows<ClienteException.ClienteNoEncontradoException> {
            services.getByDni(cliente.dni)
        }

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
            dni =Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(cliente)
        whenever(repository.save(cliente)).thenReturn(cliente)
        doNothing().`when`(cacheCliente).put(cliente.dni,cliente)

        val result = services.save(cliente)

        assertAll(
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
            dni =Dni("04246432B")
        )

        //whenever(clienteValidator.validarNombre(cliente.nombre)).thenReturn(false)
        doThrow(ClienteException.NombreException(cliente.nombre)).`when`(clienteValidator).validarCliente(cliente)


        assertThrows<ClienteException.NombreException> {
            services.save(cliente)
        }


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
            dni =Dni("04246431b")
        )


        doThrow(DniException.DniNoValidoException(cliente.dni.dni)).`when`(clienteValidator).validarCliente(cliente)

        assertThrows<DniException.DniNoValidoException> {
            services.save(cliente)
        }


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
            dni =Dni("04246431X")
        )


        doThrow(CuentaException.IbanIncorrectoException(cliente.cuenta.iban)).`when`(clienteValidator).validarCliente(cliente)

        assertThrows<CuentaException.IbanIncorrectoException> {
            services.save(cliente)
        }


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
            dni =Dni("04246431X")
        )


        doThrow(CuentaException.SaldoException(cliente.cuenta.iban)).`when`(clienteValidator).validarCliente(cliente)

        assertThrows<CuentaException.SaldoException> {
            services.save(cliente)
        }


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
            dni =Dni("04246431X")
        )


        doThrow(TarjetaException.NumeroExcepcion(cliente.tarjeta.numero)).`when`(clienteValidator).validarCliente(cliente)

        assertThrows<TarjetaException.NumeroExcepcion> {
            services.save(cliente)
        }


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
            dni =Dni("04246431X")
        )


        doThrow(TarjetaException.CaducidadException(cliente.tarjeta.fecCad)).`when`(clienteValidator).validarCliente(cliente)

        assertThrows<TarjetaException.CaducidadException> {
            services.save(cliente)
        }


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
            dni =Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(cliente)
        whenever(repository.update(cliente.dni,cliente)).thenReturn(cliente)
        doNothing().`when`(cacheCliente).put(cliente.dni,cliente)

        val result = services.update(cliente.dni,cliente)

        assertAll(
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
            dni =Dni("04246432B")
        )

        whenever(clienteValidator.validarCliente(cliente)).thenReturn(cliente)
        whenever(repository.update(cliente.dni,cliente)).thenReturn(null)



        assertThrows<ClienteException.ClienteNoActalizadoException> {
            services.update(cliente.dni,cliente)
        }

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
            dni =Dni("04246432B")
        )

        doThrow(ClienteException.NombreException(cliente.nombre)).`when`(clienteValidator).validarCliente(cliente)


        assertThrows<ClienteException.NombreException> {
            services.update(cliente.dni,cliente)
        }

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
            dni =Dni("04246432B")
        )

        doThrow(CuentaException.IbanIncorrectoException(cliente.cuenta.iban)).`when`(clienteValidator).validarCliente(cliente)

        assertThrows<CuentaException.IbanIncorrectoException> {
            services.update(cliente.dni,cliente)
        }

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
            dni =Dni("04246432B")
        )

        doThrow(CuentaException.SaldoException(cliente.cuenta.iban)).`when`(clienteValidator).validarCliente(cliente)

        assertThrows<CuentaException.SaldoException> {
            services.update(cliente.dni,cliente)
        }


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
            dni =Dni("04246432B")
        )

        doThrow(TarjetaException.CaducidadException(cliente.tarjeta.fecCad)).`when`(clienteValidator).validarCliente(cliente)

        assertThrows<TarjetaException.CaducidadException> {
            services.update(cliente.dni,cliente)
        }


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
            dni =Dni("04246432B")
        )

        doThrow(TarjetaException.NumeroExcepcion(cliente.tarjeta.numero)).`when`(clienteValidator).validarCliente(cliente)

        assertThrows<TarjetaException.NumeroExcepcion> {
            services.update(cliente.dni,cliente)
        }

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
            dni =Dni("04246431B")
        )

        doThrow(DniException.DniNoValidoException(cliente.dni.dni)).`when`(clienteValidator).validarCliente(cliente)

        assertThrows<DniException.DniNoValidoException> {
            services.update(cliente.dni,cliente)
        }

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
            dni =Dni("04246432B")
        )


        whenever(repository.delete(cliente.dni)).thenReturn(cliente)
        doNothing().`when`(cacheCliente).remove(cliente.dni)

        val result = services.delete(cliente.dni)

        assertAll(
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
            dni =Dni("04246432B")
        )


        whenever(repository.delete(cliente.dni)).thenReturn(null)


        assertThrows<ClienteException.ClienteNoEliminadoException> {
            services.delete(cliente.dni)
        }


        verify(repository, times(1)).delete(cliente.dni)
        verify(cacheCliente, times(0)).remove(cliente.dni)
    }
}