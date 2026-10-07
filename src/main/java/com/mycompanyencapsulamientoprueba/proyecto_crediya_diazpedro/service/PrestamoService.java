package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.service;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.dao.PrestamoDAO;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Cliente;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Empleado;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.EstadoPrestamo;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Prestamo;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.persistence.FileManager;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.validation.Validador;
import java.time.LocalDate;
import java.util.List;

/**
 * Lógica de negocio de los préstamos.
 * No contiene SQL: el acceso a MySQL lo hace PrestamoDAO y
 * el respaldo en archivos lo hace FileManager.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class PrestamoService {

    private final PrestamoDAO prestamoDAO;
    private final FileManager fileManager;
    private final ClienteService clienteService;
    private final EmpleadoService empleadoService;

    public PrestamoService(ClienteService clienteService, EmpleadoService empleadoService) {
        this.prestamoDAO = new PrestamoDAO();
        this.fileManager = new FileManager();
        this.clienteService = clienteService;
        this.empleadoService = empleadoService;
    }

    /**
     * Crea un préstamo asociado a un cliente y a un empleado existentes.
     * El préstamo inicia en estado PENDIENTE y con saldo igual al total
     * (monto + interés), tal como lo define el constructor de Prestamo.
     */
    public Prestamo crearPrestamo(int clienteId, int empleadoId, double monto, double interes, int cuotas) {
        Validador.validarMonto(monto);
        Validador.validarInteres(interes);
        Validador.validarCuotas(cuotas);

        Cliente cliente = clienteService.buscarPorId(clienteId);
        Empleado empleado = empleadoService.buscarPorId(empleadoId);

        Prestamo prestamo = new Prestamo(0, cliente, empleado, monto, interes, cuotas, LocalDate.now());

        prestamoDAO.guardar(prestamo);
        actualizarArchivoPrestamos();
        return prestamo;
    }

    public Prestamo buscarPorId(int id) {
        Prestamo prestamo = prestamoDAO.buscarPorId(id);
        Validador.validarExistencia(prestamo, "No existe un préstamo con ID " + id + ".");
        return prestamo;
    }

    public List<Prestamo> listarPrestamos() {
        return prestamoDAO.listar();
    }

    public List<Prestamo> listarActivos() {
        return prestamoDAO.listarActivos();
    }

    public List<Prestamo> listarPorCliente(int clienteId) {
        clienteService.buscarPorId(clienteId);
        return prestamoDAO.listarPorCliente(clienteId);
    }

    /**
     * Cambio manual de estado (por ejemplo, marcar un préstamo como VENCIDO).
     * Las reglas las aplica Prestamo.cambiarEstado().
     */
    public void cambiarEstado(int id, EstadoPrestamo nuevoEstado) {
        Prestamo prestamo = buscarPorId(id);
        prestamo.cambiarEstado(nuevoEstado);

        prestamoDAO.actualizar(prestamo);
        actualizarArchivoPrestamos();
    }

    /**
     * Reescribe prestamos.txt con los datos actuales de MySQL.
     * Se usa al crear un préstamo, al cambiar su estado y al registrar un pago.
     */
    public void actualizarArchivoPrestamos() {
        fileManager.guardarPrestamos(prestamoDAO.listar());
    }
}
