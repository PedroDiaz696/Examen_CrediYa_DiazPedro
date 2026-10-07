/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import java.util.stream.Collectors;

/**
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class GestorPrestamos {
    
    private final PrestamoService prestamoService;
    private final ClienteService clienteService;
    private final FileManager fileManager;

    public GestorPrestamos(PrestamoService prestamoService, ClienteService clienteService) {
        this.prestamoService = prestamoService;
        this.clienteService = clienteService;
        this.fileManager = new FileManager();
    }

    public List<Prestamo> FiltrarPrestamosVencidos() {
        return prestamoService.listarPrestamos().stream()
                .filter(this::estaVencido)
                .sorted(Comparator.comparing(Prestamo::getFechaInicio))
                .collect(Collectors.toList());
    }
    
    public List<Prestamo> FiltrarPrestamosActivos() {
        return prestamoService.listarPrestamos().stream()
                .filter(p -> p.getEstado() != EstadoPrestamo.PAGADO)
                .filter(this::estaActivo)
                .sorted(Comparator.comparingDouble(Prestamo::getSaldoPendiente).reversed())
                .collect(Collectors.toList());
    }
    
    
    public Map<EstadoPrestamo, Long> contarPorEstado() {
        return prestamoService.listarPrestamos().stream()
                .collect(Collectors.groupingBy(Prestamo::getEstado, Collectors.counting()));
    }
    
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
    
    public double calcularSaldoTotal(List<Prestamo> prestamos) {
        return prestamos.stream()
                .mapToDouble(Prestamo::getSaldoPendiente)
                .sum();
    }

    private boolean estaVencido(Prestamo prestamo) {
        if (prestamo.getEstado() == EstadoPrestamo.VENCIDO || prestamo.getEstado() == EstadoPrestamo.PAGADO) {
            return true;
        }
        LocalDate fechaFinal = prestamo.getFechaInicio().plusMonths(prestamo.getCuotas());
        return prestamo.getEstado() == EstadoPrestamo.PENDIENTE && LocalDate.now().isAfter(fechaFinal);
    }
    
    private boolean estaActivo(Prestamo prestamo) {
        if (prestamo.getEstado() != EstadoPrestamo.VENCIDO) {
            return true;
        }
        LocalDate fechaFinal = prestamo.getFechaInicio().plusMonths(prestamo.getCuotas());
        return prestamo.getEstado() == EstadoPrestamo.PENDIENTE && LocalDate.now().isAfter(fechaFinal);
    }
}
