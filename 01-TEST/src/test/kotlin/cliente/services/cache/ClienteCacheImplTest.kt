package cliente.services.cache

import org.example.banco.cuenta.CuentaBancaria
import org.example.banco.tarjeta.Tarjeta
import org.example.cliente.model.Cliente
import org.example.cliente.services.cache.ClienteCacheImpl
import org.example.dni.Dni
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*

class ClienteCacheImplTest {

    private val cache= ClienteCacheImpl()

    @BeforeEach
    fun setUp() {
        cache.clear()
    }


    @Test
    fun getOk() {
        val cliente = Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246431X")
        )

        cache.put(cliente.dni, cliente)
        val result = cache.get(cliente.dni)

        assertAll(
            { assertEquals(cache.size(), 1) },
            { assertNotNull(result) },
            { assertEquals(cliente, result) }
        )
    }

    @Test
    fun getNoEncontrado() {
       val dni=Dni("04246431X")

        assertNull(cache.get(dni))
    }

    @Test
    fun putOk() {

        val cliente = Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246431X")
        )

        cache.put(cliente.dni, cliente)
        val result = cache.get(cliente.dni)


        assertAll(
            { assertEquals(cache.size(), 1) },
            { assertNotNull(result) },
            { assertEquals(cliente, result) }
        )
    }


    @Test
    fun remove() {
        val cliente = Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246431X")
        )
        cache.put(cliente.dni, cliente)
        cache.remove(cliente.dni)
        val result = cache.get(cliente.dni)

        assertAll(
            { assertEquals(cache.size(), 0) },
            { assertNull(result) }
        )
    }

    @Test
    fun clear() {
        val cliente = Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246431X")
        )

        cache.put(cliente.dni, cliente)
        cache.clear()

        assertEquals(cache.size(), 0)
    }

    @Test
    fun size() {
        val cliente = Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = "Test1",
            cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
            tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
            dni = Dni("04246431X")
        )

        cache.put(cliente.dni, cliente)

        assertEquals(cache.size(), 1)
    }
}