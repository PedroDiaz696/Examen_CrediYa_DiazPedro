package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception;

/**
 * Se lanza cuando una operación no está permitida por las reglas del negocio,
 * por ejemplo: abonar más del saldo pendiente o pagar un préstamo ya pagado.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class OperacionInvalidaException extends CrediYaException {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
