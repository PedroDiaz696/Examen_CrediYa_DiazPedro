package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.ui;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.CrediYaException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.ValidacionException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Cliente;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Empleado;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.EstadoPrestamo;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Pago;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Persona;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Prestamo;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.RolEmpleado;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.service.ClienteService;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.service.EmpleadoService;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.service.PagoService;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.service.PrestamoService;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.service.ReporteService;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class MenuConsola {
    private final Scanner scanner;
    private final ClienteService clienteService;
    private final EmpleadoService empleadoService;
    private final PrestamoService prestamoService;
    private final PagoService pagoService;
    private final ReporteService reporteService;


    public MenuConsola() {
        scanner = new Scanner(System.in);
        clienteService = new ClienteService();
        empleadoService = new EmpleadoService();
        prestamoService = new PrestamoService(clienteService, empleadoService);
        pagoService = new PagoService(prestamoService);
        reporteService = new ReporteService(prestamoService, clienteService);
    }

    public void iniciar() {

        ejecutar(this::mostrarRespaldoArchivos);

        int opcion;

        do {
            System.out.println("\n===== CREDIYA =====");
            System.out.println("1. Clientes");
            System.out.println("2. Empleados");
            System.out.println("3. Préstamos");
            System.out.println("4. Pagos");
            System.out.println("5. Reportes");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    menuClientes();
                    break;

                case 2:
                    menuEmpleados();
                    break;

                case 3:
                    menuPrestamos();
                    break;

                case 4:
                    menuPagos();
                    break;

                case 5:
                    menuReportes();
                    break;

                case 6:
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        } while (opcion != 6);
    }

    private void menuClientes() {

        int opcion;

        do {
            System.out.println("\n===== CLIENTES =====");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Buscar cliente");
            System.out.println("4. Ver préstamos de un cliente");
            System.out.println("5. Volver");
            System.out.print("Seleccione una opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    ejecutar(this::registrarCliente);
                    break;

                case 2:
                    ejecutar(this::listarClientes);
                    break;

                case 3:
                    ejecutar(this::buscarCliente);
                    break;

                case 4:
                    ejecutar(this::verPrestamosDeCliente);
                    break;

                case 5:
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        } while (opcion != 5);
    }

    private void registrarCliente() {

        System.out.println("\n===== REGISTRAR CLIENTE =====");

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Documento: ");
        String documento = scanner.nextLine();

        System.out.print("Correo: ");
        String correo = scanner.nextLine();

        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();

        Cliente cliente = new Cliente(
                0,
                nombre,
                documento,
                correo,
                telefono
        );

        clienteService.registrarCliente(cliente);

        System.out.println("Cliente registrado correctamente con ID " + cliente.getId() + ".");
    }

    private void listarClientes() {

        System.out.println("\n===== LISTA DE CLIENTES =====");

        List<Cliente> clientes = clienteService.listarClientes();

        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }

        System.out.println("ID | Nombre | Documento | Correo | Teléfono");

        for (Cliente cliente : clientes) {
            mostrarPersona(cliente);
        }
    }

    private void buscarCliente() {

        System.out.println("\n===== BUSCAR CLIENTE =====");

        System.out.print("ID del cliente: ");
        int id = leerEntero();

        mostrarPersona(clienteService.buscarPorId(id));
    }

    private void menuEmpleados() {

        int opcion;

        do {
            System.out.println("\n===== EMPLEADOS =====");
            System.out.println("1. Registrar empleado");
            System.out.println("2. Listar empleados");
            System.out.println("3. Buscar empleado");
            System.out.println("4. Volver");
            System.out.print("Seleccione una opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    ejecutar(this::registrarEmpleado);
                    break;

                case 2:
                    ejecutar(this::listarEmpleados);
                    break;

                case 3:
                    ejecutar(this::buscarEmpleado);
                    break;

                case 4:
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        } while (opcion != 4);
    }

    private void registrarEmpleado() {

        System.out.println("\n===== REGISTRAR EMPLEADO =====");

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Documento: ");
        String documento = scanner.nextLine();

        System.out.print("Correo: ");
        String correo = scanner.nextLine();

        System.out.print("Salario: ");
        double salario = leerDouble();

        System.out.println("Roles disponibles:");
        System.out.println("1. ADMINISTRADOR");
        System.out.println("2. ASESOR");
        System.out.println("3. COBRADOR");
        System.out.print("Seleccione un rol: ");

        int opcionRol = leerEntero();

        RolEmpleado rol;

        switch (opcionRol) {
            case 1:
                rol = RolEmpleado.ADMINISTRADOR;
                break;

            case 2:
                rol = RolEmpleado.ASESOR;
                break;

            case 3:
                rol = RolEmpleado.COBRADOR;
                break;

            default:
                System.out.println("Rol inválido.");
                return;
        }

        Empleado empleado = new Empleado(
                0,
                nombre,
                documento,
                correo,
                rol,
                salario
        );

        empleadoService.registrarEmpleado(empleado);

        System.out.println("Empleado registrado correctamente con ID " + empleado.getId() + ".");
    }

    private void listarEmpleados() {

        System.out.println("\n===== LISTA DE EMPLEADOS =====");

        List<Empleado> empleados = empleadoService.listarEmpleados();

        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados.");
            return;
        }

        System.out.println("ID | Nombre | Documento | Correo | Rol | Salario");

        for (Empleado empleado : empleados) {
            mostrarPersona(empleado);
        }
    }

    private void buscarEmpleado() {

        System.out.println("\n===== BUSCAR EMPLEADO =====");

        System.out.print("ID del empleado: ");
        int id = leerEntero();

        mostrarPersona(empleadoService.buscarPorId(id));
    }

    /**
     * Polimorfismo: recibe cualquier Persona (Cliente o Empleado) y Java
     * ejecuta la versión de mostrarInformacion() del objeto real.
     */
    private void mostrarPersona(Persona persona) {
        System.out.println(persona.mostrarInformacion());
    }

    private void verPrestamosDeCliente() {

        System.out.println("\n===== PRÉSTAMOS DE UN CLIENTE =====");

        System.out.print("ID del cliente: ");
        int clienteId = leerEntero();

        mostrarPrestamos(prestamoService.listarPorCliente(clienteId));
    }

    // ===================== PRÉSTAMOS =====================

    private void menuPrestamos() {

        int opcion;

        do {
            System.out.println("\n===== PRÉSTAMOS =====");
            System.out.println("1. Crear préstamo");
            System.out.println("2. Listar préstamos");
            System.out.println("3. Buscar préstamo");
            System.out.println("4. Cambiar estado");
            System.out.println("5. Volver");
            System.out.print("Seleccione una opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    ejecutar(this::crearPrestamo);
                    break;

                case 2:
                    ejecutar(this::listarPrestamos);
                    break;

                case 3:
                    ejecutar(this::buscarPrestamo);
                    break;

                case 4:
                    ejecutar(this::cambiarEstadoPrestamo);
                    break;

                case 5:
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        } while (opcion != 5);
    }

    private void crearPrestamo() {

        System.out.println("\n===== CREAR PRÉSTAMO =====");

        System.out.print("ID del cliente: ");
        int clienteId = leerEntero();

        System.out.print("ID del empleado: ");
        int empleadoId = leerEntero();

        System.out.print("Monto: ");
        double monto = leerDouble();

        System.out.print("Interés (%): ");
        double interes = leerDouble();

        System.out.print("Número de cuotas: ");
        int cuotas = leerEntero();

        Prestamo prestamo = prestamoService.crearPrestamo(clienteId, empleadoId, monto, interes, cuotas);

        System.out.println("Préstamo creado correctamente.");
        mostrarDetallePrestamo(prestamo);
    }

    private void listarPrestamos() {

        System.out.println("\n===== LISTA DE PRÉSTAMOS =====");

        mostrarPrestamos(prestamoService.listarPrestamos());
    }

    private void buscarPrestamo() {

        System.out.println("\n===== BUSCAR PRÉSTAMO =====");

        System.out.print("ID del préstamo: ");
        int id = leerEntero();

        mostrarDetallePrestamo(prestamoService.buscarPorId(id));
    }

    private void cambiarEstadoPrestamo() {

        System.out.println("\n===== CAMBIAR ESTADO DE PRÉSTAMO =====");

        System.out.print("ID del préstamo: ");
        int id = leerEntero();

        Prestamo prestamo = prestamoService.buscarPorId(id);
        System.out.println("Estado actual: " + prestamo.getEstado());

        EstadoPrestamo nuevoEstado = leerEstado();
        prestamoService.cambiarEstado(id, nuevoEstado);

        System.out.println("Estado actualizado a " + nuevoEstado + ".");
    }

    // ===================== PAGOS =====================

    private void menuPagos() {

        int opcion;

        do {
            System.out.println("\n===== PAGOS =====");
            System.out.println("1. Registrar pago");
            System.out.println("2. Ver historial de pagos");
            System.out.println("3. Volver");
            System.out.print("Seleccione una opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    ejecutar(this::registrarPago);
                    break;

                case 2:
                    ejecutar(this::verHistorialPagos);
                    break;

                case 3:
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        } while (opcion != 3);
    }

    private void registrarPago() {

        System.out.println("\n===== REGISTRAR PAGO =====");

        List<Prestamo> activos = prestamoService.listarActivos();

        if (activos.isEmpty()) {
            System.out.println("No hay préstamos con saldo pendiente.");
            return;
        }

        System.out.println("Préstamos con saldo pendiente:");
        mostrarPrestamos(activos);

        System.out.print("ID del préstamo: ");
        int prestamoId = leerEntero();

        System.out.print("Monto a pagar: ");
        double monto = leerDouble();

        Pago pago = pagoService.registrarPago(prestamoId, monto);
        Prestamo prestamo = pago.getPrestamo();

        System.out.println("Pago registrado correctamente (ID " + pago.getId() + ").");
        System.out.println("Saldo pendiente: " + formatoMoneda(prestamo.getSaldoPendiente()));
        System.out.println("Estado del préstamo: " + prestamo.getEstado());

        if (prestamo.getEstado() == EstadoPrestamo.PAGADO) {
            System.out.println("¡El préstamo quedó totalmente pagado!");
        }
    }

    private void verHistorialPagos() {

        System.out.println("\n===== HISTORIAL DE PAGOS =====");

        System.out.print("ID del préstamo: ");
        int prestamoId = leerEntero();

        List<Pago> pagos = pagoService.listarPagosPorPrestamo(prestamoId);

        if (pagos.isEmpty()) {
            System.out.println("El préstamo " + prestamoId + " no tiene pagos registrados.");
            return;
        }

        System.out.println("ID pago | Fecha | Monto");

        for (Pago pago : pagos) {
            System.out.println(
                    pago.getId() + " | " +
                    pago.getFechaPago() + " | " +
                    formatoMoneda(pago.getMonto())
            );
        }

        Prestamo prestamo = pagos.get(0).getPrestamo();

        System.out.println("Total pagado: " + formatoMoneda(pagoService.calcularTotalPagado(pagos)));
        System.out.println("Saldo pendiente: " + formatoMoneda(prestamo.getSaldoPendiente()));
        System.out.println("Estado: " + prestamo.getEstado());
    }

    // ===================== REPORTES =====================

    private void menuReportes() {

        int opcion;

        do {
            System.out.println("\n===== REPORTES =====");
            System.out.println("1. Préstamos activos");
            System.out.println("2. Préstamos vencidos");
            System.out.println("3. Clientes morosos");
            System.out.println("4. Buscar por estado");
            System.out.println("5. Resumen de cartera");
            System.out.println("6. Volver");
            System.out.print("Seleccione una opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    ejecutar(() -> mostrarReportePrestamos(
                            "PRÉSTAMOS ACTIVOS", reporteService.listarPrestamosActivos()));
                    break;

                case 2:
                    ejecutar(() -> mostrarReportePrestamos(
                            "PRÉSTAMOS VENCIDOS", reporteService.listarPrestamosVencidos()));
                    break;

                case 3:
                    ejecutar(this::reporteClientesMorosos);
                    break;

                case 4:
                    ejecutar(this::reportePorEstado);
                    break;

                case 5:
                    ejecutar(this::reporteResumenCartera);
                    break;

                case 6:
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        } while (opcion != 6);
    }

    private void mostrarReportePrestamos(String titulo, List<Prestamo> prestamos) {

        System.out.println("\n===== " + titulo + " =====");

        mostrarPrestamos(prestamos);

        if (!prestamos.isEmpty()) {
            System.out.println("Cantidad: " + prestamos.size()
                    + " | Saldo pendiente total: " + formatoMoneda(reporteService.calcularSaldoTotal(prestamos)));
        }
    }

    private void reporteClientesMorosos() {

        System.out.println("\n===== CLIENTES MOROSOS =====");

        List<Cliente> morosos = reporteService.listarClientesMorosos();

        if (morosos.isEmpty()) {
            System.out.println("No hay clientes morosos.");
            return;
        }

        Map<Integer, Double> deudaPorCliente = reporteService.calcularDeudaVencidaPorCliente();

        System.out.println("ID | Nombre | Documento | Teléfono | Deuda vencida");

        for (Cliente cliente : morosos) {
            System.out.println(
                    cliente.getId() + " | " +
                    cliente.getNombre() + " | " +
                    cliente.getDocumento() + " | " +
                    cliente.getTelefono() + " | " +
                    formatoMoneda(deudaPorCliente.getOrDefault(cliente.getId(), 0.0))
            );
        }
    }

    private void reportePorEstado() {

        EstadoPrestamo estado = leerEstado();

        mostrarReportePrestamos("PRÉSTAMOS EN ESTADO " + estado, reporteService.buscarPorEstado(estado));
    }

    private void reporteResumenCartera() {

        System.out.println("\n===== RESUMEN DE CARTERA =====");

        Map<EstadoPrestamo, Long> cantidadPorEstado = reporteService.contarPorEstado();

        for (EstadoPrestamo estado : EstadoPrestamo.values()) {
            System.out.println("Préstamos " + estado + ": " + cantidadPorEstado.getOrDefault(estado, 0L));
        }

        reporteService.resumenCartera()
                .forEach((concepto, valor) -> System.out.println(concepto + ": " + formatoMoneda(valor)));
    }

    private void mostrarRespaldoArchivos() {

        Map<String, Integer> registros = reporteService.contarRegistrosEnArchivos();

        System.out.println("Respaldo leído de la carpeta data:");
        registros.forEach((archivo, cantidad) -> System.out.println("  " + archivo + ": " + cantidad + " registros"));
    }

    // ===================== MÉTODOS DE APOYO =====================

    private void mostrarPrestamos(List<Prestamo> prestamos) {

        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos para mostrar.");
            return;
        }

        System.out.println("ID | Cliente | Empleado | Monto | Interés | Cuotas | Cuota mensual | Saldo pendiente | Estado | Fecha inicio");

        for (Prestamo prestamo : prestamos) {
            System.out.println(
                    prestamo.getId() + " | " +
                    prestamo.getCliente().getNombre() + " | " +
                    prestamo.getEmpleado().getNombre() + " | " +
                    formatoMoneda(prestamo.getMonto()) + " | " +
                    prestamo.getInteres() + "% | " +
                    prestamo.getCuotas() + " | " +
                    formatoMoneda(prestamo.calcularCuotaMensual()) + " | " +
                    formatoMoneda(prestamo.getSaldoPendiente()) + " | " +
                    prestamo.getEstado() + " | " +
                    prestamo.getFechaInicio()
            );
        }
    }

    private void mostrarDetallePrestamo(Prestamo prestamo) {
        System.out.println("ID del préstamo: " + prestamo.getId());
        System.out.println("Cliente: " + prestamo.getCliente().getId() + " - " + prestamo.getCliente().getNombre());
        System.out.println("Empleado: " + prestamo.getEmpleado().getId() + " - " + prestamo.getEmpleado().getNombre());
        System.out.println("Monto: " + formatoMoneda(prestamo.getMonto()));
        System.out.println("Interés: " + prestamo.getInteres() + "%");
        System.out.println("Cuotas: " + prestamo.getCuotas());
        System.out.println("Fecha de inicio: " + prestamo.getFechaInicio());
        System.out.println("Total del préstamo: " + formatoMoneda(prestamo.calcularTotal()));
        System.out.println("Valor de la cuota: " + formatoMoneda(prestamo.calcularCuotaMensual()));
        System.out.println("Saldo pendiente: " + formatoMoneda(prestamo.getSaldoPendiente()));
        System.out.println("Estado: " + prestamo.getEstado());
    }

    private EstadoPrestamo leerEstado() {

        EstadoPrestamo[] estados = EstadoPrestamo.values();

        System.out.println("Estados disponibles:");
        for (int i = 0; i < estados.length; i++) {
            System.out.println((i + 1) + ". " + estados[i]);
        }
        System.out.print("Seleccione un estado: ");

        int opcion = leerEntero();

        if (opcion < 1 || opcion > estados.length) {
            throw new ValidacionException("Estado inválido.");
        }

        return estados[opcion - 1];
    }

    /**
     * Ejecuta una acción del menú y, si ocurre un error, muestra el mensaje
     * sin cerrar el programa. Es el único lugar donde se capturan las
     * excepciones: primero las del negocio (CrediYaException) y, como red de
     * seguridad, cualquier error no previsto.
     */
    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (CrediYaException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Lee un número entero; si el usuario escribe texto, lo vuelve a pedir.
     */
    private int leerEntero() {
        while (true) {
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido, ingrese un número entero: ");
            }
        }
    }

    /**
     * Lee un número decimal (acepta punto o coma); si el usuario escribe texto, lo vuelve a pedir.
     */
    private double leerDouble() {
        while (true) {
            String entrada = scanner.nextLine().trim().replace(',', '.');
            try {
                double valor = Double.parseDouble(entrada);
                if (Double.isFinite(valor)) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // se vuelve a pedir el valor
            }
            System.out.print("Valor inválido, ingrese un número: ");
        }
    }

    private String formatoMoneda(double valor) {
        return String.format("$%,.2f", valor);
    }
}