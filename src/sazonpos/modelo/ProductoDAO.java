package sazonpos.modelo;

import sazonpos.util.ConexionBD;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // LISTAR TODOS (con nombre de categoría y proveedor)
    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT p.id_producto, p.nombre, p.id_categoria, p.id_proveedor, "
                   + "       p.precio_venta, p.costo_unitario, p.stock_actual, p.activo, "
                   + "       c.nombre AS categoria, pr.nombre AS proveedor "
                   + "FROM producto p "
                   + "JOIN categoria_producto c ON p.id_categoria = c.id_categoria "
                   + "LEFT JOIN proveedor pr ON p.id_proveedor = pr.id_proveedor "
                   + "ORDER BY c.nombre, p.nombre";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("id_producto"));
                p.setNombre(rs.getString("nombre"));
                p.setIdCategoria(rs.getInt("id_categoria"));
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setPrecioVenta(rs.getBigDecimal("precio_venta"));
                p.setCostoUnitario(rs.getBigDecimal("costo_unitario"));
                p.setStockActual(rs.getInt("stock_actual"));
                p.setActivo(rs.getBoolean("activo"));
                p.setNombreCategoria(rs.getString("categoria"));
                p.setNombreProveedor(rs.getString("proveedor"));

                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // LISTAR SOLO INVENTARIO (BEBIDAS, PAPAS, DULCES)
    // es decir, categorías 1, 2, 3
    public List<Producto> listarInventario() {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT p.id_producto, p.nombre, p.id_categoria, p.id_proveedor, "
                   + "       p.precio_venta, p.costo_unitario, p.stock_actual, p.activo, "
                   + "       c.nombre AS categoria, pr.nombre AS proveedor "
                   + "FROM producto p "
                   + "JOIN categoria_producto c ON p.id_categoria = c.id_categoria "
                   + "LEFT JOIN proveedor pr ON p.id_proveedor = pr.id_proveedor "
                   + "WHERE p.id_categoria IN (1,2,3) "
                   + "ORDER BY c.nombre, p.nombre";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("id_producto"));
                p.setNombre(rs.getString("nombre"));
                p.setIdCategoria(rs.getInt("id_categoria"));
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setPrecioVenta(rs.getBigDecimal("precio_venta"));
                p.setCostoUnitario(rs.getBigDecimal("costo_unitario"));
                p.setStockActual(rs.getInt("stock_actual"));
                p.setActivo(rs.getBoolean("activo"));
                p.setNombreCategoria(rs.getString("categoria"));
                p.setNombreProveedor(rs.getString("proveedor"));

                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // INSERTAR
    public boolean insertar(Producto p) {
        String sql = "INSERT INTO producto "
                   + "(nombre, id_categoria, id_proveedor, precio_venta, costo_unitario, stock_actual, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getIdCategoria());
            if (p.getIdProveedor() > 0) {
                ps.setInt(3, p.getIdProveedor());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setBigDecimal(4, p.getPrecioVenta());
            if (p.getCostoUnitario() != null) {
                ps.setBigDecimal(5, p.getCostoUnitario());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }
            ps.setInt(6, p.getStockActual());
            ps.setBoolean(7, p.isActivo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ACTUALIZAR
    public boolean actualizar(Producto p) {
        String sql = "UPDATE producto SET "
                   + "nombre = ?, id_categoria = ?, id_proveedor = ?, "
                   + "precio_venta = ?, costo_unitario = ?, stock_actual = ?, activo = ? "
                   + "WHERE id_producto = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getIdCategoria());
            if (p.getIdProveedor() > 0) {
                ps.setInt(3, p.getIdProveedor());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setBigDecimal(4, p.getPrecioVenta());
            if (p.getCostoUnitario() != null) {
                ps.setBigDecimal(5, p.getCostoUnitario());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }
            ps.setInt(6, p.getStockActual());
            ps.setBoolean(7, p.isActivo());
            ps.setInt(8, p.getIdProducto());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ELIMINAR (física)
    public boolean eliminar(int idProducto) {
        String sql = "DELETE FROM producto WHERE id_producto = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // BUSCAR POR NOMBRE (para cuadro de búsqueda)
    public List<Producto> buscarPorNombre(String filtro) {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT p.id_producto, p.nombre, p.id_categoria, p.id_proveedor, "
                   + "       p.precio_venta, p.costo_unitario, p.stock_actual, p.activo, "
                   + "       c.nombre AS categoria, pr.nombre AS proveedor "
                   + "FROM producto p "
                   + "JOIN categoria_producto c ON p.id_categoria = c.id_categoria "
                   + "LEFT JOIN proveedor pr ON p.id_proveedor = pr.id_proveedor "
                   + "WHERE p.nombre LIKE ? "
                   + "ORDER BY p.nombre";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + filtro + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("id_producto"));
                    p.setNombre(rs.getString("nombre"));
                    p.setIdCategoria(rs.getInt("id_categoria"));
                    p.setIdProveedor(rs.getInt("id_proveedor"));
                    p.setPrecioVenta(rs.getBigDecimal("precio_venta"));
                    p.setCostoUnitario(rs.getBigDecimal("costo_unitario"));
                    p.setStockActual(rs.getInt("stock_actual"));
                    p.setActivo(rs.getBoolean("activo"));
                    p.setNombreCategoria(rs.getString("categoria"));
                    p.setNombreProveedor(rs.getString("proveedor"));

                    lista.add(p);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ACTUALIZAR STOCK (para restar cuando se vende algo con inventario)
    public boolean actualizarStock(int idProducto, int nuevoStock) {
        String sql = "UPDATE producto SET stock_actual = ? WHERE id_producto = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
