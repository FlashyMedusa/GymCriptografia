package org.example.gimnasio.database;

import org.example.gimnasio.security.SecurityUtils;
import org.example.gimnasio.controller.AccesoHuellaController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class ClienteDAO {
    public static void createCliente(String nombre, String domicilio, String telefono, String email, String idUsuario,
                                     String fechaInscripcion, String fechaPago, String tarjeta, byte[] huella, byte[] fotografia) {
        try (Connection conn = GimnasioDB.connect()) {
            String insertCliente = "INSERT INTO clientes (nombre, domicilio, telefono, email, id_usuario, fecha_inscripcion, fecha_pago, tarjeta, huella, fotografia) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertCliente)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, domicilio);
                pstmt.setString(3, telefono);
                pstmt.setString(4, email);
                pstmt.setString(5, idUsuario);
                pstmt.setString(6, fechaInscripcion);
                pstmt.setString(7, fechaPago);
                pstmt.setString(8, tarjeta); // cifrado de tarjeta
                pstmt.setBytes(9, huella); // Se almacenará la huella cifrada
                pstmt.setBytes(10, fotografia); // Se almacenará la foto cifrada

                pstmt.executeUpdate();
            }

        } catch (Exception e) {
            System.out.println("Error al insertar cliente: " + e.getMessage());
        }
    }

    public static String getHuellaHash(String huellaDigitalEscaneada) {
        String huellaHash = null;
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT huella FROM clientes WHERE huella = ?";  // Buscamos la huella registrada
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, huellaDigitalEscaneada); // Buscamos la huella capturada
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    huellaHash = rs.getString("huella");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener la huella del cliente: " + e.getMessage());
        }
        return huellaHash;
    }
}
