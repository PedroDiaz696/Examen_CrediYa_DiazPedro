package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model;

/**
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class Cliente extends Persona{
    private String telefono;

    public Cliente() {
    }

    public Cliente(int id, String nombre, String documento, String correo, String telefono) {
        super(id, nombre, documento, correo);
        this.telefono = telefono;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String mostrarInformacion() {
        return super.mostrarInformacion() + " | " + telefono;
    }
}
