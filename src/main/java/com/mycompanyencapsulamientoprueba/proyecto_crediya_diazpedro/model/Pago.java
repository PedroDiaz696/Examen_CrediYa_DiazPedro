package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model;

import java.time.LocalDate;

/**
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class Pago {
    private int id;
    private Prestamo prestamo;
    private LocalDate fechaPago;
    private double monto;

    public Pago(int id, Prestamo prestamo, LocalDate fechaPago, double monto) {
        this.id = id;
        this.prestamo = prestamo;
        this.fechaPago = fechaPago;
        this.monto = monto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public double getMonto() {
        return monto;
    }
}
