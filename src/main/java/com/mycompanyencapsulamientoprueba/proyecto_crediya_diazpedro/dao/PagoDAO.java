package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.dao;

import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.exception.CrediYaException;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Pago;
import com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.model.Prestamo;
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
 * Acceso a la tabla "pagos" de MySQL.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class PagoDAO {

    /**
     * Guarda el pago y actualiza el saldo y el estado del préstamo en una
     * sola transacción: o se guardan ambos cambios o no se guarda ninguno.
     */
    public void guardar(Pago pago) {

        String sqlPago = "INSERT INTO pagos (prestamo_id, fecha_pago, monto) VALUES (?, ?, ?)";
        String sqlPrestamo = "UPDATE prestamos SET estado = ?, saldo_pendiente = ? WHERE id = ?";

        Prestamo prestamo = pago.getPrestamo();

        try (Connection con = ConexionBD.MysConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement psPago = con.prepareStatement(sqlPago, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psPrestamo = con.prepareStatement(sqlPrestamo)) {

                psPago.setInt(1, prestamo.getId());
                psPago.setDate(2, Date.valueOf(pago.getFechaPago()));
                psPago.setDouble(3, pago.getMonto());
                psPago.executeUpdate();

                try (ResultSet rs = psPago.getGeneratedKeys()) {
                    if (rs.next()) {
                        pago.setId(rs.getInt(1));
                    }
                }

                psPrestamo.setString(1, prestamo.getEstado().name());
                psPrestamo.setDouble(2, prestamo.getSaldoPendiente());
                psPrestamo.setInt(3, prestamo.getId());
                psPrestamo.executeUpdate();

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new CrediYaException("Error al registrar el pago: " + e.getMessage(), e);
        }
    }

    /**
     * Historial de pagos de un préstamo, del más antiguo al más reciente.
     */
    public List<Pago> listarPorPrestamo(Prestamo prestamo) {

        List<Pago> pagos = new ArrayList<>();

        String sql = "SELECT id, fecha_pago, monto FROM pagos WHERE prestamo_id = ? ORDER BY fecha_pago, id";

        try (Connection con = ConexionBD.MysConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, prestamo.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pagos.add(new Pago(
                            rs.getInt("id"),
                            prestamo,
                            rs.getDate("fecha_pago").toLocalDate(),
                            rs.getDouble("monto")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new CrediYaException("Error al listar pagos: " + e.getMessage(), e);
        }

        return pagos;
    }
}
