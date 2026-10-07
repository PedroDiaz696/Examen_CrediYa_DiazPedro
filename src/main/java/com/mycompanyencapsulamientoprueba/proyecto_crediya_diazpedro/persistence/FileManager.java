package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.persistence;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.CrediYaException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Cliente;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Empleado;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Pago;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Prestamo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Persistencia en archivos de texto (.txt) dentro de la carpeta "data".
 * Es el segundo mecanismo de persistencia: MySQL es la fuente principal y
 * estos archivos son un respaldo legible.
 *
 * Formato: un registro por línea, campos separados por "|".
 * La primera línea (que empieza por "#") indica el nombre de cada campo.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class FileManager {

    private static final String CARPETA = "data";
    private static final String SEPARADOR = "|";

    private static final String ARCHIVO_EMPLEADOS = "empleados.txt";
    private static final String ARCHIVO_CLIENTES = "clientes.txt";
    private static final String ARCHIVO_PRESTAMOS = "prestamos.txt";
    private static final String ARCHIVO_PAGOS = "pagos.txt";

    private static final String ENCABEZADO_EMPLEADOS = "# id|nombre|documento|rol|correo|salario";
    private static final String ENCABEZADO_CLIENTES = "# id|nombre|documento|correo|telefono";
    private static final String ENCABEZADO_PRESTAMOS =
            "# id|cliente_id|empleado_id|monto|interes|cuotas|fecha_inicio|estado|saldo_pendiente";
    private static final String ENCABEZADO_PAGOS = "# id|prestamo_id|fecha_pago|monto";

    // ===================== GUARDAR / ACTUALIZAR =====================
    // Se reescribe el archivo completo con la lista actual de MySQL.

    public void guardarEmpleados(List<Empleado> empleados) {
        List<String> lineas = empleados.stream()
                .map(e -> unir(e.getId(), e.getNombre(), e.getDocumento(), e.getRol(), e.getCorreo(), e.getSalario()))
                .collect(Collectors.toList());
        escribir(ARCHIVO_EMPLEADOS, ENCABEZADO_EMPLEADOS, lineas);
    }

    public void guardarClientes(List<Cliente> clientes) {
        List<String> lineas = clientes.stream()
                .map(c -> unir(c.getId(), c.getNombre(), c.getDocumento(), c.getCorreo(), c.getTelefono()))
                .collect(Collectors.toList());
        escribir(ARCHIVO_CLIENTES, ENCABEZADO_CLIENTES, lineas);
    }

    /**
     * Se llama al crear un préstamo y cada vez que cambia su saldo o estado.
     */
    public void guardarPrestamos(List<Prestamo> prestamos) {
        List<String> lineas = prestamos.stream()
                .map(p -> unir(p.getId(), p.getCliente().getId(), p.getEmpleado().getId(),
                        p.getMonto(), p.getInteres(), p.getCuotas(), p.getFechaInicio(),
                        p.getEstado(), p.getSaldoPendiente()))
                .collect(Collectors.toList());
        escribir(ARCHIVO_PRESTAMOS, ENCABEZADO_PRESTAMOS, lineas);
    }

    /**
     * Los pagos nunca se modifican, por eso cada pago nuevo
     * se agrega al final del archivo (historial).
     */
    public void agregarPago(Pago pago) {
        String linea = unir(pago.getId(), pago.getPrestamo().getId(), pago.getFechaPago(), pago.getMonto());
        Path ruta = obtenerRuta(ARCHIVO_PAGOS);
        try {
            if (Files.notExists(ruta) || Files.size(ruta) == 0) {
                Files.write(ruta, List.of(ENCABEZADO_PAGOS), StandardCharsets.UTF_8);
            }
            Files.write(ruta, List.of(linea), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new CrediYaException("Error al escribir el archivo " + ruta + ": " + e.getMessage(), e);
        }
    }

    // ===================== LEER =====================

    /**
     * Lee los cuatro archivos y retorna cuántos registros tiene cada uno.
     */
    public Map<String, Integer> contarRegistros() {
        Map<String, Integer> resumen = new LinkedHashMap<>();
        for (String archivo : List.of(ARCHIVO_EMPLEADOS, ARCHIVO_CLIENTES, ARCHIVO_PRESTAMOS, ARCHIVO_PAGOS)) {
            resumen.put(archivo, leerRegistros(archivo).size());
        }
        return resumen;
    }

    /**
     * Retorna las líneas de datos del archivo, sin el encabezado ni líneas vacías.
     */
    private List<String> leerRegistros(String nombreArchivo) {
        Path ruta = obtenerRuta(nombreArchivo);
        if (Files.notExists(ruta)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(ruta, StandardCharsets.UTF_8).stream()
                    .filter(linea -> !linea.isBlank() && !linea.startsWith("#"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new CrediYaException("Error al leer el archivo " + ruta + ": " + e.getMessage(), e);
        }
    }

    // ===================== MÉTODOS DE APOYO =====================

    private String unir(Object... campos) {
        return Arrays.stream(campos)
                .map(String::valueOf)
                .collect(Collectors.joining(SEPARADOR));
    }

    private void escribir(String nombreArchivo, String encabezado, List<String> lineas) {
        Path ruta = obtenerRuta(nombreArchivo);
        List<String> contenido = new ArrayList<>();
        contenido.add(encabezado);
        contenido.addAll(lineas);
        try {
            Files.write(ruta, contenido, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CrediYaException("Error al escribir el archivo " + ruta + ": " + e.getMessage(), e);
        }
    }

    private Path obtenerRuta(String nombreArchivo) {
        Path carpeta = Paths.get(CARPETA);
        try {
            Files.createDirectories(carpeta);
        } catch (IOException e) {
            throw new CrediYaException("No se pudo crear la carpeta " + carpeta + ": " + e.getMessage(), e);
        }
        return carpeta.resolve(nombreArchivo);
    }
}
