package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.OperacionInvalidaException;
import java.time.LocalDate;

/**
 * Préstamo otorgado a un cliente y gestionado por un empleado.
 *
 * Encapsulamiento: los datos del préstamo no tienen setters; el saldo solo
 * cambia con registrarAbono() y el estado solo con registrarAbono() o
 * cambiarEstado(), que aplican las reglas del negocio.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class Prestamo {

    private int id;
    private Cliente cliente;
    private Empleado empleado;
    private double monto;
    private double interes;
    private int cuotas;
    private LocalDate fechaInicio;
    private EstadoPrestamo estado;
    private double saldoPendiente;

    /**
     * Préstamo nuevo: inicia PENDIENTE y con saldo igual al total a pagar.
     */
    public Prestamo(int id, Cliente cliente, Empleado empleado, double monto, double interes, int cuotas, LocalDate fechaInicio) {
        this.id = id;
        this.cliente = cliente;
        this.empleado = empleado;
        this.monto = monto;
        this.interes = interes;
        this.cuotas = cuotas;
        this.fechaInicio = fechaInicio;
        this.estado = EstadoPrestamo.PENDIENTE;
        this.saldoPendiente = calcularTotal();
    }

    /**
     * Préstamo ya existente (leído de la base de datos) con su estado y saldo guardados.
     */
    public Prestamo(int id, Cliente cliente, Empleado empleado, double monto, double interes, int cuotas,
                    LocalDate fechaInicio, EstadoPrestamo estado, double saldoPendiente) {
        this(id, cliente, empleado, monto, interes, cuotas, fechaInicio);
        this.estado = estado;
        this.saldoPendiente = saldoPendiente;
    }

    public double calcularTotal() {
        return monto + (monto * interes / 100);
    }

    public double calcularCuotaMensual() {
        return calcularTotal() / cuotas;
    }

    /**
     * Descuenta el abono del saldo. Si el saldo llega a cero, el préstamo queda PAGADO.
     */
    public void registrarAbono(double abono) {
        saldoPendiente -= abono;

        if (saldoPendiente <= 0) {
            saldoPendiente = 0;
            estado = EstadoPrestamo.PAGADO;
        }
    }

    /**
     * Cambio manual de estado (por ejemplo, marcar un préstamo como VENCIDO).
     * PAGADO no se asigna a mano: solo ocurre cuando el saldo llega a cero.
     */
    public void cambiarEstado(EstadoPrestamo nuevoEstado) {
        if (estado == EstadoPrestamo.PAGADO) {
            throw new OperacionInvalidaException("El préstamo " + id + " ya está pagado; no se puede cambiar su estado.");
        }
        if (nuevoEstado == EstadoPrestamo.PAGADO) {
            throw new OperacionInvalidaException("Un préstamo solo pasa a PAGADO cuando su saldo pendiente llega a cero.");
        }
        if (nuevoEstado == estado) {
            throw new OperacionInvalidaException("El préstamo " + id + " ya se encuentra en estado " + estado + ".");
        }
        estado = nuevoEstado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public double getMonto() {
        return monto;
    }

    public double getInteres() {
        return interes;
    }

    public int getCuotas() {
        return cuotas;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public double getSaldoPendiente() {
        return saldoPendiente;
    }
}
