package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.ClienteDAO;
import org.example.gimnasio.security.SecurityUtils;

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

    // Método para guardar cliente
    @FXML
    public void handleGuardarCliente(ActionEvent event) {
        try {
            String nombre = nombreField.getText();
            String domicilio = domicilioField.getText();
            String telefono = telefonoField.getText();
            String email = emailField.getText();
            String idUsuario = idUsuarioField.getText();
            String fechaInscripcion = fechaInscripcionField.getValue().toString();
            String fechaPago = fechaPagoField.getValue().toString();
            String tarjeta = tarjetaField.getText();  // Obtener tarjeta

            // Cifrar la tarjeta
            String tarjetaCifrada = SecurityUtils.encryptAES(tarjeta);

            // Convertir foto en base64 si es necesario
            File foto = new File("path_to_image");  // Cambiar el path
            BufferedImage bufferedImage = ImageIO.read(foto);

            // Convertir BufferedImage a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos); // Asegúrate de usar el formato correcto (jpg, png, etc.)
            byte[] fotoBytes = baos.toByteArray();  // Obtener byte[] de la imagen

            // Convertir a Base64 si es necesario
            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);

            // Insertar cliente en la base de datos
            ClienteDAO.createCliente(nombre, domicilio, telefono, email, idUsuario, fechaInscripcion, fechaPago, tarjetaCifrada, fotoBytes, fotoBytes);

            // Confirmación al usuario
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro Exitoso");
            alert.setHeaderText(null);
            alert.setContentText("Cliente registrado exitosamente.");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al procesar la imagen: " + e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al registrar el cliente: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
