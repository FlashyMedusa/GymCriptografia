package org.example.gimnasio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.gimnasio.database.GimnasioDB; // Importar la clase GimnasioDB
import javafx.scene.control.Alert;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
    try {
        // Inicializar la base de datos y crear las tablas si no existen
        GimnasioDB.createTables();// Aseguramos que las tablas se creen al iniciar la aplicación

        // Cargar la vista principal (MainView)
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/gimnasio/view/main.fxml"));
        primaryStage.setTitle("Sistema de Gimnasio");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    } catch (Exception e) {
        System.err.println("Error al iniciar la aplicación:");
        e.printStackTrace();
        
        // Opcional: mostrar un diálogo de error
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de aplicación");
        alert.setHeaderText("Se ha producido un error al iniciar la aplicación");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}

    public static void main(String[] args) {
        launch(args);
    }
}