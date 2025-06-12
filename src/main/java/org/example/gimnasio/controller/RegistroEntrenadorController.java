package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.EntrenadorDAO;
import org.example.gimnasio.security.SecurityUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RegistroEntrenadorController {

    @FXML private TextField nombreField;
    @FXML private TextField idUsuarioField;
    @FXML private TextField horarioField;
    @FXML private Button guardarBtn;

    // Método para guardar entrenador
    @FXML
    public void handleGuardarEntrenador(ActionEvent event) {
        try {
            // Obtener los datos del entrenador
            String nombre = nombreField.getText();
            String idUsuario = idUsuarioField.getText();
            String horario = horarioField.getText();

            // Generar el par de claves ECDSA para el entrenador
            KeyPair ecdsaKeyPair = SecurityUtils.generateECDSAKeyPair();  // Genera un par de claves ECDSA

            PrivateKey privateKey = ecdsaKeyPair.getPrivate();  // Obtener la clave privada
            PublicKey publicKey = ecdsaKeyPair.getPublic();    // Obtener la clave pública

            // Aquí puedes almacenar la clave privada de manera segura
            // Asegúrate de almacenar la clave privada de manera segura (por ejemplo, cifrada en la base de datos o en un sistema de almacenamiento seguro)

            // Para este ejemplo, solo almacenamos la clave pública en la base de datos
            // Puedes usar la clave pública para verificar firmas del entrenador más tarde

            // Insertar entrenador en la base de datos
            // La clave pública ECDSA se almacenará en la base de datos (la clave privada debe ser manejada de forma segura)
            EntrenadorDAO.createEntrenador(nombre, idUsuario, horario, publicKey);

            // Confirmación al usuario
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro Exitoso");
            alert.setHeaderText(null);
            alert.setContentText("Entrenador registrado exitosamente.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al registrar el entrenador: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
