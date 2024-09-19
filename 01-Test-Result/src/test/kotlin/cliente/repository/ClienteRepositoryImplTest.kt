package cliente.repository

import org.example.banco.cuenta.CuentaBancaria
import org.example.banco.tarjeta.Tarjeta
import org.example.cliente.model.Cliente
import org.example.cliente.repository.ClienteRepositoryImpl
import org.example.dni.Dni
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ClienteRepositoryImplTest {

    private var repository= ClienteRepositoryImpl()

    @BeforeEach
    fun setUp() {
        repository= ClienteRepositoryImpl()

        repository.save(
            cliente = Cliente(
                id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
                nombre = "Test1",
                cuenta = CuentaBancaria("ES91 2100 0418 4502 0005 1332",100.0),
                tarjeta = Tarjeta("4539 1488 0343 6467","12/25"),
                dni = Dni("04246431X")
            )
        )
    }

    @AfterEach
    fun tearDown() {
        repository= ClienteRepositoryImpl()
    }

    @Test
    fun getAll() {

        val result = repository.getAll()

        assertAll(
            { assert(result.size == 1) },
            { assert(result[0].nombre == "Test1") },
            { assert(result[0].dni.dni == "04246431X") },
            { assert(result[0].tarjeta.numero == "4539 1488 0343 6467") },
            { assert(result[0].cuenta.iban == "ES91 2100 0418 4502 0005 1332") }
        )
    }

    @Test
    fun getByDni() {
        val dni = Dni("04246431X")

        val cliente= repository.getByDni(dni)!!

        assertAll(
            { assert(cliente.nombre == "Test1") },
            { assert(cliente.dni.dni == "04246431X") },
            { assert(cliente.tarjeta.numero == "4539 1488 0343 6467") },
            { assert(cliente.cuenta.iban == "ES91 2100 0418 4502 0005 1332") }
        )
    }

    @Test
    fun getByDniNoEncontrado() {
        val dni = Dni("04246431B")


        assertNull( repository.getByDni(dni))
    }

    @Test
    fun saveOk() {
        val cliente= Cliente(
            id = UUID.fromString("d4e4a832-1d4b-4d1a-b1ea-8d2d9d4e54b7"),
            nombre = "Test2",
            cuenta = CuentaBancaria("DE89 3704 0044 0532 0130 00",200.0),
            tarjeta = Tarjeta("3782 822463 10005","12/25"),
            dni = Dni("04246432B")
        )

        val result =repository.save(cliente)

        assertAll(
            { assert(result.nombre == "Test2") },
            { assert(result.dni.dni == "04246432B") },
            { assert(result.tarjeta.numero == "3782 822463 10005") },
            { assert(result.cuenta.iban == "DE89 3704 0044 0532 0130 00") }
        )
    }

    @Test
    fun updateClienteValido() {
        val dni= Dni("04246431X")
        val cliente= Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = "Test2",
            cuenta = CuentaBancaria("DE89 3704 0044 0532 0130 00",200.0),
            tarjeta = Tarjeta("3782 822463 10005","12/25"),
            dni = dni
        )

        val result = repository.update(dni,cliente)!!

        assertAll(
            { assert(result.nombre == "Test2") },
            { assert(result.tarjeta.numero == "3782 822463 10005") },
            { assert(result.cuenta.iban == "DE89 3704 0044 0532 0130 00") }
        )
    }

    @Test
    fun updateDniNoEncontrado() {
        val dni= Dni("04246432B")
        val cliente= Cliente(
            id = UUID.fromString("a7c9d1f4-3e5b-46b7-9f2d-88e6b45d01a3"),
            nombre = "Test2",
            cuenta = CuentaBancaria("DE89 3704 0044 0532 0130 00",200.0),
            tarjeta = Tarjeta("3782 822463 10005","12/25"),
            dni = dni
        )


        assertNull(repository.update(dni,cliente))
    }

    @Test
    fun deleteOk() {
        val dni= Dni("04246431X")

        val cliente = repository.delete(dni)!!

        assertAll(
            { assert(cliente.nombre == "Test1") },
            { assert(cliente.dni.dni == "04246431X") },
            { assert(cliente.tarjeta.numero == "4539 1488 0343 6467") },
            { assert(cliente.cuenta.iban == "ES91 2100 0418 4502 0005 1332") }
        )
    }

    @Test
    fun deleteNoEncontrado() {
        val dni= Dni("04246432B")

        assertNull(repository.delete(dni))
    }
}