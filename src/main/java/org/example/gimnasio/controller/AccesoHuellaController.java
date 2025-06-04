package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.ClienteDAO;
import org.example.gimnasio.security.SecurityUtils;

public class AccesoHuellaController {

    @FXML private Button escaneoHuellaBtn;
    @FXML private Label resultadoAccesoLabel;

    // Simulación de escaneo de huella digital
    @FXML
    public void handleEscanearHuella(ActionEvent event) {
        try {
            String huellaCapturada = "huella_simulada_123"; // Este sería el valor capturado por el escáner de huellas
            String huellaCapturadaHash = SecurityUtils.hashSHA256(huellaCapturada);
            String huellaHashRegistrada = ClienteDAO.getHuellaHash(huellaCapturadaHash); // Buscar en la DB el hash de la huella

            if (huellaHashRegistrada != null) {
                // Verificamos si la huella es válida
                boolean accesoValido = SecurityUtils.verifyHuella(huellaCapturadaHash, huellaHashRegistrada);
                if (accesoValido) {
                    resultadoAccesoLabel.setText("Acceso otorgado.");
                    // Acceso concedido, podemos continuar con el flujo de la aplicación
                } else {
                    resultadoAccesoLabel.setText("Huella no válida. Intente nuevamente.");
                }
            } else {
                resultadoAccesoLabel.setText("No se encontró huella registrada.");
            }
        } catch (Exception e) {
            resultadoAccesoLabel.setText("Error al verificar la huella: " + e.getMessage());
        }
    }
}
