package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.dao;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.CrediYaException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Cliente;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Empleado;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.EstadoPrestamo;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Prestamo;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.RolEmpleado;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.persistence.ConexionBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla "prestamos" de MySQL.
 * Las consultas hacen JOIN con clientes y empleados para reconstruir
 * el préstamo con sus objetos Cliente y Empleado asociados.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class PrestamoDAO {

    private static final String SELECT_PRESTAMOS =
            "SELECT p.id, p.monto, p.interes, p.cuotas, p.fecha_inicio, p.estado, p.saldo_pendiente, "
            + "c.id AS cliente_id, c.nombre AS cliente_nombre, c.documento AS cliente_documento, "
            + "c.correo AS cliente_correo, c.telefono AS cliente_telefono, "
            + "e.id AS empleado_id, e.nombre AS empleado_nombre, e.documento AS empleado_documento, "
            + "e.correo AS empleado_correo, e.rol AS empleado_rol, e.salario AS empleado_salario "
            + "FROM prestamos p "
            + "INNER JOIN clientes c ON p.cliente_id = c.id "
            + "INNER JOIN empleados e ON p.empleado_id = e.id";

    /**
     * Inserta el préstamo y le asigna el ID generado por MySQL.
     */
    public void guardar(Prestamo prestamo) {

        String sql = "INSERT INTO prestamos (cliente_id, empleado_id, monto, interes, cuotas, "
                + "fecha_inicio, estado, saldo_pendiente) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.MysConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, prestamo.getCliente().getId());
            ps.setInt(2, prestamo.getEmpleado().getId());
            ps.setDouble(3, prestamo.getMonto());
            ps.setDouble(4, prestamo.getInteres());
            ps.setInt(5, prestamo.getCuotas());
            ps.setDate(6, Date.valueOf(prestamo.getFechaInicio()));
            ps.setString(7, prestamo.getEstado().name());
            ps.setDouble(8, prestamo.getSaldoPendiente());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    prestamo.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new CrediYaException("Error al guardar préstamo: " + e.getMessage(), e);
        }
    }

    public List<Prestamo> listar() {
        return consultar(SELECT_PRESTAMOS + " ORDER BY p.id");
    }

    /**
     * Retorna el préstamo con ese ID o null si no existe.
     */
    public Prestamo buscarPorId(int id) {
        List<Prestamo> resultado = consultar(SELECT_PRESTAMOS + " WHERE p.id = ?", id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    /**
     * Préstamos activos: los que aún tienen saldo por cobrar (estado distinto de PAGADO).
     */
    public List<Prestamo> listarActivos() {
        return consultar(SELECT_PRESTAMOS + " WHERE p.estado <> ? ORDER BY p.id",
                EstadoPrestamo.PAGADO.name());
    }

    public List<Prestamo> listarPorCliente(int clienteId) {
        return consultar(SELECT_PRESTAMOS + " WHERE p.cliente_id = ? ORDER BY p.id", clienteId);
    }

    /**
     * Actualiza los datos que cambian durante la vida del préstamo:
     * el estado y el saldo pendiente.
     */
    public void actualizar(Prestamo prestamo) {

        String sql = "UPDATE prestamos SET estado = ?, saldo_pendiente = ? WHERE id = ?";

        try (Connection con = ConexionBD.MysConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, prestamo.getEstado().name());
            ps.setDouble(2, prestamo.getSaldoPendiente());
            ps.setInt(3, prestamo.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new CrediYaException("Error al actualizar préstamo: " + e.getMessage(), e);
        }
    }

    /**
     * Ejecuta un SELECT con parámetros (siempre por PreparedStatement,
     * nunca concatenando datos del usuario) y convierte cada fila en un Prestamo.
     */
    private List<Prestamo> consultar(String sql, Object... parametros) {

        List<Prestamo> prestamos = new ArrayList<>();

        try (Connection con = ConexionBD.MysConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(mapearPrestamo(rs));
                }
            }

        } catch (SQLException e) {
            throw new CrediYaException("Error al consultar préstamos: " + e.getMessage(), e);
        }

        return prestamos;
    }

    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {

        Cliente cliente = new Cliente(
                rs.getInt("cliente_id"),
                rs.getString("cliente_nombre"),
                rs.getString("cliente_documento"),
                rs.getString("cliente_correo"),
                rs.getString("cliente_telefono")
        );

        Empleado empleado = new Empleado(
                rs.getInt("empleado_id"),
                rs.getString("empleado_nombre"),
                rs.getString("empleado_documento"),
                rs.getString("empleado_correo"),
                RolEmpleado.valueOf(rs.getString("empleado_rol")),
                rs.getDouble("empleado_salario")
        );

        // Constructor de préstamo existente: conserva el estado y el saldo guardados.
        return new Prestamo(
                rs.getInt("id"),
                cliente,
                empleado,
                rs.getDouble("monto"),
                rs.getDouble("interes"),
                rs.getInt("cuotas"),
                rs.getDate("fecha_inicio").toLocalDate(),
                EstadoPrestamo.valueOf(rs.getString("estado")),
                rs.getDouble("saldo_pendiente")
        );
    }
}
