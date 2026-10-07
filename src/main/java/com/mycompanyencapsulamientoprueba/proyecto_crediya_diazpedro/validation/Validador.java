package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.validation;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.EntidadNoEncontradaException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.OperacionInvalidaException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.ValidacionException;

/**
 * Centraliza las validaciones básicas del sistema.
 * Si un dato no es válido lanza una excepción con un mensaje claro
 * que luego se muestra en la consola.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public final class Validador {

    // texto@dominio.ext  (ej: pedro@gmail.com)
    private static final String REGEX_CORREO = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";
    // solo dígitos, entre 7 y 15, con "+" opcional al inicio (ej: 3001234567)
    private static final String REGEX_TELEFONO = "^\\+?\\d{7,15}$";

    private Validador() {
    }

    public static void validarTextoObligatorio(String valor, String campo, int longitudMaxima) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionException("El campo " + campo + " es obligatorio.");
        }
        if (valor.length() > longitudMaxima) {
            throw new ValidacionException("El campo " + campo + " no puede superar "
                    + longitudMaxima + " caracteres.");
        }
        // "|" es el separador de los archivos .txt
        if (valor.contains("|")) {
            throw new ValidacionException("El campo " + campo + " no puede contener el carácter '|'.");
        }
    }

    public static void validarCorreo(String correo) {
        validarTextoObligatorio(correo, "correo", 80);
        if (!correo.matches(REGEX_CORREO)) {
            throw new ValidacionException("El correo '" + correo + "' no tiene un formato válido.");
        }
    }

    public static void validarTelefono(String telefono) {
        if (telefono == null || !telefono.matches(REGEX_TELEFONO)) {
            throw new ValidacionException("El teléfono debe contener solo números (entre 7 y 15 dígitos).");
        }
    }

    public static void validarSalario(double salario) {
        if (salario <= 0) {
            throw new ValidacionException("El salario debe ser mayor a cero.");
        }
    }

    public static void validarMonto(double monto) {
        if (monto <= 0) {
            throw new ValidacionException("El monto del préstamo debe ser mayor a cero.");
        }
    }

    public static void validarInteres(double interes) {
        if (interes < 0 || interes > 100) {
            throw new ValidacionException("El interés debe estar entre 0 y 100 %.");
        }
    }

    public static void validarCuotas(int cuotas) {
        if (cuotas <= 0) {
            throw new ValidacionException("El número de cuotas debe ser mayor a cero.");
        }
    }

    public static void validarAbono(double abono, double saldoPendiente) {
        if (abono <= 0) {
            throw new ValidacionException("El valor del pago debe ser mayor a cero.");
        }
        if (abono > saldoPendiente) {
            throw new OperacionInvalidaException(String.format(
                    "El pago ($%,.2f) no puede ser mayor al saldo pendiente ($%,.2f).", abono, saldoPendiente));
        }
    }

    /**
     * Evita trabajar con referencias inexistentes (cliente, empleado o préstamo).
     */
    public static void validarExistencia(Object entidad, String mensaje) {
        if (entidad == null) {
            throw new EntidadNoEncontradaException(mensaje);
        }
    }
}
