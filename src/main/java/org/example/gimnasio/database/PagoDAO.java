package org.example.gimnasio.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    // Registrar pago con firma digital
    public static void registerPago(int idCliente, double monto, String fechaPago, String firmaDigital) {
        try (Connection conn = GimnasioDB.connect()) {
            String insertPago = "INSERT INTO pagos (id_cliente, monto, fecha_pago, firma_digital) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertPago)) {
                pstmt.setInt(1, idCliente);
                pstmt.setDouble(2, monto);
                pstmt.setString(3, fechaPago);
                pstmt.setString(4, firmaDigital);  // Guardamos la firma digital

                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Error al registrar el pago: " + e.getMessage());
        }
    }

    // Metodo para obtener los pagos del día
    public static List<String> getPagosDelDia() {
        List<String> pagos = new ArrayList<>();
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT monto, firma_digital FROM pagos WHERE fecha_pago = CURRENT_DATE";  // Suponiendo que usamos la fecha de la base de datos
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    double monto = rs.getDouble("monto");
                    String firmaDigital = rs.getString("firma_digital");
                    pagos.add("Pago: $" + monto + " | Firma Digital: " + firmaDigital);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los pagos del día: " + e.getMessage());
        }
        return pagos;
    }

    // Método para obtener la firma de un pago
    public static String getFirmaPago(int idPago) {
        String firmaPago = null;
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT firma_digital FROM pagos WHERE id_pago = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idPago);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    firmaPago = rs.getString("firma_digital");  // Obtener la firma digital del pago
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener la firma del pago: " + e.getMessage());
        }
        return firmaPago;
    }

    // Método para obtener los datos de un pago (monto y fecha) para la firma
    public static String getDatosPago(int idPago) {
        String datosPago = null;
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT monto, fecha_pago FROM pagos WHERE id_pago = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idPago);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    double monto = rs.getDouble("monto");
                    String fechaPago = rs.getString("fecha_pago");
                    datosPago = monto + ":" + fechaPago;  // Concatenar los datos importantes para la firma
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los datos del pago: " + e.getMessage());
        }
        return datosPago;
    }
}
