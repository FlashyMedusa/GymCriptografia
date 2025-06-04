package org.example.gimnasio.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GimnasioDB {

    private static final String DB_PATH = "db/gimnasio.db";  // Cambia esto a cualquier ruta local accesible
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    // Conexión a la base de datos
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Crear tablas necesarias en la base de datos
    public static void createTables() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Crear la tabla de clientes
            String createClientesTable = "CREATE TABLE IF NOT EXISTS clientes (" +
                    "id_cliente INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "domicilio TEXT," +
                    "telefono TEXT," +
                    "email TEXT," +
                    "id_usuario TEXT UNIQUE NOT NULL," +
                    "fecha_inscripcion TEXT," +
                    "fecha_pago TEXT," +
                    "tarjeta TEXT," +  // Aquí guardaremos el dato cifrado
                    "huella BLOB," +  // Huella digital cifrada
                    "fotografia BLOB" +  // Fotografía cifrada
                    ")";
            stmt.executeUpdate(createClientesTable);

            // Crear tabla de entrenadores
            String createEntrenadoresTable = "CREATE TABLE IF NOT EXISTS entrenadores (" +
                    "id_entrenador INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "id_usuario TEXT UNIQUE NOT NULL," +
                    "horario TEXT" +
                    ")";
            stmt.executeUpdate(createEntrenadoresTable);

            // Crear tabla de ventas
            String createVentasTable = "CREATE TABLE IF NOT EXISTS ventas (" +
                    "id_venta INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_cliente INTEGER," +
                    "producto TEXT NOT NULL," +
                    "monto REAL NOT NULL," +
                    "metodo_pago TEXT," +
                    "fecha_venta TEXT," +
                    "FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)" +
                    ")";
            stmt.executeUpdate(createVentasTable);

            // Crear tabla de pagos
            String createPagosTable = "CREATE TABLE IF NOT EXISTS pagos (" +
                    "id_pago INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_cliente INTEGER," +
                    "monto REAL NOT NULL," +
                    "fecha_pago TEXT," +
                    "FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)" +
                    ")";
            stmt.executeUpdate(createPagosTable);

        } catch (SQLException e) {
            System.out.println("Error al crear las tablas: " + e.getMessage());
        }
    }
}
