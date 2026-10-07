package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model;

/**
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class Empleado extends Persona{
    private RolEmpleado rol;
    private double salario;

    public Empleado() {
    }

    public Empleado(int id, String nombre, String documento, String correo,
                    RolEmpleado rol, double salario) {
        super(id, nombre, documento, correo);
        this.rol = rol;
        this.salario = salario;
    }

    public RolEmpleado getRol() {
        return rol;
    }

    public void setRol(RolEmpleado rol) {
        this.rol = rol;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    @Override
    public String mostrarInformacion() {
        return super.mostrarInformacion() + " | " + rol + " | " + String.format("$%,.2f", salario);
    }
}
