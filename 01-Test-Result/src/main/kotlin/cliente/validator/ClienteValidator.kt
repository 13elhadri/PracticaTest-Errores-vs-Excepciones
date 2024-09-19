package org.example.cliente.validator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import org.example.banco.cuenta.validator.CuentaBancariaValidator
import org.example.banco.tarjeta.validators.TarjetaValidator
import org.example.cliente.errors.ClienteError
import org.example.cliente.model.Cliente
import org.example.dni.validator.DniValidator
import org.example.error.GeneralError

class ClienteValidator (
    val tarjetaValidator: TarjetaValidator,
    val cuentaValidator: CuentaBancariaValidator,
    val dniValidator: DniValidator
) {
    fun validarCliente(cliente: Cliente): Result<Cliente,GeneralError> {
        if (!validarNombre(cliente.nombre)) return Err( ClienteError.NombreError(cliente.nombre))

        tarjetaValidator.validarTarjeta(cliente.tarjeta).mapBoth(
            success = {

            },
            failure = {
                return Err(it)
            }
        )
        cuentaValidator.validarCuenta(cliente.cuenta).mapBoth(
            success = {

            },
            failure = {
                return Err(it)
            }
        )
        dniValidator.validarDni(cliente.dni).mapBoth(
            success = {

            },
            failure = {
                return Err(it)
            }
        )

        return Ok( cliente)
    }

    fun validarNombre(nombre:String):Boolean{
        return nombre.length > 2
    }
}