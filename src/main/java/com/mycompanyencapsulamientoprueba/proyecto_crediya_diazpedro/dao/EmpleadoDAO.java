package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.dao;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.CrediYaException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Empleado;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.RolEmpleado;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.persistence.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla "empleados" de MySQL.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class EmpleadoDAO {

    /**
     * Inserta el empleado y le asigna el ID generado por MySQL.
     */
    public void guardar(Empleado empleado) {
        String sql = "INSERT INTO empleados (nombre, documento, rol, correo, salario) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.MysConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getDocumento());
            ps.setString(3, empleado.getRol().name());
            ps.setString(4, empleado.getCorreo());
            ps.setDouble(5, empleado.getSalario());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    empleado.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new CrediYaException("Error al guardar empleado: " + e.getMessage(), e);
        }
    }

    public List<Empleado> listar() {

        List<Empleado> empleados = new ArrayList<>();

        String sql = "SELECT id, nombre, documento, rol, correo, salario FROM empleados ORDER BY id";

        try (Connection con = ConexionBD.MysConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                empleados.add(mapearEmpleado(rs));
            }

        } catch (SQLException e) {
            throw new CrediYaException("Error al listar empleados: " + e.getMessage(), e);
        }

        return empleados;
    }

    /**
     * Busca un empleado por su ID. Retorna null si no existe.
     */
    public Empleado buscarPorId(int id) {

        String sql = "SELECT id, nombre, documento, rol, correo, salario FROM empleados WHERE id = ?";

        try (Connection con = ConexionBD.MysConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearEmpleado(rs);
                }
            }

        } catch (SQLException e) {
            throw new CrediYaException("Error al buscar empleado: " + e.getMessage(), e);
        }

        return null;
    }

    private Empleado mapearEmpleado(ResultSet rs) throws SQLException {
        return new Empleado(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("documento"),
                rs.getString("correo"),
                RolEmpleado.valueOf(rs.getString("rol")),
                rs.getDouble("salario")
        );
    }
}
