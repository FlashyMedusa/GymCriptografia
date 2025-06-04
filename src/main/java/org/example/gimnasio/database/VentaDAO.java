package org.example.gimnasio.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    // Registrar venta
    public static void registerVenta(int idCliente, String producto, double monto, String metodoPago, String fechaVenta) {
        try (Connection conn = GimnasioDB.connect()) {
            String insertVenta = "INSERT INTO ventas (id_cliente, producto, monto, metodo_pago, fecha_venta) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertVenta)) {
                pstmt.setInt(1, idCliente);
                pstmt.setString(2, producto);
                pstmt.setDouble(3, monto);
                pstmt.setString(4, metodoPago);
                pstmt.setString(5, fechaVenta);

                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Error al registrar la venta: " + e.getMessage());
        }
    }

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

}
