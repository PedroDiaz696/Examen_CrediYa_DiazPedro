package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception;

/**
 * Se lanza cuando se busca un cliente, empleado o préstamo por un ID
 * que no existe en la base de datos.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class EntidadNoEncontradaException extends CrediYaException {

    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
