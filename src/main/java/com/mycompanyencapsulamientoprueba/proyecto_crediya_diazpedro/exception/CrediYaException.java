package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception;

/**
 * Excepción base del sistema CrediYa.
 *
 * Jerarquía:
 * CrediYaException
 *    ├── ValidacionException          (datos de entrada inválidos)
 *    ├── EntidadNoEncontradaException (el ID buscado no existe)
 *    └── OperacionInvalidaException   (la operación viola una regla de negocio)
 *
 * Extiende RuntimeException para no obligar a declarar "throws" en cada
 * método; la capa de consola (MenuConsola) es la que captura y muestra el error.
 * También se usa directamente para envolver errores de MySQL y de archivos.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class CrediYaException extends RuntimeException {

    public CrediYaException(String mensaje) {
        super(mensaje);
    }

    public CrediYaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
