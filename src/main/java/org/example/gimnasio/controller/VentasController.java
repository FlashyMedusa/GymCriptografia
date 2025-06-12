package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.VentaDAO;
import org.example.gimnasio.security.SecurityUtils;

import java.security.KeyPair;
import java.security.PrivateKey;

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

            // Generar el par de claves ECDSA (en un entorno real, las claves se deben generar previamente)
            KeyPair ecdsaKeyPair = SecurityUtils.generateECDSAKeyPair();  // Genera el par de claves
            PrivateKey privateKey = ecdsaKeyPair.getPrivate();  // Obtener la clave privada

            // Firmar los datos de la venta (producto y monto) utilizando la clave privada
            String dataToSign = producto + monto;
            String firmaDigital = SecurityUtils.signECDSA(dataToSign, privateKey);  // Firmar los datos con la clave privada

            // Registrar la venta en la base de datos, incluyendo la firma digital
            VentaDAO.registerVenta(idCliente, producto, monto, metodoPago, fechaVenta, firmaDigital);

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
