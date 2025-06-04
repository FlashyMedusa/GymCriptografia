package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.VentaDAO;
import org.example.gimnasio.database.PagoDAO;
import org.example.gimnasio.security.SecurityUtils;

import java.util.List;

public class CorteCajaController {

    @FXML private ListView<String> ventasListView;
    @FXML private ListView<String> pagosListView;
    @FXML private Label totalVentasLabel;
    @FXML private Label totalPagosLabel;
    @FXML private Label diferenciaLabel;

    // Método para manejar el clic en "Generar Reporte"
    @FXML
    public void handleGenerarReporte(ActionEvent event) {
        try {
            // Cargar ventas y pagos del día
            List<String> ventas = VentaDAO.getVentasDelDia();  // Método que obtiene todas las ventas del día
            List<String> pagos = PagoDAO.getPagosDelDia();  // Método que obtiene todos los pagos del día

            // Calcular los totales
            double totalVentas = 0;
            for (String venta : ventas) {
                totalVentas += Double.parseDouble(venta.split(":")[1].trim().substring(1));  // Asumiendo que el formato es "Producto: $monto"
            }

            double totalPagos = 0;
            for (String pago : pagos) {
                totalPagos += Double.parseDouble(pago.split(":")[1].trim().substring(1));  // Asumiendo el formato "Pago: $monto"
            }

            // Calcular la diferencia
            double diferencia = totalVentas - totalPagos;

            // Mostrar resultados en la interfaz
            totalVentasLabel.setText("Total de Ventas: $" + totalVentas);
            totalPagosLabel.setText("Total de Pagos: $" + totalPagos);
            diferenciaLabel.setText("Diferencia: $" + diferencia);

            // Crear un reporte
            String reporte = "Reporte del Día\n\nVentas del Día:\n" + String.join("\n", ventas) +
                    "\n\nPagos del Día:\n" + String.join("\n", pagos) +
                    "\n\nTotal Ventas: $" + totalVentas +
                    "\nTotal Pagos: $" + totalPagos +
                    "\nDiferencia: $" + diferencia;

            // Firmar el reporte con un hash (SHA-256) para integridad
            String reporteFirmado = SecurityUtils.hashSHA256(reporte);

            // Mostrar mensaje con el reporte firmado
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reporte Generado");
            alert.setHeaderText("Reporte del Día");
            alert.setContentText("Reporte generado exitosamente. Reporte firmado: " + reporteFirmado);
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al generar el reporte");
            alert.setContentText("Ocurrió un error al generar el reporte: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
