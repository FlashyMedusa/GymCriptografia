package org.example.gimnasio.database;

import org.example.gimnasio.security.*;
import javax.crypto.SecretKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class ClienteDAO {

    public static void createCliente(String nombre, String domicilio, String telefono, String email,
                                     String idUsuario, String fechaInscripcion, String fechaPago,
                                     String tarjeta, byte[] huella, byte[] fotografia) throws Exception {

        // Generar clave AES para este cliente
        SecretKey aesKey = EncryptionUtils.generateAES256Key();

        // Cifrar datos sensibles
        String domicilioCifrado = EncryptionUtils.encryptAES(domicilio, aesKey);
        String telefonoCifrado = EncryptionUtils.encryptAES(telefono, aesKey);
        String emailCifrado = EncryptionUtils.encryptAES(email, aesKey);
        String tarjetaCifrada = EncryptionUtils.encryptAES(tarjeta, aesKey);

        try (Connection conn = GimnasioDB.connect()) {
            conn.setAutoCommit(false);

            try {
                // Insertar cliente
                String insertCliente = """
                    INSERT INTO clientes (nombre, domicilio, telefono, email, id_usuario, 
                                        fecha_inscripcion, fecha_pago, tarjeta, huella, fotografia) 
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

                int clienteId;
                try (PreparedStatement pstmt = conn.prepareStatement(insertCliente, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, nombre);
                    pstmt.setString(2, domicilioCifrado);
                    pstmt.setString(3, telefonoCifrado);
                    pstmt.setString(4, emailCifrado);
                    pstmt.setString(5, idUsuario);
                    pstmt.setString(6, fechaInscripcion);
                    pstmt.setString(7, fechaPago);
                    pstmt.setString(8, tarjetaCifrada);
                    pstmt.setBytes(9, huella);
                    pstmt.setBytes(10, fotografia);

                    pstmt.executeUpdate();

                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        clienteId = rs.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del cliente");
                    }
                }

                // Guardar clave AES cifrada (en producción debería cifrarse con clave maestra)
                String insertKey = "INSERT INTO cliente_keys (id_cliente, aes_key_encrypted) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertKey)) {
                    pstmt.setInt(1, clienteId);
                    pstmt.setString(2, EncryptionUtils.secretKeyToBase64(aesKey));
                    pstmt.executeUpdate();
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public static String getHuellaHash(String huellaDigitalEscaneada) {
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT huella FROM clientes WHERE huella = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setBytes(1, huellaDigitalEscaneada.getBytes());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return new String(rs.getBytes("huella"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener la huella del cliente: " + e.getMessage());
        }
        return null;
    }

    public static boolean verificarCliente(String idUsuario) {
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT COUNT(*) FROM clientes WHERE id_usuario = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, idUsuario);
                ResultSet rs = pstmt.executeQuery();
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar cliente: " + e.getMessage());
            return false;
        }
    }
}