package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class MainController {

    @FXML
    private Button btnRegistroCliente, btnRegistroEntrenador, btnAccesoHuella, btnVentas, btnPagos, btnCorte;
    @FXML
    private StackPane mainContent;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            Button source = (Button) event.getSource();
            Parent root = null;

            if (source == btnRegistroCliente) {
                root = loadFXML("/org/example/gimnasio/view/registroCliente.fxml");
            } else if (source == btnRegistroEntrenador) {
                root = loadFXML("/org/example/gimnasio/view/registroEntrenador.fxml");
            } else if (source == btnAccesoHuella) {
                root = loadFXML("/org/example/gimnasio/view/accesoHuella.fxml");
            } else if (source == btnVentas) {
                root = loadFXML("/org/example/gimnasio/view/ventas.fxml");
            } else if (source == btnPagos) {
                root = loadFXML("/org/example/gimnasio/view/pagos.fxml");
            } else if (source == btnCorte) {
                root = loadFXML("/org/example/gimnasio/view/corteCaja.fxml");
            }

            if (root != null) {
                mainContent.getChildren().setAll(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Parent loadFXML(String fxml) throws Exception {
        return FXMLLoader.load(getClass().getResource(fxml));
    }
}
