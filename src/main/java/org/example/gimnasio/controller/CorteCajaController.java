package org.example.gimnasio.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.gimnasio.database.VentaDAO;
import org.example.gimnasio.database.PagoDAO;
import org.example.gimnasio.security.SecurityUtils;
import org.example.gimnasio.database.EntrenadorDAO;

import java.security.PrivateKey;
import java.security.PublicKey;
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

            // Verificar las firmas de las ventas y pagos
            boolean ventasFirmasValidas = verificarFirmasVentas(ventas);
            boolean pagosFirmasValidas = verificarFirmasPagos(pagos);

            if (ventasFirmasValidas && pagosFirmasValidas) {
                // Firma el reporte con el método signTransaction
                PrivateKey privateKey = obtenerClavePrivadaEntrenador(); // Debes obtener la clave privada del entrenador
                String reporteFirmado = SecurityUtils.signTransaction(reporte, privateKey);

                // Validar la integridad del reporte utilizando SHA-256
                boolean reporteValido = SecurityUtils.validateIntegritySHA256(reporte, reporteFirmado);

                if (reporteValido) {
                    // Mostrar mensaje con el reporte firmado
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Reporte Generado");
                    alert.setHeaderText("Reporte del Día");
                    alert.setContentText("Reporte generado exitosamente. Reporte firmado: " + reporteFirmado);
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error de Integridad");
                    alert.setHeaderText("Integridad del Reporte Invalida");
                    alert.setContentText("La integridad del reporte no es válida.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de Verificación");
                alert.setHeaderText("Firmas Inválidas");
                alert.setContentText("Las firmas de las ventas o pagos no son válidas.");
                alert.showAndWait();
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al generar el reporte");
            alert.setContentText("Ocurrió un error al generar el reporte: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Método para verificar las firmas de las ventas del día
    private boolean verificarFirmasVentas(List<String> ventas) throws Exception {
        for (String venta : ventas) {
            // Obtener la firma de la venta (esto asume que las firmas están almacenadas)
            String firmaVenta = obtenerFirmaVenta(venta);  // Implementa la lógica para obtener la firma

            // Verificar la firma de la venta utilizando la clave pública
            PublicKey publicKey = obtenerClavePublicaEntrenador();  // Obtén la clave pública del entrenador
            String datosVenta = obtenerDatosVenta(venta);  // Extrae los datos (producto, monto, etc.) para verificar la firma
            boolean firmaValida = SecurityUtils.verifyTransaction(datosVenta, firmaVenta, publicKey);
            if (!firmaValida) {
                return false;  // Si alguna firma no es válida, retorna falso
            }
        }
        return true;  // Si todas las firmas son válidas
    }

    // Método para verificar las firmas de los pagos del día
    private boolean verificarFirmasPagos(List<String> pagos) throws Exception {
        for (String pago : pagos) {
            // Obtener la firma del pago (esto asume que las firmas están almacenadas)
            String firmaPago = obtenerFirmaPago(pago);  // Implementa la lógica para obtener la firma

            // Verificar la firma del pago utilizando la clave pública
            PublicKey publicKey = obtenerClavePublicaEntrenador();  // Obtén la clave pública del entrenador
            String datosPago = obtenerDatosPago(pago);  // Extrae los datos (monto, fecha, etc.) para verificar la firma
            boolean firmaValida = SecurityUtils.verifyTransaction(datosPago, firmaPago, publicKey);
            if (!firmaValida) {
                return false;  // Si alguna firma no es válida, retorna falso
            }
        }
        return true;  // Si todas las firmas son válidas
    }

    // Metodo para obtener la firma de la venta
    private String obtenerFirmaVenta(String venta) {
        String[] ventaInfo = venta.split(":");  // Dividir la cadena de venta por ":"
        String ventaId = ventaInfo[0].trim();  // Obtener el ID de la venta 
        return VentaDAO.getFirmaVenta(Integer.parseInt(ventaId));  // Obtener la firma de la base de datos
    }

    // Metodo para obtener la firma del pago
    private String obtenerFirmaPago(String pago) {
        String[] pagoInfo = pago.split(":");  // Dividir la cadena de pago por ":"
        String pagoId = pagoInfo[0].trim();  // Obtener el ID del pago
        return PagoDAO.getFirmaPago(Integer.parseInt(pagoId));  // Obtener la firma de la base de datos
    }

    // Metodo para obtener la clave pública del entrenador (esto puede depender de cómo gestionas las claves públicas)
    private PublicKey obtenerClavePublicaEntrenador() {
        // Debemos obtener la clave pública desde la base de datos u otro sistema de almacenamiento seguro
        return EntrenadorDAO.getPublicKey("id_usuario");  // Supongamos que el ID de usuario es conocido
    }

    // Metodo para obtener la clave privada del entrenador (esto también depende de tu gestión de claves)
    private PrivateKey obtenerClavePrivadaEntrenador() {
        // Aquí deberías implementar la lógica para obtener la clave privada desde un sistema seguro de almacenamiento
        return null;  // Este es un ejemplo, no se recomienda almacenar claves privadas en la base de datos
    }

    // Metodos para extraer los datos de las ventas y pagos (producto, monto, etc.)
    private String obtenerDatosVenta(String venta) {
        // Implementa la lógica para extraer los datos de la venta
        return "productoYMontoDeVenta";  // Este es un ejemplo
    }

    private String obtenerDatosPago(String pago) {
        // Implementa la lógica para extraer los datos del pago
        return "montoYFechaDePago";  // Este es un ejemplo
    }
}
