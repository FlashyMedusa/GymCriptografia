package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.VentaDAO;

public class VentasController {

    @FXML private TextField idClienteField;
    @FXML private TextField productoField;
    @FXML private TextField montoField;
    @FXML private TextField metodoPagoField;
    @FXML private DatePicker fechaVentaField;
    @FXML private Button guardarBtn;

    // Método para registrar venta
    @FXML
    public void handleRegistrarVenta(ActionEvent event) {
        try {
            int idCliente = Integer.parseInt(idClienteField.getText());
            String producto = productoField.getText();
            double monto = Double.parseDouble(montoField.getText());
            String metodoPago = metodoPagoField.getText();
            String fechaVenta = fechaVentaField.getValue().toString();

            // Registrar la venta en la base de datos
            VentaDAO.registerVenta(idCliente, producto, monto, metodoPago, fechaVenta);

            // Confirmación al usuario
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Venta Registrada");
            alert.setHeaderText(null);
            alert.setContentText("Venta registrada exitosamente.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al registrar la venta: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
