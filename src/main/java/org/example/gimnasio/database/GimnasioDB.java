package org.example.gimnasio.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GimnasioDB {

    private static final String DB_PATH = "db/gimnasio.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void createTables() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Crear tabla de clientes
            String createClientesTable = """
                CREATE TABLE IF NOT EXISTS clientes (
                    id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    domicilio TEXT,
                    telefono TEXT,
                    email TEXT,
                    id_usuario TEXT UNIQUE NOT NULL,
                    fecha_inscripcion TEXT,
                    fecha_pago TEXT,
                    tarjeta TEXT,
                    huella BLOB,
                    fotografia BLOB
                )""";
            stmt.executeUpdate(createClientesTable);

            // Crear tabla de entrenadores (corregida)
            String createEntrenadoresTable = """
                CREATE TABLE IF NOT EXISTS entrenadores (
                    id_entrenador INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    id_usuario TEXT UNIQUE NOT NULL,
                    horario TEXT,
                    public_key TEXT,
                    private_key_encrypted TEXT
                )""";
            stmt.executeUpdate(createEntrenadoresTable);

            // Crear tabla de ventas
            String createVentasTable = """
                CREATE TABLE IF NOT EXISTS ventas (
                    id_venta INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_cliente INTEGER,
                    producto TEXT NOT NULL,
                    monto REAL NOT NULL,
                    metodo_pago TEXT,
                    fecha_venta TEXT,
                    firma_digital TEXT,
                    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
                )""";
            stmt.executeUpdate(createVentasTable);

            // Crear tabla de pagos
            String createPagosTable = """
                CREATE TABLE IF NOT EXISTS pagos (
                    id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_cliente INTEGER,
                    monto REAL NOT NULL,
                    fecha_pago TEXT,
                    firma_digital TEXT,
                    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
                )""";
            stmt.executeUpdate(createPagosTable);

            // Crear tabla para almacenar claves AES de clientes
            String createClienteKeysTable = """
                CREATE TABLE IF NOT EXISTS cliente_keys (
                    id_cliente INTEGER PRIMARY KEY,
                    aes_key_encrypted TEXT NOT NULL,
                    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
                )""";
            stmt.executeUpdate(createClienteKeysTable);

        } catch (SQLException e) {
            System.out.println("Error al crear las tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}