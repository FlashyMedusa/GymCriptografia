package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.ClienteDAO;
import org.example.gimnasio.security.SecurityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.crypto.SecretKey;
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
            // Obtener los datos del cliente
            String nombre = nombreField.getText();
            String domicilio = domicilioField.getText();
            String telefono = telefonoField.getText();
            String email = emailField.getText();
            String idUsuario = idUsuarioField.getText();
            String fechaInscripcion = fechaInscripcionField.getValue().toString();
            String fechaPago = fechaPagoField.getValue().toString();
            String tarjeta = tarjetaField.getText();  // Obtener tarjeta

            //Generar la clave AES-256
            SecretKey aesKey = SecurityUtils.generateAES256Key();

            // Cifrar los datos utilizando AES-256
            String domicilioCifrado = SecurityUtils.encryptAES(domicilio, aesKey);
            String telefonoCifrado = SecurityUtils.encryptAES(telefono, aesKey);
            String emailCifrado = SecurityUtils.encryptAES(email, aesKey);
            String tarjetaCifrada = SecurityUtils.encryptAES(tarjeta, aesKey); // Cifrar la tarjeta

            // Procesar la imagen de la fotografía (si es necesario)
            File foto = new File("path_to_image");  // Cambiar el path a la imagen real
            BufferedImage bufferedImage = ImageIO.read(foto);

            // Convertir BufferedImage a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos); // Asegúrate de usar el formato correcto (jpg, png, etc.)
            byte[] fotoBytes = baos.toByteArray();  // Obtener byte[] de la imagen

            // Convertir la foto a Base64 si es necesario
            String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);
            String fotoCifrada = SecurityUtils.encryptAES(fotoBase64, aesKey);

            // Suponiendo que se tiene la huella digital en formato de texto o bytes, se puede hashear con SHA-256
            String huellaDigital = "huellaCapturada";  // Esta es una suposición, reemplázalo por el dato real de la huella
            String huellaHash = SecurityUtils.hashSHA256(huellaDigital);  // Generar el hash de la huella digital

            // Insertar el cliente en la base de datos (incluyendo los datos cifrados y hash)
            ClienteDAO.createCliente(nombre, domicilioCifrado, telefonoCifrado, emailCifrado, idUsuario, fechaInscripcion, fechaPago, tarjetaCifrada, huellaHash.getBytes(), fotoCifrada.getBytes());

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
