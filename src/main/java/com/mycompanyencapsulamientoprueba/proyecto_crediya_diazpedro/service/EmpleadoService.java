package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.service;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.dao.EmpleadoDAO;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.ValidacionException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Empleado;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.persistence.FileManager;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.validation.Validador;
import java.util.List;

/**
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class EmpleadoService {
    private final EmpleadoDAO empleadoDAO;
    private final FileManager fileManager;

    public EmpleadoService() {
        this.empleadoDAO = new EmpleadoDAO();
        this.fileManager = new FileManager();
    }

    public void registrarEmpleado(Empleado empleado) {
        Validador.validarTextoObligatorio(empleado.getNombre(), "nombre", 80);
        Validador.validarTextoObligatorio(empleado.getDocumento(), "documento", 30);
        Validador.validarCorreo(empleado.getCorreo());
        Validador.validarSalario(empleado.getSalario());
        validarNoDuplicado(empleado);

        empleadoDAO.guardar(empleado);
        fileManager.guardarEmpleados(empleadoDAO.listar());
    }

    public List<Empleado> listarEmpleados() {
        return empleadoDAO.listar();
    }

    public Empleado buscarPorId(int id) {
        Empleado empleado = empleadoDAO.buscarPorId(id);
        Validador.validarExistencia(empleado, "No existe un empleado con ID " + id + ".");
        return empleado;
    }

    /**
     * El documento y el correo de un empleado no se pueden repetir.
     */
    private void validarNoDuplicado(Empleado nuevo) {
        boolean duplicado = empleadoDAO.listar().stream()
                .anyMatch(e -> nuevo.getDocumento().equalsIgnoreCase(e.getDocumento())
                        || nuevo.getCorreo().equalsIgnoreCase(e.getCorreo()));
        if (duplicado) {
            throw new ValidacionException("Ya existe un empleado registrado con ese documento o correo.");
        }
    }
}
