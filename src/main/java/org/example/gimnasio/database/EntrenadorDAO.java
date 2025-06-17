package org.example.gimnasio.database;

import org.example.gimnasio.security.*;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EntrenadorDAO {

    public static void createEntrenador(String nombre, String idUsuario, String horario, PublicKey publicKey) throws Exception {
        try (Connection conn = GimnasioDB.connect()) {
            String insertEntrenador = """
                INSERT INTO entrenadores (nombre, id_usuario, horario, public_key) 
                VALUES (?, ?, ?, ?)""";

            try (PreparedStatement pstmt = conn.prepareStatement(insertEntrenador)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, idUsuario);
                pstmt.setString(3, horario);
                pstmt.setString(4, KeyUtils.publicKeyToBase64(publicKey));
                pstmt.executeUpdate();
            }
        }
    }

    public static void savePublicKey(String idUsuario, PublicKey publicKey) throws Exception {
        try (Connection conn = GimnasioDB.connect()) {
            String updateKey = "UPDATE entrenadores SET public_key = ? WHERE id_usuario = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateKey)) {
                pstmt.setString(1, KeyUtils.publicKeyToBase64(publicKey));
                pstmt.setString(2, idUsuario);
                pstmt.executeUpdate();
            }
        }
    }

    public static PublicKey getPublicKey(String idUsuario) {
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT public_key FROM entrenadores WHERE id_usuario = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, idUsuario);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String publicKeyBase64 = rs.getString("public_key");
                    return KeyUtils.base64ToPublicKey(publicKeyBase64);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener la clave pública: " + e.getMessage());
        }
        return null;
    }

    // En producción, las claves privadas deberían almacenarse en un HSM o sistema seguro
    public static void savePrivateKeyEncrypted(String idUsuario, PrivateKey privateKey, String masterPassword) throws Exception {
        // Cifrar la clave privada con una clave maestra antes de almacenar
        String privateKeyBase64 = KeyUtils.privateKeyToBase64(privateKey);
        // Aquí deberías implementar cifrado adicional con la clave maestra

        try (Connection conn = GimnasioDB.connect()) {
            String updateKey = "UPDATE entrenadores SET private_key_encrypted = ? WHERE id_usuario = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateKey)) {
                pstmt.setString(1, privateKeyBase64); // En producción: cifrar esto
                pstmt.setString(2, idUsuario);
                pstmt.executeUpdate();
            }
        }
    }

    public static PrivateKey getPrivateKeyDecrypted(String idUsuario, String masterPassword) throws Exception {
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT private_key_encrypted FROM entrenadores WHERE id_usuario = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, idUsuario);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String encryptedKey = rs.getString("private_key_encrypted");
                    // Aquí deberías descifrar con la clave maestra
                    return KeyUtils.base64ToPrivateKey(encryptedKey);
                }
            }
        }
        return null;
    }

    public static boolean verificarEntrenador(String idUsuario) {
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT COUNT(*) FROM entrenadores WHERE id_usuario = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, idUsuario);
                ResultSet rs = pstmt.executeQuery();
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar entrenador: " + e.getMessage());
            return false;
        }
    }
}