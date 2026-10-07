package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.dao;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.CrediYaException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Cliente;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.persistence.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla "clientes" de MySQL.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class ClienteDAO {

    /**
     * Inserta el cliente y le asigna el ID generado por MySQL.
     */
    public void guardar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, documento, correo, telefono) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.MysConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDocumento());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new CrediYaException("Error al guardar cliente: " + e.getMessage(), e);
        }
    }

    public List<Cliente> listar() {

        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT id, nombre, documento, correo, telefono FROM clientes ORDER BY id";

        try (Connection con = ConexionBD.MysConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            throw new CrediYaException("Error al listar clientes: " + e.getMessage(), e);
        }

        return clientes;
    }

    /**
     * Busca un cliente por su ID. Retorna null si no existe.
     */
    public Cliente buscarPorId(int id) {

        String sql = "SELECT id, nombre, documento, correo, telefono FROM clientes WHERE id = ?";

        try (Connection con = ConexionBD.MysConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }

        } catch (SQLException e) {
            throw new CrediYaException("Error al buscar cliente: " + e.getMessage(), e);
        }

        return null;
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("documento"),
                rs.getString("correo"),
                rs.getString("telefono")
        );
    }
}
