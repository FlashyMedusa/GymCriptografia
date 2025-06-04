package org.example.gimnasio.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EntrenadorDAO {

    public static void createEntrenador(String nombre, String idUsuario, String horario) {
        try (Connection conn = GimnasioDB.connect()) {
            String insertEntrenador = "INSERT INTO entrenadores (nombre, id_usuario, horario) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertEntrenador)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, idUsuario);
                pstmt.setString(3, horario);

                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Error al insertar entrenador: " + e.getMessage());
        }
    }
}
