package org.example.cliente.validator

import org.example.banco.cuenta.validator.CuentaBancariaValidator
import org.example.banco.tarjeta.validator.TarjetaValidator
import org.example.cliente.exception.ClienteException
import org.example.cliente.model.Cliente
import org.example.dni.validator.DniValidator

class ClienteValidator(
    val tarjetaValidator:TarjetaValidator,
    val cuentaValidator: CuentaBancariaValidator,
    val dniValidator: DniValidator
) {
    fun validarCliente(cliente:Cliente):Cliente{
        if (!validarNombre(cliente.nombre)) throw ClienteException.NombreException(cliente.nombre)

        tarjetaValidator.validarTarjeta(cliente.tarjeta)
        cuentaValidator.validarCuenta(cliente.cuenta)
        dniValidator.validarDni(cliente.dni)

        return cliente
    }

    fun validarNombre(nombre:String):Boolean{
        return nombre.length > 2
    }
}