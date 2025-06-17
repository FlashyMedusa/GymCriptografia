package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.ClienteDAO;
import org.example.gimnasio.database.EntrenadorDAO;
import org.example.gimnasio.database.VentaDAO;
import org.example.gimnasio.security.DigitalSignatureUtils;
import org.example.gimnasio.security.SecurityUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.LocalDate;

public class VentasController {

    @FXML private TextField idClienteField;
    @FXML private TextField productoField;
    @FXML private TextField montoField;
    @FXML private ComboBox<String> metodoPagoCombo;
    @FXML private DatePicker fechaVentaField;
    @FXML private TextField idEntrenadorField;
    @FXML private PasswordField passwordEntrenadorField;
    @FXML private Button guardarBtn;

    @FXML
    public void initialize() {
        // Configurar combo de métodos de pago
        metodoPagoCombo.getItems().addAll(
                "Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito",
                "Transferencia", "Cheque"
        );

        // Establecer fecha actual por defecto
        fechaVentaField.setValue(LocalDate.now());
    }

    @FXML
    public void handleRegistrarVenta(ActionEvent event) {
        try {
            // Validar campos
            if (!validarCampos()) {
                return;
            }

            int idCliente = Integer.parseInt(idClienteField.getText().trim());
            String producto = productoField.getText().trim();
            double monto = Double.parseDouble(montoField.getText().trim());
            String metodoPago = metodoPagoCombo.getValue();
            String fechaVenta = fechaVentaField.getValue().toString();
            String idEntrenador = idEntrenadorField.getText().trim();
            String passwordEntrenador = passwordEntrenadorField.getText();

            // Verificar que el cliente existe
            if (!ClienteDAO.verificarCliente(String.valueOf(idCliente))) {
                mostrarError("Cliente no encontrado.");
                return;
            }

            // Obtener clave privada del entrenador
            PrivateKey privateKey = EntrenadorDAO.getPrivateKeyDecrypted(idEntrenador, passwordEntrenador);
            if (privateKey == null) {
                mostrarError("Credenciales de entrenador inválidas.");
                return;
            }

            // Crear datos de la transacción para firmar
            String dataToSign = String.format("%d|%s|%.2f|%s|%s",
                    idCliente, producto, monto, metodoPago, fechaVenta);

            // Firmar la transacción
            String firmaDigital = DigitalSignatureUtils.signTransaction(dataToSign, privateKey);

            // Registrar venta
            VentaDAO.registerVenta(idCliente, producto, monto, metodoPago, fechaVenta, firmaDigital, idEntrenador);

            mostrarExito(String.format("Venta registrada exitosamente.\nProducto: %s\nMonto: $%.2f\nFirma digital verificada.",
                    producto, monto));
            limpiarFormulario();

        } catch (NumberFormatException e) {
            mostrarError("Error en formato numérico: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al registrar la venta: " + e.getMessage());
        }
    }

    @FXML
    public void handleVerificarCliente(ActionEvent event) {
        try {
            if (idClienteField.getText().trim().isEmpty()) {
                mostrarError("Ingrese el ID del cliente.");
                return;
            }

            String idCliente = idClienteField.getText().trim();
            boolean existe = ClienteDAO.verificarCliente(idCliente);

            if (existe) {
                mostrarInfo("Cliente verificado correctamente.");
            } else {
                mostrarError("Cliente no encontrado en el sistema.");
            }
        } catch (Exception e) {
            mostrarError("Error al verificar cliente: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (idClienteField.getText().trim().isEmpty()) {
            mostrarError("El ID del cliente es obligatorio.");
            return false;
        }

        try {
            Integer.parseInt(idClienteField.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("El ID del cliente debe ser un número válido.");
            return false;
        }

        if (productoField.getText().trim().isEmpty()) {
            mostrarError("El producto es obligatorio.");
            return false;
        }

        if (montoField.getText().trim().isEmpty()) {
            mostrarError("El monto es obligatorio.");
            return false;
        }

        try {
            double monto = Double.parseDouble(montoField.getText().trim());
            if (monto <= 0) {
                mostrarError("El monto debe ser mayor a cero.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El monto debe ser un número válido.");
            return false;
        }

        if (metodoPagoCombo.getValue() == null) {
            mostrarError("Seleccione un método de pago.");
            return false;
        }

        if (fechaVentaField.getValue() == null) {
            mostrarError("Seleccione la fecha de venta.");
            return false;
        }

        if (idEntrenadorField.getText().trim().isEmpty()) {
            mostrarError("El ID del entrenador es obligatorio.");
            return false;
        }

        if (passwordEntrenadorField.getText().isEmpty()) {
            mostrarError("La contraseña del entrenador es obligatoria.");
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        idClienteField.clear();
        productoField.clear();
        montoField.clear();
        metodoPagoCombo.setValue(null);
        fechaVentaField.setValue(LocalDate.now());
        passwordEntrenadorField.clear();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText("Venta Registrada");
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

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
