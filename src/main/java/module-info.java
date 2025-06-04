module org.example.gimnasio {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.swing;

    opens org.example.gimnasio to javafx.fxml;
    exports org.example.gimnasio;
    exports org.example.gimnasio.controller;  // Esto permite que FXMLLoader acceda al controlador
    opens org.example.gimnasio.controller to javafx.fxml; // Abre el paquete a javafx.fxml para la reflexión
}