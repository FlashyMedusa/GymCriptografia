package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.PagoDAO;

public class PagosController {

    @FXML private TextField idClienteField;
    @FXML private TextField montoField;
    @FXML private DatePicker fechaPagoField;
    @FXML private Button guardarBtn;

    // Método para registrar el pago
    @FXML
    public void handleRegistrarPago(ActionEvent event) {
        try {
            int idCliente = Integer.parseInt(idClienteField.getText());
            double monto = Double.parseDouble(montoField.getText());
            String fechaPago = fechaPagoField.getValue().toString();

            // Registrar el pago en la base de datos
            PagoDAO.registerPago(idCliente, monto, fechaPago);

            // Confirmación al usuario
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pago Registrado");
            alert.setHeaderText(null);
            alert.setContentText("Pago registrado exitosamente.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al registrar el pago: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
