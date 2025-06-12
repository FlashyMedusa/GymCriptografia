package org.example.gimnasio.database;

import org.example.gimnasio.security.SecurityUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    // Registrar venta con firma digital
    public static void registerVenta(int idCliente, String producto, double monto, String metodoPago, String fechaVenta, String firmaDigital) {
        try (Connection conn = GimnasioDB.connect()) {
            String insertVenta = "INSERT INTO ventas (id_cliente, producto, monto, metodo_pago, fecha_venta, firma_digital) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertVenta)) {
                pstmt.setInt(1, idCliente);
                pstmt.setString(2, producto);
                pstmt.setDouble(3, monto);
                pstmt.setString(4, metodoPago);
                pstmt.setString(5, fechaVenta);
                pstmt.setString(6, firmaDigital);  // Guardamos la firma digital

                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Error al registrar la venta: " + e.getMessage());
        }
    }

    // Metodo para obtener las ventas del día (si es necesario)
    public static List<String> getVentasDelDia() {
        List<String> ventas = new ArrayList<>();
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT producto, monto FROM ventas WHERE fecha_venta = CURRENT_DATE";  // Suponiendo que usamos la fecha de la base de datos
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String producto = rs.getString("producto");
                    double monto = rs.getDouble("monto");
                    ventas.add(producto + ": $" + monto);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener las ventas del día: " + e.getMessage());
        }
        return ventas;
    }

    // Metodo para obtener la firma de una venta
    public static String getFirmaVenta(int idVenta) {
        String firmaVenta = null;
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT firma_digital FROM ventas WHERE id_venta = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idVenta);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    firmaVenta = rs.getString("firma_digital");  // Obtener la firma digital de la venta
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener la firma de la venta: " + e.getMessage());
        }
        return firmaVenta;
    }

    // Metodo para obtener los datos de una venta (producto y monto) para la firma
    public static String getDatosVenta(int idVenta) {
        String datosVenta = null;
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT producto, monto FROM ventas WHERE id_venta = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idVenta);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String producto = rs.getString("producto");
                    double monto = rs.getDouble("monto");
                    datosVenta = producto + ":" + monto;  // Concatenar los datos importantes para la firma
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los datos de la venta: " + e.getMessage());
        }
        return datosVenta;
    }
}
