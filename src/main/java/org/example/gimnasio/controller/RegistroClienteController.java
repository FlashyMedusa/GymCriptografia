package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.gimnasio.database.ClienteDAO;
import org.example.gimnasio.security.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Base64;

public class RegistroClienteController {

    @FXML private TextField nombreField;
    @FXML private TextField domicilioField;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private TextField idUsuarioField;
    @FXML private DatePicker fechaInscripcionField;
    @FXML private DatePicker fechaPagoField;
    @FXML private PasswordField tarjetaField;
    @FXML private Button guardarBtn;
    @FXML private Button subirImagenBtn;
    @FXML private Button capturarHuellaBtn;
    @FXML private ImageView imageView;
    @FXML private Label estadoHuellaLabel;

    private File imagenSeleccionada;
    private String huellaDigitalCapturada;

    @FXML
    public void handleSubirImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar foto del cliente");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(subirImagenBtn.getScene().getWindow());
        if (file != null) {
            imagenSeleccionada = file;
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    public void handleCapturarHuella(ActionEvent event) {
        // Simulación de captura de huella digital
        try {
            huellaDigitalCapturada = simularCapturaHuella();
            estadoHuellaLabel.setText("✓ Huella capturada exitosamente");
            estadoHuellaLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            estadoHuellaLabel.setText("✗ Error al capturar huella");
            estadoHuellaLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void handleGuardarCliente(ActionEvent event) {
        try {
            // Validar campos obligatorios
            if (!validarCampos()) {
                return;
            }

            String nombre = nombreField.getText().trim();
            String domicilio = domicilioField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String email = emailField.getText().trim();
            String idUsuario = idUsuarioField.getText().trim();
            String fechaInscripcion = fechaInscripcionField.getValue().toString();
            String fechaPago = fechaPagoField.getValue().toString();
            String tarjeta = tarjetaField.getText();

            // Procesar imagen
            byte[] fotoBytes = null;
            if (imagenSeleccionada != null) {
                BufferedImage bufferedImage = ImageIO.read(imagenSeleccionada);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", baos);
                String fotoBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());
                fotoBytes = fotoBase64.getBytes();
            }

            // Procesar huella digital
            byte[] huellaBytes = null;
            if (huellaDigitalCapturada != null) {
                String huellaHash = HashUtils.hashSHA256(huellaDigitalCapturada);
                huellaBytes = huellaHash.getBytes();
            }

            // Crear cliente
            ClienteDAO.createCliente(nombre, domicilio, telefono, email, idUsuario,
                    fechaInscripcion, fechaPago, tarjeta, huellaBytes, fotoBytes);

            mostrarExito("Cliente registrado exitosamente.");
            limpiarFormulario();

        } catch (IOException e) {
            mostrarError("Error al procesar la imagen: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al registrar el cliente: " + e.getMessage());
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

        if (!SecurityValidator.isValidEmail(emailField.getText().trim())) {
            mostrarError("El formato del email no es válido.");
            return false;
        }

        if (!SecurityValidator.isValidPhone(telefonoField.getText().trim())) {
            mostrarError("El formato del teléfono no es válido (10-15 dígitos).");
            return false;
        }

        if (fechaInscripcionField.getValue() == null) {
            mostrarError("La fecha de inscripción es obligatoria.");
            return false;
        }

        if (imagenSeleccionada == null) {
            mostrarError("Por favor seleccione una imagen.");
            return false;
        }

        if (huellaDigitalCapturada == null) {
            mostrarError("Por favor capture la huella digital.");
            return false;
        }

        return true;
    }

    private String simularCapturaHuella() {
        // En producción, esto interactuaría con hardware real
        return "huella_simulada_" + System.currentTimeMillis();
    }

    private void limpiarFormulario() {
        nombreField.clear();
        domicilioField.clear();
        telefonoField.clear();
        emailField.clear();
        idUsuarioField.clear();
        fechaInscripcionField.setValue(null);
        fechaPagoField.setValue(null);
        tarjetaField.clear();
        imageView.setImage(null);
        imagenSeleccionada = null;
        huellaDigitalCapturada = null;
        estadoHuellaLabel.setText("Huella no capturada");
        estadoHuellaLabel.setStyle("-fx-text-fill: gray;");
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        estadoHuellaLabel.setText("Huella no capturada");
        estadoHuellaLabel.setStyle("-fx-text-fill: gray;");
    }
}