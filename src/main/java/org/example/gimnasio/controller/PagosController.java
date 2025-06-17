package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.PagoDAO;
import org.example.gimnasio.database.EntrenadorDAO;
import org.example.gimnasio.database.ClienteDAO;
import org.example.gimnasio.security.*;

import java.security.PrivateKey;
import java.time.LocalDate;

public class PagosController {

    @FXML private TextField idClienteField;
    @FXML private TextField montoField;
    @FXML private DatePicker fechaPagoField;
    @FXML private ComboBox<String> tipoPagoCombo;
    @FXML private TextField idEntrenadorField;
    @FXML private PasswordField passwordEntrenadorField;
    @FXML private Button guardarBtn;
    @FXML private TextArea conceptoArea;

    @FXML
    public void initialize() {
        // Configurar combo de tipos de pago
        tipoPagoCombo.getItems().addAll(
                "Mensualidad", "Inscripción", "Renovación", "Producto", "Servicio Adicional"
        );

        // Establecer fecha actual por defecto
        fechaPagoField.setValue(LocalDate.now());
    }

    @FXML
    public void handleRegistrarPago(ActionEvent event) {
        try {
            // Validar campos
            if (!validarCampos()) {
                return;
            }

            int idCliente = Integer.parseInt(idClienteField.getText().trim());
            double monto = Double.parseDouble(montoField.getText().trim());
            String fechaPago = fechaPagoField.getValue().toString();
            String tipoPago = tipoPagoCombo.getValue();
            String concepto = conceptoArea.getText().trim();
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

            // Crear datos del pago para firmar
            String dataToSign = String.format("%d|%.2f|%s|%s|%s",
                    idCliente, monto, fechaPago, tipoPago, concepto);

            // Firmar la transacción
            String firmaDigital = DigitalSignatureUtils.signTransaction(dataToSign, privateKey);

            // Registrar pago
            PagoDAO.registerPago(idCliente, monto, fechaPago, tipoPago, concepto, firmaDigital, idEntrenador);

            mostrarExito(String.format("Pago registrado exitosamente.\nTipo: %s\nMonto: $%.2f\nFirma digital verificada.",
                    tipoPago, monto));
            limpiarFormulario();

        } catch (NumberFormatException e) {
            mostrarError("Error en formato numérico: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al registrar el pago: " + e.getMessage());
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

        if (tipoPagoCombo.getValue() == null) {
            mostrarError("Seleccione un tipo de pago.");
            return false;
        }

        if (fechaPagoField.getValue() == null) {
            mostrarError("Seleccione la fecha de pago.");
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
        montoField.clear();
        tipoPagoCombo.setValue(null);
        conceptoArea.clear();
        fechaPagoField.setValue(LocalDate.now());
        passwordEntrenadorField.clear();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText("Pago Registrado");
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