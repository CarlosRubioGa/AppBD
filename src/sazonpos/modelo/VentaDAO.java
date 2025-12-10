package sazonpos.modelo;

import sazonpos.util.ConexionBD;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class VentaDAO {

    public boolean registrarVenta(
            int idUsuario,
            BigDecimal total,
            int idFormaPago,              // 1=EFECTIVO, 2=TARJETA, 3=TRANSFERENCIA
            String referenciaTarjeta,     // puede ser null
            List<Object[]> detalles       // cada Object[] = {idProd, cant, precioUnit, subtotal}
    ) {
        String sqlVenta = "INSERT INTO venta (id_usuario, fecha_hora, total, id_forma_pago, referencia_pago) " +
                          "VALUES (?, NOW(), ?, ?, ?)";
        String sqlDetalle = "INSERT INTO venta_detalle (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                            "VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateStock =
                "UPDATE producto " +
                "SET stock_actual = CASE " +
                "    WHEN stock_actual IS NULL THEN NULL " +      // comida sin inventario
                "    ELSE stock_actual - LEAST(?, stock_actual) " +// no baja de cero
                "END " +
                "WHERE id_producto = ?";

        try (Connection cn = ConexionBD.getConexion()) {
            cn.setAutoCommit(false);

            int idVentaGenerada;
            // Insert en VENTA
            try (PreparedStatement psVenta = cn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setInt(1, idUsuario);
                psVenta.setBigDecimal(2, total);
                psVenta.setInt(3, idFormaPago);
                if (referenciaTarjeta == null || referenciaTarjeta.isBlank()) {
                    psVenta.setNull(4, Types.VARCHAR);
                } else {
                    psVenta.setString(4, referenciaTarjeta);
                }
                psVenta.executeUpdate();

                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) {
                        idVentaGenerada = rs.getInt(1);
                    } else {
                        cn.rollback();
                        return false;
                    }
                }
            }

            // Detalle + stock
            try (PreparedStatement psDet   = cn.prepareStatement(sqlDetalle);
                 PreparedStatement psStock = cn.prepareStatement(sqlUpdateStock)) {

                for (Object[] det : detalles) {
                    int idProd            = (int) det[0];
                    int cant              = (int) det[1];
                    BigDecimal precioUnit = (BigDecimal) det[2];
                    BigDecimal subtotal   = (BigDecimal) det[3];

                    // detalle
                    psDet.setInt(1, idVentaGenerada);
                    psDet.setInt(2, idProd);
                    psDet.setInt(3, cant);
                    psDet.setBigDecimal(4, precioUnit);
                    psDet.setBigDecimal(5, subtotal);
                    psDet.addBatch();

                    // stock
                    psStock.setInt(1, cant);
                    psStock.setInt(2, idProd);
                    psStock.addBatch();
                }

                psDet.executeBatch();
                psStock.executeBatch();
            }

            cn.commit();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    // =========================
    // MÉTODOS DE REPORTE (que ya usabas)
    // =========================

public java.util.List<Object[]> listarPorFechaDetallado(String fecha) {
    java.util.List<Object[]> lista = new java.util.ArrayList<>();

    String sql =
        "SELECT v.id_venta, v.folio, v.fecha_hora, " +
        "       u.nombre AS usuario, " +
        "       v.id_forma_pago, " +
        "       v.total " +
        "FROM venta v " +
        "JOIN usuario u ON v.id_usuario = u.id_usuario " +
        "WHERE DATE(v.fecha_hora) = ? " +
        "ORDER BY v.fecha_hora";

    try (java.sql.Connection con = sazonpos.util.ConexionBD.getConexion();
         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, fecha);   // formato: "YYYY-MM-DD"

        try (java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idVenta      = rs.getInt("id_venta");
                String folio     = rs.getString("folio");
                java.sql.Timestamp ts = rs.getTimestamp("fecha_hora");
                String fechaHora = (ts != null) ? ts.toString() : "";
                String usuario   = rs.getString("usuario");

                int idFormaPago  = rs.getInt("id_forma_pago");
                String formaPagoTexto;
                switch (idFormaPago) {
                    case 1:  formaPagoTexto = "EFECTIVO";      break;
                    case 2:  formaPagoTexto = "TARJETA";       break;
                    case 3:  formaPagoTexto = "TRANSFERENCIA"; break;
                    default: formaPagoTexto = "DESCONOCIDA";   break;
                }

                java.math.BigDecimal total = rs.getBigDecimal("total");

                lista.add(new Object[]{
                        idVenta,
                        folio,
                        fechaHora,
                        usuario,
                        formaPagoTexto,
                        total
                });
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return lista;
}


    public BigDecimal obtenerTotalPorFecha(String fechaYYYYMMDD) {
        String sql = "SELECT IFNULL(SUM(total),0) AS total_dia " +
                     "FROM venta WHERE DATE(fecha_hora) = ?";

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, fechaYYYYMMDD);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_dia");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal obtenerTotalPorFechaYFormaPago(String fechaYYYYMMDD, int idFormaPago) {
        String sql = "SELECT IFNULL(SUM(total),0) AS total_fp " +
                     "FROM venta WHERE DATE(fecha_hora) = ? AND id_forma_pago = ?";

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, fechaYYYYMMDD);
            ps.setInt(2, idFormaPago);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_fp");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}
