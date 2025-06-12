package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.PagoDAO;
import org.example.gimnasio.security.SecurityUtils;

import java.security.KeyPair;
import java.security.PrivateKey;

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

            // Generar el par de claves ECDSA (en un entorno real, las claves se deben generar previamente)
            KeyPair ecdsaKeyPair = SecurityUtils.generateECDSAKeyPair();  // Genera el par de claves
            PrivateKey privateKey = ecdsaKeyPair.getPrivate();  // Obtener la clave privada

            // Firmar los datos del pago (monto y fecha) utilizando la clave privada
            String dataToSign = monto + fechaPago;
            String firmaDigital = SecurityUtils.signECDSA(dataToSign, privateKey);  // Firmar los datos con la clave privada

            // Registrar el pago en la base de datos, incluyendo la firma digital
            PagoDAO.registerPago(idCliente, monto, fechaPago, firmaDigital);

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
