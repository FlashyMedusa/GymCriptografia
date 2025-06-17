package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.gimnasio.database.VentaDAO;
import org.example.gimnasio.database.PagoDAO;
import org.example.gimnasio.database.EntrenadorDAO;
import org.example.gimnasio.security.*;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.List;

public class CorteCajaController {

    @FXML private ListView<String> ventasListView;
    @FXML private ListView<String> pagosListView;
    @FXML private Label totalVentasLabel;
    @FXML private Label totalPagosLabel;
    @FXML private Label diferenciaLabel;
    @FXML private Label estadoFirmasLabel;
    @FXML private DatePicker fechaCorteField;
    @FXML private TextField idEntrenadorField;
    @FXML private PasswordField passwordEntrenadorField;
    @FXML private Button generarReporteBtn;
    @FXML private Button cargarDatosBtn;
    @FXML private TextArea reporteArea;

    @FXML
    public void initialize() {
        fechaCorteField.setValue(LocalDate.now());
        estadoFirmasLabel.setText("Datos no cargados");
        estadoFirmasLabel.setStyle("-fx-text-fill: gray;");
    }

    @FXML
    public void handleCargarDatos(ActionEvent event) {
        try {
            String fechaCorte = fechaCorteField.getValue().toString();

            // Cargar ventas y pagos del día
            List<VentaDAO.VentaInfo> ventas = VentaDAO.getVentasDelDia(fechaCorte);
            List<PagoDAO.PagoInfo> pagos = PagoDAO.getPagosDelDia(fechaCorte);

            // Mostrar en las listas
            ObservableList<String> ventasItems = FXCollections.observableArrayList();
            ObservableList<String> pagosItems = FXCollections.observableArrayList();

            double totalVentas = 0;
            for (VentaDAO.VentaInfo venta : ventas) {
                ventasItems.add(String.format("ID: %d | %s: $%.2f | %s",
                        venta.id, venta.producto, venta.monto, venta.metodoPago));
                totalVentas += venta.monto;
            }

            double totalPagos = 0;
            for (PagoDAO.PagoInfo pago : pagos) {
                pagosItems.add(String.format("ID: %d | %s: $%.2f | %s",
                        pago.id, pago.tipoPago, pago.monto, pago.concepto));
                totalPagos += pago.monto;
            }

            ventasListView.setItems(ventasItems);
            pagosListView.setItems(pagosItems);

            // Actualizar totales
            totalVentasLabel.setText(String.format("Total Ventas: $%.2f", totalVentas));
            totalPagosLabel.setText(String.format("Total Pagos: $%.2f", totalPagos));

            double diferencia = totalVentas - totalPagos;
            diferenciaLabel.setText(String.format("Diferencia: $%.2f", diferencia));

            if (diferencia >= 0) {
                diferenciaLabel.setStyle("-fx-text-fill: green;");
            } else {
                diferenciaLabel.setStyle("-fx-text-fill: red;");
            }

            // Verificar firmas
            boolean firmasValidas = verificarTodasLasFirmas(ventas, pagos);
            if (firmasValidas) {
                estadoFirmasLabel.setText("✓ Todas las firmas son válidas");
                estadoFirmasLabel.setStyle("-fx-text-fill: green;");
                generarReporteBtn.setDisable(false);
            } else {
                estadoFirmasLabel.setText("✗ Algunas firmas son inválidas");
                estadoFirmasLabel.setStyle("-fx-text-fill: red;");
                generarReporteBtn.setDisable(true);
            }

        } catch (Exception e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    @FXML
    public void handleGenerarReporte(ActionEvent event) {
        try {
            // Validar credenciales del entrenador
            String idEntrenador = idEntrenadorField.getText().trim();
            String password = passwordEntrenadorField.getText();

            if (idEntrenador.isEmpty() || password.isEmpty()) {
                mostrarError("Ingrese las credenciales del entrenador.");
                return;
            }

            PrivateKey privateKey = EntrenadorDAO.getPrivateKeyDecrypted(idEntrenador, password);
            if (privateKey == null) {
                mostrarError("Credenciales de entrenador inválidas.");
                return;
            }

            // Generar reporte
            String fechaCorte = fechaCorteField.getValue().toString();
            String reporte = generarReporteTexto(fechaCorte);

            // Firmar el reporte
            String reporteFirmado = DigitalSignatureUtils.signTransaction(reporte, privateKey);

            // Mostrar reporte en el área de texto
            String reporteCompleto = reporte + "\n\n--- FIRMA DIGITAL ---\n" + reporteFirmado;
            reporteArea.setText(reporteCompleto);

            // Verificar la firma
            PublicKey publicKey = EntrenadorDAO.getPublicKey(idEntrenador);
            boolean firmaValida = DigitalSignatureUtils.verifyTransaction(reporte, reporteFirmado, publicKey);

            if (firmaValida) {
                mostrarExito("Reporte generado y firmado exitosamente.\nLa firma digital es válida.");
            } else {
                mostrarError("Error: La firma digital del reporte no es válida.");
            }

        } catch (Exception e) {
            mostrarError("Error al generar el reporte: " + e.getMessage());
        }
    }

    private boolean verificarTodasLasFirmas(List<VentaDAO.VentaInfo> ventas, List<PagoDAO.PagoInfo> pagos) {
        try {
            // Verificar firmas de ventas
            for (VentaDAO.VentaInfo venta : ventas) {
                PublicKey publicKey = EntrenadorDAO.getPublicKey(venta.idEntrenador);
                if (publicKey == null) {
                    System.out.println("No se pudo obtener clave pública para entrenador: " + venta.idEntrenador);
                    return false;
                }

                String dataToVerify = String.format("%d|%s|%.2f|%s|%s",
                        venta.idCliente, venta.producto, venta.monto, venta.metodoPago, venta.fechaVenta);

                boolean firmaValida = DigitalSignatureUtils.verifyTransaction(dataToVerify, venta.firmaDigital, publicKey);
                if (!firmaValida) {
                    System.out.println("Firma inválida para venta ID: " + venta.id);
                    return false;
                }
            }

            // Verificar firmas de pagos
            for (PagoDAO.PagoInfo pago : pagos) {
                PublicKey publicKey = EntrenadorDAO.getPublicKey(pago.idEntrenador);
                if (publicKey == null) {
                    System.out.println("No se pudo obtener clave pública para entrenador: " + pago.idEntrenador);
                    return false;
                }

                String dataToVerify = String.format("%d|%.2f|%s|%s|%s",
                        pago.idCliente, pago.monto, pago.fechaPago, pago.tipoPago, pago.concepto);

                boolean firmaValida = DigitalSignatureUtils.verifyTransaction(dataToVerify, pago.firmaDigital, publicKey);
                if (!firmaValida) {
                    System.out.println("Firma inválida para pago ID: " + pago.id);
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            System.out.println("Error al verificar firmas: " + e.getMessage());
            return false;
        }
    }

    private String generarReporteTexto(String fecha) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("========================================\n");
        reporte.append("         REPORTE DE CORTE DE CAJA\n");
        reporte.append("========================================\n");
        reporte.append("Fecha: ").append(fecha).append("\n");
        reporte.append("Generado: ").append(LocalDate.now()).append("\n\n");

        reporte.append("RESUMEN:\n");
        reporte.append(totalVentasLabel.getText()).append("\n");
        reporte.append(totalPagosLabel.getText()).append("\n");
        reporte.append(diferenciaLabel.getText()).append("\n\n");

        reporte.append("VENTAS DEL DÍA:\n");
        for (String venta : ventasListView.getItems()) {
            reporte.append("- ").append(venta).append("\n");
        }

        reporte.append("\nPAGOS DEL DÍA:\n");
        for (String pago : pagosListView.getItems()) {
            reporte.append("- ").append(pago).append("\n");
        }

        reporte.append("\nESTADO DE VERIFICACIÓN:\n");
        reporte.append(estadoFirmasLabel.getText()).append("\n");

        reporte.append("\n========================================");

        return reporte.toString();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText("Reporte Generado");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error al Generar Reporte");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}