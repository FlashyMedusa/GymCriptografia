package org.example.gimnasio.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    public static class PagoInfo {
        public int id;
        public int idCliente;
        public double monto;
        public String fechaPago;
        public String tipoPago;
        public String concepto;
        public String firmaDigital;
        public String idEntrenador;

        public PagoInfo(int id, int idCliente, double monto, String fechaPago,
                        String tipoPago, String concepto, String firmaDigital, String idEntrenador) {
            this.id = id;
            this.idCliente = idCliente;
            this.monto = monto;
            this.fechaPago = fechaPago;
            this.tipoPago = tipoPago;
            this.concepto = concepto;
            this.firmaDigital = firmaDigital;
            this.idEntrenador = idEntrenador;
        }
    }

    public static void registerPago(int idCliente, double monto, String fechaPago,
                                    String tipoPago, String concepto, String firmaDigital, String idEntrenador) throws SQLException {
        try (Connection conn = GimnasioDB.connect()) {
            String insertPago = """
                INSERT INTO pagos (id_cliente, monto, fecha_pago, tipo_pago, concepto, firma_digital, id_entrenador) 
                VALUES (?, ?, ?, ?, ?, ?, ?)""";

            try (PreparedStatement pstmt = conn.prepareStatement(insertPago)) {
                pstmt.setInt(1, idCliente);
                pstmt.setDouble(2, monto);
                pstmt.setString(3, fechaPago);
                pstmt.setString(4, tipoPago);
                pstmt.setString(5, concepto);
                pstmt.setString(6, firmaDigital);
                pstmt.setString(7, idEntrenador);
                pstmt.executeUpdate();
            }
        }
    }

    public static List<PagoInfo> getPagosDelDia(String fecha) {
        List<PagoInfo> pagos = new ArrayList<>();
        try (Connection conn = GimnasioDB.connect()) {
            String query = """
                SELECT id_pago, id_cliente, monto, fecha_pago, tipo_pago, concepto, firma_digital, id_entrenador 
                FROM pagos WHERE fecha_pago = ?""";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, fecha);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    pagos.add(new PagoInfo(
                            rs.getInt("id_pago"),
                            rs.getInt("id_cliente"),
                            rs.getDouble("monto"),
                            rs.getString("fecha_pago"),
                            rs.getString("tipo_pago"),
                            rs.getString("concepto"),
                            rs.getString("firma_digital"),
                            rs.getString("id_entrenador")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los pagos del día: " + e.getMessage());
        }
        return pagos;
    }

    public static List<String> getPagosDelDiaSimple(String fecha) {
        List<String> pagos = new ArrayList<>();
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT monto, tipo_pago FROM pagos WHERE fecha_pago = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, fecha);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    double monto = rs.getDouble("monto");
                    String tipoPago = rs.getString("tipo_pago");
                    pagos.add(tipoPago + ": $" + monto);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los pagos del día: " + e.getMessage());
        }
        return pagos;
    }

    public static String getFirmaPago(int idPago) {
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT firma_digital FROM pagos WHERE id_pago = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idPago);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("firma_digital");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener la firma del pago: " + e.getMessage());
        }
        return null;
    }

    public static String getDatosPago(int idPago) {
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT monto, fecha_pago FROM pagos WHERE id_pago = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idPago);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    double monto = rs.getDouble("monto");
                    String fechaPago = rs.getString("fecha_pago");
                    return monto + ":" + fechaPago;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los datos del pago: " + e.getMessage());
        }
        return null;
    }

    public static double getTotalPagosDia(String fecha) {
        double total = 0;
        try (Connection conn = GimnasioDB.connect()) {
            String query = "SELECT SUM(monto) as total FROM pagos WHERE fecha_pago = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, fecha);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular total de pagos: " + e.getMessage());
        }
        return total;
    }
}