package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.service;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Cliente;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.EstadoPrestamo;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Prestamo;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.persistence.FileManager;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reportes del sistema. Obtiene los préstamos desde MySQL (vía PrestamoService)
 * y los filtra, ordena y agrupa en memoria con Lambda y Stream API.
 *
 * Definiciones usadas:
 * - Activo:  préstamo que todavía tiene saldo por cobrar (estado distinto de PAGADO).
 * - Vencido: préstamo marcado como VENCIDO, o PENDIENTE cuya fecha final
 *            (fecha de inicio + número de cuotas en meses) ya pasó.
 * - Moroso:  cliente que tiene al menos un préstamo vencido.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class ReporteService {

    private final PrestamoService prestamoService;
    private final ClienteService clienteService;
    private final FileManager fileManager;

    public ReporteService(PrestamoService prestamoService, ClienteService clienteService) {
        this.prestamoService = prestamoService;
        this.clienteService = clienteService;
        this.fileManager = new FileManager();
    }

    public List<Prestamo> listarPrestamosActivos() {
        return prestamoService.listarPrestamos().stream()
                .filter(p -> p.getEstado() != EstadoPrestamo.PAGADO)
                .sorted(Comparator.comparingDouble(Prestamo::getSaldoPendiente).reversed())
                .collect(Collectors.toList());
    }

    public List<Prestamo> listarPrestamosVencidos() {
        return prestamoService.listarPrestamos().stream()
                .filter(this::estaVencido)
                .sorted(Comparator.comparing(Prestamo::getFechaInicio))
                .collect(Collectors.toList());
    }

    public List<Cliente> listarClientesMorosos() {
        Set<Integer> idsMorosos = calcularDeudaVencidaPorCliente().keySet();

        return clienteService.listarClientes().stream()
                .filter(c -> idsMorosos.contains(c.getId()))
                .sorted(Comparator.comparing(Cliente::getNombre))
                .collect(Collectors.toList());
    }

    /**
     * Suma el saldo de los préstamos vencidos agrupado por ID de cliente.
     */
    public Map<Integer, Double> calcularDeudaVencidaPorCliente() {
        return listarPrestamosVencidos().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCliente().getId(),
                        Collectors.summingDouble(Prestamo::getSaldoPendiente)));
    }

    public List<Prestamo> buscarPorEstado(EstadoPrestamo estado) {
        return prestamoService.listarPrestamos().stream()
                .filter(p -> p.getEstado() == estado)
                .collect(Collectors.toList());
    }

    public double calcularSaldoTotal(List<Prestamo> prestamos) {
        return prestamos.stream()
                .mapToDouble(Prestamo::getSaldoPendiente)
                .sum();
    }

    /**
     * Cantidad de préstamos en cada estado.
     */
    public Map<EstadoPrestamo, Long> contarPorEstado() {
        return prestamoService.listarPrestamos().stream()
                .collect(Collectors.groupingBy(Prestamo::getEstado, Collectors.counting()));
    }

    /**
     * Totales de la cartera: lo prestado, lo que se debe cobrar con interés,
     * lo ya recaudado y lo que falta por cobrar.
     */
    public Map<String, Double> resumenCartera() {
        List<Prestamo> prestamos = prestamoService.listarPrestamos();

        Map<String, Double> resumen = new LinkedHashMap<>();
        resumen.put("Total prestado", prestamos.stream()
                .mapToDouble(Prestamo::getMonto).sum());
        resumen.put("Total a cobrar (con interés)", prestamos.stream()
                .mapToDouble(Prestamo::calcularTotal).sum());
        resumen.put("Total recaudado", prestamos.stream()
                .mapToDouble(p -> p.calcularTotal() - p.getSaldoPendiente()).sum());
        resumen.put("Saldo pendiente por cobrar", calcularSaldoTotal(prestamos));
        return resumen;
    }

    /**
     * Cantidad de registros guardados en cada archivo .txt de respaldo.
     */
    public Map<String, Integer> contarRegistrosEnArchivos() {
        return fileManager.contarRegistros();
    }

    private boolean estaVencido(Prestamo prestamo) {
        if (prestamo.getEstado() == EstadoPrestamo.VENCIDO) {
            return true;
        }
        LocalDate fechaFinal = prestamo.getFechaInicio().plusMonths(prestamo.getCuotas());
        return prestamo.getEstado() == EstadoPrestamo.PENDIENTE && LocalDate.now().isAfter(fechaFinal);
    }
}
