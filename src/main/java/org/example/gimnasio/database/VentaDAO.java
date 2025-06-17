package org.example.gimnasio.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public static class VentaInfo {
        public int id;
        public int idCliente;
        public String producto;
        public double monto;
        public String metodoPago;
        public String fechaVenta;
        public String firmaDigital;
        public String idEntrenador;

        public VentaInfo(int id, int idCliente, String producto, double monto,
                         String metodoPago, String fechaVenta, String firmaDigital, String idEntrenador) {
            this.id = id;
            this.idCliente = idCliente;
            this.producto = producto;
            this.monto = monto;
            this.metodoPago = metodoPago;
            this.fechaVenta = fechaVenta;
            this.firmaDigital = firmaDigital;
            this.idEntrenador = idEntrenador;
        }
    }

    public static void registerVenta(int idCliente, String producto, double monto,
                                     String metodoPago, String fechaVenta, String firmaDigital, String idEntrenador) throws SQLException {
        try (Connection conn = GimnasioDB.connect()) {
            String insertVenta = """
                INSERT INTO ventas (id_cliente, producto, monto, metodo_pago, fecha_venta, firma_digital, id_entrenador) 
                VALUES (?, ?, ?, ?, ?, ?, ?)""";

            try (PreparedStatement pstmt = conn.prepareStatement(insertVenta)) {
                pstmt.setInt(1, idCliente);
                pstmt.setString(2, producto);
                pstmt.setDouble(3, monto);
                pstmt.setString(4, metodoPago);
                pstmt.setString(5, fechaVenta);
                pstmt.setString(6, firmaDigital);
                pstmt.setString(7, idEntrenador);
                pstmt.executeUpdate();
            }
        }
    }

    public static List<VentaInfo> getVentasDelDia(String fecha) {
        List<VentaInfo> ventas = new ArrayList<>();
        try (Connection conn = GimnasioDB.connect()) {
            String query = """
                SELECT id_venta, id_cliente, producto, monto, metodo_pago, fecha_venta, firma_digital, id_entrenador 
                FROM ventas WHERE fecha_venta = ?""";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, fecha);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    ventas.add(new VentaInfo(
                            rs.getInt("id_venta"),
                            rs.getInt("id_cliente"),
                            rs.getString("producto"),
                            rs.getDouble("monto"),
                            rs.getString("metodo_pago"),
                            rs.getString("fecha_venta"),
                            rs.getString("firma_digital"),
                            rs.getString("id_entrenador")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener las ventas del día: " + e.getMessage());
        }
        return ventas;
    }

    public static List<String> getVentasDelDiaSimple(String fecha) {
        List<String> ventas = new ArrayList<>();
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT producto, monto FROM ventas WHERE fecha_venta = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, fecha);
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

    public static String getFirmaVenta(int idVenta) {
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT firma_digital FROM ventas WHERE id_venta = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idVenta);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("firma_digital");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener la firma de la venta: " + e.getMessage());
        }
        return null;
    }

    public static String getDatosVenta(int idVenta) {
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT producto, monto FROM ventas WHERE id_venta = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idVenta);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String producto = rs.getString("producto");
                    double monto = rs.getDouble("monto");
                    return producto + ":" + monto;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los datos de la venta: " + e.getMessage());
        }
        return null;
    }

    public static double getTotalVentasDia(String fecha) {
        double total = 0;
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT SUM(monto) as total FROM ventas WHERE fecha_venta = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, fecha);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular total de ventas: " + e.getMessage());
        }
        return total;
    }
}