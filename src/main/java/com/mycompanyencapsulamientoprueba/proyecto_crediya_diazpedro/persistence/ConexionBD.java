package com.mycompanyencapsulamientoprueba.proyecto_crediya_diazpedro.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Única clase de conexión a MySQL (base de datos crediya_db).
 * Cada llamada abre una conexión nueva que el DAO cierra con try-with-resources.
 * Si MySQL no responde se lanza SQLException para que el DAO informe el error.
 *
 * @author PEDRO ANTONIO DÍAZ V
 */
public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/crediya_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Pedrodiaz22334!";

    private ConexionBD() {
    }

    public static Connection MysConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
