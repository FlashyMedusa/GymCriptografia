package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.EntrenadorDAO;
import org.example.gimnasio.security.DigitalSignatureUtils;
import org.example.gimnasio.security.SecurityUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RegistroEntrenadorController {

    @FXML private TextField nombreField;
    @FXML private TextField idUsuarioField;
    @FXML private TextField horarioField;
    @FXML private Button guardarBtn;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML
    public void handleGuardarEntrenador(ActionEvent event) {
        try {
            // Validar campos
            if (!validarCampos()) {
                return;
            }

            String nombre = nombreField.getText().trim();
            String idUsuario = idUsuarioField.getText().trim();
            String horario = horarioField.getText().trim();
            String password = passwordField.getText();

            // Generar par de claves ECDSA
            KeyPair ecdsaKeyPair = DigitalSignatureUtils.generateECDSAKeyPair();

            // Crear entrenador con clave pública
            EntrenadorDAO.createEntrenador(nombre, idUsuario, horario, ecdsaKeyPair.getPublic());

            // Guardar clave privada cifrada (en producción usar HSM)
            EntrenadorDAO.savePrivateKeyEncrypted(idUsuario, ecdsaKeyPair.getPrivate(), password);

            mostrarExito("Entrenador registrado exitosamente.\nClave pública generada y almacenada.");
            limpiarFormulario();

        } catch (Exception e) {
            mostrarError("Error al registrar el entrenador: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (nombreField.getText().trim().isEmpty()) {
            mostrarError("El nombre es obligatorio.");
            return false;
        }

        if (idUsuarioField.getText().trim().isEmpty()) {
            mostrarError("El ID de usuario es obligatorio.");
            return false;
        }

        if (horarioField.getText().trim().isEmpty()) {
            mostrarError("El horario es obligatorio.");
            return false;
        }

        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.isEmpty()) {
            mostrarError("La contraseña es obligatoria.");
            return false;
        }

        if (password.length() < 8) {
            mostrarError("La contraseña debe tener al menos 8 caracteres.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            mostrarError("Las contraseñas no coinciden.");
            return false;
        }

        // Verificar que el usuario no exista
        if (EntrenadorDAO.verificarEntrenador(idUsuarioField.getText().trim())) {
            mostrarError("Ya existe un entrenador con este ID de usuario.");
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        nombreField.clear();
        idUsuarioField.clear();
        horarioField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText("Registro Exitoso");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error de Validación");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
