package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.service;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.dao.PagoDAO;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.OperacionInvalidaException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.EstadoPrestamo;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Pago;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Prestamo;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.persistence.FileManager;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.validation.Validador;
import java.time.LocalDate;
import java.util.List;

/**
 * Lógica de negocio de los pagos (abonos) a un préstamo.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class PagoService {

    private final PagoDAO pagoDAO;
    private final FileManager fileManager;
    private final PrestamoService prestamoService;

    public PagoService(PrestamoService prestamoService) {
        this.pagoDAO = new PagoDAO();
        this.fileManager = new FileManager();
        this.prestamoService = prestamoService;
    }

    /**
     * Registra un pago:
     * 1. Busca el préstamo (debe existir) y verifica que no esté pagado.
     * 2. Valida el abono (mayor a cero y no mayor al saldo).
     * 3. Descuenta el saldo; si llega a 0 el préstamo queda PAGADO.
     * 4. Guarda el pago y el nuevo saldo en MySQL (una sola transacción).
     * 5. Actualiza pagos.txt y prestamos.txt.
     */
    public Pago registrarPago(int prestamoId, double monto) {
        Prestamo prestamo = prestamoService.buscarPorId(prestamoId);
        if (prestamo.getEstado() == EstadoPrestamo.PAGADO) {
            throw new OperacionInvalidaException("El préstamo " + prestamoId + " ya está pagado.");
        }
        Validador.validarAbono(monto, prestamo.getSaldoPendiente());

        prestamo.registrarAbono(monto);

        Pago pago = new Pago(0, prestamo, LocalDate.now(), monto);
        pagoDAO.guardar(pago);

        fileManager.agregarPago(pago);
        prestamoService.actualizarArchivoPrestamos();
        return pago;
    }

    /**
     * Historial de pagos de un préstamo existente.
     */
    public List<Pago> listarPagosPorPrestamo(int prestamoId) {
        Prestamo prestamo = prestamoService.buscarPorId(prestamoId);
        return pagoDAO.listarPorPrestamo(prestamo);
    }

    public double calcularTotalPagado(List<Pago> pagos) {
        return pagos.stream()
                .mapToDouble(Pago::getMonto)
                .sum();
    }
}
