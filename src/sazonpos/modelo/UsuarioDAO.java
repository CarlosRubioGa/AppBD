package sazonpos.modelo;

import sazonpos.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class UsuarioDAO {

    /**
     * Valida usuario y contraseña contra la BD.
     * @param login    valor de columna usuario_login
     * @param password valor de columna password_hash
     * @return Usuario lleno si es válido, null si no coincide
     */
    public Usuario validarLogin(String login, String password) {
        Usuario u = null;

        String sql =
                "SELECT u.id_usuario, u.nombre, u.usuario_login, u.password_hash, " +
                "       u.activo, r.nombre AS rol " +
                "FROM usuario u " +
                "INNER JOIN rol r ON r.id_rol = u.id_rol " +
                "WHERE u.usuario_login = ? " +
                "  AND u.password_hash = ? " +
                "  AND u.activo = 1";

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    // 👇 AQUÍ ESTABA EL PROBLEMA: usar setUsuarioLogin, no setUsuario
                    u.setUsuarioLogin(rs.getString("usuario_login"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setRol(rs.getString("rol"));
                    u.setActivo(rs.getBoolean("activo"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return u;
    }

    public boolean eliminar(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public boolean inactivar(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public boolean actualizar(Usuario u) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public boolean insertar(Usuario u) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public List<Usuario> listarTodos() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
