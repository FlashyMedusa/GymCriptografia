package org.example.gimnasio.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    // Registrar pago
    public static void registerPago(int idCliente, double monto, String fechaPago) {
        try (Connection conn = GimnasioDB.connect()) {
            String insertPago = "INSERT INTO pagos (id_cliente, monto, fecha_pago) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertPago)) {
                pstmt.setInt(1, idCliente);
                pstmt.setDouble(2, monto);
                pstmt.setString(3, fechaPago);

                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Error al registrar el pago: " + e.getMessage());
        }
    }

    public static List<String> getPagosDelDia() {
        List<String> pagos = new ArrayList<>();
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT monto FROM pagos WHERE fecha_pago = CURRENT_DATE";  // Suponiendo que usamos la fecha de la base de datos
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    double monto = rs.getDouble("monto");
                    pagos.add("Pago: $" + monto);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los pagos del día: " + e.getMessage());
        }
        return pagos;
    }

}
