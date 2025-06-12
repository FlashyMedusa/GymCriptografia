package org.example.gimnasio.database;

import org.example.gimnasio.security.SecurityUtils;

import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.Base64;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EntrenadorDAO {

    // Método para guardar la clave pública en la base de datos
    public static void savePublicKey(String idUsuario, PublicKey publicKey) {
        try (Connection conn = GimnasioDB.connect()) {
            String insertPublicKey = "INSERT INTO entrenadores (id_usuario, public_key) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertPublicKey)) {
                pstmt.setString(1, idUsuario);
                pstmt.setString(2, SecurityUtils.publicKeyToBase64(publicKey));  // Convertimos la clave pública a Base64
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Error al guardar la clave pública: " + e.getMessage());
        }
    }

    // Método para obtener la clave pública desde la base de datos
    public static PublicKey getPublicKey(String idUsuario) {
        PublicKey publicKey = null;
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT public_key FROM entrenadores WHERE id_usuario = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, idUsuario);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String publicKeyBase64 = rs.getString("public_key");
                    publicKey = SecurityUtils.base64ToPublicKey(publicKeyBase64);  // Convertimos de Base64 a PublicKey
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener la clave pública: " + e.getMessage());
        }
        return publicKey;
    }

    // Método para guardar la clave privada de forma segura (no se recomienda hacerlo en la base de datos de manera directa en producción)
    public static void savePrivateKey(String idUsuario, PrivateKey privateKey) {
        // Lógica para almacenar la clave privada de manera segura, podría ser en un almacén seguro de claves o cifrada en la base de datos
    }

    // Método para obtener la clave privada de manera segura (debería ser gestionada de forma segura)
    public static PrivateKey getPrivateKey(String idUsuario) {
        // Lógica para recuperar la clave privada desde un almacén seguro o un sistema de almacenamiento seguro
        return null;  // Retornar la clave privada de manera segura
    }
}
