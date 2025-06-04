package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.EntrenadorDAO;

public class RegistroEntrenadorController {

    @FXML private TextField nombreField;
    @FXML private TextField idUsuarioField;
    @FXML private TextField horarioField;
    @FXML private Button guardarBtn;

    // Método para guardar entrenador
    @FXML
    public void handleGuardarEntrenador(ActionEvent event) {
        try {
            String nombre = nombreField.getText();
            String idUsuario = idUsuarioField.getText();
            String horario = horarioField.getText();

            // Insertar entrenador en la base de datos
            EntrenadorDAO.createEntrenador(nombre, idUsuario, horario);

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
