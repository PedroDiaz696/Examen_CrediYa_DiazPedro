package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception;

/**
 * Se lanza cuando un dato ingresado no cumple las reglas de validación
 * (campo vacío, correo inválido, monto negativo, cuotas en cero, etc.).
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class ValidacionException extends CrediYaException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
