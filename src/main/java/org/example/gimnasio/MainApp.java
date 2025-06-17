package org.example.gimnasio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.gimnasio.database.GimnasioDB;
import javafx.scene.control.Alert;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Inicializar la base de datos y crear las tablas
            System.out.println("Inicializando base de datos...");
            GimnasioDB.createTables();
            System.out.println("Base de datos inicializada correctamente.");

            // Cargar la vista principal
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/gimnasio/view/main.fxml"));

            primaryStage.setTitle("Sistema de Gimnasio - Gestión Segura");
            primaryStage.setScene(new Scene(root, 1200, 800));
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();

            System.out.println("Aplicación iniciada correctamente.");

        } catch (Exception e) {
            System.err.println("Error crítico al iniciar la aplicación:");
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Crítico");
            alert.setHeaderText("No se pudo iniciar la aplicación");
            alert.setContentText("Error: " + e.getMessage() + "\n\nVerifique que la carpeta 'db' exista y tenga permisos de escritura.");
            alert.showAndWait();

            System.exit(1);
        }
    }

    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Gimnasio...");
        launch(args);
    }
}